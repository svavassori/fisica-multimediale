package ui;

import java.awt.*;
import java.awt.peer.ComponentPeer;

/**
 * An individual constraint for the PropLayout layout manager
 * @see ui.PropConstraints
 * @see ui.PropLayout
 * @author Pasquale Foggia
 * @version 0.99, Dec 1997
 */
public class PropConstraint
  { boolean unconstrained=true;
    boolean leave=false;
    boolean minimum=false;
    int fixed=0;
    Component other=null;
    int part;
    double ratio;


    /* Values for the 'part' parameter */
    public static final int LEFT=0;
    public static final int XCENTER=1;
    public static final int XCENTRE=1;
    public static final int RIGHT=2;
    public static final int WIDTH=3;
    public static final int TOP=4;
    public static final int YCENTER=5;
    public static final int YCENTRE=5;
    public static final int BOTTOM=6;
    public static final int HEIGHT=7;


    public void set(int fixed)
      { unconstrained=false;
        leave=false;
        this.fixed=fixed;
        other=null;
      }

    public void set(Component other, int part)
      { unconstrained=false;
        leave=false;
        fixed=0;
        this.other=other;
        this.part=part;
        ratio=1.0;
      }

    public void set(Component other, int part, int perc)
      { unconstrained=false;
        leave=false;
        fixed=0;
        this.other=other;
        this.part=part;
        ratio=perc/100.0;
      }


    public void set(int fixed, Component other, int part)
      { unconstrained=false;
        leave=false;
        this.fixed=fixed;
        this.other=other;
        this.part=part;
        ratio=1.0;
      }

    public void set(int fixed, Component other, int part, int perc)
      { unconstrained=false;
        leave=false;
        this.fixed=fixed;
        this.other=other;
        this.part=part;
        ratio=perc/100.0;
      }

    public void setUnconstrained()
      { unconstrained=true;
        leave=false;
        other=null;
        fixed=0;
      }

    public void setLeave()
      { unconstrained=false;
        leave=true;
        other=null;
        fixed=0;
      }


    public void setMinimum(boolean onoff)
      { minimum=onoff;
      }

    boolean isUnconstrained()
      { return unconstrained;
      }

    int compute(int old_value)
      { if (unconstrained || leave)
          return old_value;

        int value=fixed;

        if (other!=null)
          { Point l=other.location();
            Dimension d=other.size();
            if (minimum)
              { ComponentPeer pee=other.getPeer();
                if (pee!=null)
                  { Dimension md=pee.minimumSize();
                    if (d.width<md.width)
                      d.width=md.width;
                    if (d.height<md.height)
                      d.height=md.height;
                  }
               }
                
            int v;

            switch (part)
              { case LEFT:
                  v=l.x;
                  break;
                case RIGHT:
                  v=l.x+d.width-1;
                  break;
                case XCENTER:
                  v= l.x+d.width/2;
                  break;
                case WIDTH:
                  v= d.width;
                  break;
                case TOP:
                  v=l.y;
                  break;
                case BOTTOM:
                  v=l.y+d.height-1;
                  break;
                case YCENTER:
                  v= l.y+d.height/2;
                  break;
                case HEIGHT:
                  v= d.height;
                  break;
                default:
                  throw new IllegalArgumentException(
                             "Bad value for 'part': "+part );
              }
            value+=(int)(ratio*v);
          }
        return value; 
      }

  }
