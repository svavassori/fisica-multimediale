package gas;

import ui.*;
import java.awt.*;
import gas.*;


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


    //Choice tipo, dati;

    List tipo, dati;
    NumericField p,t,rho;

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
        // Crea il pannello con l'input dei dati 
        //
        Panel panel=new Panel();
        panel.setFont(font_medium);
        panel.setLayout(new VerticalLayout(VerticalLayout.JUSTIFIED));


        Label lab;

        //tipo=new Choice();

        tipo=new AutoDoubleClickList();
        tipo.addItem("Moto browniano");
        tipo.addItem("Moto di n molecole");
        tipo.select(initial.tipo);
        panel.add("", new LabeledComponent(tipo, "Tipo di esperimento",
                                               LabeledComponent.LEFT));


        //dati=new Choice();

        dati=new AutoDoubleClickList();
        dati.addItem("Pressione + Temperatura");
        dati.addItem("Densità + Temperatura");
        dati.addItem("Pressione + Densità");
        dati.select(initial.dati);
        panel.add("", new LabeledComponent(dati, "Dati di input",
                                               LabeledComponent.LEFT));

        p=new NumericField(7, 1000.0, 1e7);
        p.setValue(initial.p);
        panel.add("", new LabeledComponent(p, "Pressione [Pa]",
                           LabeledComponent.LEFT));

        t=new NumericField(7, 10.0, 1e4);
        t.setValue(initial.t);
        panel.add("", new LabeledComponent(t, "Temperatura [°K]",
                           LabeledComponent.LEFT));

        rho=new NumericField(7, 2.5e-5, 2.5e2);
        rho.setValue(initial.rho);
        panel.add("", new LabeledComponent(rho, "Densità [Kg/m^3]",
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

        aggiornaDati();
      }

    /**
     * Aggiorna i dati selezionati come input
     */
    void aggiornaDati()
      { double M=Settings.M;
        double R=Settings.R;

        int d=dati.getSelectedIndex();

        switch(d)
          { case Settings.P_T:
                p.enable();
                t.enable();
                rho.disable();
                rho.setValue(M*p.getValue()/(R*t.getValue()));
                break;
            case Settings.RHO_T:
                p.disable();
                t.enable();
                rho.enable();
                p.setValue(R*t.getValue()*rho.getValue()/M);
                break;
            case Settings.P_RHO:
                p.enable();
                t.disable();
                rho.enable();
                t.setValue(M*p.getValue()/(R*rho.getValue()));
                break;
           }

      }


    public boolean action(Event evt, Object what)
      { if ("OK".equals(what))
          { aggiornaDati();
            dispose();
            Settings settings=new Settings();
            settings.tipo=tipo.getSelectedIndex();
            settings.dati=dati.getSelectedIndex();
            settings.p=p.getValue();
            settings.t=t.getValue();
            settings.rho=rho.getValue();

            target.deliverEvent(new Event(target, Event.ACTION_EVENT, settings));
          }
        else if ("Annulla".equals(what))
          { dispose();
          }
        else if ("Aiuto".equals(what))
          { help.displayHelp("help/gas", "param");
          }
        else if (evt.target == dati)
          aggiornaDati();

        return true;
      }

    public boolean handleEvent(Event evt)
      { if (evt.id == Event.WINDOW_DESTROY)
          dispose();

        return super.handleEvent(evt);
      }



  }
