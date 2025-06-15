package cinemat;

import java.awt.*;
import java.applet.*;
import ui.*;
import util.Preloader;
/*
public class Cinemat implements Exit
  { CinematFrame frame=null;
    ImageLoader ail;
    HelpDisplayer help;
    Parameters parm;
	
        public static void main(String args[]) { 
		Cinemat aaa=new Cinemat(args);
	}

    public Cinemat(String args[])
      { 
        ail=new ApplicationImageLoader();
        help=new TextHelpDisplayer();
        parm=new StringVectorParameters(args);

        UserInterface.setImageLoader(ail);
        UserInterface.setHelpDisplayer(help);

		frame=new CinematFrame();
		frame.go1(parm, this);
        frame.show();
        frame.toFront();

        frame.centerOnScreen();
      }

  public void exit(Object obj, Object arg)
      { if (frame!=null)
          { frame.hide();
            frame.dispose();
            frame=null;
          }
	    System.exit(0);
      }
  }
*/

public class Cinemat extends CinematFrame implements Exit { // CinematFrame frame;
    
    public static void main(String args[]) {
        Parameters tmpparam=new StringVectorParameters(args);        
        WindowsTracker.enable(tmpparam.getBoolean("ontop",true));
        // WindowsTracker.enable(false);
        new Cinemat(args);
    }

    public Cinemat(String args[]) {
        super();
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
