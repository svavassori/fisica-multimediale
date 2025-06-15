package elemag;

import ui.*;
import util.*;
import java.awt.*;


/**
 * A dialog box to input initial conditions
 * for a charge
 */
public class CaricaDialog extends TrackedDialog
  { 
    Component target;
    HelpDisplayer help;
    static Font font_small=new Font("TimesRoman", Font.PLAIN, 12);
    static Font font_medium=new Font("TimesRoman", Font.PLAIN, 14);
    static Font font_large=new Font("TimesRoman", Font.BOLD, 16);

    NumericField xPos, yPos;
    NumericField carica;



    /**
     * Create a new charge input dialog. If the user confirms
     * the changes, the target component will be notified with
     * an action event having a CaricaDialog object as the argument
     */
    public CaricaDialog(Component target, double x, double y)
      { super(UserInterface.getDummyFrame(), 
              "Aggiungi carica", true);

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

        Label lab=new Label("Aggiungi una carica");
        lab.setFont(font_large);
        panel.add("", lab);

        carica=new NumericField(11, -1e-1, 1e-1);
        carica.setValue(1e-8);
        panel.add(new LabeledComponent(carica, "Carica [C]", 
                                LabeledComponent.LEFT));

        xPos=new NumericField(8, -500, 500);
        xPos.setValue(x);
        panel.add(new LabeledComponent(xPos, "Posizione X [m]", 
                                LabeledComponent.LEFT));

        yPos=new NumericField(8, -500, 500);
        yPos.setValue(y);
        panel.add(new LabeledComponent(yPos, "Posizione Y [m]", 
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
          { help.displayHelp("help/elemag", "carica");
          }

        return true;
      }

    public boolean handleEvent(Event evt)
      { if (evt.id == Event.WINDOW_DESTROY)
          dispose();

        return super.handleEvent(evt);
      }

    public double getCarica()
      { return carica.getValue();
      }

    public double getXPos()
      { return xPos.getValue();
      }

    public double getYPos()
      { return yPos.getValue();
      }
  }
