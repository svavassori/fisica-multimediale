package cinemat;

import ui.*;
import util.*;
import java.awt.*;

/**
 * A canvas displaying a graph of the simulation
 */
public class SimulationMultiGraphic extends MultiGraphicDisplay
  { 
    public void setData(int dispnum, Simulation sim, int xchoice, int ychoice)
      { double x[], y[];               
        int n=sim.getStepCount();
        x=new double[n];
        y=new double[n];        
        int i;
//        System.out.println("Ci arrivo "+ n+ " " + xchoice +" "+ ychoice);
        for(i=0; i<n; i++) {
            x[i]=sim.getOutput(xchoice, i);
            y[i]=sim.getOutput(ychoice, i);
        }
       getDisplay(dispnum).setData(x, sim.getOutputShortName(xchoice), 
                                   y, sim.getOutputShortName(ychoice));

      }
  }
