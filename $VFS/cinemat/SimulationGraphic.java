package cinemat;

import ui.*;
import util.*;
import java.awt.*;

/**
 * A canvas displaying a graph of the simulation
 */
public class SimulationGraphic extends GraphicDisplay
  { 
    public void setData(Simulation sim, int xchoice, int ychoice)
      { double x[], y[];
        int n=sim.getStepCount();
        x=new double[n];
        y=new double[n];
        int i;
        for(i=0; i<n; i++)
          { x[i]=sim.getOutput(xchoice, i);
            y[i]=sim.getOutput(ychoice, i);
          }

        setData(x, sim.getOutputShortName(xchoice), 
                y, sim.getOutputShortName(ychoice));

      }
  }
