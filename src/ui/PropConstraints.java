package ui;

import java.awt.*;
import java.awt.peer.ComponentPeer;

/**
 * Represents the set of constraints for a component
 */

public class PropConstraints
  { 
    public PropConstraint left= new PropConstraint();
    public PropConstraint xcenter= new PropConstraint();
    public PropConstraint right= new PropConstraint();
    public PropConstraint width= new PropConstraint();
    public PropConstraint minwidth= new PropConstraint();
    public PropConstraint top= new PropConstraint();
    public PropConstraint ycenter= new PropConstraint();
    public PropConstraint bottom= new PropConstraint();
    public PropConstraint height= new PropConstraint();
    public PropConstraint minheight= new PropConstraint();



    public void above(Component com, int offset, int part)
      { bottom.set(-offset, com, PropConstraint.TOP);
        switch (part)
          { case PropConstraint.LEFT:
              left.set(com, PropConstraint.LEFT);
              break;
            case PropConstraint.RIGHT:
              right.set(com, PropConstraint.RIGHT);
              break;
            case PropConstraint.XCENTER:
              xcenter.set(com, PropConstraint.XCENTER);
              break;
            case PropConstraint.WIDTH:
              left.set(com, PropConstraint.LEFT);
              width.set(com, PropConstraint.WIDTH);
              break;
            default:
              throw new IllegalArgumentException("Bad value for 'part': "+
                                                 part);
           }

      }

    public void below(Component com, int offset, int part)
      { top.set(offset, com, PropConstraint.BOTTOM);
        switch (part)
          { case PropConstraint.LEFT:
              left.set(com, PropConstraint.LEFT);
              break;
            case PropConstraint.RIGHT:
              right.set(com, PropConstraint.RIGHT);
              break;
            case PropConstraint.XCENTER:
              xcenter.set(com, PropConstraint.XCENTER);
              break;
            case PropConstraint.WIDTH:
              left.set(com, PropConstraint.LEFT);
              width.set(com, PropConstraint.WIDTH);
              break;
            default:
              throw new IllegalArgumentException("Bad value for 'part': "+
                                                 part);
           }

      }

    public void leftOf(Component com, int offset, int part)
      { right.set(-offset, com, PropConstraint.LEFT);
        switch (part)
          { case PropConstraint.TOP:
              top.set(com, PropConstraint.TOP);
              break;
            case PropConstraint.BOTTOM:
              bottom.set(com, PropConstraint.BOTTOM);
              break;
            case PropConstraint.YCENTER:
              ycenter.set(com, PropConstraint.YCENTER);
              break;
            case PropConstraint.HEIGHT:
              top.set(com, PropConstraint.TOP);
              height.set(com, PropConstraint.HEIGHT);
              break;
            default:
              throw new IllegalArgumentException("Bad value for 'part': "+
                                                 part);
           }

      }

    public void rightOf(Component com, int offset, int part)
      { left.set(offset, com, PropConstraint.RIGHT);
        switch (part)
          { case PropConstraint.TOP:
              top.set(com, PropConstraint.TOP);
              break;
            case PropConstraint.BOTTOM:
              bottom.set(com, PropConstraint.BOTTOM);
              break;
            case PropConstraint.YCENTER:
              ycenter.set(com, PropConstraint.YCENTER);
              break;
            case PropConstraint.HEIGHT:
              top.set(com, PropConstraint.TOP);
              height.set(com, PropConstraint.HEIGHT);
              break;
            default:
              throw new IllegalArgumentException("Bad value for 'part': "+
                                                 part);
           }

      }


    /**
     * Compute and apply a constraint to a component
     * returns true if the component has been modified.
     */
    public boolean compute(Component com)
      { Dimension d=com.size();
        Dimension old=com.size();
        Point l=com.location();
        int res[]=new int[2];

        // TODO Component peer should not be used
//        ComponentPeer cp=com.getPeer();
//        if (cp!=null)
//          { Dimension dd=cp.getMinimumSize();
//            if (d.width<dd.width)
//              d.width=dd.width;
//            if (d.height<dd.height)
//              d.height=dd.height;
//
//          }

        compute(left, xcenter, right, width, minwidth, l.x, d.width, res);
        int new_x=res[0];
        int new_width=res[1];

        compute(top, ycenter, bottom, height, minheight, l.y, d.height, res);
        int new_y=res[0];
        int new_height=res[1];

        
        if (new_x!=l.x || new_y!=l.y || 
            new_width!=old.width || new_height!=old.height)
          { com.reshape(new_x, new_y, new_width, new_height);
            return true;
          }
        else
          return false;
      }  

    private void compute(PropConstraint min, PropConstraint center,
                         PropConstraint max, PropConstraint dim,
                         PropConstraint mindim, int pos0, int dim0,
                         int res[])
      { int p=pos0;
        int d=dim0;

        if (dim.isUnconstrained())
          { if (!min.isUnconstrained())
              { p=min.compute(pos0);
                if (!max.isUnconstrained())
                  d=max.compute(pos0+dim0-1)-p+1;
                else if (!center.isUnconstrained())
                  d=(center.compute(pos0+(dim0-1)/2) - p)*2+1;
              }
            else if (!center.isUnconstrained())
              { if (max.isUnconstrained())
                  p=center.compute(pos0+(dim0-1)/2)-(d-1)/2;
                else
                  { int ce=center.compute(pos0+(dim0-1)/2);
                    int mx=max.compute(pos0+dim0-1);
                    d=(mx-ce)*2;
                    p=mx-d+1;
                  }
              }
            else if (!max.isUnconstrained())
              { p=max.compute(pos0+dim0-1)-(d-1);
              }
            
            int mind=mindim.compute(0);
          
            if (mind>0 && d<mind)
              { PropConstraint pc=new PropConstraint();
                pc.setUnconstrained();
                compute(min, center, max, mindim, pc, pos0, dim0, res);
                return;
              }
          }
        else
          { d=dim.compute(dim0);
            int mind=mindim.compute(0);
            if (d<mind)
              d=mind;
            if (!min.isUnconstrained())
              p=min.compute(pos0);
            else if (!center.isUnconstrained())
              p=center.compute(pos0+(dim0-1)/2)-(d-1)/2;
            else if (!max.isUnconstrained())
              p=max.compute(pos0+dim0-1)-(d-1);
          }
        
        res[0]=p;
        res[1]=d;

      }
  }
