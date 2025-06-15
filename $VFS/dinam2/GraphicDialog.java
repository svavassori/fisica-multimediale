package dinam2;

import ui.*;
import dinam2.*;
import util.*;
import java.awt.*;


/**
 * A dialog box to input the desired type of graphic
 * @author Pasquale Foggia
 * @version 0.99, Dec 1997
 */
public class GraphicDialog extends TrackedDialog
  { 
    Component target;
    HelpDisplayer help;
    static Font font_small=new Font("TimesRoman", Font.PLAIN, 12);
    static Font font_medium=new Font("TimesRoman", Font.PLAIN, 14);
    static Font font_large=new Font("TimesRoman", Font.BOLD, 16);


    CheckboxGroup cbg;
    Checkbox cb[];

    /**
     * Create a new. If the user confirms
     * the changes, the target component will be notified with
     * an action event having the GraphicDialog  object as the argument
     */
    public GraphicDialog(Component target, 
                         Simulation simulation)
      { super(UserInterface.getDummyFrame(), 
              "Grandezza da visualizzare", true);

        this.target=target;
        help=UserInterface.getHelpDisplayer();

        setLayout(new BorderLayout());

        // Crea il pannello con i radiobuttons

        Panel pan=new Panel();
        pan.setLayout(new GridLayout(0, 3));
        pan.setFont(font_medium);

        cbg=new CheckboxGroup();
        int i;
        int n=simulation.getOutputCount();

        cb=new Checkbox[n];

        for(i=0; i<n; i++)
          { cb[i]=new Checkbox(simulation.getOutputName(i), cbg, false);
            pan.add("", cb[i]);
          }

        cbg.setCurrent(cb[0]);


        add("Center", pan);

        //
        // Crea la barra di bottoni in basso
        //
        ImageLoader il=UserInterface.getImageLoader();
        Panel confirm_panel=new Panel();
        confirm_panel.setFont(font_large);
        confirm_panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        confirm_panel.add("", new ImageButton3D("OK",
                                   il.load("icons/ok.gif")));
        confirm_panel.add("", new ImageButton3D("Annulla",
                                   il.load("icons/cancel.gif")));
        confirm_panel.add("", new ImageButton3D("Aiuto",
                                   il.load("icons/help.gif")));
        add("South", confirm_panel);

      }



    public boolean action(Event evt, Object what)
      { if ("OK".equals(what))
          { dispose();
            target.deliverEvent(new Event(target, Event.ACTION_EVENT, this));
          }
        else if ("Annulla".equals(what))
          { dispose();
          }
        else if ("Aiuto".equals(what))
          { help.displayHelp("help/dinam2", "graphsel");
          }

        return true;
      }

    public boolean handleEvent(Event evt)
      { if (evt.id == Event.WINDOW_DESTROY)
          dispose();

        return super.handleEvent(evt);
      }



    public int getChoice()
      { Checkbox curr=cbg.getCurrent();
        int i;

        for(i=0; i<cb.length; i++)
          if (curr==cb[i])
            return i;

        return 0;
      }


  }
