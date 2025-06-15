package oscill;

import ui.*;
import util.*;
import numeric.*;

import java.awt.*;


/**
 * A canvas displaying the graphic animation
 */

public class Graphic extends Canvas
                     implements Painter, SimulationDisplay, Runnable
  { static final int delay=30;
    static final int dx=8;

    Settings settings;
    GraphicSettings graphicSettings;
    Component legenda;
    StatusDisplayer status;
    Dimension dim;
    Insets insets;
    CoordinateMapper map;
    NAxesDrawer axes;
    CursorChanger curs;
    Thread thread=null;

    double time0=0;


    public Graphic(Settings settings, 
                   GraphicSettings graphicSettings,
                   Component legenda,
                   StatusDisplayer status) 
      { this.settings=settings;
        this.graphicSettings=graphicSettings;
        this.legenda=legenda;
        this.status=status;
        setBackground(Color.black);
        map=new CoordinateMapper();
        axes=new NAxesDrawer(map);
        curs=new CursorChanger(this);
        recomputeWindow();
      }

    public void set(Settings settings)
      { stopAnimation();
        this.settings=settings;
        time0=0;
        recomputeWindow();
        repaint();
        legenda.repaint();
      }


    public void set(GraphicSettings graphicSettings)
      { stopAnimation();
        this.graphicSettings=graphicSettings;
        time0=0;
        recomputeWindow();
        repaint();
        legenda.repaint();
      }

    
        
    /**
     * Usato per disegnare la legenda.
     * @see ui.Painter
     */ 
    public void paint(Component com, Graphics g)
      { 
        Dimension d=com.size();
        FontMetrics fm=g.getFontMetrics();
        int asc=fm.getAscent();
        int h=fm.getHeight();
        String str="Grafico delle oscillazioni";
        int w=fm.stringWidth(str);
        g.setColor(Color.lightGray);
        g.drawString(str, (d.width-w)/2+1, asc+3+1);
        g.setColor(Color.blue);
        g.drawString(str, (d.width-w)/2-2, asc+3-2);
        g.setColor(Color.gray);
        if (settings.smorzamento[0]!=0)
        {str="Onda 1   "+Format.format(".3", settings.ampiezza[0])
            +" exp(-"+Format.format(".3", settings.smorzamento[0])
            +"t) sin("+Format.format(".3", 360.0*settings.frequenza[0])
            +" t + "+settings.fase[0]+")";}
        else
        {str="Onda 1   "+Format.format(".3", settings.ampiezza[0])
            +" sin("+Format.format(".3", 360.0*settings.frequenza[0])
            +" t + "+settings.fase[0]+")";}


        g.drawString(str, 50, h+asc+3);

        if (settings.smorzamento[1]!=0)
        {str="Onda 2   "+Format.format(".3", settings.ampiezza[1])
            +" exp(-"+Format.format(".3", settings.smorzamento[1])
            +"t) sin("+Format.format(".3", 360.0*settings.frequenza[1])
            +" t + "+settings.fase[1]+")";}
        else
        {str="Onda 2   "+Format.format(".3", settings.ampiezza[1])
            +" sin("+Format.format(".3", 360.0*settings.frequenza[1])
            +" t + "+settings.fase[1]+")";}


        g.drawString(str, 50, h*2+asc+3);
        str="Onda 1 + Onda 2";
        g.drawString(str, 50, h*3+asc+3);
        g.setColor(Color.red);
        g.drawLine(10, h+asc/2+3, 40, h+asc/2+3);
        g.setColor(Color.cyan);
        g.drawLine(10, h*2+asc/2+3, 40, h*2+asc/2+3);
        g.setColor(Color.green);
        g.drawLine(10, h*3+asc/2+3, 40, h*3+asc/2+3);
      }    

    public synchronized void startAnimation()
      { stopAnimation();
        repaint();
        thread=new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
      }

    public synchronized void stopAnimation()
      { if (thread!=null)
          thread.stop();
        thread=null;
      }

    /**
     * Esegue il lavoro di animazione
     */
    public void run()
      { repaint();

        Graphics g=getGraphics();
        if (g==null)
          return;

        //g.setColor(Color.black);


        Point pt=new Point(insets.left, dim.height-insets.bottom);
        axes.drawAxes(g, pt, Color.black);

        g.drawRect(insets.left, insets.top,
                   dim.width-insets.left-insets.right,
                   dim.height-insets.top-insets.bottom);
         
        if (status!=null)
          status.showStatus("Clicca sulla finestra per interrompere la "
                            +"simulazione");
        try
          { Thread.sleep(250);
          }
        catch (InterruptedException e)
          {
          }

        double dt=chooseDeltaTime();
        time0=0;


        double z[]={0, 0};
        Point zero=realToPixel(0, 0);

        while(true)
          {



            
            try
              { Thread.sleep(delay);
              }
            catch (InterruptedException e)
              {
              }

            pixelToReal(insets.left+dx, 0, z);
            time0+=z[0];


            Rectangle old_clip=clip(g);
            g.copyArea(insets.left+1+dx, insets.top+1,
                       dim.width-insets.left-insets.right-1-dx,
                       dim.height-insets.top-insets.bottom-1,
                       -dx, 0);
            g.setColor(getBackground());
            g.fillRect(dim.width-insets.right-dx, 
                       insets.top+1,
                       dx,
                       dim.height-insets.top-insets.bottom-1);

            g.setColor(Color.blue);

            g.drawLine(dim.width-insets.right-dx-1, zero.y,
                       dim.width-insets.right, zero.y);
            drawWaves(g, dim.width-insets.right-dx-1, 
                         dim.width-insets.right, dt);
            clip(g, old_clip);
            if (status!=null)
              status.showStatus("t="+Format.remove(time0, dt));
          }

      }


    /**
     * Calcola l'intervallo di discretizzazione delle curve
     */
    double chooseDeltaTime()
      {
        double periodo1=1/settings.frequenza[0];
        double periodo2=1/settings.frequenza[1];
        double dt=Math.min(periodo1, periodo2)/30;
        return dt;
      }

    /**
     * Ricalcola l'estensione della finestra da visualizzare
     * Assegna il valore di side.
     */
    void recomputeWindow()
      {
        double a1=settings.ampiezza[0]*1.05;
        double a2=settings.ampiezza[1]*1.05;
        double periodo1=1/settings.frequenza[0];
        double periodo2=1/settings.frequenza[1];
        double maxa=Math.max(Math.max(a1, a2), a1+a2);
        double mint=Math.min(periodo1, periodo2);
        map.set(0, -maxa, 4*mint, maxa);

        Font font=new Font("Courier", Font.PLAIN, 10);
        FontMetrics fm=getFontMetrics(font);
        insets=new Insets(0, 0, 0, 0);
        insets.top=10;
        insets.bottom=fm.getHeight()+13;
        insets.left=6*fm.charWidth('A')+10;
        insets.right=2*fm.charWidth('A')+10;

        map.set(size(), insets);

      }

    /**
     * Ferma l'animazione se l'utente clicca sulla finestra
     */
    public boolean mouseDown(Event evt, int x, int y)
      { stopAnimation();
        if (status!=null)
          status.showStatus("Simulazione interrotta!");
        curs.setCursor(Frame.DEFAULT_CURSOR);
        return super.mouseDown(evt, x, y);
      }

    /**
     * Cambia il cursore se la simulazione sta girando
     */
    public boolean mouseEnter(Event evt, int x, int y)
       { curs.setCursor(thread!=null? Frame.HAND_CURSOR: 
                                     Frame.DEFAULT_CURSOR);
         return true;
       }

    /**
     * Ripristina il cursore se il mouse esce dalla finestra
     */
    public boolean mouseExit(Event evt, int x, int y)
       { curs.setCursor(Frame.DEFAULT_CURSOR);
         return true;
       }

 
      

    public void paint(Graphics g)
      { super.paint(g);
        Dimension d=size();

        if (dim==null || d.width!=dim.width || d.height!=dim.height)
          { dim=d;
            map.set(dim, insets);
          }

        g.setColor(Color.blue);
        g.drawRect(insets.left, insets.top,
                   dim.width-insets.left-insets.right,
                   dim.height-insets.top-insets.bottom);
        Point pt=new Point(insets.left, dim.height-insets.bottom);
        axes.drawAxes(g, pt);

        Point zero=realToPixel(0, 0);
        g.drawLine(insets.left, zero.y, dim.width-insets.right, zero.y);
        
        Rectangle old_clip=g.getClipRect();
        Rectangle new_clip=new Rectangle(insets.left+1, insets.top+1,
                                     dim.width-insets.left-insets.right-1,
                                     dim.height-insets.top-insets.bottom-1);
        if (old_clip!=null)
          new_clip=new_clip.intersection(old_clip);
        double dt=chooseDeltaTime();
        clip(g, new_clip);
        drawWaves(g, new_clip.x, new_clip.x+new_clip.width-1, dt);
        clip(g, old_clip);
      }

    /**
     * Converte da coordinate reali a coordinate in pixel
     */
    Point realToPixel(double x, double y)
      { return map.realToPixel(x, y);
      }

    /**
     * Converte da coordinate in pixel a coordinate reali
     */
    void pixelToReal(int ix, int iy, double coords[])
      { map.pixelToReal(ix, iy, coords);
      }      

    /**
     * Calcola il valore corrente di una delle onde
     */
    double calcOnda(int onda, double t)
      { return settings.ampiezza[onda]
              *Math.exp(-settings.smorzamento[onda]*t)
              *Math.sin(2*Math.PI*settings.frequenza[onda]*t
                        +settings.fase[onda]*Math.PI/180);
      }


    /**
     * Imposta il clipping rectangle al rettangolo in cui devono essere
     * disegnate le onde
     * Restituisce il vecchio clipping rect, o null se non era
     * definito.
     */
    Rectangle clip(Graphics g)
      { return clip(g, 
             new Rectangle(insets.left+1, insets.top+1,
                           dim.width-insets.left-insets.right-1,
                           dim.height-insets.top-insets.bottom-1));
      }


    /**
     * Imposta il clipping rectangle al rettangolo specificato,
     * o all'intera area della canvas se il rettangolo e' null. 
     * Restituisce il vecchio clipping rect, o null se non era
     * definito.
     */
    Rectangle clip(Graphics g, Rectangle r)
      { if (r==null)
          r=new Rectangle(0,0, dim.width, dim.height);
        Rectangle old=g.getClipRect();
        g.clipRect(r.x, r.y, r.width, r.height);
        return old;
      }


    /**
     * Disegna parte le onde in una striscia della canvas
     */
    void drawWaves(Graphics g, int ix1, int ix2, double dt)
      { if (graphicSettings==null)
          return;

        double z[]={0, 0};

        pixelToReal(ix1, 0, z);
        double t1=z[0];
        pixelToReal(ix2, 0, z);
        double t2=z[0];

        double t;

        Point pt10, pt20, pts0;
        Point pt1, pt2, pts;

        double onda1, onda2, ondas;

        onda1=calcOnda(0, time0+t1);
        onda2=calcOnda(1, time0+t1);
        ondas=onda1+onda2;
        pt10=realToPixel(t1, onda1);
        pt20=realToPixel(t1, onda2);
        pts0=realToPixel(t1, ondas);

        for(t=t1+dt; t<t2+dt; t+=dt)
          { 
            onda1=calcOnda(0, time0+t);
            onda2=calcOnda(1, time0+t);
            ondas=onda1+onda2;
            pt1=realToPixel(t, onda1);
            pt2=realToPixel(t, onda2);
            pts=realToPixel(t, ondas);
            if (graphicSettings.onda[0])
              { g.setColor(Color.red);
                g.drawLine(pt10.x, pt10.y, pt1.x, pt1.y);
              }
            if (graphicSettings.onda[1])
              { g.setColor(Color.cyan);
                g.drawLine(pt20.x, pt20.y, pt2.x, pt2.y);
              }
            if (graphicSettings.somma)
              { g.setColor(Color.green);
                g.drawLine(pts0.x, pts0.y, pts.x, pts.y);
              }
            pt10=pt1;
            pt20=pt2;
            pts0=pts;
          }
      }
  }
