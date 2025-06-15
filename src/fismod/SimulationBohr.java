package fismod;

import ui.*;
import util.*;

import java.awt.*;



/**
 * Simulazione dell'atomo di Bohr
 */
public class SimulationBohr extends Panel
                            implements SimulationDisplay, Painter, Runnable
  { static final int ORBITS=7;
    static final double PLANCK=6.626176E-34;
    static final double eV=1.6E-19;
    static final double E1=13.54;

    SpriteCanvas    atomDisplay;
    TextArea        freqDisplay;
    PaintableCanvas graphDisplay;

    Image ball;
    int ball_width, ball_height;

    double time=0.0;
    static final double frameDelay=1.0/12;
    static final double timePerOrbit=4.0;
    static final double angSpeed=Math.PI;
    static final double maxTime=ORBITS*timePerOrbit;

    Thread thread=null;


    public SimulationBohr()
      { ball=UserInterface.getImageLoader().load("icons/sml-ball.gif");
        ball_width=ball.getWidth(this);
        ball_height=ball.getHeight(this);

        Font fixed_font=new Font("Courier", Font.PLAIN, 12);
        FontMetrics fm=getFontMetrics(fixed_font);
		
        atomDisplay=new SpriteCanvas();
        atomDisplay.setBackgroundPainter(this);
        atomDisplay.setFont(new Font("TimesRoman", Font.PLAIN, 12));
        freqDisplay=new TextArea();
        freqDisplay.setEditable(false);
        freqDisplay.setFont(fixed_font);
        graphDisplay=new PaintableCanvas();
        graphDisplay.setPainter(this);

		Dimension tot_panel_size=size();
        int minAtomSize=2*orbitRadius(ORBITS)+5;
		int maxAtomSize=tot_panel_size.width;
		if (maxAtomSize<tot_panel_size.height)
			maxAtomSize=tot_panel_size.height;
		if (maxAtomSize<minAtomSize)
			maxAtomSize=minAtomSize;
			
        int font_height=fm.getHeight();
        int font_width=fm.charWidth('A');

        SizeConstraint atomSizeConstraint=
               new SizeConstraint(atomDisplay, 
								  new Dimension(minAtomSize, maxAtomSize));		
  
        SizeConstraint freqSizeConstraint=
               new SizeConstraint(freqDisplay, 
                          new Dimension(20*font_width, 8*font_height));

        SizeConstraint graphSizeConstraint=
               new SizeConstraint(graphDisplay, 
                          new Dimension(12*font_width, 11*font_height));

        setLayout(new BorderLayout());
        add("Center", atomSizeConstraint);
        add("East", freqSizeConstraint);
        add("South", graphSizeConstraint);

        atomDisplay.setBackground(Color.white);
        freqDisplay.setBackground(Color.lightGray);
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
          paintGraph(g, 0, time, true);
      }

    /**
     * Disegna l'atomo e le orbite
     */
    void paintAtom(Graphics g)
      { Dimension d=atomDisplay.size();
        FontMetrics fm=g.getFontMetrics();
        String str="Atomo di Bohr";
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
     * Disegna il grafico dell'energia
     */
    void paintGraph(Graphics g, double t0, double t1, boolean paintAxes)
      { Dimension d=graphDisplay.size();
        FontMetrics fm=getFontMetrics(graphDisplay.getFont());
        CoordinateMapper map=new CoordinateMapper();
        Insets insets=new Insets(0,0,0,0);
        insets.top=12;
        insets.bottom=5;
        insets.left=fm.charWidth('M')*8;
        insets.right=10;
        map.set(d, insets);
        map.set(0,-14, maxTime, 0);

        if (paintAxes)
          { g.setColor(Color.blue);
            Point zero=map.realToPixel(0,0);
            g.drawLine(zero.x, zero.y, zero.x, d.height-insets.bottom);
            g.drawLine(zero.x, zero.y, d.width-insets.right, zero.y);
            AxesDrawer axes=new AxesDrawer(map);
            axes.drawYAxis(g, zero.x);
            String str="E [eV]";
            g.drawString(str, zero.x-fm.stringWidth(str)-3, 
                              d.height-fm.getHeight());
            g.drawString("t", d.width-insets.right-10, zero.y-2);

            Point pt=map.realToPixel(0, -E1);
            str=Format.format(".2", -E1)+"eV";
            g.drawString(str, pt.x+3, pt.y+6);
          }

        // Disegna il grafico

        g.setColor(Color.red);
        t0=frameDelay*Math.floor(t0/frameDelay);
        t1=frameDelay*Math.ceil(t1/frameDelay);

        double t;
        Point pt0=map.realToPixel(t0, continuousEnergy(t0));
        for(t=t0+frameDelay; t<=t1+frameDelay/4; t+=frameDelay)
          { Point pt=map.realToPixel(t, continuousEnergy(t));
            g.drawLine(pt0.x, pt0.y, pt.x, pt.y);
            pt0=pt;
            if (getOrbit(t)>getOrbit(t-frameDelay))
              { g.setColor(Color.blue);
                int orb=getOrbit(t);
                double Eorb=-E1/(orb*orb);
                Point pta=map.realToPixel((orb-1)*timePerOrbit, Eorb);
                g.drawLine(pta.x, pta.y-3, pta.x, pta.y+3);
                String str=Format.format(".2", Eorb)+"eV";
                g.drawString(str, pta.x+2, pta.y+10+fm.getAscent());
                g.setColor(Color.red);
              }
          }
      }


    /** Calcola l'energia in funzione del tempo; 
     *  l'andamento e' tale da avere a intervalli regolari il salto di
     *  orbita
     */
    double continuousEnergy(double t)
      { double le=1+t/timePerOrbit;

        return -E1/(le*le);
    }     

    public synchronized void startAnimation()
      { stopAnimation();
        thread=new Thread(this);
        thread.start();
      }

    public synchronized void stopAnimation()
      { if (thread!=null)
          thread.stop();
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
      { int orb=(int)Math.floor(t/timePerOrbit)+1;
        if (orb>ORBITS)
          orb=ORBITS;
        return orb;
      }       

    /**
     * Raggio dell'n-sima orbita
     */
    int orbitRadius(int n) {
		return ball_width+(ball_width*3/4+1)*n+n*n/3;		
    }

    /**
     * Esegue materialmente la simulazione
     */
    public void run()
      { time=0;
        graphDisplay.repaint();
        freqDisplay.setText("Frequenza (Hz)\n"+
                            "--------------\n");
        repositionBall();
        int lastOrb=1;

        Graphics g=graphDisplay.getGraphics();

        while(time<=maxTime)
          { repositionBall();

            time+=frameDelay;
            int orb=getOrbit(time);
            if (orb!=lastOrb)
              { double Eleap=E1*(1.0/lastOrb-1.0/orb);
                double freq=Eleap*eV/PLANCK;
                freqDisplay.appendText(Format.format_e(".3", freq)+"\n");

                lastOrb=orb;
              }

            if (time>0)
              { if (g!=null)
                  paintGraph(g, time-frameDelay, time, false);
              }


            try
              { Thread.sleep((int)(frameDelay*1000));
              }
            catch (InterruptedException e) {

                ;

            }            
          }
        g.dispose();
        graphDisplay.repaint();
      }

  }
