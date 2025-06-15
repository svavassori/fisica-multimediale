package oscill;

import ui.*;
import onde.*;
import util.*;
import java.awt.*;


/**
 * A dialog box to input the graphic to be displayed
 */
public class GraphicDialog extends TrackedDialog
  { 
    Component target;
    HelpDisplayer help;
    static Font font_small=new Font("TimesRoman", Font.PLAIN, 12);
    static Font font_medium=new Font("TimesRoman", Font.PLAIN, 14);
    static Font font_large=new Font("TimesRoman", Font.BOLD, 16);


    Checkbox cb[];

    /**
     * Create a new settings dialog. If the user confirms
     * the changes, the target component will be notified with
     * an action event having a GraphicSettings object as the argument
     */
    public GraphicDialog(Component target, 
                         GraphicSettings initial)
      { super(UserInterface.getDummyFrame(), 
              "Grafico", true);

        this.target=target;
        help=UserInterface.getHelpDisplayer();


        Label lab;
        int i;

        
        //
        // Crea il pannello con l'input di onda e angolo 
        //
        Panel panel=new Panel();
        panel.setLayout(new VerticalLayout(VerticalLayout.LEFT));


        cb=new Checkbox[Settings.ONDE+1];

        for(i=0; i<Settings.ONDE; i++)
          { cb[i]=new Checkbox("Onda "+(i+1));
            cb[i].setState(initial.onda[i]);
            cb[i].setFont(font_medium);
            panel.add("", cb[i]);
          }
        cb[Settings.ONDE]=new Checkbox("Somma");
        cb[Settings.ONDE].setState(initial.somma);
        cb[Settings.ONDE].setFont(font_medium);
        panel.add("", cb[Settings.ONDE]);


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
            GraphicSettings settings=new GraphicSettings();
            int i;
            for(i=0; i<Settings.ONDE; i++)
              settings.onda[i]=cb[i].getState();
            settings.somma=cb[Settings.ONDE].getState();
            target.deliverEvent(new Event(target, Event.ACTION_EVENT, 
                                          settings));
          }
        else if ("Annulla".equals(what))
          { dispose();
          }
        else if ("Aiuto".equals(what))
          { help.displayHelp("help/oscill", "graphic");
          }

        return true;
      }

    public boolean handleEvent(Event evt)
      { if (evt.id == Event.WINDOW_DESTROY)
          dispose();

        return super.handleEvent(evt);
      }


  }
