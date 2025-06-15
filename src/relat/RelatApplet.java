package relat;

import java.awt.*;
import java.applet.*;
import ui.*;
import relat.*;

public class RelatApplet extends Applet implements Exit	
  { RelatFrame frame=null;
    AppletImageLoader ail;
    HelpDisplayer help;

    public void init()
      { ail=new AppletImageLoader(this);
        help=new AppletHelpDisplayer(this);

        UserInterface.setImageLoader(ail);
        UserInterface.setHelpDisplayer(help);

        String iconName=getParameter("icon", "icons/simul.gif");
        ImageButton3D ib=new ImageButton3D(ail.load(iconName));
        ib.setExpand(true);
        // ib.setTip("Avvia la simulazione");
        Dimension d=size();
        ib.reshape(0, 0, d.width, d.height);
        setLayout(new BorderLayout());
        add("Center", ib);
      }

    public void destroy()
      { if (frame!=null)
         frame.dispose();
        frame=null;
      }

    public boolean action(Event evt, Object what) {
        if (frame==null) {
            frame=new RelatFrame();

            frame.go(null,this);

        }
        frame.show();
        frame.toFront();

        frame.centerOnScreen();

        return true;
      }

    public String getParameter(String name, String deflt)
      { String p=getParameter(name);
        return p==null? deflt: p;
      }


    /** 
     * Invoked by the frame when the user wants to exit
     * @see ui.Exit
     */
    public void exit(Object obj, Object arg)
      { if (frame!=null)
          { frame.hide();
            frame.dispose();
            frame=null;
          }
      }


  }
