package ui;

import java.awt.*;

/**
 * A simple toolbar class
 * @author Pasquale Foggia
 * @version 0.99, Dec 1997
 */
public class Toolbar extends Panel
  { 

    public static final int VERTICAL=0;
    public static final int HORIZONTAL=1;

    static Color dark=new Color(96,96,96);
    static Color light=new Color(224,224,224);//Color.white;


    static Image sfondo;


    
    StatusDisplayer status=null;




    static {
        //sfondo=UserInterface.getImageLoader().load("icons/prova.jpg");
    }

    public Toolbar()
      { this(HORIZONTAL);
      }

    public Toolbar(int orient)
      { if (orient==VERTICAL)
          setLayout(new VerticalLayout());
        else
          setLayout(new FlowLayout(FlowLayout.LEFT));

        setBackground(Color.lightGray);
      }

    public ImageButton3D addTool(String id, Image img)
      { return addTool(id, img, img, null);
      }

    public ImageButton3D addTool(String id, Image upImg, Image downImg)
      { return addTool(id, upImg, downImg, null);
      }

    public ImageButton3D addTool(String id, Image img, String tip)
      { return addTool(id, img, img, tip);
      }

    public ImageButton3D addTool(String id, Image upImg, Image downImg, String tip)
      { ImageButton3D ib=new ImageButton3D(id, upImg, downImg);
        ib.setTip(tip); 
        ib.setBorder(1);
        add("", ib);

        return ib;
      }

    public void setStatusDisplayer(StatusDisplayer status)
      { this.status=status;
      }

    public void enableTip(boolean enable)
      { Component com;
        int i;

        for(i=0; i<countComponents(); i++)
          { com=getComponent(i);
            if (com instanceof TooltipButton)
              ((TooltipButton)com).enableTip(enable);
          }

      }





    public void drawBackground(Graphics g) {


      //g.drawImage(sfondo, 0, 0, size().width, size().height, Color.lightGray, this);


    }

    public void paint(Graphics g)
      { Dimension d=size();


        drawBackground(g);

        g.setColor(light);
        g.drawLine(0, 0, 0, d.height-1);
        g.drawLine(0, 0, d.width-1, 0);

        g.setColor(dark);
        g.drawLine(d.width-1, 0, d.width-1, d.height-1);
        g.drawLine(0, d.height-1, d.width-1, d.height-1);
        
        // super.paint(g);
      }

    public boolean mouseEnter(Event evt, int x, int y)
      { if (status!=null && evt.target instanceof TooltipButton)
          { status.showStatus(((TooltipButton)evt.target).getTip());
          }
        return false;
      }

    public boolean mouseExit(Event evt, int x, int y)
      { if (status!=null)
          status.showStatus("");
        return false;
      }

  }

