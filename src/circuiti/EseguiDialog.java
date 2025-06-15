package circuiti;

import ui.*;
import util.*;
import java.awt.*;


/**
 * A dialog box to input the parameters of the simulation
 */
public class EseguiDialog extends TrackedDialog
  { 
    Component target;
    HelpDisplayer help;
    static Font font_small=new Font("TimesRoman", Font.PLAIN, 12);
    static Font font_medium=new Font("TimesRoman", Font.PLAIN, 14);
    static Font font_large=new Font("TimesRoman", Font.BOLD, 16);

    NumericField start, duration;

    /**
     * Create a new execution dialog. If the user confirms
     * the changes, the target component will be notified with
     * an action event having a EseguiDialog object as the argument
     */
    public EseguiDialog(Component target)
      { super(UserInterface.getDummyFrame(), 
              "Esegui simulazione", true);

        this.target=target;
        help=UserInterface.getHelpDisplayer();

        // setLayout(new VerticalLayout(VerticalLayout.LEFT));
        setLayout(new BorderLayout());

        
        //
        // Crea il pannello con l'input 
        //
        Panel panel=new Panel();
        panel.setLayout(new VerticalLayout(VerticalLayout.LEFT));
        panel.setFont(font_medium);

        Label lab=new Label("Esegui la simulazione");
        lab.setFont(font_large);
        panel.add("", lab);

        start=new NumericField(8, 0, 1000);
        start.setValue(0);
        panel.add(new LabeledComponent(start, "Istante iniziale [s]", 
                                LabeledComponent.LEFT));

        duration=new NumericField(8, 1e-5, 1000);
        duration.setValue(0.1);
        panel.add(new LabeledComponent(duration, "Durata [s]", 
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
            target.deliverEvent(new Event(target, Event.ACTION_EVENT, this));
          }
        else if ("Annulla".equals(what))
          { dispose();
          }
        else if ("Aiuto".equals(what))
          { help.displayHelp("help/circuiti", "esegui");
          }

        return true;
      }

    public boolean handleEvent(Event evt)
      { if (evt.id == Event.WINDOW_DESTROY)
          dispose();

        return super.handleEvent(evt);
      }

    public double getStartTime()
      { return start.getValue();
      }

    public double getDuration()
      { return duration.getValue();
      }

  }
