package ui;

import numeric.*;
import util.*;

import java.awt.*;


/**
 * This class allows to draw coordinate axes on a Graphics
 */
public class AxesDrawer
  { CoordinateMapper map;
    int tickSize=4;
    Font font=new Font("Helvetica", Font.PLAIN, 10);
    boolean above=false;
    boolean right=false;


    public AxesDrawer(CoordinateMapper map)
      { this.map=map;
      }

    public void setTickSize(int tickSize)
      { this.tickSize=tickSize;
      }

    public void setFont(Font font)
      { this.font=font;
      }

    public void set(CoordinateMapper map)
      { this.map=map;
      }

    public void setPosition(boolean right, boolean above)
      { this.right=right;
        this.above=above;
      }

    public void drawAxes(Graphics g, Point pt)
      { drawAxes(g, pt, g.getClipRect());
      
      } 

    public void drawAxes(Graphics g, Point pt, Rectangle clip)
      { if (clip==null)
          { Dimension d=map.getDimension();
            Insets   in=map.getInsets();
            clip=new Rectangle(0, 0, 
                               d.width+in.left+in.right, 
                               d.height+in.top+in.bottom);
          }

        drawXAxis(g, pt.y, clip);
        drawYAxis(g, pt.x, clip);
      }


    public void drawXAxis(Graphics g, int y)
      { drawXAxis(g, y, g.getClipRect());
      }
      

    public void drawXAxis(Graphics g, int y, Rectangle clip)
      { if (clip==null)
          { Dimension d=map.getDimension();
            Insets   in=map.getInsets();
            clip=new Rectangle(0, 0, 
                               d.width+in.left+in.right, 
                               d.height+in.top+in.bottom);
          }
        Font old_font=g.getFont();
        g.setFont(font);
        FontMetrics fm=g.getFontMetrics();
        int fh=fm.getHeight();
        int fa=fm.getAscent();

        Dimension d=map.getDimension();
        Insets   in=map.getInsets();

        if (y>=clip.y && y<clip.y+clip.height)
          g.drawLine(in.left, y, in.left+d.width-1, y);

        double zz[]={0, 0};
        map.pixelToReal(in.left, 0, zz);
        double x1=zz[0];
        map.pixelToReal(in.left+d.width-1, 0, zz);
        double x2=zz[0];

        double inc1=Functions.chooseIncrement((x2-x1)/7);
        int maxlabelwidth=computeMaxLabelWidth(fm, x1, x2, inc1);
        double scale_x=map.getScaleX();
        double inc=inc1;
        while (inc*scale_x<=maxlabelwidth+1)
          inc+=inc1;
        

        double x=Math.ceil(x1/inc)*inc;

        int last=-1;
        Rectangle r=new Rectangle(0, 0, 0, 0);

        while(x<=x2)
          { Point p=map.realToPixel(x, 0);
            if (!above)
              { if (clip.inside(p.x, y+1) || clip.inside(p.x, y+tickSize))
                  g.drawLine(p.x, y+1, p.x, y+tickSize);
              }
            else
              { if (clip.inside(p.x, y-1) || clip.inside(p.x, y-tickSize))
                  g.drawLine(p.x, y-1, p.x, y-tickSize);
              }
            
            if (p.x > last + 1)
              {
                String s=Format.remove(x, inc);
                int w=fm.stringWidth(s);
                r.x=p.x+1;
                r.y=y+tickSize+2;
                r.width=w;
                r.height=fh;
                if (above)
                  r.y=y-tickSize-2-fh;
                if (clip.intersects(r))
                  g.drawString(s, p.x+1, r.y+fa);
                last=p.x+w;
              }

            x+=inc;
          }
        if (old_font!=null)
          g.setFont(old_font);
      }

    public void drawYAxis(Graphics g, int x)
      { drawYAxis(g, x, g.getClipRect());
      }
      

    public void drawYAxis(Graphics g, int x, Rectangle clip)
      { if (clip==null)
          { Dimension d=map.getDimension();
            Insets   in=map.getInsets();
            clip=new Rectangle(0, 0, 
                               d.width+in.left+in.right, 
                               d.height+in.top+in.bottom);
          }
        Font old_font=g.getFont();
        g.setFont(font);
        FontMetrics fm=g.getFontMetrics();
        int fh=fm.getHeight();
        int fa=fm.getAscent();

        Dimension d=map.getDimension();
        Insets   in=map.getInsets();

        if (x>=clip.x && x<clip.x+clip.width)
              g.drawLine(x, in.top, x, in.top+d.height-1);

        double zz[]={0, 0};
        map.pixelToReal(0, in.top+d.height-1, zz);
        double y1=zz[1];
        map.pixelToReal(0, in.top, zz);
        double y2=zz[1];

        double inc1=Functions.chooseIncrement((y2-y1)/7);
        double scale_y=map.getScaleY();
        double inc=inc1;
        while (inc*scale_y <= fh+1)
          { inc+=inc1;
          }
        
        

        double y=Math.ceil(y1/inc)*inc;

        int last=in.top+d.height+10;
        Rectangle r=new Rectangle(0, 0, 0, 0);

        while(y<=y2)
          { Point p=map.realToPixel(0, y);
            if (!right)
              { if (clip.inside(x-1, p.y) || clip.inside(x-tickSize, p.y))
                  g.drawLine(x-1, p.y, x-tickSize, p.y);
              }
            else
              { if (clip.inside(x+1, p.y) || clip.inside(x+tickSize, p.y))
                  g.drawLine(x+1, p.y, x+tickSize, p.y);
              }
            
            if (p.y < last-2)
              { String s=Format.remove(y, inc);
                int w=fm.stringWidth(s);
                r.x=x-tickSize-2-w;
                r.y=p.y-fa-1;
                r.width=w;
                r.height=fh;
                if (right)
                  r.x=x+tickSize+2;
                if (clip.intersects(r))
                  g.drawString(s, r.x, p.y-1);
                last=p.y-fh;
              }

            y+=inc;
          }

        if (old_font!=null)
          g.setFont(old_font);

      }

    /**
     * Computes the maximum width of a label for the x axis
     */
    private int computeMaxLabelWidth(FontMetrics fm, double x1, double x2,
                                     double inc)
      { int maxw=0;
        double x=Math.floor(x1/inc)*inc;
        while (x<=x2)
          { String s=Format.remove(x, inc);
            int w=fm.stringWidth(s);
            if (w>maxw)
              maxw=w;
            x+=inc;
          }
        return maxw;
      }
  }
