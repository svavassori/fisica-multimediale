package oscill;

import ui.*;
import util.*;
import oscill.*;
import java.awt.*;


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

    TabsPanel tabs;

    NumericInput ampiezza[];
    NumericInput frequenza[];
    NumericInput fase[];
    NumericInput smorzamento[];

    Panel cards_panel;
    CardLayout cards;


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
        // Crea la barra di bottoni in alto
        //
        tabs=new TabsPanel();
        tabs.setFont(font_small);
        tabs.add("Onda 1");
        tabs.add("Onda 2");
        add("North", tabs);

        cards_panel=new Panel();
        cards=new CardLayout();
        cards_panel.setLayout(cards);
        
        //
        // Crea i pannelli con l'input per le caratteristiche
        // delle onde
        //
        int i;
        Panel onda_panel;
        ampiezza=new NumericInput[Settings.ONDE];
        frequenza=new NumericInput[Settings.ONDE];
        fase=new NumericInput[Settings.ONDE];
        smorzamento=new NumericInput[Settings.ONDE];
        for(i=0; i<Settings.ONDE; i++)
          {
            onda_panel=new Panel();
            onda_panel.setLayout(new VerticalLayout(VerticalLayout.JUSTIFIED));
            Label lab;

            lab=new Label("Caratteristiche delle onde - Onda "+(i+1));
            lab.setFont(font_large);
            onda_panel.add("", lab);

            FontMetrics fm=getFontMetrics(font_medium);
            Dimension dim=new Dimension(fm.stringWidth("Smorzamento [1/s] X"),
                                        fm.getHeight()+4);
    
            Panel sub_panel=new Panel();
            sub_panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            lab=new Label("Ampiezza");
            lab.setFont(font_medium);
            sub_panel.add("", new SizeConstraint(lab, dim));
    
            ampiezza[i]=new NumericInput(1, 100, 1, 0.1);
            ampiezza[i].setValue(initial.ampiezza[i]);
            sub_panel.add("", ampiezza[i]);
            onda_panel.add("", sub_panel);

            sub_panel=new Panel();
            sub_panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            lab=new Label("Frequenza [Hz]");
            lab.setFont(font_medium);
            sub_panel.add("", new SizeConstraint(lab, dim));
    
            frequenza[i]=new NumericInput(1, 100, 1, 1);
            frequenza[i].setValue(initial.frequenza[i]);
            sub_panel.add("", frequenza[i]);
            onda_panel.add("", sub_panel);

            sub_panel=new Panel();
            sub_panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            lab=new Label("Fase [deg]");
            lab.setFont(font_medium);
            sub_panel.add("", new SizeConstraint(lab, dim));
    
            fase[i]=new NumericInput(-180, 180, 0, 1);
            fase[i].setValue(initial.fase[i]);
            sub_panel.add("", fase[i]);
            onda_panel.add("", sub_panel);

            sub_panel=new Panel();
            sub_panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            lab=new Label("Smorzamento [1/s]");
            lab.setFont(font_medium);
            sub_panel.add("", new SizeConstraint(lab, dim));
    
            smorzamento[i]=new NumericInput(0, 100, 0, 0.01);
            smorzamento[i].setValue(initial.smorzamento[i]);
            sub_panel.add("", smorzamento[i]);
            onda_panel.add("", sub_panel);


            cards_panel.add("Onda "+(i+1), onda_panel);

          }
    
        

        add("Center", cards_panel);
        cards.show(cards_panel, "Tipo");


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
            int i;
            for(i=0; i<Settings.ONDE; i++)
              { settings.ampiezza[i]=ampiezza[i].getValue();
                settings.frequenza[i]=frequenza[i].getValue();
                settings.fase[i]=(int)fase[i].getValue();
                settings.smorzamento[i]=smorzamento[i].getValue();
              }

            target.deliverEvent(new Event(target, Event.ACTION_EVENT, settings));
          }
        else if ("Annulla".equals(what))
          { dispose();
          }
        else if ("Aiuto".equals(what))
          { help.displayHelp("help/oscill", "param");
          }
        else if ("Onda 1".equals(what))
          { cards.show(cards_panel, "Onda 1");
            tabs.show("Onda 1");
             validate();
          }
        else if ("Onda 2".equals(what))
          { cards.show(cards_panel, "Onda 2");
            tabs.show("Onda 2");
             validate();
          }

        return true;
      }


    public boolean handleEvent(Event evt)
      { if (evt.id == Event.WINDOW_DESTROY)
          dispose();

        return super.handleEvent(evt);
      }


  }