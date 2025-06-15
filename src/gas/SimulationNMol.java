package gas;

import util.*;

/**
 * Simulazione del moto di N molecole
 */
public class SimulationNMol extends Simulation
  { double gamma;
    double interval;

    public SimulationNMol(Settings settings)
      { super(settings);
        allocMol(Settings.N);

        gamma=1/Math.sqrt(300/1e5);
        interval=8e-3;
      }


    /**
     * Vertical side of the recipient for the gas
     */
    public double getVerticalSide()
      { return gamma*Math.sqrt(t/p);
      }

    /**
     * Horizontal side of the recipient for the gas
     */
    public double getHorizontalSide()
      { return 1.3*getVerticalSide();
      }

    /**
     * Compute the results of the simulation 
     */
    public void compute()
      { int m;

        for(m=0; m<Settings.N; m++)
          compute(m);
      }

    /**
     * Compute the results of the simulation for the molecule m
     */
    void compute(int m)
      { double t=0;
        int i=0;

        while(interval-t>=interval*1e-3)
          { 
            growSegments(m, i+1);
            if (i==0)
              { x0[m][i]=(rand.nextDouble()*0.9+0.05)*getHorizontalSide();
                y0[m][i]=(rand.nextDouble()*0.9+0.05)*getVerticalSide();
                vel[m][i]=generateVelocity();
                dir[m][i]=generateDirection();
              }
            else
              { x0[m][i]=x0[m][i-1]+Math.cos(dir[m][i-1])*len[m][i-1];
                y0[m][i]=y0[m][i-1]+Math.sin(dir[m][i-1])*len[m][i-1];
                vel[m][i]=vel[0][0];
                dir[m][i]=reflect(dir[m][i-1], x0[m][i], y0[m][i]);
              }


            double l=getMaxLengthBeforeSide(x0[m][i], y0[m][i], dir[m][i]);
            double dt=l/vel[m][i];
            if (t+dt<=interval)
              sideHits++;
            else
              { l*=(interval-t)/dt;
                dt=interval-t;
              }
            len[m][i]=l;
            t0[m][i]=t;
            dur[m][i]=dt;
            t+=dt;
            i++;
          }

      }

    /**
     * Calcola l'angolo dopo l'urto con una parete
     */
    double reflect(double dir, double x, double y)
      { double w=getHorizontalSide();
        double h=getVerticalSide();
        double eps=1e-4;

        double sin=Math.sin(dir);
        double cos=Math.cos(dir);
        if (x<eps*w)
          cos=Math.abs(cos);
        else if (x>(1-eps)*w)
          cos=-Math.abs(cos);
        if (y<eps*h)
          sin=Math.abs(sin);
        else if (y>(1-eps)*h)
          sin=-Math.abs(sin);

        return Math.atan2(sin, cos);

      }

    /**
     * Get the heading for the table
     */
    public String getTableHeading()
      { String names[]={ "molecola", "x0[m]", "y0[m]", "dir[deg]",
                         "vel.[m/s]"};
        StringBuffer sb=new StringBuffer();
        int i;
        for(i=0; i<names.length; i++)
          { if (i>0)
              sb.append(" ");
            sb.append(Format.format("^11", names[i]));
          }

        return sb.toString();

      }

    /**
     * Get the number of rows in the table
     */
    public int getTableRowCount()
      { return molNumber;
      }

    /**
     * Get a row of the table
     */
    public String getTableRow(int i)
      { String str="";

        str= Format.format("11.0", i+1)+" ";
        if (segments[i]>0)
          { str+=Format.format_e("11.3", x0[i][0])+" ";
            str+=Format.format_e("11.3", y0[i][0])+" ";
            str+=Format.format("11.1", dir[i][0]*180/Math.PI)+" ";
            str+=Format.format("11.3", vel[i][0]);
          }

        return str;
      }



  }
