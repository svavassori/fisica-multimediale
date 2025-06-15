package relat;

/*import java.awt.*;
import ui.*;

public class Relat implements Exit
  { RelatFrame frame;

    public static void main(String args[])
      { new Relat(args);
      }

    public Relat(String args[])
      { ImageLoader ail=new ApplicationImageLoader();
        HelpDisplayer help=new TextHelpDisplayer();
        Parameters parm=new StringVectorParameters(args);

        UserInterface.setImageLoader(ail);
        UserInterface.setHelpDisplayer(help);

        frame=new RelatFrame(this);
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



public class Relat extends RelatFrame implements Exit { // CinematFrame frame;



    public static void main(String args[]) {

        Parameters tmpparam=new StringVectorParameters(args);        

        WindowsTracker.enable(tmpparam.getBoolean("ontop",true));

        new Relat(args);

    }



    public Relat(String args[]) {

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

