package oscill;

import java.awt.*;
import java.applet.*;
import ui.*;
import oscill.*;

public class OscillApplet extends Applet implements Exit
  { OscillFrame frame=null;
    AppletImageLoader ail;
    HelpDisplayer help;
    Parameters parm;

    public void init()
      { ail=new AppletImageLoader(this);
        help=new AppletHelpDisplayer(this);
        parm=new AppletParameters(this);

        UserInterface.setImageLoader(ail);
        UserInterface.setHelpDisplayer(help);

        String iconName=parm.getString("icon", "icons/simul.gif");
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
          frame=new OscillFrame();

          frame.go(parm, this);

        }
        frame.show();
        frame.toFront();

        frame.centerOnScreen();

        return true;
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
