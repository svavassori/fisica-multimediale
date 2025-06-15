package cinemat;

import java.io.*;
import ui.*;
import java.awt.*;

import java.applet.*;


public class CinematAppletFrame extends Applet implements StatusDisplayer
  { ImageLoader il;
    HelpDisplayer help;
    Parameters parm;
    Panel cardsPanel;
    CardLayout cards;
    Settings settings;
    Options options;
    Simulation simulation;
    SimulationInfoDisplay simulationInfoDisplay;
    SimulationDisplay simulationDisplay;
    SimulationTable simulationTable;
    SimulationGraphic simulationGraphic;
    Exit exit;


    public void init()

      { 

        il=new AppletImageLoader(this);

        help=new AppletHelpDisplayer(this);

        parm=new AppletParameters(this);



        UserInterface.setImageLoader(il);

        UserInterface.setHelpDisplayer(help);

        //resize(512, 384);

      }



    public void start() {

        go(parm,exit);

    }



    //public CinematFrame(Parameters parm, Exit exit)
    public void go(Parameters parm, Exit exit) {
        il=UserInterface.getImageLoader();
        help=UserInterface.getHelpDisplayer();
        this.parm=parm;
        this.exit=exit;
        settings=new Settings(parm);
        options=new Options();
        simulation=null;

        setLayout (new BorderLayout());
        createToolbar();
        createPanel();
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
        tb.addTool("Tabella", il.load("icons/tabella.gif"),
                   "Visualizza tabella");
        /*tb.addTool("Uscita...", il.load("icons/exit.gif"),
                   "Esci dal programma");*/

        tb.setStatusDisplayer(this);
        add("West", tb);
      }


    void createPanel()
      { cardsPanel=new Panel();
        cards=new CardLayout();
        cardsPanel.setLayout(cards);

        Panel simulationPanel=new Panel();
        simulationPanel.setLayout(new BorderLayout());

        //simulationPanel.setLayout(new VerticalLayout());
        simulationInfoDisplay=new SimulationInfoDisplay();
        simulationPanel.add("North",simulationInfoDisplay);

        simulationDisplay=new SimulationDisplay(this, options, settings,
                                                simulationInfoDisplay);
        createSimulation();
        simulationInfoDisplay.set(simulation);
        simulationDisplay.play(simulation);



        simulationPanel.add("Center", simulationDisplay);

        simulationDisplay.resize (simulationInfoDisplay.size().width,

               size().height-simulationInfoDisplay.size().height);



        cardsPanel.add("Simulazione", simulationPanel);

        simulationTable=new SimulationTable();
        cardsPanel.add("Tabella", simulationTable);

        simulationGraphic=new SimulationGraphic();
        simulationGraphic.setStatusDisplayer(this);
        cardsPanel.add("Grafico", simulationGraphic);
                
        add("Center", cardsPanel);
      }
 
    public void show(String name) {

        cards.show(cardsPanel, name);
        cardsPanel.repaint();

    }

    public boolean action(Event evt, Object what)
      { 
        if ("Uscita...".equals(what))
          MessageBox.confirm(this, "Cinematica", "Vuoi uscire dal programma?",
                             "*Exit*");
        else if ("*Exit*".equals(what))
          { hide();
            if (simulationDisplay!=null)
              simulationDisplay.stopAnimation();
            exit.exit(this, null);
          }
        else if ("Imposta parametri...".equals(what))
          { Dialog dialog=new SettingsDialog(this, settings);
            dialog.reshape(50, 50, 500, 360);
            dialog.show(true);
            dialog.move(50, 50);
            //toFront();
          }
        else if ("Esegui".equals(what))
          { show("Simulazione");
            simulationDisplay.stopAnimation();
            simulationDisplay.startAnimation();
          }
        else if ("Opzioni simulazione...".equals(what))
          { Dialog dialog=new OptionsDialog(this, options);
            dialog.reshape(50, 50, 450, 340);
            dialog.show(true);
            dialog.move(50, 50);
            //toFront();

          }
        else if ("Simulazione".equals(what))
          show("Simulazione");
        else if ("Grafico...".equals(what))
          { if (simulation==null || simulation.getStepCount()==0)
              MessageBox.message(this,"Cinematica - Grafico",
                     "Devi eseguire la simulazione prima\n"+
                     "di poter visualizzare il grafico");
            else
              { Dialog dialog=new GraphicDialog(this, simulation);
                dialog.reshape(50, 50, 550, 400);
                dialog.show(true);
                dialog.move(50, 50);
                //toFront();

              }
          }
        else if ("Tabella".equals(what))
          { if (simulation!=null && simulation.getStepCount()>0)
              { simulationTable.update(simulation);
              }
            show("Tabella");
          }
        else if ("Aiuto".equals(what))
          help.displayHelp("help/cinemat", "cinemat");
        else if (what!=null && what instanceof Settings)
          { settings=(Settings)what;
            simulationDisplay.reset(settings);
            createSimulation();
            simulationInfoDisplay.set(simulation);
            simulationDisplay.play(simulation);
          }
        else if (what!=null && what instanceof Options)
          { options=(Options)what;
            simulationDisplay.setOptions(options);
          }
        else if (what!=null && what instanceof GraphicDialog)
          { GraphicDialog gd=(GraphicDialog)what;
            simulationGraphic.setData(simulation, 
                gd.getXChoice(), gd.getYChoice());
            show("Grafico");
          }
        
        return true;
      }

    public boolean handleEvent(Event evt)
      { if (evt.id==Event.WINDOW_DESTROY)
          MessageBox.confirm(this, "Cinematica", "Vuoi uscire dal programma?",
                                       "*Exit*");
        return super.handleEvent(evt);
      }

    public void createSimulation()
      { int base=settings.moto_base;
        int rel=settings.moto_relativo;

        if (base==Settings.TRASLATORIO &&
            (rel==Settings.NESSUNO || rel==Settings.TRASLATORIO))
          { simulation=new SimulationTransl(settings);
          } 
        else if (base==Settings.TRASLATORIO &&
                 rel==Settings.ROTATORIO)
          { simulation=new SimulationTranslRot(settings);
          } 
        else if (base==Settings.ROTATORIO &&
                 rel==Settings.TRASLATORIO)
          { simulation=new SimulationRotTransl(settings);
          } 
        else if (base==Settings.ROTATORIO &&
            (rel==Settings.NESSUNO || rel==Settings.ROTATORIO))
          { simulation=new SimulationRot(settings);
          } 
        else
          MessageBox.alert(this,"Cinematica - Esegui",
                           "Tipo di moto non implementato");

      }

 }

