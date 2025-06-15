package ottica;

import ui.*;
import java.awt.*;
import ottica.*;



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
    Panel cards_panel;
    CardLayout cards;

    //Choice tipo;
    //Choice materiale;
    List tipo;

    List materiale;

    NumericField rifraz_rosso, rifraz_giallo, rifraz_blu;

    //Choice tipo_lente;
    List tipo_lente;

    ImageButton lente_image;
    Image lente_images[];
    NumericField lente_raggio_1;
    NumericField lente_raggio_2;

    //Choice prisma_angolo;
    List prisma_angolo;

    ImageButton prisma_image;
    Image prisma_images[];

    NumericInput lastra_spessore;

    //Choice tipo_superficie;

    List tipo_superficie;
    ImageButton superficie_image;
    Image superficie_images[];
    Checkbox superficie_riflettente;
    NumericField superficie_raggio; 


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
        tabs.add("Tipo di esperimento");
        tabs.add("Lente");
        tabs.add("Prisma");
        tabs.add("Lastra");
        tabs.add("Superficie");
        add("North", tabs);


        cards_panel=new Panel();
        cards=new CardLayout();
        cards_panel.setLayout(cards);


        //
        // Crea i singoli pannelli
        //
        Panel tipo_panel=createTipoPanel(initial);
        cards_panel.add("Tipo", tipo_panel);

        Panel lente_panel=createLentePanel(initial);
        cards_panel.add("Lente", lente_panel);

        Panel prisma_panel=createPrismaPanel(initial);
        cards_panel.add("Prisma", prisma_panel);

        Panel lastra_panel=createLastraPanel(initial);
        cards_panel.add("Lastra", lastra_panel);

        Panel superficie_panel=createSuperficiePanel(initial);
        cards_panel.add("Superficie", superficie_panel);

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
        add("South", confirm_panel);

        invalidate();
      }


    /**
     * Crea il panel con l'input del tipo di esperimento
     */
    Panel createTipoPanel(Settings initial)
      { Panel tipo_panel=new Panel();
        tipo_panel.setLayout(new VerticalLayout(VerticalLayout.LEFT));

        tipo_panel.setFont(font_medium);

        Label lab=new Label("Tipo di esperimento");
        lab.setFont(font_large);
        tipo_panel.add("", lab);


        tipo=new AutoDoubleClickList();//Choice();
        tipo.addItem("Lente");
        tipo.addItem("Prisma");
        tipo.addItem("Lastra");
        tipo.addItem("Superficie");
        tipo.select(initial.tipo);
        tipo_panel.add("", new LabeledComponent(tipo, "Tipo di esperimento",
                                       LabeledComponent.LEFT));

        materiale=new AutoDoubleClickList();//Choice();
        materiale.addItem("Acqua");
        materiale.addItem("Quarzo");
        materiale.addItem("Vetro crown");
        materiale.addItem("Vetro flint");
        materiale.addItem("Definito dall'utente");
        materiale.select(initial.materiale);
        tipo_panel.add("", new LabeledComponent(materiale, "Materiale",
                                       LabeledComponent.LEFT));

        rifraz_rosso=new NumericField(5, 1.0, 4.0);
        rifraz_rosso.setValue(initial.rifraz_rosso);
        tipo_panel.add("", new LabeledComponent(rifraz_rosso, 
                                       "Indice rifraz. rosso",
                                       LabeledComponent.LEFT));

        rifraz_giallo=new NumericField(5, 1.0, 4.0);
        rifraz_giallo.setValue(initial.rifraz_giallo);
        tipo_panel.add("", new LabeledComponent(rifraz_giallo, 
                                       "Indice rifraz. giallo",
                                       LabeledComponent.LEFT));

        rifraz_blu=new NumericField(5, 1.0, 4.0);
        rifraz_blu.setValue(initial.rifraz_blu);
        tipo_panel.add("", new LabeledComponent(rifraz_blu, 
                                       "Indice rifraz. blu   ",
                                       LabeledComponent.LEFT));
        
        aggiornaMateriale();

        return tipo_panel;
      }


    /**
     * Crea il panel con l'input del tipo di lente
     */
    Panel createLentePanel(Settings initial)
      { Panel lente_panel=new Panel();
        lente_panel.setLayout(new VerticalLayout(VerticalLayout.LEFT));

        lente_panel.setFont(font_medium);

        Label lab=new Label("Lente");
        lab.setFont(font_large);
        lente_panel.add("", lab);

        Panel sub_panel=new Panel();
        sub_panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        tipo_lente=new AutoDoubleClickList();//Choice();
        tipo_lente.addItem("Biconvessa");
        tipo_lente.addItem("Biconcava");
        tipo_lente.addItem("Piano convessa a sinistra");
        tipo_lente.addItem("Piano convessa a destra");
        tipo_lente.addItem("Piano concava a sinistra");
        tipo_lente.addItem("Piano concava a destra");
        tipo_lente.addItem("Menisco a sinistra");
        tipo_lente.addItem("Menisco a destra");
        tipo_lente.select(initial.tipo_lente);
        sub_panel.add("", new LabeledComponent(tipo_lente, "Tipo di lente",
                                       LabeledComponent.LEFT));
        String icon_names[]={"len_cc.gif", "len_dd.gif",
                             "len_cp.gif", "len_pc.gif",
                             "len_dp.gif", "len_pd.gif",
                             "len_cd.gif", "len_dc.gif"};
        
        ImageLoader il=UserInterface.getImageLoader();
        int i;
        lente_images=new Image[icon_names.length];
        for(i=0; i<icon_names.length; i++)
          lente_images[i]=il.load("icons/"+icon_names[i]);
        lente_image=new ImageButton(lente_images[0]);
        lente_image.setBackground(Color.lightGray);
        lente_image.disable();
        sub_panel.add("", lente_image);

        lente_panel.add("", sub_panel);


        lente_raggio_1=new NumericField(5, 0.1, 1000.0);
        lente_raggio_1.setValue(initial.lente_raggio_1);

        lente_panel.add("", new LabeledComponent(lente_raggio_1, 
                                       "Raggio sinistro",
                                       LabeledComponent.LEFT));

        

        lente_raggio_2=new NumericField(5, 0.1, 1000.0);
        lente_raggio_2.setValue(initial.lente_raggio_2);

        lente_panel.add("", new LabeledComponent(lente_raggio_2, 
                                       "Raggio destro  ",
                                       LabeledComponent.LEFT));


        aggiornaLente();

        return lente_panel;
      }

    /**
     * Crea il panel con l'input del tipo di prisma
     */
    Panel createPrismaPanel(Settings initial)
      { Panel prisma_panel=new Panel();
        prisma_panel.setLayout(new VerticalLayout(VerticalLayout.LEFT));

        prisma_panel.setFont(font_medium);

        Label lab=new Label("Prisma");
        lab.setFont(font_large);
        prisma_panel.add("", lab);

        Panel sub_panel=new Panel();
        sub_panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        prisma_angolo=new AutoDoubleClickList();//Choice();
        prisma_angolo.addItem("0°");
        prisma_angolo.addItem("45°");
        prisma_angolo.addItem("90°");
        prisma_angolo.addItem("135°");
        prisma_angolo.addItem("180°");
        prisma_angolo.addItem("225°");
        prisma_angolo.addItem("270°");
        prisma_angolo.addItem("315°");
        prisma_angolo.select(initial.prisma_angolo);
        sub_panel.add("", new LabeledComponent(prisma_angolo, "Angolo",
                                       LabeledComponent.LEFT));
        String icon_names[]={"pri000.gif", "pri045.gif",
                             "pri090.gif", "pri135.gif",
                             "pri180.gif", "pri225.gif",
                             "pri270.gif", "pri315.gif"};
        
        ImageLoader il=UserInterface.getImageLoader();
        int i;
        prisma_images=new Image[icon_names.length];
        for(i=0; i<icon_names.length; i++)
          prisma_images[i]=il.load("icons/"+icon_names[i]);
        prisma_image=new ImageButton(prisma_images[0]);
        prisma_image.setBackground(Color.lightGray);
        prisma_image.disable();
        sub_panel.add("", prisma_image);

        prisma_panel.add("", sub_panel);

        aggiornaPrisma();
        return prisma_panel;
      }

    /**
     * Crea il panel con l'input del tipo di lastra
     */
    Panel createLastraPanel(Settings initial)
      { Panel lastra_panel=new Panel();
        lastra_panel.setLayout(new VerticalLayout(VerticalLayout.LEFT));

        lastra_panel.setFont(font_medium);

        Label lab=new Label("lastra");
        lab.setFont(font_large);
        lastra_panel.add("", lab);

        lastra_spessore=new NumericInput(1, 20, 1, 0.05);
        lastra_spessore.setValue(initial.lastra_spessore);
        lastra_panel.add("", new LabeledComponent(lastra_spessore, 
                                       "Spessore",
                                       LabeledComponent.ABOVE));

        return lastra_panel;
      }
      

    /**
     * Crea il panel con l'input del tipo di superficie
     */
    Panel createSuperficiePanel(Settings initial)
      { Panel superficie_panel=new Panel();
        superficie_panel.setLayout(new VerticalLayout(VerticalLayout.LEFT));

        superficie_panel.setFont(font_medium);

        Label lab=new Label("Superficie");
        lab.setFont(font_large);
        superficie_panel.add("", lab);

        Panel sub_panel=new Panel();
        sub_panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        tipo_superficie=new AutoDoubleClickList();//Choice();
        tipo_superficie.addItem("Piana");
        tipo_superficie.addItem("Convessa");
        tipo_superficie.addItem("Concava");
        tipo_superficie.select(initial.tipo_superficie);
        sub_panel.add("", new LabeledComponent(tipo_superficie, 
                                       "Tipo di superficie",
                                       LabeledComponent.LEFT));
        String icon_names[]={"sup_p.gif", "sup_c.gif",
                             "sup_d.gif"};
        
        ImageLoader il=UserInterface.getImageLoader();
        int i;
        superficie_images=new Image[icon_names.length];
        for(i=0; i<icon_names.length; i++)
          superficie_images[i]=il.load("icons/"+icon_names[i]);
        superficie_image=new ImageButton(superficie_images[0]);
        superficie_image.setBackground(Color.lightGray);
        superficie_image.disable();
        sub_panel.add("", superficie_image);

        superficie_panel.add("", sub_panel);


        superficie_raggio=new NumericField(5, 0.5, 1000.0);
        superficie_raggio.setValue(initial.superficie_raggio);
        superficie_panel.add("", new LabeledComponent(superficie_raggio, 
                                       "Raggio",
                                       LabeledComponent.LEFT));


        superficie_riflettente=new Checkbox("Superficie riflettente");
        superficie_riflettente.setState(initial.superficie_riflettente);
        superficie_panel.add("", superficie_riflettente);

        aggiornaSuperficie();

        return superficie_panel;
      }

    /**
     * Aggiorna gli indici di rifraz. in funzione del materiale scelto
     */
    void aggiornaMateriale()
      { int mat=materiale.getSelectedIndex();
        if (mat==Settings.USER)
          { rifraz_rosso.enable();
            rifraz_giallo.enable();
            rifraz_blu.enable();
          }
        else
          { rifraz_rosso.setValue(Settings.tabella_rifrazione[mat][0]);
            rifraz_giallo.setValue(Settings.tabella_rifrazione[mat][1]);
            rifraz_blu.setValue(Settings.tabella_rifrazione[mat][2]);
            rifraz_rosso.disable();
            rifraz_giallo.disable();
            rifraz_blu.disable();
          }
      }

    /**
     * Aggiorna il tipo di lente
     */
    void aggiornaLente()
      { int i=tipo_lente.getSelectedIndex();

        lente_image.setImage(lente_images[i]);
        if (i==Settings.PIANO_CONVESSA_SINISTRA 
            || i==Settings.PIANO_CONCAVA_SINISTRA)
          lente_raggio_2.disable();
        else
          lente_raggio_2.enable();

        if (i==Settings.PIANO_CONVESSA_DESTRA 
            || i==Settings.PIANO_CONCAVA_DESTRA)
          lente_raggio_1.disable();
        else
          lente_raggio_1.enable();
      }
       
    /**
     * Aggiorna il tipo di prisma
     */
    void aggiornaPrisma()
      { int i=prisma_angolo.getSelectedIndex();

        prisma_image.setImage(prisma_images[i]);
      }

    /**
     * Aggiorna il tipo di superficie
     */
    void aggiornaSuperficie()
      { int i=tipo_superficie.getSelectedIndex();

        superficie_image.setImage(superficie_images[i]);
        if (i==Settings.PIANA)
          superficie_raggio.disable();
        else
          superficie_raggio.enable();
      }

      
    /**
     * Risposta a un'azione
     */
    public boolean action(Event evt, Object what)
      { if ("OK".equals(what))
          { dispose();
            Settings settings=new Settings();
            settings.tipo=tipo.getSelectedIndex();
            settings.materiale=materiale.getSelectedIndex();
            settings.rifraz_rosso=rifraz_rosso.getValue();
            settings.rifraz_giallo=rifraz_giallo.getValue();
            settings.rifraz_blu=rifraz_blu.getValue();
            settings.tipo_lente=tipo_lente.getSelectedIndex();
            settings.lente_raggio_1=lente_raggio_1.getValue();
            settings.lente_raggio_2=lente_raggio_2.getValue();
            settings.prisma_angolo=prisma_angolo.getSelectedIndex();
            settings.lastra_spessore=lastra_spessore.getValue();
            settings.tipo_superficie=tipo_superficie.getSelectedIndex();
            settings.superficie_riflettente=superficie_riflettente.getState();
            settings.superficie_raggio=superficie_raggio.getValue();

            target.deliverEvent(new Event(target, Event.ACTION_EVENT, settings));
          }
        else if ("Annulla".equals(what))
          { dispose();
          }
        else if ("Aiuto".equals(what))
          { if (help!=null)
              help.displayHelp("help/ottica", "param");
          }
        else if (evt.target==materiale)
          aggiornaMateriale();
        else if (evt.target==tipo_lente)
          aggiornaLente();
        else if (evt.target==prisma_angolo)
          aggiornaPrisma();
        else if (evt.target==tipo_superficie)
          aggiornaSuperficie();
        else if ("Tipo di esperimento".equals(what))
          { cards.show(cards_panel, "Tipo");
            tabs.show("Tipo di esperimento");
             validate();
          }
        else if ("Lente".equals(what) 
                 && evt.target != tipo
                 && tipo.getSelectedIndex()==Settings.LENTE)
          { cards.show(cards_panel, "Lente");
            tabs.show("Lente");
             validate();
          }
        else if ("Prisma".equals(what) 
                 && evt.target != tipo
                 && tipo.getSelectedIndex()==Settings.PRISMA)
          { cards.show(cards_panel, "Prisma");
            tabs.show("Prisma");
             validate();
          }
        else if ("Lastra".equals(what) 
                 && evt.target != tipo
                 && tipo.getSelectedIndex()==Settings.LASTRA)
          { cards.show(cards_panel, "Lastra");
            tabs.show("Lastra");
             validate();
          }
        else if ("Superficie".equals(what) 
                 && evt.target != tipo
                 && tipo.getSelectedIndex()==Settings.SUPERFICIE)
          { cards.show(cards_panel, "Superficie");
            tabs.show("Superficie");
             validate();
          }

        return true;
      }

    /**
     * Gestore degli eventi
     */
    public boolean handleEvent(Event evt)
      { if (evt.id == Event.WINDOW_DESTROY)
          dispose();
        return super.handleEvent(evt);
      }
  }