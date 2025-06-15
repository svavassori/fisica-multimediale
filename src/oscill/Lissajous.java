package oscill;

import ui.*;
import util.*;
import numeric.*;

import java.awt.*;


/**
 * A canvas displaying the Lissajous animation
 */

public class Lissajous extends Canvas 
                     implements Painter, SimulationDisplay, Runnable
  { static final int delay=50; //previously 2

    Settings settings;
    Component legenda;
    StatusDisplayer status;
    Dimension dim;
    Insets insets;
    CoordinateMapper map;
    AxesDrawer axes;
    CursorChanger curs;
    Thread thread=null;

    public Lissajous(Settings settings, Component legenda,
                     StatusDisplayer status) 
      { this.settings=settings;
        this.legenda=legenda;
        this.status=status;
        setBackground(Color.black);
        map=new CoordinateMapper();
        map.setIsometric(true);
        axes=new AxesDrawer(map);
        curs=new CursorChanger(this);
        recomputeWindow();
      }

    public void set(Settings settings)
      { stopAnimation();
        this.settings=settings;
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
        String str="Figure di Lissajous";
        int w=fm.stringWidth(str);
        g.setColor(Color.lightGray);
        g.drawString(str, (d.width-w)/2+1, asc+3+1);
        g.setColor(Color.blue);
        g.drawString(str, (d.width-w)/2-2, asc+3-2);
        g.setColor(Color.gray);
        if (settings.smorzamento[0]!=0)
        {
        str="x = "+Format.format(".3", settings.ampiezza[0])
            +" exp(-"+Format.format(".3", settings.smorzamento[0])
            +"t) sin("+Format.format(".3", 360.0*settings.frequenza[0])
            +" t + "+settings.fase[0]+")";
        }
        else
        {

         str="x = "+Format.format(".3", settings.ampiezza[0])
            +" sin("+Format.format(".3", 360.0*settings.frequenza[0])
            +" t + "+settings.fase[0]+")";

        }
        g.drawString(str, 40, h+asc+3);

        if (settings.smorzamento[1]!=0)
        {
        str="y = "+Format.format(".3", settings.ampiezza[1])
            +" exp(-"+Format.format(".3", settings.smorzamento[1])
            +"t) sin("+Format.format(".3", 360.0*settings.frequenza[1])
            +" t + "+settings.fase[1]+")";
        }
        else
        {
        str="y = "+Format.format(".3", settings.ampiezza[1])
            +" sin("+Format.format(".3", 360.0*settings.frequenza[1])
            +" t + "+settings.fase[1]+")";
        }



        g.drawString(str, 40, h*2+asc+3);
        str="rapporto frequenze = "
             +Format.format(".3", settings.frequenza[0]/settings.frequenza[1]);
        g.drawString(str, 40, h*3+asc+3);
      }    

    public synchronized void startAnimation()
      { stopAnimation();
        repaint();
        thread=new Thread(this);
        // thread.setPriority(Thread.NORM_PRIORITY-2);
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
      { if (status!=null)
          status.showStatus("Clicca sulla finestra per interrompere la "
                            +"simulazione");
        double periodo1=1/settings.frequenza[0];
        double periodo2=1/settings.frequenza[1];
        double dt=Math.min(periodo1, periodo2)/40;

        Point pt0=realToPixel(calcOnda(0, 0.0), calcOnda(1, 0.0));
        double t=0;
        Graphics g=getGraphics();
        if (g==null)
          return;

        while(true)
          { double x=calcOnda(0, t);
            double y=calcOnda(1, t);
            Point pt=realToPixel(x, y);
            g.setColor(Color.green);
            g.drawLine(pt0.x, pt0.y, pt.x, pt.y);
            g.setColor(Color.black);
            g.drawLine(pt.x, pt.y, pt.x, pt.y);

            t+=dt;
            
            pt0=pt;
            try 
              { if (delay>0)
                  Thread.sleep(delay);
              }
            catch (InterruptedException e)
              {
              }
          }


      }

    /**
     * Ricalcola l'estensione della finestra da visualizzare
     * Assegna il valore di side.
     */
    void recomputeWindow()
      { double a1=settings.ampiezza[0]*1.05;
        double a2=settings.ampiezza[1]*1.05;
        map.set(-a1, -a2, a1, a2);

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
        curs.setCursor(Frame.DEFAULT_CURSOR);
        if (status!=null)
          status.showStatus("Simulazione interrotta!");
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
      { Dimension d=size();

        if (dim==null || d.width!=dim.width || d.height!=dim.height)
          { dim=d;
            map.set(dim, insets);
          }

        g.setColor(Color.blue);
        g.drawRect(insets.left, insets.top,
                   dim.width-insets.left-insets.right,
                   dim.height-insets.top-insets.bottom);
        axes.drawAxes(g, new Point(insets.left,
                                   dim.height-insets.bottom));
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

  }
