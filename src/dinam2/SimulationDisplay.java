package dinam2;

import dinam2.*;
import ui.*;
import numeric.*;
import util.*;
import java.awt.*;
import java.awt.event.*;


/**
 * The canvas used to display the simulation (actually a panel)
 * @author Pasquale Foggia
 * @version 0.90, Dec 1997
 */
public class SimulationDisplay extends SpriteCanvas implements Runnable
  {  double win_x1, win_y1, win_x2, win_y2;
     CursorChanger curs;
     Thread thread=null;
     CStatusDisplayer status;
     Component controller; // Componente che riceve i messaggi di controllo
     Settings settings;
     Simulation simulation;
     static final double frameDelay=1.0/10;
     static final int defaultStepsPerFrame=5;
     Image ball;
     int ball_id;
     int ball_width, ball_height;
     int markers;
     static final int MARKERS=50;
     double mark_x[], mark_y[];
     static final int MAX_OLD_POINTS=5000;
     int old_points;
     double old_x[], old_y[];
     int step;
     static final double log10=Math.log(10);
     Options options;
     CoordinateMapper map;
     AxesDrawer axesDrawer;


     public SimulationDisplay(Component controller, CStatusDisplayer status,
                              Options options, Settings settings)
       { this.controller=controller;
         this.status=status;
         this.options=options;
         this.settings=settings;
         simulation=null;
         curs=new CursorChanger(this);
         curs.setCursor(Frame.CROSSHAIR_CURSOR);
         setBackground(Color.white);
         markers=0;
         old_points=0;
         old_x=null;
         old_y=null;
         mark_x=new double[MARKERS];
         mark_y=new double[MARKERS];
         ball=UserInterface.getImageLoader().load("icons/red-ball.gif");
         ball_width=ball.getWidth(this);
         ball_height=ball.getHeight(this);
         ball_id=addSprite(ball);
         map=new CoordinateMapper();
         map.setIsometric(true);
         axesDrawer=new AxesDrawer(map);
         recomputeWindow();
         
         this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
       }

     public void setOptions(Options options)
       { this.options=options;
       }

     public synchronized void startAnimation()
       { stopAnimation();
         thread=new Thread(this);
         thread.start();
         
       }

     public synchronized void stopAnimation()
       { if (thread!=null)
           { thread.stop();
           }
         thread=null;
       }


     public void run()
       { if (simulation!=null && simulation.getStepCount()>0)
           saveOldPoints();
         int stepsPerFrame=
                 (int)Math.ceil(options.timeWarp*defaultStepsPerFrame);
         if (stepsPerFrame<1)
           stepsPerFrame=1;
         double factor=0.1*simulation.goodTimeInterval();
         updateInfo("Elaborazione in corso - Attendere...");
         //System.out.println("Chi Conta: " + simulation.getStopTime() + " ; " + factor + " ; " + options.duration);
         simulation.runUpTo(simulation.getStopTime()+factor*options.duration, 
                            factor*frameDelay*options.timeWarp/stepsPerFrame);
         int s, n=simulation.getStepCount();
         synchronized (this)
           { step=0;
           }
         addMarker(simulation.getX(0), simulation.getY(0));
         recomputeWindow();
         repaintBackground();
         Point pt=getBallPos(step);
         moveSprite(ball_id, pt.x, pt.y); 
         Graphics g=getGraphics();
         Graphics gb=getBackgroundGraphics();
         Dimension d=size();
         Rectangle clip=new Rectangle(0, 0, d.width, d.height);

         long expectedTime, time;

         expectedTime=System.currentTimeMillis();

         int last_s=0;
         for(s=0; s<n; s+=stepsPerFrame)
           { expectedTime+=(int)(1000*frameDelay);

             time=System.currentTimeMillis();
             if (time>expectedTime)
               continue;

             synchronized(this)
               { step=s;
               }

             if (s>last_s)
               { paintLine(gb, clip, last_s, s);
                 paintLine(g, clip, last_s, s);
                 last_s=s;
               }

             pt=getBallPos(s);
             moveSprite(ball_id, pt.x, pt.y);
             updateInfo("T="+Format.remove(simulation.getTime(s),
                                           frameDelay*options.timeWarp)+" s");

             time=System.currentTimeMillis();
             if (time<expectedTime)
               { try { Thread.sleep(expectedTime-time); }
                 catch (InterruptedException e) { }
               }
           }

         if (s<n-1)
           { s=n-1;
             synchronized(this)
               { step=n-1;
               }
                 
             if (s>last_s)
               { paintLine(gb, clip, last_s, s);
                 paintLine(g, clip, last_s, s);
               }
             pt=getBallPos();
             moveSprite(ball_id, pt.x, pt.y);
           }
         g.dispose();
         gb.dispose();
         updateInfo("");
         if (simulation.Approx) {
         updateInfo("Attenzione! Questa Simulazione riporta valori fortemente approssimati",Color.red);
                   }

         try { Thread.sleep(500); } catch (InterruptedException e) { };
         repaintBackground();
         thread=null;
       }

     void addMarker(double x, double y)
       { if (markers>=MARKERS)
           { int i;
             for(i=0; i<MARKERS-1; i++)
               { mark_x[i]=mark_x[i+1];
                 mark_y[i]=mark_y[i+1];
               }
             markers=MARKERS-1;
           }
         mark_x[markers]=x;
         mark_y[markers]=y;
         markers++;
       }

     public boolean mouseEnter(Event evt, int x, int y)
       { curs.setCursor(thread==null? Frame.CROSSHAIR_CURSOR: 
                                     Frame.HAND_CURSOR);
         return true;
       }

     public boolean mouseExit(Event evt, int x, int y)
       { curs.setCursor(Frame.DEFAULT_CURSOR);
         return true;
       }

     public boolean mouseMove(Event evt, int x, int y)
       { if (thread==null)
           { if (curs.getCursorType()!=Frame.CROSSHAIR_CURSOR)
               curs.setCursor(Frame.CROSSHAIR_CURSOR);

             double coords[]={0, 0};
             pixelToReal(x, y, coords);
             String suffix;
             if (settings.campo==Settings.GRAVITAZIONALE)
               suffix="*10^6 m";
             else
               suffix=" m";
             updateInfo("X="+Format.format(".3", coords[0])+suffix+
                        "   Y="+Format.format(".3", coords[1])+suffix);
           }
         return true;
       }

     public boolean mouseDown(Event evt, int x, int y)
       { 
         if (thread!=null)
           { stopAnimation();
             curs.setCursor(Frame.CROSSHAIR_CURSOR);
             updateInfo("Simulazione interrotta");
           }
         else 
           { double coords[]=new double[2];
             pixelToReal(x, y, coords);
             if (settings.campo==Settings.ELASTICO)
               { Dialog dialog=new InitialElastDialog(
                         controller,
                         settings,
                         (SimulationElast)simulation,
                         coords[0], coords[1]);
                 dialog.reshape(50, 10, 550, 460);
                 dialog.show(true);
                 dialog.move(50, 10);
                 ((Window)getParent()).toFront();
               }
             else if (settings.campo==Settings.GRAVITAZIONALE)
               { Dialog dialog=new InitialGravDialog(
                         controller,
                         settings,
                         (SimulationGrav)simulation,
                         coords[0], coords[1]);
                 dialog.reshape(50, 10, 550, 460);
                 dialog.show(true);
                 dialog.move(50, 10);
                 // ((Window)getParent()).toFront();
               }
           }

         return true;
       }
     

     protected void processMouseEvent(MouseEvent e) {
                 if (thread!=null && e.getID()==MouseEvent.MOUSE_CLICKED)
           { stopAnimation();
             curs.setCursor(Frame.CROSSHAIR_CURSOR);
             updateInfo("Simulazione interrotta");
           }
         else if (e.getID()==MouseEvent.MOUSE_CLICKED)
           { double coords[]=new double[2];
             pixelToReal(e.getX(),e.getY(), coords);
             if (settings.campo==Settings.ELASTICO)
               { Dialog dialog=new InitialElastDialog(
                         controller,
                         settings,
                         (SimulationElast)simulation,
                         coords[0], coords[1]);
                 dialog.reshape(50, 10, 550, 460);
                 dialog.show(true);
                 dialog.move(50, 10);
                // ((Window)getParent()).toFront();
               }
             else if (settings.campo==Settings.GRAVITAZIONALE)
               { 
                 Dialog dialog=new InitialGravDialog(
                         controller,
                         settings,
                         (SimulationGrav)simulation,
                         coords[0], coords[1]);
                 
                 dialog.reshape(50, 10, 550, 460);
                 dialog.show(true);
                 dialog.move(50, 10);
                 
                 // ((Window)getParent()).toFront();
                 
               }
           }
         else
             super.processMouseEvent(e);

//       return true;
     }
     public synchronized void reset(Settings settings)
       { stopAnimation();
         this.settings=settings;
         markers=0;
         old_points=0;
         simulation=null;
         step=0;
         recomputeWindow();
         showSprite(ball_id, false);
         repaintBackground();
         repaint(300);
       }

     public synchronized void play(Simulation simul)
       { stopAnimation();
         simulation=simul;
         markers=0;
         old_points=0;
         step=0;
         Point p=getBallPos();
         moveSprite(ball_id, p.x, p.y);
         showSprite(ball_id, true);
         recomputeWindow();
         repaintBackground();
       }


    Point realToPixel(double x, double y)
      { return map.realToPixel(x, y);
      }


    void pixelToReal(int ix, int iy, double coords[])
      { map.pixelToReal(ix, iy, coords);
      }      

    void updateInfo(String s)
      { if (status!=null)
          status.showStatus(s,Color.black);
      }

    void updateInfo(String s,Color c)
      { if (status!=null)
          status.showStatus(s,c);
      }

    public void updateBeforeResize()
      { Dimension d=size();
        map.set(d);

        if (spriteVisible(ball_id))
          { Point pt=getBallPos();
            setSpritePosition(ball_id, pt.x, pt.y);
          }
      }


    public void paintBackground(Graphics g, Rectangle clip)
      { super.paintBackground(g, clip);

        paintAxes(g, clip);
        Point pt=realToPixel(0, 0);
        if (clip.inside(pt.x, pt.y))
          { g.setColor(Color.cyan);
            g.fillOval(pt.x-5, pt.y-5, 11, 11);
          }
        if (markers>0)
          paintMarks(g, clip);
        if (old_points>0)
          paintOldLine(g, clip);
        if (simulation!=null && step>0)
          paintLine(g, clip);
      }


    void paintAxes(Graphics g, Rectangle clip)
      { 
        Point zero=realToPixel(0, 0);
        g.setColor(Color.blue);
        axesDrawer.drawAxes(g, zero, clip);

      }

    void paintMarks(Graphics g, Rectangle clip)
      { int i;
        Rectangle rec;
        final int RADIUS=ball_width/3;

        g.setColor(Color.red);
        for(i=0; i<markers; i++)
          { Point p=realToPixel(mark_x[i], mark_y[i]);
            rec=new Rectangle(p.x-RADIUS, p.y-RADIUS, 2*RADIUS, 2*RADIUS);
            if (rec.intersects(clip))
              { g.drawOval(rec.x, rec.y, rec.width, rec.height);
              }
          }
      }

    void paintOldLine(Graphics g, Rectangle clip)
      { int i;

        Point pt0=realToPixel( old_x[0], old_y[0]);
        g.setColor(Color.green);
        for(i=1; i<old_points; i++)
          { Point pt=realToPixel( old_x[i], old_y[i]);
            if (clip.inside(pt0.x, pt0.y) ||
                clip.inside(pt.x, pt.y) ||
                clip.inside(pt0.x, pt.y) ||
                clip.inside(pt.x, pt0.y)) 
              g.drawLine(pt0.x, pt0.y, pt.x, pt.y);
            pt0=pt;
          }
      }

    void paintLine(Graphics g, Rectangle clip)
      { paintLine(g, clip, 0, step-1);

      }

    void paintLine(Graphics g, Rectangle clip, int from, int to)
      { int i;

        Point pt0=realToPixel( simulation.getX(from), simulation.getY(from));
        g.setColor(Color.green);
        for(i=from+1; i<=to; i++)
          { Point pt=realToPixel( simulation.getX(i), simulation.getY(i));
            if (clip.inside(pt0.x, pt0.y) ||
                clip.inside(pt.x, pt.y) ||
                clip.inside(pt0.x, pt.y) ||
                clip.inside(pt.x, pt0.y)) 
              g.drawLine(pt0.x, pt0.y, pt.x, pt.y);
            pt0=pt;
          }
      }


            

    Point getBallPos()
      { synchronized(this)
          { return getBallPos(step);
          }
      }
      
    Point getBallPos(int step)
      { Point pos;
        if (simulation!=null)
          pos=realToPixel(simulation.getX(step), 
                           simulation.getY(step));
        else
          pos=new Point(0, 0);
        pos.x-=ball_width/2;
        pos.y-=ball_height/2;
        return pos;
      }


    void recomputeWindow()
      { win_x1=-3;
        win_x2=3;
        win_y1=-3;
        win_y2=3;
        int i;

        if (settings.campo==Settings.GRAVITAZIONALE)
          { win_x1=-0.5e2;
            win_y1=-0.5e2;
            win_x2=0.5e2;
            win_y2=0.5e2;
          }

        for(i=0; i<markers; i++)
          { if (mark_x[i]<win_x1)
              win_x1=mark_x[i];
            if (mark_x[i]>win_x2)
              win_x2=mark_x[i];
            if (mark_y[i]<win_y1)
              win_y1=mark_y[i];
            if (mark_y[i]>win_y2)
              win_y2=mark_y[i];
          }

        if (simulation!=null)
          for(i=0; i==0 || i<simulation.getStepCount(); i++)
          { double x=simulation.getX(i);
            double y=simulation.getY(i);
            if (x<win_x1)
              win_x1=x;
            if (x>win_x2)
              win_x2=x;
            if (y<win_y1)
              win_y1=y;
            if (y>win_y2)
              win_y2=y;
          }

        double w=win_x2-win_x1;
        double h=win_y2-win_y1;
        double d=Math.max(w, h);
        win_x1-=d/10;
        win_x2+=d/10;
        win_y1-=d/10;
        win_y2+=d/6;
        map.set(win_x1, win_y1, win_x2, win_y2);
        updateBeforeResize();
      }

    void saveOldPoints()
      { int i, nn=simulation.getStepCount();

        if (old_x==null)
          old_x=new double[2*nn];
        if (old_y==null)
          old_y=new double[2*nn];

        int n=old_x.length;
        if (n<old_points+nn)
          { n=Math.max(2*n+1, old_points+nn);
            n=Math.min(n, MAX_OLD_POINTS+nn/2);
            n=Math.max(n, nn);
            double xx[]=new double[n];
            double yy[]=new double[n];

            int ofs=Math.max(0, old_points+nn-n);
            for(i=ofs; i<old_points; i++)
              { xx[i-ofs]=old_x[i];
                yy[i-ofs]=old_y[i];
              }
            old_points-=ofs;
            old_x=xx;
            old_y=yy;
          }

        for(i=0; i<nn; i++)
          { old_x[i+old_points]=simulation.getX(i);
            old_y[i+old_points]=simulation.getY(i);
          }
        old_points+=nn;
      }

  }
