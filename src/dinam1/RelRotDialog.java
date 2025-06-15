package dinam1;

import ui.*;
import dinam1.*;
import util.*;
import java.awt.*;


/**
 * A dialog box to input the parameters of a rotational
 * relative motion
 */
public class RelRotDialog extends TrackedDialog
  { 
    Component target;
    HelpDisplayer help;
    static Font font_small=new Font("TimesRoman", Font.PLAIN, 12);
    static Font font_medium=new Font("TimesRoman", Font.PLAIN, 14);
    static Font font_large=new Font("TimesRoman", Font.BOLD, 16);


    NumericInput vel;
    NumericInput acc;



    /**
     * Create a new dialog. If the user confirms
     * the changes, the target component will be notified with
     * an action event having a RelRotDialog  object as the argument
     */
    public RelRotDialog(Component target)
      { super(UserInterface.getDummyFrame(), 
              "Opzioni della simulazione", true);

        this.target=target;
        help=UserInterface.getHelpDisplayer();


        setLayout(new BorderLayout());

        Label lab;

        
        //
        // Crea il pannello con l'input di velocita' e accelerazione
        //
        Panel panel=new Panel();
        panel.setLayout(new VerticalLayout(VerticalLayout.LEFT));

        lab=new Label("Velocità angolare [rad/s]");
        lab.setFont(font_medium);
        panel.add("", lab);
        vel=new NumericInput(-60, 60, 0, 0.1);
        panel.add("", vel);

        lab=new Label("Accelerazione angolare");
        lab.setFont(font_medium);
        panel.add("", lab);
        acc=new NumericInput(-50, 50, 0, 0.02);
        panel.add("", acc);



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
        // add("", confirm_panel);
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
          { help.displayHelp("help/dinam1", "relrot");
          }

        return true;
      }

    public boolean handleEvent(Event evt)
      { if (evt.id == Event.WINDOW_DESTROY)
          dispose();

        return super.handleEvent(evt);
      }

    public double getVel()
      { return vel.getValue();
      }

    public double getAcc()
      { return acc.getValue();
      }

  }
