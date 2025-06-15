package circuiti;

import util.*;
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

    public void setData(Circuito circ, Componente com, int n1, int n2)
      { StringBuffer sb=new StringBuffer();

        // Costruisce l'intestazione
        sb.append(Format.format("^11", "t[s]"));
        int i, j;

        for(j=0; j<com.getInfoCount(); j++)
          sb.append(" "+Format.format("^11", com.getInfoShortName(j)));

        sb.append("\n");
        
        int n=circ.getStepCount();

        for(i=0; i<n; i++)
          { sb.append(Format.format_e("11.5", circ.getStepTime(i)));
            for(j=0; j<com.getInfoCount(); j++)
              { double V=circ.getV(i, n1, n2);
                double I=circ.getI(i, n1, n2);
                sb.append(" "+Format.format_e("11.5", com.getInfo(j, V, I)));
              }
            sb.append("\n");
          }
        setText(sb.toString());
        select(0,0);
      }

  }
