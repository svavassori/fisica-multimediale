package circuiti;

import util.*;
import java.awt.*;

/**
 * Un resistore
 */
public class Resistore extends Componente
  { double R;

    public Resistore(double R)
      { this.R=R;
      }

    public double getR()
      { return R;
      }

    public String toString()
      { return "Resistore R="+Format.format_e(".5", R)+" Ohm";
      }

    public void paint(Graphics g, int x1, int y1, int x2, int y2)
      { int dx=x2-x1;
        int dy=y2-y1;

        paintOrientation(g, x1, y1, x2, y2);

        g.setColor(Color.black);

        if (dx==0)
          { g.drawLine(x1, y1, x1, y1+dy/8);
            g.drawLine(x1, y1+dy/8, x1+dy/8, y1+3*dy/16);
            g.drawLine(x1-dy/8, y1+5*dy/16, x1+dy/8, y1+3*dy/16);
            g.drawLine(x1-dy/8, y1+5*dy/16, x1+dy/8, y1+7*dy/16);
            g.drawLine(x1-dy/8, y1+9*dy/16, x1+dy/8, y1+7*dy/16);
            g.drawLine(x1-dy/8, y1+9*dy/16, x1+dy/8, y1+11*dy/16);
            g.drawLine(x1-dy/8, y1+13*dy/16, x1+dy/8, y1+11*dy/16);
            g.drawLine(x1-dy/8, y1+13*dy/16, x1, y1+14*dy/16);
            g.drawLine(x1, y2, x1, y1+14*dy/16);
          }
        else
          { g.drawLine(x1, y1, x1+dx/8, y1);
            g.drawLine(x1+3*dx/16, y1+dx/8, x1+dx/8, y1);
            g.drawLine(x1+3*dx/16, y1+dx/8, x1+5*dx/16, y1-dx/8);
            g.drawLine(x1+7*dx/16, y1+dx/8, x1+5*dx/16, y1-dx/8);
            g.drawLine(x1+7*dx/16, y1+dx/8, x1+9*dx/16, y1-dx/8);
            g.drawLine(x1+11*dx/16, y1+dx/8, x1+9*dx/16, y1-dx/8);
            g.drawLine(x1+11*dx/16, y1+dx/8, x1+13*dx/16, y1-dx/8);
            g.drawLine(x1+14*dx/16, y1, x1+13*dx/16, y1-dx/8);
            g.drawLine(x1+14*dx/16, y1, x2, y1);
          }
      }
  }
