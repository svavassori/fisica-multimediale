package elemag;

import java.awt.*;
import ui.PrintableTextPanel;

/**
 * A table containing the simulation output
 * @author P. Foggia
 * @version 0.99, Dec 1997
 */
public class SimulationTable extends PrintableTextPanel
  { 
    public SimulationTable()
      { setEditable(false);
        setText("Esegui una simulazione prima di\n"+
                "esaminare la tabella");
        Font font=new Font("Courier", Font.PLAIN, 12);
        setFont(font);
      }

    public void update(Animation sim)
      { if (sim==null)
          return;
        StringBuffer sb=new StringBuffer();
        sb.append(sim.getTableHeading()+"\n");
        int i;
        int step=1;
        int n=sim.getStepCount();
        if (n>1200)
          step=16;
        else if (n>600)
          step=8;
        else if (n>300)
          step=4;
        else if (n>150)
          step=2;
        for(i=0; i<n; i+=step)
          sb.append(sim.getTableRow(i)+"\n");
        setText(sb.toString());
        select(0,0);
      }

  }
