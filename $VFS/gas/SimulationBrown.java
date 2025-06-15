package gas;

import util.*;

/**
 * Simulazione di moto Browniano
 */
public class SimulationBrown extends Simulation
  { 

    public SimulationBrown(Settings settings)
      { super(settings);
        allocMol(1);
      }


    /**
     * Vertical side of the recipient for the gas
     */
    public double getVerticalSide()
      { return 6*getExpectedFreePath();
      }

    /**
     * Horizontal side of the recipient for the gas
     */
    public double getHorizontalSide()
      { return 8*getExpectedFreePath();
      }


    /**
     * Compute the results of the simulation
     */
    public void compute()
      { double t=0.0;
        int i;

        for(i=0; i<50; i++)
          { 
            growSegments(0, i+1);
            if (i==0)
              { x0[0][i]=(rand.nextDouble()*0.9+0.05)*getHorizontalSide();
                y0[0][i]=(rand.nextDouble()*0.9+0.05)*getVerticalSide();
              }
            else
              { x0[0][i]=x0[0][i-1]+Math.cos(dir[0][i-1])*len[0][i-1];
                y0[0][i]=y0[0][i-1]+Math.sin(dir[0][i-1])*len[0][i-1];
              }

            loop:
            while (true) 
              { double d=generateDirection();
                double s=Math.sin(d);
                double c=Math.cos(d);


                if (!((x0[0][i]<0.03*getHorizontalSide() && c<0) ||
                    (x0[0][i]>0.97*getHorizontalSide() && c>0) ||
                    (y0[0][i]<0.03*getVerticalSide() && s<0) ||
                    (y0[0][i]>0.97*getVerticalSide() && s>0)))
                  { dir[0][i]=d;
                    break loop;
                  }
              }


            vel[0][i]=generateVelocity();


            double l1=getMaxLengthBeforeSide(x0[0][i], y0[0][i], dir[0][i]);
            double l2=generateExponential(1/getExpectedFreePath());
            if (l1<l2)
              { sideHits++;
                len[0][i]=l1;
              }
            else
              { molHits++;
                len[0][i]=l2;
              }

            t0[0][i]=t;
            double dt=len[0][i]/vel[0][i];
            dur[0][i]=dt;
            t+=dt;
          }

      }

    /**
     * Get the heading for the table
     */
    public String getTableHeading()
      { String names[]={ "tratto", "x0[m]", "y0[m]", "dir[deg]",
                         "vel.[m/s]","lungh.[m]", "tempo [s]"};
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
      { return segments[0];
      }


    /**
     * Get a row of the table
     */
    public String getTableRow(int i)
      { String str="";

        str= Format.format("11.0", i+1)+" ";
        str+=Format.format_e("11.3", x0[0][i])+" ";
        str+=Format.format_e("11.3", y0[0][i])+" ";
        str+=Format.format("11.1", dir[0][i]*180/Math.PI)+" ";
        str+=Format.format("11.3", vel[0][i])+" ";
        str+=Format.format_e("11.3", len[0][i])+" ";
        str+=Format.format_e("11.3", dur[0][i]);

        return str;
      }

 

  }
