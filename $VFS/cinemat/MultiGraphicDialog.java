package cinemat;


import ui.*;
import util.*;
import java.awt.*;


/**
 * A dialog box to input the desired type of graphic
 * @author Pasquale Foggia
 * @version 0.99, Dec 1997
 */
public class MultiGraphicDialog extends TrackedDialog
  {     
    Component target;
    HelpDisplayer help;
    static Font font_small=new Font("TimesRoman", Font.PLAIN, 12);
    static Font font_medium=new Font("TimesRoman", Font.PLAIN, 14);
    static Font font_large=new Font("TimesRoman", Font.BOLD, 16);
    Simulation sim;

    List sel;


    
    // the variables involved in the choice of Multigraphs
    // edit num_of_multiple_graphics to estabilish how many Multigraphs
    // the user can choose from.

        int num_of_multiple_graphics; // numero di possibili multigraphs
        int num_functions;  // numero di funzioni da visualizzare

        String multiple_graphs_names[];
        String multiple_graphs_choices[][];
                                
    

    /**
     * Create a new. If the user confirms
     * the changes, the target component will be notified with
     * an action event having the GraphicDialog  object as the argument
     */
    public MultiGraphicDialog(Component target, 
                         Simulation simulation)
      { super(UserInterface.getDummyFrame(), 
              "Grandezza da visualizzare", true);
        sim=simulation;
        this.target=target;
        int FS=((CinematFrame)target).settings.ForcedSimulation;
        if (FS==1)
        {setTitle("Grandezza da visualizzare - Moto Rettilineo Uniforme");}
        else if (FS==2){setTitle("Grandezza da visualizzare - Moto Rettilineo Uniformemente Accelerato");}
        else if (FS==3){setTitle("Grandezza da visualizzare - Moto Circolare");}

        else if (FS==4){setTitle("Grandezza da visualizzare - Moto Armonico");}


        int base=((CinematFrame)target).settings.moto_base;
        int rel=((CinematFrame)target).settings.moto_relativo;
        double acc=((CinematFrame)target).settings.acc_base;
        boolean ZeroTrasc=(((CinematFrame)target).settings.vel_rel_x==0) && (((CinematFrame)target).settings.vel_rel_y==0);

        // accelerazione nulla , assenza di trascinamento
        if ((base==Settings.TRASLATORIO) &&
            ((rel==Settings.NESSUNO) || ((rel==Settings.TRASLATORIO)&&(ZeroTrasc))) &&
            (acc==0))
                {
                // System.out.println("Uniforme");
                 num_of_multiple_graphics=1;
                 num_functions=2;
                 multiple_graphs_names=new String[num_of_multiple_graphics];
                 multiple_graphs_choices= new String[1][3];
                 multiple_graphs_names[0]="x, Vx rispetto al tempo";

                 multiple_graphs_choices[0][0]="Tempo";
                 multiple_graphs_choices[0][1]="Posizione X";
                 multiple_graphs_choices[0][2]="Velocità X";

                }
        // accelerazione costante , assenza di trascinamennto
        else if ((base==Settings.TRASLATORIO) &&
                 ((rel==Settings.NESSUNO) || ((rel==Settings.TRASLATORIO) && (ZeroTrasc))) &&
                 (acc!=0))
                {
               //  System.out.println("Uniformemente Acc");

                 num_of_multiple_graphics=1;
                 num_functions=3;
                 multiple_graphs_names=new String[num_of_multiple_graphics];
                 multiple_graphs_choices= new String[1][4];
                 multiple_graphs_names[0]="x, Vx, Ax rispetto al tempo";

                 multiple_graphs_choices[0][0]="Tempo";
                 multiple_graphs_choices[0][1]="Posizione X";
                 multiple_graphs_choices[0][2]="Velocità X";
                 multiple_graphs_choices[0][3]="Accelerazione X";

                }

        // accelerazione nulla, presenza di trascinamento

        else if ((base==Settings.TRASLATORIO) &&
                 ((rel==Settings.TRASLATORIO) && (ZeroTrasc==false) ) &&
                 (acc==0))
                {
                // System.out.println("Uniforme+trasc");

                 num_of_multiple_graphics=2;
                 num_functions=2;
                 multiple_graphs_names=new String[num_of_multiple_graphics];
                 multiple_graphs_choices= new String[2][3];

                 multiple_graphs_names[0]="x, Vx rispetto al tempo";
                 multiple_graphs_names[1]="y, Vy rispetto al tempo";

                 multiple_graphs_choices[0][0]="Tempo";
                 multiple_graphs_choices[0][1]="Posizione X";
                 multiple_graphs_choices[0][2]="Velocità X";

                 multiple_graphs_choices[1][0]="Tempo";
                 multiple_graphs_choices[1][1]="Posizione Y";
                 multiple_graphs_choices[1][2]="Velocità Y";

                }

        else
                {
                // System.out.println("Totale");

                 num_of_multiple_graphics=2;
                 num_functions=3;
                 multiple_graphs_names=new String[num_of_multiple_graphics];
                 multiple_graphs_choices= new String[2][4];

                 multiple_graphs_names[0]="x, Vx, Ax rispetto al tempo";
                 multiple_graphs_names[1]="y, Vy, Ay rispetto al tempo";

                 multiple_graphs_choices[0][0]="Tempo";
                 multiple_graphs_choices[0][1]="Posizione X";
                 multiple_graphs_choices[0][2]="Velocità X";
                 multiple_graphs_choices[0][3]="Accelerazione X";

                 multiple_graphs_choices[1][0]="Tempo";
                 multiple_graphs_choices[1][1]="Posizione Y";
                 multiple_graphs_choices[1][2]="Velocità Y";
                 multiple_graphs_choices[1][3]="Accelerazione Y";

                }
        help=UserInterface.getHelpDisplayer();

        setLayout(new BorderLayout());

        // Crea il pannello con i choicebuttons
        Panel pan=new Panel();
        pan.setLayout(new VerticalLayout(VerticalLayout.LEFT));
        pan.setFont(font_large);

        Label lab=new Label("Grafici multipli");
        pan.add("", lab);
        sel=new AutoDoubleClickList();
        pan.add("", sel);

        int i;
        
        for(i=0; i<num_of_multiple_graphics; i++) {
                    String s=multiple_graphs_names[i];
            sel.addItem(s);
        }

        sel.select(0);
        add("Center", pan);

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
        doLayout();
      }

    synchronized public boolean action(Event evt, Object what)
      { if ("OK".equals(what))
          { dispose();
            target.deliverEvent(new Event(target, Event.ACTION_EVENT, this));
          }
        else if ("Annulla".equals(what))
          { dispose();
          }
        else if ("Aiuto".equals(what))
          { help.displayHelp("help/cinemat", "graphsel");
          }

        return true;
      }

    synchronized public boolean handleEvent(Event evt)
      { if (evt.id == Event.WINDOW_DESTROY)
          dispose();

        return super.handleEvent(evt);
      }
    
    public int getYChoice(int graph_num) {


//    System.out.println("Saverio:" + graph_num + "-" +sel.getSelectedIndex() +"-" + multiple_graphs_choices[sel.getSelectedIndex()][graph_num]+ "-" );

    return sim.searchOutputName(multiple_graphs_choices[sel.getSelectedIndex()][graph_num]);

    }

    public int getXChoice() {
        return sim.searchOutputName(multiple_graphs_choices[sel.getSelectedIndex()][0]);
        // always return time
      }
    public int getNum_of_Functions() {
      return num_functions; 
      }


}
