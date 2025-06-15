package ui;

import java.awt.*;


/**
 * Implements a tooltip window
 *
 */
public class Tooltip extends Window
  { static Frame frame;

    static 
      { frame=new Frame();
        if (!frame.isDisplayable())
          frame.addNotify();
      }

    public Tooltip(String tip, int x, int y)
      { super(frame);
        Label l=new Label(tip, Label.CENTER);
        add("Center", l);
        reshape(1, 1, 2, 2);
        show();
        Dimension d=textSize(l, tip);
        reshape(x, y, d.width+10, d.height+10);
        setBackground(new Color(255, 255, 208));
        show();
        repaint();
      }

    Dimension textSize(Component c, String s)
      { FontMetrics fm=getToolkit().getFontMetrics(c.getFont());
        return new Dimension(fm.stringWidth(s), fm.getHeight());
      }


    public boolean mouseExit(Event evt, int x, int y)
      { dispose();
        return true;
      }
  }
