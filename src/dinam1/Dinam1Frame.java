package dinam1;
import java.io.*;
import ui.*;
import java.awt.*;

import util.Preloader;


public class Dinam1Frame extends TrackedFrame implements StatusDisplayer
  { ImageLoader il;
    HelpDisplayer help;
    Panel cardsPanel;
    CardLayout cards;
    Settings settings;
    Options options;
    Simulation simulation;
    SimulationDisplay simulationDisplay;
    SimulationTable simulationTable;
    SimulationGraphic simulationGraphic;
    SimulationMultiGraphic simulationMultiGraphic;    
    RelGraphic relGraphic;
    Label info_text;
    Exit exit;

    public Dinam1Frame() { 

        super("Dinamica I");

    }



    public void go(Parameters parm, Exit exit) {
        this.exit=exit;
        il=UserInterface.getImageLoader();
        help=UserInterface.getHelpDisplayer();
        settings=new Settings();
        options=new Options();
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
        m.add("Opzioni simulazione...");
        m.addSeparator();
        m.add("Uscita...");
        m.setFont(font);
        mb.add(m);

        m=new Menu("Visualizza");
        m.add("Simulazione");
        m.add("Grafico...");
        m.add("Tabella");
        Menu sm=new Menu("Moti relativi");
        sm.add("Moto traslatorio...");
        sm.add("Moto rotatorio...");
        m.add(sm);
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
        tb.addTool("Grafico...", il.load("icons/grafico.gif"),
                   "Visualizza grafico");
        tb.addTool("Grafico Multiplo...", il.load("icons/graficom.gif"),
           "Visualizza grafico multiplo");
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

        simulationDisplay=new SimulationDisplay(this, this, options, settings);
        cardsPanel.add("Simulazione", simulationDisplay);
        
        cardsPanel.add("ExitPanel", new ExitPanel("*Exit*","*CancelExit*"));
                
        simulationGraphic=new SimulationGraphic();
        simulationGraphic.setStatusDisplayer(this);
        cardsPanel.add("Grafico", simulationGraphic);

        simulationMultiGraphic=new SimulationMultiGraphic();
        cardsPanel.add("Grafico Multiplo", simulationMultiGraphic);        
        simulationMultiGraphic.setNumberOfDisplay(3);
        simulationMultiGraphic.setStatusDisplayer(this);
        
        relGraphic=new RelGraphic();
        relGraphic.setStatusDisplayer(this);
        relGraphic.setIsometric(true);
        cardsPanel.add("Rel", relGraphic);
        
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


/*    public void centerOnScreen()
      { Dimension s=getToolkit().getScreenSize();
        Dimension d=size();

        move( (s.width-d.width)/2, (s.height-d.height)/2 );
      }*/

    public boolean action(Event evt, Object what)
      { 
        if ("Uscita...".equals(what))
            show("ExitPanel");
/*                  MessageBox.confirm(this, "Dinamica I", "Vuoi uscire dal programma?",
                             "*Exit*");*/
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
        else if ("Imposta parametri...".equals(what))
          { Dialog dialog=new SettingsDialog(this, settings);
            dialog.reshape(50, 10, 530, 460);
            dialog.show(true);
            dialog.move(50, 10);
            toFront();
          }
        else if ("Esegui".equals(what))
          { if (simulation==null)
              MessageBox.message(this,"Dinamica I - Esegui",
                     "Devi scegliere le condizioni iniziali\n"+
                     "cliccando su un punto dello schermo");
            else
              { show("Simulazione");
                simulationDisplay.startAnimation();
                toFront();
              }
          }
        else if ("Opzioni simulazione...".equals(what))
          { Dialog dialog=new OptionsDialog(this, options);
            dialog.reshape(50, 50, 450, 340);
            dialog.show(true);
            dialog.move(50, 50);
            toFront();
          }
        else if ("Simulazione".equals(what))
          show("Simulazione");
        else if ("Grafico...".equals(what))
          { if (simulation==null || simulation.getStepCount()==0)
              MessageBox.message(this,"Dinamica I - Grafico",
                     "Devi eseguire la simulazione prima\n"+
                     "di poter visualizzare il grafico");
            else
              { Dialog dialog=new GraphicDialog(this, simulation);
                dialog.reshape(50, 50, 550, 400);
                dialog.show(true);
                dialog.move(50, 50);
                toFront();
              }
          }
        else if ("Grafico Multiplo...".equals(what)) {
            if (simulation==null || simulation.getStepCount()==0)
              MessageBox.message(this,"Cinematica - Grafico Multiplo",
                     "Devi eseguire la simulazione prima\n"+
                     "di poter visualizzare il grafico");
            else {
                Dialog dialog=new MultiGraphicDialog(this, simulation);
                dialog.reshape(50, 50, 550, 400);
                dialog.show(true);
                dialog.move(50, 50);                
            }
        }
        else if ("Tabella".equals(what))
          { if (simulation!=null && simulation.getStepCount()>0)
              { simulationTable.update(simulation);
              }
            show("Tabella");
          }
        else if ("Moto traslatorio...".equals(what))
          { if (simulation==null || simulation.getStepCount()==0)
              MessageBox.message(this,"Dinamica I - Moti relativi",
                     "Devi eseguire la simulazione prima\n"+
                     "di poter visualizzare i moti relativi");
            else
              { Dialog dialog=new RelTransDialog(this);
                dialog.reshape(50, 10, 550, 460);
                dialog.show(true);
                dialog.move(50, 50);
                toFront();
              }
          }
        else if ("Moto rotatorio...".equals(what))
          { if (simulation==null || simulation.getStepCount()==0)
              MessageBox.message(this,"Dinamica I - Moti relativi",
                     "Devi eseguire la simulazione prima\n"+
                     "di poter visualizzare i moti relativi");
            else
              { Dialog dialog=new RelRotDialog(this);
                dialog.reshape(50, 50, 450, 350);
                dialog.show(true);
                dialog.move(50, 50);
                toFront();
              }
          }
        else if ("Aiuto".equals(what))
          help.displayHelp("help/dinam1", "dinam1");
        else if (what!=null && what instanceof Settings)
          { settings=(Settings)what;
            simulation=null;
            simulationDisplay.reset(settings);
          }
        else if (what!=null && what instanceof Options)
          { options=(Options)what;
            simulationDisplay.setOptions(options);
            toFront();     //
          }
        else if (what!=null && what instanceof Simulation)
          { simulation=(Simulation)what;
            simulationDisplay.play(simulation);
            toFront();      //
          }
        else if (what!=null && what instanceof GraphicDialog)
          { GraphicDialog gd=(GraphicDialog)what;
            simulationGraphic.setData(simulation, gd.getChoice());
            show("Grafico");
            toFront();           //
          }
        else if (what!=null && what instanceof MultiGraphicDialog) {
            MultiGraphicDialog gd=(MultiGraphicDialog)what;
            simulationMultiGraphic.setData(0,simulation,gd.getYChoice(1));
            simulationMultiGraphic.setData(1,simulation,gd.getYChoice(2));
            simulationMultiGraphic.setData(2,simulation,gd.getYChoice(3));
            simulationMultiGraphic.autoResizeItems();
            show("Grafico Multiplo");                
        }                
        else if (what!=null && what instanceof RelTransDialog)
          { RelTransDialog gd=(RelTransDialog)what;
            relGraphic.setData(simulation, gd.getVelX(), gd.getVelY(),
                                           gd.getAccX(), gd.getAccY());
            show("Rel");    
            toFront();           //
          }
        else if (what!=null && what instanceof RelRotDialog)
          { RelRotDialog gd=(RelRotDialog)what;
            relGraphic.setData(simulation, gd.getVel(), gd.getAcc());
            show("Rel");
            toFront();            //
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