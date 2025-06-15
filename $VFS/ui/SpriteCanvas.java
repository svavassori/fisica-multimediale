package ui;

import java.awt.*;


/** 
 * A canvas  which supports sprites, i.e. movable images
 * Actually it is a panel, not a canvas, to allow placement of 
 * other items.
 * @author Pasquale Foggia
 * @version 0.99, Dec 1997
 */
public class SpriteCanvas extends Panel
  { static final int SPRITES=128;

    Rectangle rec[]=new Rectangle[SPRITES];
    Image     img[]=new Image[SPRITES];
    boolean   vis[]=new boolean[SPRITES];
    int sprites;

    Image  backgroundPlane;
    Image  scratchPlane;
    protected Dimension dim=null;

    Painter backgroundPainter=null;

    boolean delayed=false; 

    public SpriteCanvas()
      { sprites=0;
      }

    /**
     * In delayed mode, screen update only occurs when the user calls
     * nextFrame
     * @see #nextFrame
     */
    public void setDelayedMode(boolean delayed)
      { this.delayed=delayed;
      }

    public int getSpriteCount()
      { return sprites;
      }

    /**
     * @return the sprite id, or -1 if the maximum number
     *         of sprites has been reached
     */
    public int addSprite(Image image)
      { if (sprites>=SPRITES)
          return -1;
        vis[sprites]=false;
        rec[sprites]=new Rectangle(0, 0, 0, 0);
        sprites++;
        setSprite(sprites-1, image);
        return sprites-1;
      }

    /**
     * Remove all the sprites
     */
    public void removeSprites()
      { int i;

        for(i=0; i<sprites; i++)
          { showSprite(i, false);
            img[i]=null;
          }
        sprites=0;
      }


    public void setSprite(int id, Image image)
      { if (id<0 || id>=sprites)
          throw new IllegalArgumentException("Sprite index out of bounds");
        img[id]=image;

        Rectangle old_rect=clone(rec[id]);

        rec[id].width=image.getWidth(this);
        rec[id].height=image.getHeight(this);

        if (vis[id] && !delayed)
          { redrawRect(old_rect.union(rec[id]));
          }
      }


    public void showSprite(int id, boolean visible)
      { if (id<0 || id>=sprites)
          throw new IllegalArgumentException("Sprite index out of bounds");
        if (vis[id]==visible)
          return;

        vis[id]=visible;
        if (!delayed)
          redrawRect(rec[id]);
      }

    public boolean spriteVisible(int id)
      { if (id<0 || id>=sprites)
          throw new IllegalArgumentException("Sprite index out of bounds");
        return vis[id];
      }

    public void moveSprite(int id, int x, int y)
      { if (id<0 || id>=sprites)
          throw new IllegalArgumentException("Sprite index out of bounds");
        Rectangle old_rec=clone(rec[id]);
        rec[id].move(x, y);

        Rectangle inter=rec[id].intersection(old_rec);
        if (vis[id] && !delayed)
          { if (inter.width>old_rec.width/4 &&
                inter.height>old_rec.height/4)
              { redrawRect(old_rec.union(rec[id]));
              }
            else
              { redrawRect(old_rec);
                redrawRect(rec[id]);
              }
          }

      }

    /**
     * Delegate the painting of the background to another object
     */
    public void setBackgroundPainter(Painter bp)
      { backgroundPainter=bp;
      }

    /**
     * change the sprite position without redrawing.
     * is useful only from within updateBeforeResize()/updateAfterResize()
     */
    protected void setSpritePosition(int id, int x, int y)
      { rec[id].move(x, y);
      }

    public Rectangle getSpriteRect(int id)
      { if (id<0 || id>=sprites)
          throw new IllegalArgumentException("Sprite index out of bounds");
        return clone(rec[id]);
      }


    /**
     * @return The id of the topmost sprite which contains point
     *         (x, y), or -1
     */
    public int getSpriteAt(int x, int y)
      { int i;
        for(i=sprites-1; i>=0; i--)
          if (vis[i] && rec[i].contains(x, y))
            return i;
        return -1;
      }

    public Rectangle clone(Rectangle r)
      { return new Rectangle(r.x, r.y, r.width, r.height);
      }

    public void paint(Graphics g)
      { Dimension d=size();
        if (dim==null || d.width!=dim.width || d.height!=dim.height)
          { dim=d;
            backgroundPlane=createImage(d.width, d.height);
            scratchPlane=createImage(d.width, d.height);
            System.gc();
            updateBeforeResize();
            paintBackground();
            updateAfterResize();
          }

        redrawRect(g, g.getClipRect());

      }

    void redrawRect()
      { redrawRect(getGraphics(), null);
      }

    void redrawRect(Graphics g)
      { redrawRect(g, g.getClipRect());
      }

    void redrawRect(Rectangle r)
      { redrawRect(getGraphics(), r);
      }

    void redrawRect(Graphics g, Rectangle r)
      { if (dim==null || g==null)
          return;

        if (r==null)
          r=new Rectangle(0, 0, dim.width, dim.height);
        Graphics gs=scratchPlane.getGraphics();
        gs.clipRect(r.x, r.y, r.width, r.height);
        gs.drawImage(backgroundPlane, 0, 0, this);

        int i;
        for(i=0; i<sprites; i++)
          { if (vis[i] && r.intersects(rec[i]))
              { gs.drawImage(img[i], rec[i].x, rec[i].y, this);
              }
          }
        gs.dispose();
        Rectangle oldClip=g.getClipRect();
        g.clipRect(r.x, r.y, r.width, r.height);
        g.drawImage(scratchPlane, 0, 0, this);
        if (oldClip!=null)
          g.clipRect(oldClip.x, oldClip.y, oldClip.width, oldClip.height);
      }

    public void repaintBackground()
      { paintBackground();
        repaint();
      }

    public void repaintBackground(Rectangle r)
      { paintBackground(r);
        redrawRect(r);
      }
     
    public void paintBackground()
      { if (backgroundPlane==null)
          return;
        Graphics g=backgroundPlane.getGraphics();
        if (g==null)
          return;
        paintBackground(g, new Rectangle(0, 0, dim.width, dim.height));
        g.dispose();
      }

    public void paintBackground(Rectangle r)
      { Graphics g=backgroundPlane.getGraphics();
        if (g==null)
          return;
        paintBackground(g, r);
        g.dispose();
      }


    /**
     * The user should redefine this method.
     */
    public void paintBackground(Graphics g, Rectangle r)
      { g.setColor(getBackground());
        g.fillRect(r.x, r.y, r.width, r.height);
        if (backgroundPainter!=null)
          { Rectangle oldClip=g.getClipRect();
            g.clipRect(r.x, r.y, r.width, r.height);
            backgroundPainter.paint(this, g);
            if (oldClip!=null)
              g.clipRect(oldClip.x, oldClip.y, oldClip.width, oldClip.height);
            else
              g.clipRect(0, 0, dim.width, dim.height);
          }
      }
            
    public Graphics getBackgroundGraphics()
      { return backgroundPlane.getGraphics();
      }

    /**
     * The user could redefine this method, if needed, in order to
     * change the sprite positions before a resize, using
     * setSpritePosition
     * This method is called before the paintBackground 
     * with the new size
     */
    public void updateBeforeResize()
      {
      }

    /**
     * The user could redefine this method, if needed, in order to
     * change the sprite positions after a resize, using
     * setSpritePosition
     * This method is called after the paintBackground 
     * with the new size
     */
    public void updateAfterResize()
      {
      }


    /**
     * In delayed mode draw the next frame
     */
    public void nextFrame()
      { updateBeforeNextFrame();
        redrawRect();
        updateAfterNextFrame();
      }

    /**
     * @see #nextFrame
     * @param when  time in millis of the desired end of the operation
     *              if current time > when, the update is not performed
     */
    public void nextFrame(long when)
      { updateBeforeNextFrame();

        long time=System.currentTimeMillis();
        if (time<when)
          redrawRect();

        updateAfterNextFrame();

        time=System.currentTimeMillis();
        if (time<when)
          try { Thread.sleep(when-time); }
          catch (InterruptedException e) {}
      }


    /**
     * The user can redefine this method to do something useful
     * before the display of the next frame
     */
    public void updateBeforeNextFrame()
      {
      }

    /**
     * The user can redefine this method to do something useful
     * after the display of the next frame
     */
    public void updateAfterNextFrame()
      {
      }


    /**
     * Overridden from Component for a problem under Windoze
     */
    public boolean imageUpdate(Image img, int flags,
                               int x, int y, int w, int h)
      { repaintBackground();
        return super.imageUpdate(img, flags, x, y, w, h);
      }

  }

