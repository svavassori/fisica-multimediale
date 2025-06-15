package dinam1;

import ui.*;
import dinam1.*;
import util.*;
import java.awt.*;


/**
 * A dialog box to input the parameters of a translational
 * relative motion
 */
public class RelTransDialog extends TrackedDialog
  { 
    Component target;
    HelpDisplayer help;
    static Font font_small=new Font("TimesRoman", Font.PLAIN, 12);
    static Font font_medium=new Font("TimesRoman", Font.PLAIN, 14);
    static Font font_large=new Font("TimesRoman", Font.BOLD, 16);

    TabsPanel tabs;
    VectorInput vel;
    VectorInput acc;

    CardLayout cards;
    Panel cards_panel;
    Settings settings;


    /**
     * Create a new dialog. If the user confirms
     * the changes, the target component will be notified with
     * an action event having a RelTransDialog object as the argument
     */
    public RelTransDialog(Component target) 
      { super(UserInterface.getDummyFrame(), 
              "Moto relativo traslatorio", true);

        this.target=target;
        help=UserInterface.getHelpDisplayer();

        // setLayout(new VerticalLayout(VerticalLayout.JUSTIFIED));
        setLayout(new BorderLayout());
        
        //
        // Crea la barra di bottoni in alto
        //
        tabs=new TabsPanel();
        tabs.setFont(font_small);
        tabs.add("Velocità");
        tabs.add("Accelerazione");
        add("North", tabs);

        cards_panel=new Panel();
        cards=new CardLayout();
        cards_panel.setLayout(cards);

        //
        // Crea il pannello con l'input della velocita'
        //
        Panel vel_panel=new Panel();
        vel_panel.setLayout(new VerticalLayout(VerticalLayout.JUSTIFIED));
        Label lab=new Label("Velocità [m/s]");
        lab.setFont(font_large);
        vel_panel.add("", lab);

        vel=new VectorInput(-20, 20, -20, 20);
        vel_panel.add("", vel);

        cards_panel.add("Vel", vel_panel);


        //
        // Crea il pannello con l'input della accelerazione'
        //
        Panel acc_panel=new Panel();
        acc_panel.setLayout(new VerticalLayout(VerticalLayout.JUSTIFIED));
        lab=new Label("Accelerazione [m/s^2]");
        lab.setFont(font_large);
        acc_panel.add("", lab);

        acc=new VectorInput(-20, 20, -20, 20);
        acc_panel.add("", acc);

        cards_panel.add("Acc", acc_panel);
        add("Center", cards_panel);

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
          { help.displayHelp("help/dinam1", "reltran");
          }
        else if ("Velocità".equals(what))
          { cards.show(cards_panel, "Vel");
            tabs.show("Velocità");
          }
        else if ("Accelerazione".equals(what))
          { cards.show(cards_panel, "Acc");
            tabs.show("Accelerazione");
          }

        return true;
      }

    public boolean handleEvent(Event evt)
      { if (evt.id == Event.WINDOW_DESTROY)
          dispose();

        return super.handleEvent(evt);
      }

    public double getVelX()
      { return vel.getX();
      }

    public double getVelY()
      { return vel.getY();
      }

    public double getAccX()
      { return acc.getX();
      }

    public double getAccY()
      { return acc.getY();
      }
  }
