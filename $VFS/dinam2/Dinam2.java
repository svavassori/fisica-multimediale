package dinam2;


import java.awt.*;

import java.applet.*;

import ui.*;

import util.Preloader;




/*import java.awt.*;
import ui.*;

public class Dinam2 implements Exit
  { Dinam2Frame frame;

    public static void main(String args[])
      { new Dinam2(args);
      }

    public Dinam2(String args[])
      { ImageLoader ail=new ApplicationImageLoader();
        HelpDisplayer help=new TextHelpDisplayer();
        Parameters parm=new StringVectorParameters(args);

        UserInterface.setImageLoader(ail);
        UserInterface.setHelpDisplayer(help);

        frame=new Dinam2Frame(this);
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



public class Dinam2 extends Dinam2Frame implements Exit { // CinematFrame frame;



    public static void main(String args[]) {

        Parameters tmpparam=new StringVectorParameters(args);        

        WindowsTracker.enable(tmpparam.getBoolean("ontop",true));

        new Dinam2(args);

    }



    public Dinam2(String args[]) {

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

