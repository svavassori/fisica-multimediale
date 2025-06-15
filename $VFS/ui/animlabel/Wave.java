package ui.animlabel;

import java.awt.*;

/**
 * Adds a sinusoidal vertical movement to the chars
 */
public class Wave implements Animation
  { int height=10;
    int length=100;
    int period=20;

    public void setHeight(int height)
      { this.height=height;
      }

    public void setLength(int length)
      { this.length=length;
      }

    public void setPeriod(int period)
      { this.period=period;
      }

    public void perform(Graphics g, int frame, int idx, Token tok)
      { double x=tok.pos.x*2*Math.PI/length;
        double t=frame*2*Math.PI/period;
        int dy=(int)Math.ceil(height*Math.sin(x-t));
        tok.pos.y+=dy;
      }
  }
