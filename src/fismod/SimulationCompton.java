package fismod;

import ui.*;
import util.*;

import java.awt.*;



/**
 * Simulazione dell'effetto Compton
 */
public class SimulationCompton extends Panel
                            implements SimulationDisplay, Painter, Runnable
  { 
    static final double PLANCK=6.626176E-34;
    static final double eV=1.6E-19;
    static final double LIGHT_SPEED=2.998E8;
    static final double ANGSTROM=1e-10;
    static final double lambdaC=0.025*ANGSTROM;

    SpriteCanvas    motionDisplay;
    PaintableCanvas legenda;

    Image photon;
    int photon_width, photon_height;
    int photon_id;
    Image electron;
    int electron_width, electron_height;
    int electron_id;

    double time=0.0;
    static final double frameDelay=1.0/10;
    static final double timeBefore=4.0;
    static final double timeAfter=2.5;
    static final double maxTime=timeBefore+timeAfter;

    Thread thread=null;

    double lambdaI, lambdaE;
    double ang, ang1;
    double Ei, Ee, Ec;



    public SimulationCompton()
      { photon=UserInterface.getImageLoader().load("icons/sml-ball.gif");
        photon_width=photon.getWidth(this);
        photon_height=photon.getHeight(this);
        electron=UserInterface.getImageLoader().load("icons/red-ball.gif");
        electron_width=electron.getWidth(this);
        electron_height=electron.getHeight(this);

        Font fixed_font=new Font("Courier", Font.PLAIN, 12);
        FontMetrics fm=getFontMetrics(fixed_font);

        motionDisplay=new SpriteCanvas();
        motionDisplay.setBackgroundPainter(this);
        legenda=new PaintableCanvas();
        legenda.setPainter(this);
        legenda.setFont(new Font("TimesRoman", Font.PLAIN, 12));

        int font_height=fm.getHeight();
        int font_width=fm.charWidth('A');

        SizeConstraint motionSizeConstraint=
               new SizeConstraint(motionDisplay, 
                          new Dimension(12*font_width, 14*font_height));
        
        SizeConstraint legendaSizeConstraint=
               new SizeConstraint(legenda, 
                          new Dimension(12*font_width, 10*font_height));

        setLayout(new BorderLayout());
        add("Center", motionSizeConstraint);
        add("North", legendaSizeConstraint);

        motionDisplay.setBackground(Color.white);
        legenda.setBackground(Color.white);

        photon_id=motionDisplay.addSprite(photon);
        electron_id=motionDisplay.addSprite(electron);
        repositionSprites();
        motionDisplay.showSprite(photon_id, true);
        motionDisplay.showSprite(electron_id, true);
      }



    /**
     * Disegna un sotto-componente
     */
    public void paint(Component com, Graphics g)
      { if (com==motionDisplay)
          { paintMotion(g);
            repositionSprites();
          }
        if (com==legenda)
          paintLegenda(g);
      }

    /**
     * Disegna il quadro del moto
     */
    void paintMotion(Graphics g)
      { Dimension d=motionDisplay.size();

        g.setColor(Color.green);

        Point pt0=photonPosition(0);
        if (time<timeBefore)
          { Point pt=photonPosition(time);
            g.drawLine(pt0.x, pt0.y, pt.x, pt.y);
          }
        else
          { Point pt1=electronPosition(0);
            g.drawLine(pt0.x, pt0.y, pt1.x, pt1.y);
            Point ptp=photonPosition(time);
            g.drawLine(pt1.x, pt1.y, ptp.x, ptp.y);
            g.setColor(Color.yellow);
            Point pte=electronPosition(time);
            g.drawLine(pt1.x, pt1.y, pte.x, pte.y);
          }

      }

    /**
     * Disegna la legenda 
     */
    void paintLegenda(Graphics g)
      { Dimension d=legenda.size();
        FontMetrics fm=getFontMetrics(legenda.getFont());
        int h=fm.getHeight();

        String str="Effetto Compton";
        g.setColor(Color.gray);
        g.drawString(str, (d.width-fm.stringWidth(str))/2+1, h+1);
        g.setColor(Color.blue);
        g.drawString(str, (d.width-fm.stringWidth(str))/2, h);

        String txt[][]=
              {{"Angolo di deflessione del fotone: ", ""},
               {"Angolo di deflessione dell'elettrone: ", ""},
               {"Lunghezza d'onda del fotone incidente: ", ""},
               {"Lunghezza d'onda del fotone deflesso: ", ""},
               {"Energia del fotone incidente: ", ""},
               {"Energia del fotone deflesso: ", ""},
               {"Energia dell'elettrone: ", ""}};
        if (time>timeBefore)
          { txt[0][1]=Format.format(".1", ang*180/Math.PI)+"°";
            txt[1][1]=Format.format(".1", ang1*180/Math.PI)+"°";
            txt[2][1]=Format.format(".3", lambdaI/ANGSTROM)+" \u00c5";
            txt[3][1]=Format.format(".3", lambdaE/ANGSTROM)+" \u00c5";
            txt[4][1]=Format.format_e(".6", Ei/eV)+" eV";
            txt[5][1]=Format.format_e(".6", Ee/eV)+" eV";
            txt[6][1]=Format.format_e(".6", Ec/eV)+" eV";
          }

        g.setColor(Color.black);
        int i;
        for(i=0; i<txt.length; i++)
          { g.drawString(txt[i][0], 10, 8+2*h+h*i);
            g.drawString(txt[i][1], 10+d.width*4/7, 8+2*h+h*i);
          }
      }

    public synchronized void startAnimation()
      { stopAnimation();
        Dialog dialog=new ComptonDialog(this);
        dialog.reshape(100,100,400,300);
        dialog.show();
      }

    public synchronized void startAnimation(double lami, double ang)
      { 
        stopAnimation();
        lambdaI=lami*ANGSTROM;
        this.ang=ang*Math.PI/180;
        compute();
        thread=new Thread(this);
        thread.start();
      }

    public synchronized void stopAnimation()
      { if (thread!=null)
          thread.stop();
        thread=null;
      }

    /**
     * Ricalcola la posizione delle palline che rappresentano l'elettrone
     * e il fotone e muove gli sprites corrispondenti
     */
    void repositionSprites()
      { Point ptp=photonPosition(time);
        motionDisplay.moveSprite(photon_id, ptp.x-photon_width/2,
                                            ptp.y-photon_height/2);
        Point pte=electronPosition(time);
        motionDisplay.moveSprite(electron_id, pte.x-electron_width/2,
                                            pte.y-electron_height/2);

      }


    /**
     * Posizione del fotone in funz. del tempo
     */
    Point photonPosition(double t)
      { Dimension d=motionDisplay.size();

        int x1=50;
        int x2=(int)((d.width-50)*timeBefore/maxTime);
        double pspeed=(x2-x1)/timeBefore;

        if (t<timeBefore)
          return new Point(x1+(int)(t*pspeed), d.height/2);
        else
          { double cos=Math.cos(ang);
            double sin=Math.sin(ang);

            int x=x2+(int)(cos*pspeed*(t-timeBefore));
            int y=d.height/2-(int)(sin*pspeed*(t-timeBefore));
            return new Point(x,y);
          }
      }

    /**
     * Posizione dell'elettrone in funz. del tempo
     */
    Point electronPosition(double t)
      { Dimension d=motionDisplay.size();

        int x1=50;
        int x2=(int)((d.width-50)*timeBefore/maxTime);
        double espeed=0.5*(x2-x1)/timeBefore;

        if (t<timeBefore)
          return new Point(x2, d.height/2);
        else
          { double cos=Math.cos(ang1);
            double sin=Math.sin(ang1);

            int x=x2+(int)(cos*espeed*(t-timeBefore));
            int y=d.height/2+(int)(sin*espeed*(t-timeBefore));
            return new Point(x,y);
          }
      }


        



    /**
     * Esegue materialmente la simulazione
     */
    public void run()
      { time=0;
        motionDisplay.repaintBackground();
        legenda.repaint();
        repositionSprites();
        int lastOrb=1;

        Graphics g=legenda.getGraphics();
        Graphics mg=motionDisplay.getGraphics();
        Graphics mbg=motionDisplay.getBackgroundGraphics();

        while(time<=maxTime)
          { 
            paintMotion(mbg);
            repositionSprites();
            paintMotion(mg);
            repositionSprites();

            time+=frameDelay;


            try
              { Thread.sleep((int)(frameDelay*1000));
              }
            catch (InterruptedException e)
              {
              }
          }

        g.dispose();
        mg.dispose();
        mbg.dispose();
        
        legenda.repaint();
        motionDisplay.repaint();
      }


    /**
     * Calcola i parametri della simulazione
     */
    void compute()
      { double sin=Math.sin(ang);
        double cos=Math.cos(ang);

        lambdaE=lambdaI+lambdaC*(1-cos);
        Ei=PLANCK*LIGHT_SPEED/lambdaI;
        Ee=PLANCK*LIGHT_SPEED/lambdaE;
        Ec=Ei-Ee;

        double sin2=Math.sin(ang/2);
        double cos2=Math.cos(ang/2);

        double sin1=cos2/(1+lambdaC/lambdaI);
        double cos1=sin2;

        ang1=Math.atan2(sin1, cos1);
      }

  }
