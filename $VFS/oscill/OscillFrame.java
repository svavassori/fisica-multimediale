package oscill;

import java.io.*;

import ui.*;
import java.awt.*;
import util.Preloader;


public class OscillFrame extends TrackedFrame implements StatusDisplayer
  { ImageLoader il;
    HelpDisplayer help;
    Parameters parm;
    Panel cardsPanel;
    CardLayout cards;
    Settings settings;
    Label info_text;
    PaintableCanvas legenda;
    SimulationDisplay simulationDisplay;
    Lissajous lissajous;
    GraphicSettings graphicSettings;
    Graphic graphic;
    Exit exit;

    public OscillFrame() {
        super("Fenomeni oscillatori");
    }
    
    public void go (Parameters parm, Exit exit) { 
        this.exit=exit;
        il=UserInterface.getImageLoader();
        help=UserInterface.getHelpDisplayer();
        this.parm=parm;
        settings=new Settings(parm);
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
        m.add("Grafico...");
        m.add("Figure di Lissajous");
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
        tb.addTool("Grafico...", il.load("icons/onde.gif"),
                   "Visualizza grafico");
        tb.addTool("Figure di Lissajous", il.load("icons/lissaj.gif"),
                   "Visualizza figure di Lissajous");
        tb.addTool("Uscita...", il.load("icons/exit.gif"),
                   "Esci dal programma");

        tb.setStatusDisplayer(this);
        add("West", tb);
      }


    void createPanel()
      { 
        Panel panel=new Panel();
        panel.setLayout(new BorderLayout());

        legenda=new PaintableCanvas();
        legenda.setBackground(Color.white);
        legenda.setFont(new Font("Helvetica", Font.BOLD, 12));
        FontMetrics fm=legenda.getFontMetrics(legenda.getFont());
        Dimension d=new Dimension(100, fm.getHeight()*4+10);
        panel.add("North", new SizeConstraint(legenda, d));

        cardsPanel=new Panel();
        cards=new CardLayout();
        cardsPanel.setLayout(cards);

        graphicSettings=new GraphicSettings();

        graphic=new Graphic(settings, graphicSettings, legenda, this);
        cardsPanel.add("Grafico", graphic);
        simulationDisplay=graphic;

        lissajous=new Lissajous(settings, legenda, this);
        cardsPanel.add("Lissajous", lissajous);

        cardsPanel.add("ExitPanel", new ExitPanel("*Exit*","*CancelExit*"));
        
        panel.add("Center", cardsPanel);
        
        add("Center", panel);

        show("Grafico");
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
        simulationDisplay.stopAnimation();
        if (name.equals("Lissajous"))
          { simulationDisplay=lissajous;
            legenda.setPainter(lissajous);
          }
        if (name.equals("Grafico"))
          { simulationDisplay=graphic;
            legenda.setPainter(graphic);
          }
        cardsPanel.repaint();
        legenda.repaint();
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
        else if ("*CancelExit*".equals(what)) {
            show("Grafico");
        }
        else if ("*Exit*".equals(what))
          { hide();
            if (simulationDisplay!=null)
              simulationDisplay.stopAnimation();
           //salva su file C://Simul il numero 1
            salva();
            show("Grafico");

            exit.exit(this, null);
          }
        else if ("Imposta parametri...".equals(what))
          { 
            Dialog dialog=new SettingsDialog(this, settings);
            dialog.reshape(50, 50, 600, 360);
            dialog.show(true);
            dialog.move(50, 50);
            toFront();
          }
        else if ("Esegui".equals(what))
          { 
            simulationDisplay.stopAnimation();
            simulationDisplay.startAnimation();
          }
        else if ("Grafico...".equals(what))
          { Dialog dialog=new GraphicDialog(this, graphicSettings);
            dialog.reshape(50, 50, 400, 300);
            dialog.show(true);
            dialog.move(50, 50);
            toFront();
          }
        else if ("Figure di Lissajous".equals(what))
          { show("Lissajous");
          }
        else if ("Aiuto".equals(what))
          help.displayHelp("help/oscill", "oscill");
        else if (what!=null && what instanceof Settings)
          { settings=(Settings)what;
            lissajous.set(settings);
            graphic.set(settings);
          }
        else if (what!=null && what instanceof GraphicSettings)
          { graphicSettings=(GraphicSettings)what;
            graphic.set(graphicSettings);
            show("Grafico");
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
