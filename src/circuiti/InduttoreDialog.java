package circuiti;

import ui.*;
import util.*;
import java.awt.*;


/**
 * A dialog box to input the value of a resistor
 */
public class InduttoreDialog extends TrackedDialog
  { 
    Component target;
    HelpDisplayer help;
    static Font font_small=new Font("TimesRoman", Font.PLAIN, 12);
    static Font font_medium=new Font("TimesRoman", Font.PLAIN, 14);
    static Font font_large=new Font("TimesRoman", Font.BOLD, 16);

    NumericField L;

    /**
     * Create a new charge input dialog. If the user confirms
     * the changes, the target component will be notified with
     * an action event having a Induttore object as the argument
     */
    public InduttoreDialog(Component target)
      { super(UserInterface.getDummyFrame(), 
              "Aggiungi induttore", true);

        this.target=target;
        help=UserInterface.getHelpDisplayer();

        // setLayout(new VerticalLayout(VerticalLayout.JUSTIFIED));
        setLayout(new BorderLayout());

        
        //
        // Crea il pannello con l'input 
        //
        Panel panel=new Panel();
        panel.setLayout(new VerticalLayout(VerticalLayout.LEFT));
        panel.setFont(font_medium);

        Label lab=new Label("Aggiungi un Induttore");
        lab.setFont(font_large);
        panel.add("", lab);

        L=new NumericField(11, 0, 1e3);
        L.setValue(1);
        panel.add(new LabeledComponent(L, "Induttanza [H]", 
                                LabeledComponent.LEFT));


        add("Center", panel);


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
            target.deliverEvent(new Event(target, Event.ACTION_EVENT,
                                          new Induttore(L.getValue())));
          }
        else if ("Annulla".equals(what))
          { dispose();
          }
        else if ("Aiuto".equals(what))
          { help.displayHelp("help/circuiti", "induttore");
          }

        return true;
      }

    public boolean handleEvent(Event evt)
      { if (evt.id == Event.WINDOW_DESTROY)
          dispose();

        return super.handleEvent(evt);
      }

  }
