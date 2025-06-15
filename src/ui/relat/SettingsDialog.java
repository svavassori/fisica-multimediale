package relat;

import ui.*;
import java.awt.*;
import relat.*;


/**
 * A dialog box to input new settings
 */
public class SettingsDialog extends TrackedDialog
  { 
    Component target;
    Settings initial;
    HelpDisplayer help;
    static Font font_small=new Font("TimesRoman", Font.PLAIN, 12);
    static Font font_medium=new Font("TimesRoman", Font.PLAIN, 14);
    static Font font_large=new Font("TimesRoman", Font.BOLD, 16);


    NumericInput massa, forza, maxSpeed;

    /**
     * Create a new settings dialog. If the user confirms
     * the changes, the target component will be notified with
     * an action event having a Settings object as the argument
     */
    public SettingsDialog(Component target, Settings initial)
      { super(UserInterface.getDummyFrame(), 
              "Impostazione dei parametri", true);

        this.target=target;
        this.initial=initial;
        help=UserInterface.getHelpDisplayer();

        // setLayout(new VerticalLayout(VerticalLayout.JUSTIFIED));
        setLayout(new BorderLayout());

        


        
        //
        // Crea il pannello con l'input di massa e forza
        //
        Panel panel=new Panel();
        panel.setLayout(new VerticalLayout(VerticalLayout.JUSTIFIED));


        Label lab=new Label("Massa a riposo [kg]");
        lab.setFont(font_medium);
        panel.add("", lab);
        massa=new NumericInput(1,1000,10,0.1);
        massa.setValue(initial.massa);
        panel.add("", massa);

        lab=new Label("Forza costante [N]");
        lab.setFont(font_medium);
        panel.add("", lab);
        forza=new NumericInput(1, 1000, 10, 0.1);
        forza.setValue(initial.forza);
        panel.add("", forza);
        
        lab=new Label("Vel. massima / vel. luce");
        lab.setFont(font_medium);
        panel.add("", lab);
        maxSpeed=new NumericInput(1, 399, 396, 0.0025);
        maxSpeed.setValue(initial.maxSpeed);
        panel.add("", maxSpeed);
        

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
            Settings settings=new Settings();
            settings.massa=massa.getValue();
            settings.forza=forza.getValue();
            settings.maxSpeed=maxSpeed.getValue();

            target.deliverEvent(new Event(target, Event.ACTION_EVENT, settings));
          }
        else if ("Annulla".equals(what))
          { dispose();
          }
        else if ("Aiuto".equals(what))
          { help.displayHelp("help/relat", "param");
          }

        return true;
      }

    public boolean handleEvent(Event evt)
      { if (evt.id == Event.WINDOW_DESTROY)
          dispose();

        return super.handleEvent(evt);
      }



  }
