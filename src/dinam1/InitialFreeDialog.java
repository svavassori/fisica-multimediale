package dinam1;

import ui.*;
import dinam1.*;
import util.*;
import java.awt.*;


/**
 * A dialog box to input initial conditions
 * for free motion
 */
public class InitialFreeDialog extends TrackedDialog
  { 
    Component target;
    HelpDisplayer help;
    static Font font_small=new Font("TimesRoman", Font.PLAIN, 12);
    static Font font_medium=new Font("TimesRoman", Font.PLAIN, 14);
    static Font font_large=new Font("TimesRoman", Font.BOLD, 16);

    TabsPanel tabs;
    NewVectorInput vel;
    NumericInput massa; 
    TextField xPos, yPos;

    CardLayout cards;
    Panel cards_panel;
    Settings settings;


    /**
     * Create a new settings dialog. If the user confirms
     * the changes, the target component will be notified with
     * an action event having a SimulationFree object as the argument
     */
    public InitialFreeDialog(Component target, 
                             Settings settings,
                             SimulationFree initial,
                             double x, double y)
      { /*super((Frame)target, 
              "Condizioni iniziali", true);*/
        super(UserInterface.getDummyFrame(), 
              "Condizioni iniziali", true);
        
        
        this.target=target;
        this.settings=settings;
        help=UserInterface.getHelpDisplayer();

        // setLayout(new VerticalLayout(VerticalLayout.JUSTIFIED));
        setLayout(new BorderLayout());

        
        //
        // Crea la barra di bottoni in alto
        //
        tabs=new TabsPanel();
        tabs.setFont(font_small);
        tabs.add("Posizione e massa");
        tabs.add("Velocità iniziale");
        add("North", tabs);


        cards_panel=new Panel();
        cards=new CardLayout();
        cards_panel.setLayout(cards);

        //
        // Crea il pannello con l'input della velocita'
        //
        Panel vel_panel=new Panel();
        vel_panel.setLayout(new VerticalLayout(VerticalLayout.JUSTIFIED));
        Label lab=new Label("Velocità iniziale [m/s]");
        lab.setFont(font_large);
        vel_panel.add("", lab);

        vel=new NewVectorInput(-400, 400, -400, 400, 1000);
        if (initial!=null)
          vel.setValue(initial.getInitialVelX(), initial.getInitialVelY());
        vel_panel.add("", vel);

        cards_panel.add("Vel", vel_panel);

        
        //
        // Crea il pannello con l'input di massa e posizione
        //
        Panel mass_panel=new Panel();
        mass_panel.setLayout(new VerticalLayout(VerticalLayout.LEFT));
        lab=new Label("Massa e posizione iniziale");
        lab.setFont(font_large);
        mass_panel.add("", lab);

        lab=new Label("Massa [kg]");
        lab.setFont(font_medium);
        mass_panel.add("", lab);
        massa=new NumericInput(1, 200, 100, 0.01);
        if (initial!=null)
          massa.setValue(initial.getMass());
        mass_panel.add("", massa);

        lab=new Label("Posizione X [m]");
        lab.setFont(font_medium);
        mass_panel.add("", lab);
        xPos=new TextField(8);
        xPos.setText(Format.format(".3", x));
        mass_panel.add("", xPos);

        lab=new Label("Posizione Y [m]");
        lab.setFont(font_medium);
        mass_panel.add("", lab);
        yPos=new TextField(8);
        yPos.setText(Format.format(".3", y));
        mass_panel.add("", yPos);


        
        cards_panel.add("Massa", mass_panel);

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

        cards.show(cards_panel, "Massa");
      }



    public boolean action(Event evt, Object what)
      { if ("OK".equals(what))
          { dispose();
            SimulationFree simul=
              new SimulationFree(settings,
                                 massa.getValue(),
                                 getValue(xPos),
                                 getValue(yPos),
                                 vel.getX(),
                                 vel.getY());

            target.deliverEvent(new Event(target, Event.ACTION_EVENT, simul));
          }
        else if ("Annulla".equals(what))
          { dispose();
          }
        else if ("Aiuto".equals(what))
          { help.displayHelp("help/dinam1", "initfree");
          }
        else if ("Posizione e massa".equals(what))
          { cards.show(cards_panel, "Massa");
            tabs.show("Posizione e massa");
          }
        else if ("Velocità iniziale".equals(what))
          { cards.show(cards_panel, "Vel");
            tabs.show("Velocità iniziale");
          }

        return true;
      }

    public boolean handleEvent(Event evt)
      { if (evt.id == Event.WINDOW_DESTROY)
          dispose();
        if (evt.target instanceof TextField && 
              ((evt.id==Event.KEY_PRESS &&
                   (evt.key=='\n' || evt.key=='\t')) ||
               evt.id==Event.LOST_FOCUS))

          { TextField f=(TextField)evt.target;
            if (f==xPos || f==yPos)
              { double val=getValue(f);
                f.setText(Format.format(".3", val));
              }
          }

        return super.handleEvent(evt);
      }

    double getValue(TextField f)
      { try 
          { return Double.valueOf(f.getText()).doubleValue();
          }
        catch (NumberFormatException e)
          { return 0;
          }

      }

  }
