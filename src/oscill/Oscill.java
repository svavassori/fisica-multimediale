package oscill;

/*import java.awt.*;
import ui.*;

public class Oscill implements Exit
  { OscillFrame frame;

    public static void main(String args[])
      { new Oscill(args);
      }

    public Oscill(String args[])
      { ImageLoader ail=new ApplicationImageLoader();
        HelpDisplayer help=new TextHelpDisplayer();
        Parameters parm=new StringVectorParameters(args);

        UserInterface.setImageLoader(ail);
        UserInterface.setHelpDisplayer(help);

        frame=new OscillFrame(parm, this);
        frame.show();
        frame.toFront();

        frame.centerOnScreen();

      }

    public void exit(Object obj, Object arg)
      { if (frame!=null)
          { frame.hide();
            frame.dispose();
          }
        System.exit(0);
      }

  }
*/



import java.awt.*;

import java.applet.*;

import ui.*;

import util.Preloader;



public class Oscill extends OscillFrame implements Exit { // CinematFrame frame;



    public static void main(String args[]) {

        Parameters tmpparam=new StringVectorParameters(args);        

        WindowsTracker.enable(tmpparam.getBoolean("ontop",true));

        new Oscill(args);

    }



    public Oscill(String args[]) {

        ImageLoader ail=new ApplicationImageLoader();

        HelpDisplayer help=new TextHelpDisplayer();

        Parameters parm=new StringVectorParameters(args);



        UserInterface.setImageLoader(ail);

        UserInterface.setHelpDisplayer(help);



        Preloader.setSimulFileName (parm.getString("simulfile",Preloader.getSimulFileName()));

        

        super.go( parm, this ) ;



        if (!Preloader.isPreloadActive()) {

            show();

            toFront();

        }

    }



    public void exit(Object obj, Object arg) {

        hide();

        dispose();

        if (!Preloader.isPreloadActive())

            System.exit(0);

    }

}

