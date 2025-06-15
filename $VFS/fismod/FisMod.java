package fismod;

import java.awt.*;
import ui.*;

import util.Preloader;



/**

 * The following parameters can be used to construct a 

 * an object through a Parameters instance:

 *   moto_base      one of "bohr", "compton", "spettro" 

*/


/*public class FisMod implements Exit
  { FisModFrame frame;

    public static void main(String args[])
      { new FisMod(args);
      }

    public FisMod(String args[])
      { ImageLoader ail=new ApplicationImageLoader();
        HelpDisplayer help=new TextHelpDisplayer();
        Parameters parm=new StringVectorParameters(args);

        UserInterface.setImageLoader(ail);
        UserInterface.setHelpDisplayer(help);

        frame=new FisModFrame(parm, this);
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

  }*/



public class FisMod extends FisModFrame implements Exit { // CinematFrame frame;



    public static void main(String args[]) {

        Parameters tmpparam=new StringVectorParameters(args);        

        WindowsTracker.enable(tmpparam.getBoolean("ontop",true));

        new FisMod(args);

    }



    public FisMod(String args[]) {

        ImageLoader ail=new ApplicationImageLoader();

        HelpDisplayer help=new TextHelpDisplayer();

        Parameters parm=new StringVectorParameters(args);



        UserInterface.setImageLoader(ail);

        UserInterface.setHelpDisplayer(help);



        //frame=new CinematFrame(parm, this);

        super.go( parm, this ) ;

 

        if (!Preloader.isPreloadActive()) {

            /*show();

            Event a=new Event(this,Event.ACTION_EVENT,"");

            String tipo=parm.getString("tipo", "null");

            show("*Blank*");

            if ("bohr".equals(tipo))

                action(a, "Atomo di Bohr");

            else if ("compton".equals(tipo)) 

                action(a, "Effetto Compton...");

            else if ("spettro".equals(tipo)) 

                action(a, "Spettro atomico dell'idrogeno...");

            toFront();*/

            show();

            String tipo=parm.getString("tipo", "null");

            show("*Blank*");

            if ("bohr".equals(tipo))

                show("Bohr");

            else if ("compton".equals(tipo)) 

                show("Compton");

            else if ("spettro".equals(tipo)) 

                show("Spettro");

                

            /*Event a;

            if ("bohr".equals(tipo))

                a=new Event(this,Event.ACTION_EVENT,"Atomo di Bohr");

            else if ("compton".equals(tipo)) 

                a=new Event(this,Event.ACTION_EVENT,"Effetto Compton...");

            else if ("spettro".equals(tipo)) 

                a=new Event(this,Event.ACTION_EVENT,"Spettro atomico dell'idrogeno...");

            else

                a=new Event(this,Event.ACTION_EVENT,"");

            postEvent(a);*/

            toFront();

        }

    }



    public void exit(Object obj, Object arg) {

        hide();

        dispose();

//

//          if (frame!=null)

//          {

//            frame.hide();

//            frame.dispose();

//          }

//

        if (!Preloader.isPreloadActive())

            System.exit(0);

    }

}