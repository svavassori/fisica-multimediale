package gas;

import java.io.*;

import ui.*;

import util.Preloader;
import java.awt.*;


public class GasFrame extends TrackedFrame implements StatusDisplayer
  { ImageLoader il;
    HelpDisplayer help;
    Parameters parm;
    Exit exit;
    Settings settings;
    Label info_text;
    Panel cardsPanel;
    CardLayout cards;
    PaintableCanvas legenda;
    SimulationDisplay simulationDisplay;
    Simulation simulation;
    SimulationTable simulationTable;


    public GasFrame() { 

        super("Teoria cinetica dei gas perfetti");

    }


    public void go(Parameters parm, Exit exit)
      { 
        il=UserInterface.getImageLoader();
        help=UserInterface.getHelpDisplayer();
        this.parm=parm;
        this.exit=exit;
        settings=new Settings(parm);
        simulation=null;
        setIconImage(il.load("icons/simul.gif"));


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

        m.add("Imposta parametri...");
        m.add("Esegui");
        m.addSeparator();
        m.add("Uscita...");
        m.setFont(font);
        mb.add(m);

        m=new Menu("Visualizza");
        
        m.add("Simulazione");
        m.add("Tabella");
        m.setFont(font);
        mb.add(m);

        m=new Menu("Aiuto");
        m.add("Aiuto");
        mb.add(m);

        mb.setFont(font);
        //setMenuBar(mb);
      }

    void createToolbar()
      { Toolbar tb=new Toolbar(Toolbar.VERTICAL);

        tb.addTool("Imposta parametri...", il.load("icons/settings.gif"),
                   "Imposta parametri");
        tb.addTool("Esegui", il.load("icons/go.gif"),
                   "Esegui simulazione");
        tb.addTool("Simulazione", il.load("icons/simul.gif"),
                   "Visualizza simulazione");
        tb.addTool("Tabella", il.load("icons/tabella.gif"),
                   "Visualizza tabella");
        tb.addTool("Uscita...", il.load("icons/exit.gif"),
                   "Esci dal programma");

        tb.setStatusDisplayer(this);
        add("West", tb);
      }


    void createPanel()
      { cardsPanel=new Panel();
        cards=new CardLayout();
        cardsPanel.setLayout(cards);

        Panel simulationPanel=new Panel();
        simulationPanel.setLayout(new BorderLayout());

        legenda=new PaintableCanvas();
        legenda.setBackground(Color.white);
        legenda.setFont(new Font("Helvetica", Font.PLAIN, 10));
        FontMetrics fm=legenda.getFontMetrics(legenda.getFont());
        Dimension d=new Dimension(100, fm.getHeight()*5+15);
        simulationPanel.add("North", new SizeConstraint(legenda, d));

        simulationDisplay=new SimulationDisplay(settings, legenda, this);
        simulationPanel.add("Center", simulationDisplay);
        cardsPanel.add("Simulazione", simulationPanel);
        
        cardsPanel.add("ExitPanel", new ExitPanel("*Exit*","*CancelExit*"));

        simulationTable=new SimulationTable();
        cardsPanel.add("Tabella", simulationTable);

        add("Center", cardsPanel);

      }

    public void show(String name)
      { cards.show(cardsPanel, name);
        cardsPanel.repaint();
      }

    public void createStatus()
      { Font font=new Font("Helvetica", Font.PLAIN, 12);
        info_text=new Label("");
        info_text.setBackground(Color.lightGray);
        info_text.setAlignment(Label.CENTER);
        info_text.setFont(font);
        add("South", info_text);
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
          {
          	//salva su file C://Simul il numero 1
            salva();
            show("Simulazione");
       
            exit.exit(this, null);
          }
        else if ("Imposta parametri...".equals(what))
          { 
            Dialog dialog=new SettingsDialog(this, settings);
            dialog.reshape(50, 50, 500, 360);
            dialog.show(true);
            dialog.move(50, 50);
            toFront();
          }
        else if ("Esegui".equals(what))
          { show("Simulazione");
            if (simulation==null)
              createSimulation();
            if (simulation!=null)
              { simulation.compute();
                simulationTable.update(simulation);
              }
            simulationDisplay.startAnimation();
          }
        else if ("Simulazione".equals(what))
          { show("Simulazione");
          }
        else if ("Tabella".equals(what))
          { show("Tabella");
          }
        else if ("Aiuto".equals(what))
          help.displayHelp("help/gas", "gas");
        else if (what!=null && what instanceof Settings)
          { settings=(Settings)what;
            simulationDisplay.reset(settings);
            createSimulation();
          }
        
        return true;
      }

    public boolean handleEvent(Event evt)
      { if (evt.id==Event.WINDOW_DESTROY)
            show("ExitPanel");
        return super.handleEvent(evt);
      }


    /**
     * Create a new simulation with the current settings
     */
    void createSimulation()
      { if (settings.tipo==Settings.MOTO_BROWNIANO)
          simulation=new SimulationBrown(settings);
        else if (settings.tipo==Settings.MOTO_NMOLECOLE)
          simulation=new SimulationNMol(settings);
        simulationDisplay.play(simulation);
        if (simulation!=null)
          simulationTable.update(simulation);
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