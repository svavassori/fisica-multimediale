package onde;

import ui.*;
import util.*;
import numeric.*;

import java.awt.*;


/**
 * A canvas displaying the interference animation
 */
public class Interf extends Canvas 
                     implements Painter, SimulationDisplay, Runnable
  { static final int ONDE_PIANE=Settings.ONDE_PIANE;
    static final int ONDE_CIRCOLARI=Settings.ONDE_CIRCOLARI;

    static final int delay=500;

    Settings settings;
    StatusDisplayer status;
    Component legenda;
    Dimension dim;
    Insets insets;
    CoordinateMapper map;
    AxesDrawer axes;
    Thread thread=null;
    int step=0;
    boolean drawMarks=false;

    /**
     * Semi-dimensione (nello spazio delle coordinate reali)
     * dell'area visualizzata
     */
    double side;


    public Interf(Settings settings, Component legenda, 
                   StatusDisplayer status)
      { this.settings=settings;
        this.legenda=legenda;
        this.status=status;
        setBackground(Color.black);
        map=new CoordinateMapper();
        map.setIsometric(true);
        axes=new AxesDrawer(map);
        recomputeWindow();
      }

    public void set(Settings settings)
      { this.settings=settings;
        recomputeWindow();
        step=0;
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
        String str="Interferenza";
        int w=fm.stringWidth(str);
        g.setColor(Color.lightGray);
        g.drawString(str, (d.width-w)/2+1, asc+3+1);
        g.setColor(Color.blue);
        g.drawString(str, (d.width-w)/2-2, asc+3-2);
        g.setColor(Color.black);
        g.drawString("Onda 1", 100, h+asc+3);
        g.drawString("Onda 2", 100, h*2+asc+3);
        g.drawString("Interferenza costruttiva",
                     d.width/2+40, h+asc+3);
        g.drawString("Interferenza distruttiva",
                     d.width/2+40, h*2+asc+3);
        g.setColor(Color.blue);
        g.fillRect(60, h+3+1, 14, asc-2);
        g.setColor(Color.cyan);
        g.fillRect(60+14, h+3+1, 14, asc-2);
        g.setColor(Color.red);
        g.fillRect(60, h*2+3+1, 14, asc-2);
        g.setColor(Color.orange);
        g.fillRect(60+14, h*2+3+1, 14, asc-2);
        g.setColor(Color.green);
        g.fillRect(d.width/2+20, h+3+1, 10, asc-2);
        g.setColor(Color.magenta);
        g.fillRect(d.width/2+20, h*2+3+1, 10, asc-2);
      }    

    public void startAnimation()
      { stopAnimation();
        repaint();
        thread=new Thread(this);
        thread.start();
      }

    public void stopAnimation()
      { if (thread!=null)
          thread.stop();
        thread=null;
      }

    /**
     * Esegue il lavoro di animazione
     */
    public void run()
      { double periodo=settings.periodo;
        step=0;
        drawMarks=false;
        Graphics g=getGraphics();
        if (g==null)
          { stopAnimation();
            return;
          }
        boolean out=false;
        while (!out)
          { if (status!=null)
              { status.showStatus("t="
                                  +Format.remove(step*periodo, periodo)
                                  +"s");
              }
            paintFront(g, step, true);
            try
              { Thread.sleep(delay);
              }
            catch (InterruptedException e)
              {
              }
            out=paintFront(g, step, false);
            step++;
          }
        drawMarks=true;
        paintInterf(g);
      }

    /**
     * Ricalcola l'estensione della finestra da visualizzare
     * Assegna il valore di side.
     */
    void recomputeWindow()
      { side=settings.vel*settings.periodo*6;
        if (settings.tipo==ONDE_CIRCOLARI)
          { 
            double x=settings.x[0];
            double y=settings.y[0];
            double x1=settings.x[1]; 
            double y1=settings.y[1]; 

            if (Math.abs(x)>side)
              side=Math.abs(x);
            if (Math.abs(y)>side)
              side=Math.abs(y);
            if (Math.abs(x1)>side)
              side=Math.abs(x1);
            if (Math.abs(y1)>side)
              side=Math.abs(y1);

            double xa=Math.min(x, x1)-side/4;
            double xb=Math.max(x, x1)+side/4;
            double ya=Math.min(y, y1)-side/4;
            double yb=Math.max(y, y1)+side/4;
            double xc=(xa+xb)/2;
            double yc=(ya+yb)/2;
            if (xa<xc-side)
              xa=xc-side;
            if (xb>xc+side)
              xb=xc+side;
            if (ya<yc-side)
              ya=yc-side;
            if (yb>yc+side)
              yb=yc+side;
            map.set(xa, ya, xb, yb);
            side=0.5*Math.max(xb-xa, yb-ya);
          }
        else
          map.set(-side, -side, side, side);
          

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
        repaint();
        if (status!=null)
          status.showStatus("Simulazione interrotta!");
        return super.mouseDown(evt, x, y);
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
        if (settings.tipo==ONDE_CIRCOLARI)
          paintSources(g);
        int i;
        for(i=0; i<step; i++)
          paintFront(g, i, false);
        paintInterf(g);
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
     * Disegna le sorgenti dell'onda incidente e dell'onda riflessa
     * per le onde circolari
     */
    void paintSources(Graphics g)
      { 
        double x=settings.x[0];
        double y=settings.y[0];
        double x1=settings.x[1];
        double y1=settings.y[1];

        Rectangle old=clip(g);
        Point pt=realToPixel(x, y);
        g.setColor(Color.blue);
        g.fillOval(pt.x-3, pt.y-3, 7, 7);
        pt=realToPixel(x1, y1);
        g.setColor(Color.red);
        g.fillOval(pt.x-3, pt.y-3, 7, 7);
        clip(g, old);
      }


    /**
     * Disegna il fronte dell'onda ad un dato step
     * Restituisce true se il fronte e' fuori dello schermo
     */
    boolean paintFront(Graphics g, int st, boolean last)
      { int o;
        boolean out=true;
        for(o=0; o<2; o++)
          { 
            if (settings.tipo==ONDE_PIANE)
              out=paintFront(g, st, last, settings.dir[o], o) && out;
            else
              out=paintFront(g, st, last, settings.x[o], 
                                    settings.y[o], o) && out;
          }
        return out;
      }

    /**
     * Disegna il fronte dell'onda ad un dato step per onde piane
     * Restituisce true se il fronte e' fuori dello schermo
     */
    boolean paintFront(Graphics g, int st, boolean last, int dir, int onda)
      { Rectangle old=clip(g);

        // raggio 
        double alpha=dir*Math.PI/180;
        double s=Math.sin(alpha);
        double c=Math.cos(alpha);


        // fronte 
        double beta=alpha+Math.PI/2;
        double fs=Math.sin(beta);
        double fc=Math.cos(beta);


        double x0=-2*c*side;
        double y0=-2*s*side;

        double x=x0+c*settings.vel*st*settings.periodo;
        double y=y0+s*settings.vel*st*settings.periodo;

        if (onda==0)
          g.setColor(last? Color.blue: Color.cyan);
        else
          g.setColor(last? Color.red: Color.orange);
        double r=10*side;
        double xa=x-r*fc;
        double ya=y-r*fs;
        double xb=x+r*fc;
        double yb=y+r*fs;
        Point pta=realToPixel(xa, ya);
        Point ptb=realToPixel(xb, yb);
        g.drawLine(pta.x, pta.y, ptb.x, ptb.y);

        clip(g, old);
        return Functions.hypot(x, y)>1.2*Functions.hypot(x0, y0);
      }

    /**
     * Disegna il fronte dell'onda ad un dato step per onde circolari
     * Restituisce true se il fronte e' fuori dello schermo
     */
    boolean paintFront(Graphics g, int st, boolean last, double x, double y,
                       int onda)
      { 
        double side1=2*side*1.41;
        double r=settings.vel*settings.periodo*st;
        double f=(360-settings.fase[onda])%360*Math.PI/180;
        r+=f*settings.vel*settings.periodo/(2*Math.PI);
        Rectangle old=clip(g);
        boolean out;

        if (onda==0)
          g.setColor(last? Color.blue: Color.cyan);
        else
          g.setColor(last? Color.red: Color.orange);
        Point pt1=realToPixel(x-r, y+r);
        Point pt2=realToPixel(x+r, y-r);
        g.drawOval(pt1.x, pt1.y, pt2.x-pt1.x, pt2.y-pt1.y);
        out=pt2.x-pt1.x>size().width*1.4;

        clip(g, old);
        return out;
      }


    /**
     * Disegna i markers che indicano il tipo di interferenza
     */
    void paintInterf(Graphics g)
      { if (step==0 || !drawMarks)
          return;
        Rectangle old=clip(g);
        if (settings.tipo==ONDE_PIANE)
          paintInterfPlane(g);
        else
          paintInterfCirc(g);

        clip(g, old);
      }



    /**
     * Disegna i markers che indicano il tipo di interferenza
     * per onde piane
     */
    void paintInterfPlane(Graphics g)
      { double sin[]=new double[2];
        double cos[]=new double[2];
        double dx[]=new double[2];
        double dy[]=new double[2];
        int o;
        double l=settings.vel*settings.periodo;
        double l2=0.5*l;
        
        for(o=0; o<2; o++)
          { double ang=settings.dir[o]*Math.PI/180;
            cos[o]=Math.cos(ang);
            sin[o]=Math.sin(ang);
          }

        double discr=cos[0]*sin[1]-sin[0]*cos[1];
        double adiscr=Math.abs(discr);

        if (adiscr<1e-5)
          { if (status!=null)
              status.showStatus("Onde parallele! Interferenza uniforme");
            return;
          }

        int initial=0;

        int ilim=(int)Math.ceil(10*side*adiscr/l2/
                     Math.max(Math.abs(sin[1]), Math.abs(cos[1])));
        int jlim=(int)Math.ceil(10*side*adiscr/l2/
                     Math.max(Math.abs(sin[0]), Math.abs(cos[0])));
        
        int i,j;
        for(i=-ilim; i<=ilim; i++)
          for(j=-jlim; j<=jlim; j++)
            { double x=l2*(i*sin[1]-j*sin[0])/discr;
              double y=l2*(j*cos[0]-i*cos[1])/discr;
              Point pt=realToPixel(x, y);
              boolean cons=((Math.abs(i)+Math.abs(j)+initial) & 1)==0;
              g.setColor(cons? Color.green: Color.magenta);
              g.fillRect(pt.x-1, pt.y-1, 3, 3);
            }
      }

    /**
     * Disegna i markers che indicano il tipo di interferenza
     * per onde circolari
     */
    void paintInterfCirc(Graphics g)
      { double x=settings.x[0];
        double y=settings.y[0];
        double x1=settings.x[1];
        double y1=settings.y[1];
        double dist=Functions.hypot(x-x1, y-y1);
        if (dist<1e-5)
          { if (status!=null)
              status.showStatus("Onde concentriche! Interferenza uniforme");
            return;
          }
        double ang1=Math.atan2(y1-y, x1-x);
        double l=settings.vel*settings.periodo;
        int lim=2*(int)(6*side/l);
        int i, j;
        double fase=settings.fase[0]*Math.PI/180;
        double fase1=settings.fase[1]*Math.PI/180;

        double r, r1;

        for(i=0; i<lim; i++)
          for(j=0; j<lim; j++)
            { r=0.5*i*l;
              r1=0.5*j*l;
              if (r+r1<dist)
                continue;
              double cos;
              if (r>0)
                { cos=(r*r+dist*dist-r1*r1)/(2*r*dist);
                  if (cos>1)
                    continue;
                }
              else
                { if (Math.abs(r1-dist)<1e-5)
                    cos=0;
                  else
                    continue;
                }
              double alfa=Math.acos(cos);
              double beta=ang1+alfa;
              double beta1=ang1-alfa;
              double xa=x+r*Math.cos(beta);
              double ya=y+r*Math.sin(beta);
              double xb=x+r*Math.cos(beta1);
              double yb=y+r*Math.sin(beta1);
              double diff=(2*Math.PI*r/l-fase)
                          -(2*Math.PI*r1/l-fase1);
              boolean cons=Functions.sign(Math.cos(diff))>=0;
              Point pta=realToPixel(xa, ya);
              Point ptb=realToPixel(xb, yb);
              g.setColor(cons? Color.green: Color.magenta);
              g.fillRect(pta.x-1, pta.y-1, 3, 3);
              g.fillRect(ptb.x-1, ptb.y-1, 3, 3);
            }
      }

  }
