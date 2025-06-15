package elemag;

import java.io.*;

import ui.*;
import util.Preloader;
import java.awt.*;


public class EleMagFrame extends TrackedFrame implements StatusDisplayer
  { ImageLoader il;
    HelpDisplayer help;
    Parameters parm;
    Label info_text;
    Exit exit;
    MenuItem mi_moto;
    CheckboxMenuItem mi_campo;
    CardLayout cards;
    Panel       cardsPanel;
    SimulationDisplay simulationDisplay;
    SimulationTable   simulationTable;

    public EleMagFrame() { 
        super("Elettromagnetismo");
    }
        
    public void go(Parameters parm, Exit exit)
      { 
        il=UserInterface.getImageLoader();
        help=UserInterface.getHelpDisplayer();
        this.parm=parm;
        this.exit=exit;
        setIconImage(il.load("icons/campo.gif"));

        if (!WindowsTracker.isEnabled())
            createMenuBar();

        createToolbar();

        createPanel();

        createStatus();

        resize(512, 384);
        centerOnScreen();
      }


    void createMenuBar()
      { Font font=new Font("Helvetica", Font.PLAIN, 12);

        MenuBar mb=new MenuBar();
        Menu m=new Menu("Esperimento");

        m.add("Aggiungi carica");
        m.add("Aggiungi piano");
        m.add("Aggiungi lastre");
        m.add("Campo magnetico...");
        m.addSeparator();
        m.add("Linee di forza");
        m.add("Linee equipotenziali");
        m.add("Moto di una particella");
        m.addSeparator();
        m.add("Cancella oggetto");
        m.add("Cancella esperimento...");
        m.addSeparator();
        m.add("Uscita...");
        m.setFont(font);
        mb.add(m);

        mi_moto=m=new Menu("Simulazione moto");
        m.add("Esegui simulazione");
        m.add("Visualizza tabella del moto");
        m.add("Visualizza simulazione");
        m.setFont(font);
        m.disable();
        mb.add(m);

        m=new Menu("Visualizza");
        m.add("Zoom 16:1");
        m.add("Zoom 8:1");
        m.add("Zoom 4:1");
        m.add("Zoom 2:1");
        m.add("Zoom 1:1");
        m.add("Zoom 1:2");
        m.add("Zoom 1:4");
        m.add("Zoom 1:8");
        m.add("Zoom 1:16");
        m.addSeparator();
        mi_campo=new CheckboxMenuItem("Visualizza campo");
        mi_campo.setState(false);
        m.add(mi_campo);
        m.setFont(font);
        mb.add(m);

        m=new Menu("Aiuto");
        m.add("Aiuto");
        mb.add(m);

        mb.setFont(font);
        //setMenuBar(mb);
      }

    void createToolbar()
      { Panel panel=new Panel();
        panel.setLayout(new BorderLayout(1,1));

        Toolbar tb=new Toolbar(Toolbar.VERTICAL);

        tb.addTool("Aggiungi carica", il.load("icons/carica.gif"),
                   "Aggiungi carica");
        tb.addTool("Aggiungi piano", il.load("icons/piano.gif"),
                   "Aggiungi piano");
        tb.addTool("Aggiungi lastre", il.load("icons/coppia.gif"),
                   "Aggiungi lastre");
        tb.addTool("Campo magnetico...", il.load("icons/magnet.gif"),
                   "Campo magnetico");
        tb.addTool("Cancella oggetto", il.load("icons/gomma.gif"),
                   "Cancella oggetto");
        tb.addTool("Uscita...", il.load("icons/exit.gif"),
                   "Esci dal programma");

        tb.setStatusDisplayer(this);
        panel.add("West", tb);

        Toolbar tb2=new Toolbar(Toolbar.VERTICAL);
        tb2.addTool("Linee di forza", il.load("icons/campo.gif"),
                   "Linee di forza");
        tb2.addTool("Linee equipotenziali", il.load("icons/potenzle.gif"),
                   "Linee equipotenziali");
        tb2.addTool("Moto di una particella", il.load("icons/moto.gif"),
                   "Moto di una particella");
        tb2.addTool("Esegui simulazione", il.load("icons/go.gif"),
                   "Esegui simulazione moto");

        
        tb2.setStatusDisplayer(this);
        panel.add("East", tb2);

        Panel dummy=new Panel();
        panel.add("South", new SizeConstraint(dummy, new Dimension(1,1)));

        add("West", panel);
      }


    void createPanel()
      { cardsPanel=new Panel();
        cards=new CardLayout();
        cardsPanel.setLayout(cards);

        simulationDisplay=new SimulationDisplay(this, this);
        cardsPanel.add("Simulazione", simulationDisplay);
        
        cardsPanel.add("ExitPanel", new ExitPanel("*Exit*","*CancelExit*"));

        simulationTable=new SimulationTable();
        cardsPanel.add("Tabella", simulationTable);

        add("Center", cardsPanel);
      }

    public void createStatus()
      { Font font=new Font("Helvetica", Font.PLAIN, 12);
        info_text=new Label("");
        info_text.setBackground(Color.lightGray);
        info_text.setAlignment(Label.CENTER);
        info_text.setFont(font);
        add("South", info_text);
      }

    
    public void show(String name)
      { cards.show(cardsPanel, name);
        cardsPanel.repaint();
      }

    public void showStatus(String status)
      { info_text.setText(status);
      }

/*
    public void centerOnScreen()
      { Dimension s=getToolkit().getScreenSize();
        Dimension d=size();

        move( (s.width-d.width)/2, (s.height-d.height)/2 );
      }
*/
    public boolean action(Event evt, Object what)
      { 
        if ("Uscita...".equals(what))
            show("ExitPanel");
        else if ("*CancelExit*".equals(what)) {
            show("Simulazione");
        }
        else if ("*Exit*".equals(what))
          { hide();
            if (simulationDisplay!=null)
              simulationDisplay.stopAnimation();
             //salva su file C://Simul il numero 1
            salva();
            show("Simulazione");
            exit.exit(this, null);
          }
        else if ("Aiuto".equals(what))
          help.displayHelp("help/elemag", "elemag");

        else if ("Zoom 16:1".equals(what))
          simulationDisplay.setZoom(16.0);
        else if ("Zoom 8:1".equals(what))
          simulationDisplay.setZoom(8.0);
        else if ("Zoom 4:1".equals(what))
          simulationDisplay.setZoom(4.0);
        else if ("Zoom 2:1".equals(what))
          simulationDisplay.setZoom(2.0);
        else if ("Zoom 1:1".equals(what))
          simulationDisplay.setZoom(1.0);
        else if ("Zoom 1:2".equals(what))
          simulationDisplay.setZoom(1.0/2);
        else if ("Zoom 1:4".equals(what))
          simulationDisplay.setZoom(1.0/4);
        else if ("Zoom 1:8".equals(what))
          simulationDisplay.setZoom(1.0/8);
        else if ("Zoom 1:16".equals(what))
          simulationDisplay.setZoom(1.0/16);
        else if (evt.target==mi_campo)
          {
          simulationDisplay.visualizzaCampo(mi_campo.getState());
          toFront();
          }
        else if ("Aggiungi carica".equals(what))
          { show("Simulazione");
            simulationDisplay.aggiungiCarica();
          }
        else if ("Aggiungi piano".equals(what))
          { show("Simulazione");
            simulationDisplay.aggiungiPiano();
          }
        else if ("Aggiungi lastre".equals(what))
          { show("Simulazione");
            simulationDisplay.aggiungiCoppia();
          }
        else if ("Campo magnetico...".equals(what))
          { show("Simulazione");
            simulationDisplay.aggiungiCampo();
            toFront();
          }

        else if ("Linee di forza".equals(what))
          { show("Simulazione");
            simulationDisplay.visualizzaLineeDiForza();
          }
        else if ("Linee equipotenziali".equals(what))
          { show("Simulazione");
            simulationDisplay.visualizzaLineeEquipotenziali();
          }
        else if ("Moto di una particella".equals(what))
          { show("Simulazione");
            simulationDisplay.visualizzaMoto();
          }
        else if ("*Moto*".equals(what))
          { //System.out.println("proto");
           boolean aio=simulationDisplay.motoVisualizzato();
            //System.out.println("aio");
            if(mi_moto!=null)
            mi_moto.setEnabled(aio);
            //System.out.println("qui");
          }
        else if ("Esegui simulazione".equals(what))
          { if (simulationDisplay.motoVisualizzato())
              simulationDisplay.startAnimation();
            else
              MessageBox.message(this,"Elettromagnetismo - Esegui simulazione moto",
               "Devi selezionare 'Moto di una particella'\n"+
               "e scegliere le condizioni iniziali cliccando\n"+
               "su un punto dello schermo");
          }
        else if ("Visualizza simulazione".equals(what))
          show("Simulazione");
        else if ("Visualizza tabella del moto".equals(what))
          { show("Tabella");
            simulationTable.update(simulationDisplay.getAnimation());
          }

        else if ("Cancella oggetto".equals(what))
          { show("Simulazione");
            simulationDisplay.cancellaOggetto();
          }

        else if ("Cancella esperimento...".equals(what))
          { MessageBox.confirm(this, "Elettromagnetismo",
                               "Vuoi cancellare l'esperimento?",
                               "*Reset*");
          }
        else if ("*Reset*".equals(what))
          { show("Simulazione");
            simulationDisplay.reset();
          }
        
        return true;
      }

    public boolean handleEvent(Event evt)
      { if (evt.id==Event.WINDOW_DESTROY)
            show("ExitPanel");
        return super.handleEvent(evt);
      }

    public void salva()
      {
               
      try {	
            Writer out = new FileWriter(Preloader.getSimulFileName());
            out.write("1");
            out.flush();
            out.close(); 
          } 
       catch(Exception e) 
          {
     	      System.out.println("File non trovato");
     	    }       	
    }
  
  }
