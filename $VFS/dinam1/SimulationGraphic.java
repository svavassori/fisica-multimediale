package dinam1;

import ui.*;
import util.*;
import dinam1.*;
import java.awt.*;
import ui.PrintableTextPanel;


/**
 * A canvas displaying a graph of the simulation
 */
public class SimulationGraphic extends GraphicDisplay
  { 
    public void setData(Simulation sim, int choice)
      { double x[], y[];

        int n=sim.getStepCount();
        x=new double[n];
        y=new double[n];
        int i;
        for(i=0; i<n; i++)
          { x[i]=sim.getTime(i);
            y[i]=sim.getOutput(choice, i);
          }

        setData(x, "t [s]", y, sim.getOutputShortName(choice));

      }

  }
