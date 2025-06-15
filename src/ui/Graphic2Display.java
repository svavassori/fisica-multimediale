package ui;

import util.*;
import numeric.*;
import java.awt.*;


/**
 * A canvas displaying two X-Y graph
 * @author Pasquale Foggia
 * @version 0.99, Mar 1998
 */
public class Graphic2Display extends Canvas
  { String xlab, ylab, zlab;
    double x1, y1, x2, y2, z1, z2;
    double x[], y[], z[];
    int n;
    static final int tickWidth=4;
    Insets ins;
    CoordinateMapper map, map2;
    AxesDrawer axesDrawer, axesDrawer2;
    StatusDisplayer status=null;
    

    public Graphic2Display()
      { 
        this(new double[0], "", new double[0], "", null, "");
      }

    public Graphic2Display(double x[], String xlab, double y[], String ylab)
      { this(x, xlab, y, ylab, null, "");
      }

    public Graphic2Display(double x[], String xlab, double y[], String ylab,
                           double z[], String zlab)
      { setBackground(Color.white);
        map=new CoordinateMapper();
        map2=new CoordinateMapper();
        axesDrawer=new AxesDrawer(map);
        axesDrawer2=new AxesDrawer(map2);
        axesDrawer2.setPosition(true, true);
        setData(x, xlab, y, ylab, z, zlab);
      }

    public void setStatusDisplayer(StatusDisplayer status)
      { this.status=status;
      }



    public void setData(double x[], String xlab, double y[], String ylab)
      { setData(x, xlab, y, ylab, null, "");
      }


    public void setData(double x[], String xlab, double y[], String ylab,
                        double z[], String zlab)
      { this.xlab=xlab;
        this.ylab=ylab;
        this.zlab=zlab;
        n=x.length;
        this.x=new double[n];
        this.y=new double[n];
        if (z!=null)
          this.z=new double[n];
        else
          this.z=null;
        int i;

        for(i=0; i<n; i++)
          { this.x[i]=x[i];
            this.y[i]=y[i];
            if (z!=null)
              this.z[i]=z[i];
          }
        if (n>0)
          { x1=x2=x[0];
            y1=y2=y[0];
            if (z!=null)
              z1=z2=z[0];
          }
        else
          x1=x2=y1=y2=z1=z2=0;
        for(i=1; i<n; i++)
          { if (x[i]<x1)
              x1=x[i];
            else if (x[i]>x2)
              x2=x[i];
            if (y[i]<y1)
              y1=y[i];
            else if (y[i]>y2)
              y2=y[i];
            if (z!=null)
              { if (z[i]<z1)
                  z1=z[i];
                else if (z[i]>z2)
                  z2=z[i];
              }
          }
        x1-=1e-7;
        y1-=1e-7;
        z1-=1e-7;
        x2+=1e-7;
        y2+=1e-7;
        z2+=1e-7;
        x1 -= 1e-2*Math.abs(x1);
        y1 -= 1e-2*Math.abs(y1);
        z1 -= 1e-2*Math.abs(z1);
        x2 += 1e-2*Math.abs(x2);
        y2 += 1e-2*Math.abs(y2);
        z2 += 1e-2*Math.abs(z2);

        if (z!=null)
          { if ((y1<0 || z1<0) && (y2>0 || z2>0))
            { y2=Math.max(Math.abs(y1), Math.abs(y2));
              y1=-y2;
              z2=Math.max(Math.abs(z1), Math.abs(z2));
              z1=-z2;
            }
          }

        map.set(x1,y1,x2,y2);
        map2.set(x1,z1, x2, z2);
        repaint();
      }


    public void paint(Graphics g)
      { super.paint(g);

        Dimension d=size();
        FontMetrics fm=getFontMetrics(getFont());
        if (ins==null)
          { ins=new Insets(0, 0, 0, 0);

            ins.left=tickWidth+8+fm.stringWidth("9.9999E99");
            ins.top=fm.getHeight()+8;
            ins.bottom=2*fm.getHeight()+tickWidth+15;
            ins.right=tickWidth+8+fm.stringWidth("9.9999E99");
          }

        map.set(d, ins);
        map2.set(d, ins);

        paintAxes(g);
        g.setColor(Color.red);
        g.drawString(ylab, 5, fm.getAscent()+2);
        g.setColor(Color.green);
        g.drawString(zlab, d.width-5-fm.stringWidth(zlab), fm.getAscent()+2);
        g.setColor(Color.blue);
        g.drawString(xlab,
                     d.width-ins.right/2-fm.stringWidth(xlab),
                     d.height-fm.getHeight());

        g.setColor(Color.red);

        if (n<1)
          return;
        Point pt0=realToPixel(x[0], y[0]);
        int i;
        for(i=1; i<n; i++)
          { Point pt=realToPixel(x[i], y[i]);
            g.drawLine(pt0.x, pt0.y, pt.x, pt.y);
            pt0=pt;
          }

        if (z==null)
          return;

        g.setColor(Color.green);
        pt0=map2.realToPixel(x[0], z[0]);
        for(i=1; i<n; i++)
          { Point pt=map2.realToPixel(x[i], z[i]);
            g.drawLine(pt0.x, pt0.y, pt.x, pt.y);
            pt0=pt;
          }

      }

    void paintAxes(Graphics g)
      { Dimension d=size();
        FontMetrics fm=getFontMetrics(getFont());

        g.setColor(Color.blue);
        g.drawRect(ins.left, ins.top, 
                   d.width-ins.left-ins.right,
                   d.height-ins.bottom-ins.top) ;

        axesDrawer.drawAxes(g, new Point(ins.left, d.height-ins.bottom));
        if (z!=null)
          axesDrawer2.drawYAxis(g, d.width-ins.right);
      }


    public boolean mouseMove(Event evt, int x, int y)
      { if (status!=null)
          { double r[]={0, 0};
            double r2[]={0, 0};

            pixelToReal(x,y,r);
            map2.pixelToReal(x,y,r2);
            String msg;
            if (r[0]>=x1 && r[0]<=x2 && r[1]>=y1 && r[1]<=y2)
              { msg=xlab+":"+Format.remove(r[0], (x2-x1)/1000)+
                       "  "+ylab+":"+Format.remove(r[1], (y2-y1)/1000);
                if (z!=null)
                  msg+=" "+zlab+":"+Format.remove(r2[1], (z2-z1)/1000);
              }
            else
              msg="";
            status.showStatus(msg);
          }
        return super.mouseMove(evt, x, y);
      }

    public boolean mouseExit(Event evt, int x, int y)
      { if (status!=null)
          status.showStatus("");
        return super.mouseMove(evt, x, y);
      }


    Point realToPixel(double x, double y)
      { 
        return map.realToPixel(x, y);
      }

    void pixelToReal(int x, int y, double z[])
      { map.pixelToReal(x, y, z);
      }
  }

