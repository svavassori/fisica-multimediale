
package cinemat;

import ui.*;
import numeric.*;
import util.*;
import java.awt.*;


/**
 * The canvas used to display the simulation (actually a panel)
 * @author Pasquale Foggia
 * @version 0.90, Dec 1997
 */
public class SimulationDisplay extends SpriteCanvas implements Runnable
  {  double win_x1, win_y1, win_x2, win_y2;
     CursorChanger curs;
     Thread thread=null;
     StatusDisplayer status;
     Settings settings;
     Simulation simulation;
     SimulationInfoDisplay simulationInfoDisplay;
     static final double frameDelay=1.0/10;
     static final int defaultStepsPerFrame=4;
     Image ball;
     int ball_id, ball_x_id, ball_y_id;
     int ball_width, ball_height;
     static final int MARKERS=150;
     int markers;
     double mark_x[], mark_y[];
     int step;
     static final double log10=Math.log(10);
     Options options;
     CoordinateMapper map;
     AxesDrawer axesDrawer;
     Insets insets;
     static final double proj=0.3;

     public SimulationDisplay(StatusDisplayer status,
                              Options options, Settings settings,
                              SimulationInfoDisplay simulationInfoDisplay)
       { 
         this.status=status;
         this.options=options;
         this.settings=settings;
         this.simulationInfoDisplay=simulationInfoDisplay;
         simulation=null;
         curs=new CursorChanger(this);
         curs.setCursor(Frame.DEFAULT_CURSOR);
         setBackground(Color.white);
         markers=0;
         mark_x=new double[MARKERS];
         mark_y=new double[MARKERS];
         ball=UserInterface.getImageLoader().load("icons/red-ball.gif");
         ball_width=ball.getWidth(this);
         ball_height=ball.getHeight(this);
         ball_id=addSprite(ball);
         ball_x_id=addSprite(ball);
         ball_y_id=addSprite(ball);
         Font font=new Font("Courier", Font.PLAIN, 10);
         FontMetrics fm=getFontMetrics(font);
         insets=new Insets(0, 0, 0, 0);
         insets.top=10;
         insets.bottom=fm.getHeight()+10;
         insets.left=6*fm.charWidth('A')+10;
         insets.right=10;
         map=new CoordinateMapper();
         map.setIsometric(true);
         axesDrawer=new AxesDrawer(map);
         recomputeWindow();
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
         curs.setCursor(Frame.DEFAULT_CURSOR);
       }


     public void run()
       { markers=0;
         Point pro=getProjectionPos();
         int stepsPerFrame=
                 (int)Math.ceil(options.timeWarp*defaultStepsPerFrame);
         if (stepsPerFrame<1)
           stepsPerFrame=1;
         double stepDuration1=frameDelay*options.timeWarp/stepsPerFrame;
         int stepsBetweenUpdates=(int)
                         Math.ceil(options.stepDuration/stepDuration1);
         int nextUpdate=0;
         if (stepsBetweenUpdates<1)
           stepsBetweenUpdates=1;
         double stepDuration=options.stepDuration/stepsBetweenUpdates;
         double delay=stepDuration*stepsPerFrame/options.timeWarp;


         simulation.runUpTo(options.duration, 
                            stepDuration);



         int s, n=simulation.getStepCount();
         synchronized (this)
           { step=0;
           }
         recomputeWindow();
         repaintBackground();
         showBallSprites();
         Point pt=getBallPos(step);
         moveSprite(ball_id, pt.x, pt.y);
         {
         moveSprite(ball_x_id, pt.x, pro.y); 
         moveSprite(ball_y_id, pro.x, pt.y); 
         }
         Graphics g=getGraphics();
         Graphics gb=getBackgroundGraphics();
         Dimension d=size();
         Rectangle clip=new Rectangle(0, 0, d.width, d.height);

         long expectedTime, time;

         expectedTime=System.currentTimeMillis();

         int last_s=0;
         for(s=0; s<n; s+=stepsPerFrame)
           { if (s >= nextUpdate)
               { simulationInfoDisplay.update(nextUpdate);
                 addMarker(simulation.getX(nextUpdate), 
                           simulation.getY(nextUpdate));
                 paintMarks(g, clip, markers-1, markers-1);
                 paintMarks(gb, clip, markers-1, markers-1);
                 nextUpdate+=stepsBetweenUpdates;
               }

             expectedTime+=(int)(1000*delay);

             time=System.currentTimeMillis();
             if (time>expectedTime)
               continue;

             synchronized(this)
               { step=s;
               }

             if (s>last_s)
               { paintSpecialLine(gb, clip, last_s);
                 paintSpecialLine(g, clip, last_s);
                 paintLine(gb, clip, last_s, s);
                 paintLine(g, clip, last_s, s);
                 paintSpecialLine(gb, clip, s);
                 paintSpecialLine(g, clip, s);
                 last_s=s;
               }

             pt=getBallPos(s);
             moveSprite(ball_id, pt.x, pt.y);
             moveSprite(ball_x_id, pt.x, pro.y);
             moveSprite(ball_y_id, pro.x, pt.y);
             updateInfo("T="+Format.remove(simulation.getTime(s),
                                           delay*options.timeWarp)+" s");

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
              moveSprite(ball_x_id, pt.x, pro.y);
              moveSprite(ball_y_id, pro.x, pt.y);
             
           }
         g.dispose();
         gb.dispose();
         updateInfo("");
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
       { curs.setCursor(thread==null? Frame.DEFAULT_CURSOR: 
                                     Frame.HAND_CURSOR);
         return true;
       }

     public boolean mouseExit(Event evt, int x, int y)
       { curs.setCursor(Frame.DEFAULT_CURSOR);
         return true;
       }

     public boolean mouseMove(Event evt, int x, int y)
       { if (thread==null)
           { if (curs.getCursorType()!=Frame.DEFAULT_CURSOR)
               curs.setCursor(Frame.DEFAULT_CURSOR);
           }
         return true;
       }

     public boolean mouseDown(Event evt, int x, int y)
       { if (thread!=null)
           { stopAnimation();
             curs.setCursor(Frame.DEFAULT_CURSOR);
             updateInfo("Simulazione interrotta");
           }

         return true;
       }

     public synchronized void reset(Settings settings)
       { stopAnimation();
         this.settings=settings;
         markers=0;
         simulation=null;
         step=0;
         recomputeWindow();
         showSprite(ball_id, false);
         if (settings.ShowProjection)
         {
         showSprite(ball_x_id, false);
         showSprite(ball_y_id, false);
         }
         repaintBackground();
         repaint(300);
       }

     public synchronized void play(Simulation simul)
       { stopAnimation();
         simulation=simul;
         markers=0;
         step=0;
         showSprite(ball_id, false);
         showSprite(ball_x_id, false);
         showSprite(ball_y_id, false);
         repaintBackground();
         recomputeWindow();
         repaintBackground();
         repaint();
       }

     public synchronized void showBallSprites()
       { Point p=getBallPos();
         moveSprite(ball_id, p.x, p.y);
         showSprite(ball_id, true);
         if ((simulation instanceof SimulationRot) && (settings.ShowProjection))
           { Point pro=getProjectionPos();
             moveSprite(ball_x_id, p.x, pro.y);
             moveSprite(ball_y_id, pro.x, p.y);
             
             
             showSprite(ball_x_id, true);
             showSprite(ball_y_id, true);
             
           }
         else
           { 
            //System.out.println("Arrivo e show false");
             showSprite(ball_x_id, false);
             showSprite(ball_y_id, false);
            
           }
       }


    Point realToPixel(double x, double y)
      { return map.realToPixel(x, y);
      }


    void pixelToReal(int ix, int iy, double coords[])
      { map.pixelToReal(ix, iy, coords);
      }      

    void updateInfo(String s)
      { if (status!=null)
          status.showStatus(s);
      }

    public void updateBeforeResize()
      { Dimension d=size();
        map.set(d, insets);

        if (spriteVisible(ball_id))
          { Point pt=getBallPos();
            Point pro=getProjectionPos();
            setSpritePosition(ball_id, pt.x, pt.y);
            setSpritePosition(ball_x_id, pt.x, pro.y);
            setSpritePosition(ball_y_id, pro.x, pt.y);
          }
      }


    public void paintBackground(Graphics g, Rectangle clip)
      { super.paintBackground(g, clip);

        paintAxes(g, clip);
        if (simulation!=null && simulation instanceof SimulationRot)
          paintCircle(g, clip);
        if (markers>0)
          paintMarks(g, clip);
        if (simulation!=null && step>0)
          paintLine(g, clip);
        if (simulation!=null /* && simulation.getStepCount()>0 */)
          paintSpecialLine(g, clip, step);
      }


    void paintAxes(Graphics g, Rectangle clip)
      { Dimension d=size();
        Point pt=new Point(insets.left, d.height-insets.bottom);
        g.setColor(Color.blue);
        axesDrawer.drawAxes(g, pt, clip);

      }

    void paintCircle(Graphics g, Rectangle clip)
      { Point zero=realToPixel(0, 0);
        double r=settings.raggio;
        Point radius=realToPixel(r, -r);
        radius.x-=zero.x;
        radius.y-=zero.y;
        Point px=realToPixel(-r-proj*r, 0);
        Point py=realToPixel(0, -r-proj*r);

        g.setColor(Color.cyan);
        g.drawOval(zero.x-radius.x, zero.y-radius.y,
                   2*radius.x, 2*radius.y);
        if (settings.ShowProjection)
        {
        g.drawLine(zero.x-radius.x, py.y, zero.x+radius.x, py.y);
        g.drawLine(px.x, zero.y-radius.y, px.x, zero.y+radius.y);
        }
      }



    void paintMarks(Graphics g, Rectangle clip)
      { paintMarks(g, clip, 0, markers-1);
      }

    void paintMarks(Graphics g, Rectangle clip, int from, int to)
      { int i;
        Rectangle rec;
        final int RADIUS=ball_width/3;

        g.setColor(Color.red);
        for(i=from; i<=to; i++)
          { Point p=realToPixel(mark_x[i], mark_y[i]);
            rec=new Rectangle(p.x-RADIUS, p.y-RADIUS, 2*RADIUS, 2*RADIUS);
            if (rec.intersects(clip))
              { g.drawOval(rec.x, rec.y, rec.width, rec.height);
              }
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


    /**
     * Uses XOR mode
     */
    void paintSpecialLine(Graphics g, Rectangle clip, int step)
      { g.setColor(Color.cyan);
        g.setXORMode(getBackground());
        Dimension d=size();
        Rectangle oldClip=g.getClipRect();
        if (oldClip==null)
          oldClip=new Rectangle(0, 0, d.width, d.height);
        Rectangle newClip=new Rectangle(
                                    insets.left,
                                    insets.top,
                                    d.width-insets.left-insets.right,
                                    d.height-insets.top-insets.bottom);
        newClip=newClip.intersection(clip);
        g.clipRect(newClip.x, newClip.y, newClip.width, newClip.height);

        if (simulation instanceof SimulationTransl)
          { Point pt=realToPixel(0, simulation.getY(step));
            if (pt.y>=clip.y && pt.y<clip.y+clip.height)
              { g.drawLine(insets.left, pt.y, d.width-insets.right, pt.y);
              }
          }
        else if (simulation instanceof SimulationTranslRot)
          { double s=((SimulationTranslRot)simulation).getSin(step);
            double c=((SimulationTranslRot)simulation).getCos(step);
            int x1, y1, x2, y2;
            Point zero=realToPixel(0, 0);

            if (Math.abs(s)>Math.abs(c))
              { y1=insets.top;
                y2=d.height-insets.bottom-1;
                x1=zero.x+(int)((zero.y-y1)*c/s);
                x2=zero.x+(int)((zero.y-y2)*c/s);
              }
            else
              { x1=insets.left;
                x2=d.width-insets.right-1;
                y1=zero.y-(int)((x1-zero.x)*s/c);
                y2=zero.y-(int)((x2-zero.x)*s/c);
              }

            g.drawLine(x1, y1, x2, y2);
          }
        else if (simulation instanceof SimulationRotTransl)
          { double r=settings.raggio;
            double x=((SimulationRotTransl)simulation).getCenterX(step);
            double y=((SimulationRotTransl)simulation).getCenterY(step);
            Point cen=realToPixel(x,y);
            Point zero=realToPixel(0, 0);
            Point pt=realToPixel(r, -r);
            Point radius=new Point(pt.x-zero.x, pt.y-zero.y);

            g.drawOval(cen.x-radius.x, cen.y-radius.y,
                     2*radius.x, 2*radius.y);
          }

        g.setPaintMode();
        g.clipRect(oldClip.x, oldClip.y, oldClip.width, oldClip.height);
      }

    Point getBallPos()
      { synchronized(this)
          { return getBallPos(step);
          }
      }

    Point getBallPos(int step)
      { Point pos;
        if (simulation!=null && simulation.getStepCount()>0)
          pos=realToPixel(simulation.getX(step), 
                           simulation.getY(step));
        else
          pos=realToPixel(0, 0);
        pos.x-=ball_width/2;
        pos.y-=ball_height/2;
        return pos;
      }

    Point getProjectionPos()
      { double r=settings.raggio*(1+proj);
        Point pt=realToPixel(-r,-r);
        pt.x-=ball_width/2;
        pt.y-=ball_height/2;
        return pt;
      }


    void recomputeWindow()
      { win_x1=-3;
        win_x2=3;
        win_y1=-3;
        win_y2=3;
        int i;

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

        if (simulation!=null && simulation.getStepCount()>0)
          for(i=0;  i<simulation.getStepCount(); i++)
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

        if (simulation!=null 
            && simulation instanceof SimulationRotTransl
            && simulation.getStepCount()>0)
          for(i=0;  i<simulation.getStepCount(); i++)
          { double x=((SimulationRotTransl)simulation).getCenterX(i);
            double y=((SimulationRotTransl)simulation).getCenterY(i);
            double r=settings.raggio;
            if (x-r<win_x1)
              win_x1=x-r;
            if (x+r>win_x2)
              win_x2=x+r;
            if (y-r<win_y1)
              win_y1=y-r;
            if (y+r>win_y2)
              win_y2=y+r;
          }

        if (simulation!=null 
            && simulation instanceof SimulationRot)
          { double r=settings.raggio;
            win_x1=-r-(0.1+proj)*r;
            win_y1=-r-(0.1+proj)*r;
            win_x2=+r+0.1*r;
            win_y2=+r+0.1*r;
          }

        double w=win_x2-win_x1;
        double h=win_y2-win_y1;
        double d=Math.max(w, h);
        win_x1-=d/10;
        win_x2+=d/10;
        win_y1-=d/10;
        win_y2+=d/10;
        map.set(win_x1, win_y1, win_x2, win_y2);
        updateBeforeResize();
      }
  }
