package ui.animlabel;

import java.awt.*;
import java.util.*;


/**
 * A label with animated text
 */
public class AnimatedLabel extends Canvas implements Runnable
  { String text;
    Vector anim;
    Image buffer;
    Thread thread;
    int delay;
    Dimension dim=new Dimension(-1, -1);
    int hpad=5, vpad=3;


    public AnimatedLabel()
      { this("", 100);
      }

    public AnimatedLabel(String text)
      { this(text, 100);
      }

    public AnimatedLabel(int delay)
      { this("", delay);
      }

    public AnimatedLabel(String text, int delay)
      { this.text=text;
        this.delay=delay;
        thread=null;
        anim=new Vector();
        buffer=null;
      }

    public void setText(String text)
      { this.text=text;
      }

    public void setPad(int hpad, int vpad)
      { this.hpad=hpad;
        this.vpad=vpad;
      }

    public void addAnimation(Animation a)
      { anim.addElement(a);
      }

    public void start()
      { stop();
        thread=new Thread(this);
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
      }

    public void stop()
      { if (thread!=null) 
          thread.stop();
        thread=null;
      }

    public void run()
      { int frame;
        int idx;

        for(frame=0; true; frame++)
          { try
              { Thread.sleep(delay);
              }
            catch (InterruptedException e)
              {
              }
            if (buffer!=null && isShowing())
              { Token tok=new Token();
                Graphics g=buffer.getGraphics();
                if (g==null)
                  continue;
                g.setColor(getBackground());
                g.fillRect(0, 0, dim.width, dim.height);

                tok.next_font=getFont();
                if (tok.next_font==null)
                  tok.next_font=new Font("TimesRoman", Font.PLAIN, 12);
                FontMetrics fm=getFontMetrics(tok.next_font);
                tok.next_color=getForeground();
                int x, y;
                tok.next_pos=new Point(hpad, (dim.height+fm.getAscent())/2);
                tok.text=text;
                int i;
                for(i=0; i<tok.text.length(); i++)
                  { tok.ch=tok.text.charAt(i);
                    tok.color=tok.next_color;
                    tok.font=tok.next_font;
                    fm=getFontMetrics(tok.font);
                    tok.pos=tok.next_pos;
                    tok.next_pos=new Point(tok.pos.x+fm.charWidth(tok.ch),
                                           tok.pos.y);
                    int j;
                    for(j=0; j<anim.size(); j++)
                      { Animation a=(Animation)anim.elementAt(j);
                        a.perform(g, frame, i, tok);
                      }
                    g.setFont(tok.font);
                    g.setColor(tok.color);
                    g.drawString(""+tok.ch, tok.pos.x, tok.pos.y);
                  }
                g.dispose();

                g=getGraphics();
                if (g!=null)
                  g.drawImage(buffer, 0, 0, this);
                else
                  buffer=null;
              }
          }
      }

    public Dimension minimumSize()
      { return preferredSize();
      }

    public Dimension preferredSize()
      { Font font=getFont();
        if (font==null)
          font=new Font("TimesRoman", Font.PLAIN, 12);
        FontMetrics fm=getFontMetrics(font);
        return new Dimension(fm.stringWidth(text)+2*hpad, 
                             fm.getHeight()+2*vpad);
      }



    public void update(Graphics g)
      { paint(g);
      }
       
    public synchronized void paint(Graphics g)
      { Dimension d=size();
        if (d.width!=dim.width || d.height!=dim.height || buffer==null)
          { dim=d;
            buffer=createImage(d.width, d.height);
            if (thread==null)
              { Graphics g1=buffer.getGraphics();
                g1.setColor(getBackground());
                g1.fillRect(0, 0, d.width, d.height);
                g1.dispose();
              }
          }
        
        g.drawImage(buffer, 0, 0, this);

      }

    public boolean handleEvent(Event evt)
      { if (evt.id==Event.WINDOW_DESTROY)
          stop();
        return super.handleEvent(evt);
      }

  }
