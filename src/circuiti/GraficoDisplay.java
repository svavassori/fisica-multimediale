package circuiti;

import ui.*;

/**
 * Visualizza i grafici relativi alle grandezze di un componente
 */
public class GraficoDisplay extends Graphic2Display
  { public void setData(Circuito circ, Componente com, int n1, int n2,
                        int info1, int info2)
      { int n=circ.getStepCount();
        double x[]=new double[n];
        double y[]=new double[n];
        double z[]=null;
        if (info2>=0)
          z=new double[n];
        int i;
        for(i=0; i<n; i++)
          { x[i]=circ.getStepTime(i);
            double V=circ.getV(i, n1, n2);
            double I=circ.getI(i, n1, n2);
            y[i]=com.getInfo(info1, V, I);
            if (info2>=0)
              z[i]=com.getInfo(info2, V, I);
          }

        if (info2<0)
          setData(x, "t[s]", y, com.getInfoShortName(info1));
        else
          setData(x, "t[s]", y, com.getInfoShortName(info1),
                             z, com.getInfoShortName(info2));

      }
  }
