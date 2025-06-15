package ui;

import java.awt.*;
import ui.Tooltip;

public class TooltipButton extends Canvas implements Runnable
  { String tip;
    Thread thread;
    boolean moved; 
    Tooltip tooltip;
    int last_x, last_y;
    int tipDelay=1000;
    boolean tipEnabled=false;


    public TooltipButton()
      { tip=null;
        thread=null;
        moved=false;
        tooltip=null;
      }

    public synchronized void setTip(String tip)
      { this.tip=tip;
      }

    public String getTip()
      { return tip;
      }

    public void enableTip(boolean enable)
      { tipEnabled=enable;
      }

    public synchronized boolean mouseEnter(Event evt, int x, int y)
      { if (thread!=null)
          thread.stop();
        moved=false;
        if (tipEnabled && tip!=null && tip.length()>0)
          { thread=new Thread(this);
            thread.start();
          }
        return false; // Pass this event to the parent
      }

    public synchronized boolean mouseExit(Event evt, int x, int y)
      { if (thread!=null)
          thread.stop();
        thread=null;
        if ((x!=last_x || y!=last_y) && tooltip!=null)
          { tooltip.dispose();
            tooltip=null;
          }
         
        return false; // Pass this event to the parent
      }

    public synchronized boolean mouseMove(Event evt, int x, int y)
      { last_x=x;
        last_y=y;
        if (tooltip!=null)
          tooltip.dispose();
        tooltip=null;
        moved=true;
        return true;
      }

    public synchronized boolean mouseDown(Event evt, int x, int y)
      { if (thread!=null)
          thread.stop();
          
        thread=null;
       
        if (tooltip!=null)
          tooltip.dispose();
        tooltip=null;

        return true;
      }
    
    public void run()
      { boolean endloop;
        do {
          synchronized (this)
            { moved=false;
            }
          try
            { Thread.sleep(tipDelay);
            }
          catch (InterruptedException e)
            {
            }
          synchronized (this)
            { endloop=!moved;
            }
        } while (!endloop);

        synchronized (this)
          { if (tooltip!=null)
              tooltip.dispose();
            if (tip!=null)
              { Point p=getAbsolutePosition(last_x, last_y);
                tooltip=new Tooltip(tip, p.x+10, p.y+10);
                tooltip.show();
              }
            else
              tooltip=null;
            thread=null;
          }
      }

    

    Point getAbsolutePosition(int x, int y)
      { Point loc;

        /*try { loc=getLocationOnScreen();
        } catch (NoSuchMethodError e) */{
            Component comp=this;
            loc=new Point(0,0);
            while (comp!=null)
              { Point p=comp.location();
                loc.x+=p.x;
                loc.y+=p.y;
                comp=comp.getParent();
              }
        }

        return new Point(loc.x+x,loc.y+y);
      }
  }
