package onde;

import ui.*;
import onde.*;
import util.*;
import java.awt.*;


/**
 * A dialog box to input conditions for refraction
 */
public class RefractDialog extends TrackedDialog
  { 
    Component target;
    HelpDisplayer help;
    static Font font_small=new Font("TimesRoman", Font.PLAIN, 12);
    static Font font_medium=new Font("TimesRoman", Font.PLAIN, 14);
    static Font font_large=new Font("TimesRoman", Font.BOLD, 16);


    NumericInput angolo;
    CheckboxGroup cbg;
    Checkbox cb[];
    NumericInput indice;





    /**
     * Create a new settings dialog. If the user confirms
     * the changes, the target component will be notified with
     * an action event having a RefractSettings object as the argument
     */
    public RefractDialog(Component target, 
                         RefractSettings initial)
      { super(UserInterface.getDummyFrame(), 
              "Rifrazione", true);

        this.target=target;
        help=UserInterface.getHelpDisplayer();


        Label lab;
        int i;

        
        //
        // Crea il pannello con l'input di onda e angolo 
        //
        Panel panel=new Panel();
        panel.setLayout(new VerticalLayout(VerticalLayout.LEFT));


        cbg=new CheckboxGroup();
        cb=new Checkbox[Settings.ONDE];

        for(i=0; i<Settings.ONDE; i++)
          { cb[i]=new Checkbox("Onda "+(i+1), cbg, false);
            cb[i].setFont(font_medium);
            panel.add("", cb[i]);
          }

        cbg.setCurrent(cb[initial.onda]);


        lab=new Label("Angolo del piano [deg]");
        lab.setFont(font_medium);
        panel.add("", lab);

        angolo=new NumericInput(-180, 180, 0, 1);
        angolo.setValue(initial.angolo);
        panel.add("", angolo);

        lab=new Label("Indice di rifrazione relativo");
        lab.setFont(font_medium);
        panel.add("", lab);

        indice=new NumericInput(1, 50, 15, 0.1);
        indice.setValue(initial.indice);
        panel.add("", indice);


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
            RefractSettings settings=new RefractSettings();
            int i;
            Checkbox curr=cbg.getCurrent();
            for(i=0; i<Settings.ONDE; i++)
              if (curr==cb[i])
                settings.onda=i;
            settings.angolo=(int)angolo.getValue();
            settings.indice=indice.getValue();
            target.deliverEvent(new Event(target, Event.ACTION_EVENT, 
                                          settings));
          }
        else if ("Annulla".equals(what))
          { dispose();
          }
        else if ("Aiuto".equals(what))
          { help.displayHelp("help/onde", "refract");
          }

        return true;
      }

    public boolean handleEvent(Event evt)
      { if (evt.id == Event.WINDOW_DESTROY)
          dispose();

        return super.handleEvent(evt);
      }


  }
