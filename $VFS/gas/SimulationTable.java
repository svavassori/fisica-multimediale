package gas;

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

    public void update(Simulation sim)
      { StringBuffer sb=new StringBuffer();
        sb.append(sim.getTableHeading()+"\n");
        int i;
        int n=sim.getTableRowCount();
        for(i=0; i<n; i++)
          sb.append(sim.getTableRow(i)+"\n");
        setText(sb.toString());
        select(0,0);
      }

  }
