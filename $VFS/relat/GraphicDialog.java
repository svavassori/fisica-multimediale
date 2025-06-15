package relat;

import ui.*;
import relat.*;
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

    //Choice xsel, ysel;

    List xsel, ysel;



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

        // Crea il pannello con i choicebuttons

        Panel pan=new Panel();
        pan.setLayout(new VerticalLayout(VerticalLayout.LEFT));
        pan.setFont(font_large);

        Label lab=new Label("Ascisse");
        pan.add("", lab);
        xsel=new AutoDoubleClickList();//Choice();
        pan.add("", xsel);

        lab=new Label("Ordinate");
        pan.add("", lab);
        ysel=new AutoDoubleClickList();//Choice();
        pan.add("", ysel);

        int i;
        int n=simulation.getOutputCount();


        for(i=0; i<n; i++)
          { String s=simulation.getOutputName(i);
            xsel.addItem(s);
            ysel.addItem(s);
          }

        xsel.select(0);
        ysel.select(1);



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
          { help.displayHelp("help/relat", "graphsel");
          }

        return true;
      }

    public boolean handleEvent(Event evt)
      { if (evt.id == Event.WINDOW_DESTROY)
          dispose();

        return super.handleEvent(evt);
      }



    public int getXChoice()
      { return xsel.getSelectedIndex();
      }

    public int getYChoice()
      { return ysel.getSelectedIndex();
      }

  }
