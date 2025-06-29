package fismod;

import ui.*;
import util.*;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Simulazione dello spettro dell'atomo di idrogeno
 */
public class SimulationSpettro extends Panel
                            implements SimulationDisplay, Painter, Runnable
  { static final int ORBITS=7;
    static final double PLANCK=6.626176E-34;
    static final double eV=1.6E-19;
    static final double E1=13.54;
    static final double LIGHT_SPEED=2.998E8;

    SpriteCanvas    atomDisplay;
    PaintableCanvas graphDisplay;

    Image ball;
    int ball_width, ball_height;

    double time=0.0;
    static final double frameDelay=1.0/12;
    static final double timePerOrbit=6.0;
    static final double angSpeed=Math.PI;
    static final double maxTime=2*timePerOrbit;

    boolean righe[];
    int level=1;
    Thread thread=null;

    private final AtomicBoolean isRunning = new AtomicBoolean();

    public SimulationSpettro()
      { ball=UserInterface.getImageLoader().load("icons/sml-ball.gif");
        ball_width=ball.getWidth(this);
        ball_height=ball.getHeight(this);

        righe=new boolean[ORBITS-1];

        Font fixed_font=new Font("Courier", Font.PLAIN, 12);
        FontMetrics fm=getFontMetrics(fixed_font);

        atomDisplay=new SpriteCanvas();
        atomDisplay.setBackgroundPainter(this);
        atomDisplay.setFont(new Font("TimesRoman", Font.PLAIN, 12));
        graphDisplay=new PaintableCanvas();
        graphDisplay.setPainter(this);
        graphDisplay.setFont(new Font("TimesRoman", Font.PLAIN, 12));

        int minAtomSize=2*orbitRadius(ORBITS)+5;
        int font_height=fm.getHeight();
        int font_width=fm.charWidth('A');

        SizeConstraint atomSizeConstraint=
               new SizeConstraint(atomDisplay, 
                          new Dimension(minAtomSize, minAtomSize));
        
        SizeConstraint graphSizeConstraint=
               new SizeConstraint(graphDisplay, 
                          new Dimension(12*font_width, 8*font_height));

        setLayout(new BorderLayout());
        add("Center", atomSizeConstraint);
        add("South", graphSizeConstraint);

        atomDisplay.setBackground(Color.white);
        graphDisplay.setBackground(Color.white);

        atomDisplay.addSprite(ball);
        repositionBall();
        atomDisplay.showSprite(0, true);
      }



    /**
     * Disegna un sotto-componente
     */
    public void paint(Component com, Graphics g)
      { if (com==atomDisplay)
          paintAtom(g);
        if (com==graphDisplay)
          paintGraph(g);
      }

    /**
     * Disegna l'atomo e le orbite
     */
    void paintAtom(Graphics g)
      { Dimension d=atomDisplay.size();
        FontMetrics fm=g.getFontMetrics();
        String str="Spettro atomico dell'idrogeno";
        g.setColor(Color.gray);
        g.drawString(str, (d.width-fm.stringWidth(str))/2+1,
                          fm.getHeight()+1);
        g.setColor(Color.blue);
        g.drawString(str, (d.width-fm.stringWidth(str))/2,
                          fm.getHeight());


        int xc=d.width/2;
        int yc=d.height/2;

        g.setColor(Color.red) ;
        int r=orbitRadius(0);
        g.fillOval(xc-r, yc-r, 2*r, 2*r);
        g.setColor(Color.gray);
        int i;
        for(i=1; i<=ORBITS; i++)
          { r=orbitRadius(i);
            g.drawOval(xc-r, yc-r, 2*r, 2*r);
          }
        repositionBall();
      }

    /**
     * Disegna il grafico dello spettro
     */
    void paintGraph(Graphics g)
      { Dimension d=graphDisplay.size();
        FontMetrics fm=getFontMetrics(graphDisplay.getFont());
        CoordinateMapper map=new CoordinateMapper();
        Insets insets=new Insets(0,0,0,0);
        insets.top=d.height/8;
        insets.bottom=d.height/2+d.height/4;
        insets.left=d.width/7;
        insets.right=d.width/7;
        map.set(d, insets);
        map.set(900,0, 1255, 1);

        g.setColor(Color.blue);
        Point pta=map.realToPixel(900,1);
        Point ptb=map.realToPixel(1255,0);
        g.drawRect(pta.x, pta.y, ptb.x-pta.x, ptb.y-pta.y);
        AxesDrawer axes=new AxesDrawer(map);
        axes.drawXAxis(g, ptb.y);
        String str="Lunghezza d'onda [\u00c5]";
        g.drawString(str, d.width/2, ptb.y+2*fm.getHeight()+2);
                          


        // Disegna  le linee dello spettro

        g.setColor(Color.red);


        int i;
        for(i=2; i<=ORBITS; i++)
          { if (righe[i-2])
              { double E=E1*(1-1.0/(i*i));
                double ni=E*eV/PLANCK;
                double lam=LIGHT_SPEED/ni*1E10;
                Point pt=map.realToPixel(lam, 0);
                g.drawLine(pt.x, pta.y+1, pt.x, ptb.y-1);
                if (i==level)
                  { str="Lunghezza d'onda = "+
                              Format.format(".1", lam)+" \u00c5";
                    g.drawString(str,d.width/2, ptb.y+3*fm.getHeight()+4);
                  }

              }

          }


      }


    
    public synchronized void startAnimation()
      { stopAnimation();
        Dialog dialog=new SpettroDialog(this);
        dialog.reshape(100,100,400,300);
        dialog.show();
      }

    public synchronized void startAnimation(int lev, boolean erasePrev)
      { level=lev;
        if (erasePrev)
          { int i;
            for(i=0; i<ORBITS-1; i++)
              righe[i]=false;
          }
        stopAnimation();
        isRunning.set(true);
        thread=new Thread(this);
        thread.start();
      }

    public synchronized void stopAnimation()
      {
        if (thread!=null) {
//          thread.stop();
          isRunning.set(false);
          thread.interrupt(); // this is to wakeup sleep()
        }
        thread=null;
      }

    /**
     * Ricalcola la posizione della pallina che rappresenta l'elettrone
     * e muove lo sprite corrispondente
     */
    void repositionBall()
      { Dimension d=atomDisplay.size();
        int orb=getOrbit(time);
        double ang=angSpeed*time;
        double cos=Math.cos(ang);
        double sin=Math.sin(ang);
        int r=orbitRadius(orb);
        int x=(int)Math.floor(r*cos+0.5);
        int y=(int)Math.floor(r*sin+0.5);

        atomDisplay.moveSprite(0, d.width/2+x-ball_width/2,
                                  d.height/2-y-ball_height/2);

      }

    /** 
     * Orbitale in funzione del tempo
     */
    int getOrbit(double t)
      { 
        return (t<timePerOrbit)? 1: level;
      }
        

    /**
     * Raggio dell'n-sima orbita
     */
    int orbitRadius(int n)
      { return ball_width+(ball_width*3/4+1)*n+n*n/3;

      }


    /**
     * Esegue materialmente la simulazione
     */
    public void run()
      { time=0;
        righe[level-2]=false;
        graphDisplay.repaint();
        repositionBall();
        int lastOrb=1;

        Graphics g=graphDisplay.getGraphics();

        while(time<=maxTime && isRunning.get())
          { repositionBall();

            time+=frameDelay;

            if (time>timePerOrbit && righe[level-2]==false)
              { righe[level-2]=true;
                if (g!=null)
                  paintGraph(g);
              }


            try
              { Thread.sleep((int)(frameDelay*1000));
              }
            catch (InterruptedException e)
              {
              }
          }

        g.dispose();
        
        graphDisplay.repaint();

      }

  }