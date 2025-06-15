package ui;

import java.util.*;
import java.awt.*;

/**
 * A panel showing the tabs for a tabbed dialog
 * whenever the user select a tab, an action event is generated.
 * It is up to the owner of the TabsPanel to change the selected tab.
 */
public class TabsPanel extends Panel
  { Vector labels=new Vector();
    int selected=0;
    static final int pad=8;
    static final int ipad=2;
    static final int ypad=2;

    public void add(String label)
      { labels.addElement(label);
        repaint();
      }

    public void show(String label)
      { int i;

        for(i=0; i<labels.size(); i++)
          if (label.equals(labels.elementAt(i)))
            selected=i;
        repaint();
      }

    public Dimension minimumSize()
      { Font font=getFont();
        if (font==null)
          return new Dimension(100, 10);
        FontMetrics fm=getFontMetrics(font);
        return new Dimension(100, fm.getHeight()+2*ypad+8);
      }

    public Dimension preferredSize()
      { return minimumSize();
      }

    public void paint(Graphics g)
      { int i;
        int x, w;
        int selx=-30, selw=0;
        FontMetrics fm=g.getFontMetrics();
        int asc=fm.getAscent();
        int height=fm.getHeight();
        Dimension d=size();
        int y=asc+(d.height-height)/2;
        x=pad;
        for(i=0; i<labels.size(); i++)
          { String str=(String)labels.elementAt(i);
            w=fm.stringWidth(str);
            g.drawString(str, x, y);
            if (i==selected)
              { selx=x;
                selw=w;
              }
            x += w+pad;
          }

        int y1=ypad;
        int y2=d.height-1-ypad;
        g.drawLine(0, y2, selx-4-ipad, y2);
        g.drawLine(selx-4-ipad, y2, selx-ipad, y1);
        g.drawLine(selx-ipad, y1, selx+selw-1+ipad, y1);
        g.drawLine(selx+selw-1+ipad, y1, selx+selw+3+ipad, y2);
        g.drawLine(selx+selw+3+ipad, y2, d.width-1, y2);
      }


    public boolean mouseDown(Event evt, int mx, int my)
      { int i;
        int x, w;
        FontMetrics fm=getFontMetrics(getFont());
        x=pad;
        for(i=0; i<labels.size(); i++)
          { String str=(String)labels.elementAt(i);
            w=fm.stringWidth(str);
            if (mx<=x+w+pad/2)
              { deliverEvent(new Event(this, Event.ACTION_EVENT, str));
                break;
              }
            x += w+pad;
          }

        return super.mouseDown(evt, mx, my);
      }
   
  } 
