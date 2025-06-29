package onde;

import ui.*;
import util.*;
import numeric.*;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * A canvas displaying the reflection animation
 */
public class Reflect extends Canvas 
                     implements Painter, SimulationDisplay, Runnable
  { static final int ONDE_PIANE=Settings.ONDE_PIANE;
    static final int ONDE_CIRCOLARI=Settings.ONDE_CIRCOLARI;

    static final int delay=500;

    Settings settings;
    ReflectSettings reflectSettings=null;
    StatusDisplayer status;
    Component legenda;
    Dimension dim;
    Insets insets;
    CoordinateMapper map;
    AxesDrawer axes;
    Thread thread=null;
    int step=0;

    /**
     * Semi-dimensione (nello spazio delle coordinate reali)
     * dell'area visualizzata
     */
    double side;

    private final AtomicBoolean isRunning = new AtomicBoolean();

    public Reflect(Settings settings, Component legenda, 
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

    public void set(ReflectSettings reflectSettings)
      { this.reflectSettings=reflectSettings;
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
      { if (reflectSettings==null)
          return;
        Dimension d=com.size();
        FontMetrics fm=g.getFontMetrics();
        int asc=fm.getAscent();
        int h=fm.getHeight();
        String str="Riflessione";
        int w=fm.stringWidth(str);
        g.setColor(Color.lightGray);
        g.drawString(str, (d.width-w)/2+1, asc+3+1);
        g.setColor(Color.blue);
        g.drawString(str, (d.width-w)/2-2, asc+3-2);
        g.setColor(Color.black);
        g.drawString("Onda incidente", 100, h+asc+3);
        g.drawString("Onda riflessa", 100, h*2+asc+3);
        double l1=settings.vel*settings.periodo;
        g.drawString("lungh. d'onda: "+Format.format(".3", l1)+" m",
                     d.width/2+20, h+asc+3);

        if  (settings.tipo==Settings.ONDE_PIANE)
        {
        double varauto=Math.abs(settings.dir[reflectSettings.onda]-reflectSettings.angolo);
        g.drawString("Angolo di Incidenza  :"+Format.format(".3", varauto),d.width/2+20, 2*h+asc+3+3);
        g.drawString("Angolo di Riflessione:"+Format.format(".3", varauto),d.width/2+20, 3*h+asc+3*3);
        }

        g.setColor(Color.blue);
        g.fillRect(60, h+3+1, 14, asc-2);
        g.setColor(Color.cyan);
        g.fillRect(60+14, h+3+1, 14, asc-2);
        g.setColor(Color.red);
        g.fillRect(60, h*2+3+1, 14, asc-2);
        g.setColor(Color.orange);
        g.fillRect(60+14, h*2+3+1, 14, asc-2);
      }    

    public void startAnimation()
      { stopAnimation();
        repaint();
        if (reflectSettings!=null)
          {
            isRunning.set(true);
            thread=new Thread(this);
            thread.start();
          }
        else
          MessageBox.message(this,"Onde - Riflessione", 
                  "Prima di eseguire la simulazione\n"
                 +"occorre impostare i parametri cliccando\n"
                 +"l'icona della riflessione\n");
      }

    public void stopAnimation()
      {
        if (thread!=null) {
//          thread.stop();
          isRunning.set(false);
          thread.interrupt(); // this is to wakeup sleep()
        }
        thread=null;
      }

    /**
     * Esegue il lavoro di animazione
     */
    public void run()
      { double periodo=settings.periodo;
        step=0;
        Graphics g=getGraphics();
        if (g==null)
          { stopAnimation();
            return;
          }
        boolean out=false;
        while (!out && isRunning.get())
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
      }

    /**
     * Ricalcola l'estensione della finestra da visualizzare
     * Assegna il valore di side.
     */
    void recomputeWindow()
      { side=settings.vel*settings.periodo*6;
        if (settings.tipo==ONDE_CIRCOLARI && reflectSettings!=null)
          { 
            int o=reflectSettings.onda;
            double x=settings.x[o];
            double y=settings.y[o];
            double z[]={0, 0};
            imagePoint(x, y, z);
            double x1=z[0];  // x della sorg. riflessa
            double y1=z[1];  // y della sorg. riflessa 

            if (Math.abs(x)>side)
              side=Math.abs(x);
            if (Math.abs(y)>side)
              side=Math.abs(y);
            if (Math.abs(x1)>side)
              side=Math.abs(x1);
            if (Math.abs(y1)>side)
              side=Math.abs(y1);

            double xa=Math.min(x, x1)-side/8;
            double xb=Math.max(x, x1)+side/8;
            double ya=Math.min(y, y1)-side/8;
            double yb=Math.max(y, y1)+side/8;
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
        axes.drawAxes(g, new Point(insets.left,
                                   dim.height-insets.bottom));
        if (reflectSettings!=null)
          { double ang=reflectSettings.angolo*Math.PI/180;
            double sin=Math.sin(ang);
            double cos=Math.cos(ang);
            paintPlane(g, sin, cos);

            if (settings.tipo==ONDE_PIANE)
              paintRays(g);
            else if (settings.tipo==ONDE_CIRCOLARI)
              paintSources(g);
          }
        int i;
        for(i=0; i<step; i++)
          paintFront(g, i, false);
      }

    /**
     * Disegna il piano, dati il seno e il coseno dell'angolo
     */
    void paintPlane(Graphics g, double s, double c)
      { int x1,y1, x2, y2;
        Point zero=realToPixel(0, 0);
        Dimension d=dim;

        if (Math.abs(s) > Math.abs(c))
          { y1=0;
            x1=zero.x+(int)((zero.y-y1)*c/s);
            y2=d.height-1;
            x2=zero.x+(int)((zero.y-y2)*c/s);
          }
        else
          { x1=0;
            y1 = zero.y - (int)((x1-zero.x)*s/c);
            x2=d.width-1;
            y2 = zero.y - (int)((x2-zero.x)*s/c);
          }

        Rectangle old=clip(g);
        g.setColor(Color.magenta);
        g.drawLine(x1, y1, x2, y2);
        clip(g, old);
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
     * Disegna il raggio incidente e il raggio riflesso 
     * per le onde piane.
     */
    void paintRays(Graphics g)
      { int o=reflectSettings.onda;
        double ang=settings.dir[o]*Math.PI/180;
        double x=3*side*Math.cos(ang);
        double y=3*side*Math.sin(ang);
        double z[]={0,0};
        imagePoint(x, y, z);
        double x1=z[0];
        double y1=z[1];
        double nx=(x1-x);
        double ny=(y1-y);
        double norm=Functions.hypot(nx, ny);
        nx*=3*side/norm;
        ny*=3*side/norm;
                                            
        Point pt;
        Point zero=realToPixel(0, 0);
        Rectangle old=clip(g);

        g.setColor(Color.blue);
        pt=realToPixel(-x, -y);
        g.drawLine(pt.x, pt.y, zero.x, zero.y);

        g.setColor(Color.red);
        pt=realToPixel(x1, y1);
        g.drawLine(pt.x, pt.y, zero.x, zero.y);

        g.setColor(Color.yellow);
        pt=realToPixel(nx, ny);
        g.drawLine(pt.x, pt.y, zero.x, zero.y);

        clip(g, old);

      }

    /**
     * Disegna le sorgenti dell'onda incidente e dell'onda riflessa
     * per le onde circolari
     */
    void paintSources(Graphics g)
      { int o=reflectSettings.onda;
        double x=settings.x[o];
        double y=settings.y[o];
        double z[]={0,0};
        imagePoint(x, y, z);
        double x1=z[0];
        double y1=z[1];

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
     * Calcola il punto immagine di un punto assegnato
     */
    void imagePoint(double x, double y, double z[])
      { double ang=reflectSettings.angolo*Math.PI/180;
        double alfa=Math.atan2(y, x);
        double beta=2*ang-alfa;
        double r=Functions.hypot(x, y);
        double x1=r*Math.cos(beta);  // x della sorg. riflessa
        double y1=r*Math.sin(beta);  // y della sorg. riflessa 

        z[0]=x1;
        z[1]=y1;
      }

    /**
     * Disegna il fronte dell'onda ad un dato step
     * Restituisce true se il fronte e' fuori dello schermo
     */
    boolean paintFront(Graphics g, int st, boolean last)
      { int o=reflectSettings.onda;
        if (settings.tipo==ONDE_PIANE)
          return paintFront(g, st, last, settings.dir[o]);
        else
          return paintFront(g, st, last, settings.x[o], settings.y[o]);
      }

    /**
     * Disegna il fronte dell'onda ad un dato step per onde piane
     * Restituisce true se il fronte e' fuori dello schermo
     */
    boolean paintFront(Graphics g, int st, boolean last, int dir)
      { Rectangle old=clip(g);
        double ang=reflectSettings.angolo*Math.PI/180;
        double sin=Math.sin(ang);
        double cos=Math.cos(ang);

        // raggio incidente
        double alpha=dir*Math.PI/180;
        double s=Math.sin(alpha);
        double c=Math.cos(alpha);

        // raggio riflesso
        double alpha1=2*ang-alpha;
        double s1=Math.sin(alpha1);
        double c1=Math.cos(alpha1);

        // fronte incidente
        double beta=alpha+Functions.sign(c*s1-s*c1)*Math.PI/2;
        if (Functions.sign(c*s1-s*c1)==0)
          beta=alpha+Math.PI/2; 
        double fs=Math.sin(beta);
        double fc=Math.cos(beta);

        // fronte riflesso
        double beta1=alpha1+Functions.sign(c*s1-s*c1)*Math.PI/2;
        if (Functions.sign(c*s1-s*c1)==0)
          beta1=alpha1+Math.PI/2; 
        double fs1=Math.sin(beta1);
        double fc1=Math.cos(beta1);


        double x0=-2*c*side;
        double y0=-2*s*side;

        double x=x0+c*settings.vel*st*settings.periodo;
        double y=y0+s*settings.vel*st*settings.periodo;

        // Prima della rifl. del raggio?
        boolean before= Functions.sign(cos*y-sin*x)
                        ==Functions.sign(cos*y0-sin*x0);
        // Se sono paralleli, considera sempre 'prima'.
        if (Math.abs(cos*s-sin*c)<1e-7)
          before=true;

        double fsx=fs;
        double fcx=fc;
        
        if (!before)
          { double z[]={0, 0};
            imagePoint(x, y, z);
            x=z[0];
            y=z[1];
            fsx=fs1;
            fcx=fc1;
          }

        // Calcola l'intersezione
        // del fronte con il piano
        double denom=sin*fcx-cos*fsx;
        double numer=cos*y-sin*x;

        // Il punto e' all'infinito?
        boolean infty=Math.abs(numer)>=10*side*Math.abs(denom);
        if (Math.abs(denom)<1e-5)
          infty=true;


        if (infty)
          { if (before)
              g.setColor(last? Color.blue: Color.cyan);
            else
              g.setColor(last? Color.red: Color.orange);
            double r=10*side;
            double xa=x-r*fcx;
            double ya=y-r*fsx;
            double xb=x+r*fcx;
            double yb=y+r*fsx;
            Point pta=realToPixel(xa, ya);
            Point ptb=realToPixel(xb, yb);
            g.drawLine(pta.x, pta.y, ptb.x, ptb.y);
          }
        else
          { double t=numer/denom;
            double xx=x+t*fcx;
            double yy=y+t*fsx;
            double r=20*side;
            double xa=xx+r*fc;
            double ya=yy+r*fs;
            double xb=xx+r*fc1;
            double yb=yy+r*fs1;
            Point pt=realToPixel(xx, yy);
            Point pta=realToPixel(xa, ya);
            Point ptb=realToPixel(xb, yb);
            g.setColor(last? Color.blue: Color.cyan);
            g.drawLine(pt.x, pt.y, pta.x, pta.y);
            g.setColor(last? Color.red: Color.orange);
            g.drawLine(pt.x, pt.y, ptb.x, ptb.y);
          }



        clip(g, old);
        return Functions.hypot(x, y)>1.2*Functions.hypot(x0, y0);
      }

    /**
     * Disegna il fronte dell'onda ad un dato step per onde circolari
     * Restituisce true se il fronte e' fuori dello schermo
     */
    boolean paintFront(Graphics g, int st, boolean last, double x, double y)
      { double ang=reflectSettings.angolo*Math.PI/180;
        double sin=Math.sin(ang);
        double cos=Math.cos(ang);
        // Calcola la proiezione di x,y sul piano
        double xx=cos*(x*cos+y*sin);
        double yy=sin*(x*cos+y*sin);
        double h=Functions.hypot(x-xx, y-yy);
        double side1=2*side*1.41;
        double r=settings.vel*settings.periodo*st;
        double f=(360-settings.fase[reflectSettings.onda])%360*Math.PI/180;
        r+=f*settings.vel*settings.periodo/(2*Math.PI);
        Rectangle old=clip(g);
        boolean out;

        if (r<=h)
          { g.setColor(last? Color.blue: Color.cyan);
            Point pt1=realToPixel(x-r, y+r);
            Point pt2=realToPixel(x+r, y-r);
            g.drawOval(pt1.x, pt1.y, pt2.x-pt1.x, pt2.y-pt1.y);
            out=pt2.x-pt1.x>size().width*1.4;
          }
        else
          { double delta=Math.acos(h/(r*1.01));
            double alpha=Math.atan2(yy-y, xx-x);
            if (h<1e-4*r)
              alpha=ang-Math.PI/2;
            double z[]={0, 0};
            imagePoint(x, y, z);
            double x1=z[0];
            double y1=z[1];
            int ang1=(int)Math.floor((alpha-delta)*180/Math.PI+0.5);
            int ang2=(int)Math.floor((alpha+delta)*180/Math.PI+0.5);
            g.setColor(last? Color.blue: Color.cyan);
            Point pt1=realToPixel(x-r, y+r);
            Point pt2=realToPixel(x+r, y-r);
            g.drawArc(pt1.x, pt1.y, pt2.x-pt1.x, pt2.y-pt1.y, 
                      ang2, 360-(ang2-ang1));
            g.setColor(last? Color.red: Color.orange);
            pt1=realToPixel(x1-r, y1+r);
            pt2=realToPixel(x1+r, y1-r);
            if (ang2-ang1>2)
              g.drawArc(pt1.x, pt1.y, pt2.x-pt1.x, pt2.y-pt1.y, 
                      ang1+180+1, ang2-ang1-2);
            out=pt2.x-pt1.x>size().width*1.4;
          }

        clip(g, old);
        return out;
      }


  }
