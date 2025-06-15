package cinemat;

import util.*;
import numeric.*;

/**
 * A base class for the simulation of base rotational motion
 * with a relative translational motion
 * @author Pasquale Foggia
 * @version 0.99, Jan 1997
 */
public class SimulationRotTransl extends Simulation
  { Settings settings;

    public SimulationRotTransl(Settings settings)
      { this.settings=settings;
      }


    
    /**
     * Runts the simulation up to the time specified by
     * the user.
     * The values are saved at intervals specified by the user
     */    
    public synchronized void runUpTo(double endTime, double timeStep)
      { steps=(int)Math.ceil(endTime/timeStep)+1;
        int i;
        double vang=settings.vel_ang_base;
        double vx=settings.vel_rel_x;
        double vy=settings.vel_rel_y;
        double r=settings.raggio;

        t=new double[steps];
        x=new double[steps];
        y=new double[steps];

        for(i=0; i<steps; i++)
          { t[i]=i*timeStep;
            double ang=vang*t[i];
            x[i]=r*Math.cos(ang)+vx*t[i];
            y[i]=r*Math.sin(ang)+vy*t[i];
          }

      }



    /**
     * Types of output information available
     * The time is included, and should be always outpun n. 0
     */
    public synchronized int getOutputCount()
      { return 14;
      }

    /**
     * Names associated to the output information
     * The time is included, and should be always outpun n. 0
     */
    public synchronized String getOutputName(int i)
      { String name[]={ 
                        "Tempo",
                        "Posizione X",
                        "Posizione Y",
                        "Raggio",
                        "Angolo",
                        "Velocità X",
                        "Velocità Y",
                        "Velocità",
                        "Velocità angolare",
                        "Accelerazione X",
                        "Accelerazione Y",
                        "Accelerazione centripeta",
                        "Frequenza",
                        "Periodo"
                      };
        return name[i];
      }


    /**
     * Short names associated to the output information
     * The time is included, and should be always outpun n. 0
     */
    public synchronized String getOutputShortName(int i)
      { String name[]={ 
                        "t[s]",
                        "X[m]",
                        "Y[m]",
                        "R[m]",
                        "Ang[rad]",
                        "Vx[m/s]",
                        "Vy[m/s]",
                        "V[m/s]",
                        "Va[rad/s]",
                        "Ax[m/s^2]",
                        "Ay[m/s^2]",
                        "Ac[m/s^2]",
                        "Freq[Hz]",
                        "T[s]"
                      };
        return name[i];
      }

    /**
     * Value of output information
     * The time is included, and should be always outpun n. 0
     */
    public synchronized double getOutput(int i, int step)
      { 
        double vang=settings.vel_ang_base;
        double vx=settings.vel_rel_x;
        double vy=settings.vel_rel_y;
        double r=settings.raggio;
        double ang=vang*t[step];
        double sin=Math.sin(ang);
        double cos=Math.cos(ang);



        switch(i)
          { case 0:
              return t[step];
            case 1:
              return x[step];
            case 2:
              return y[step];
            case 3:
              return r;
            case 4:
              return ang;
            case 5:
              return vx-r*sin*vang;
            case 6:
              return vy+r*cos*vang;
            case 7:
              return Functions.hypot(getOutput(5,step), getOutput(6, step));
            case 8:
              return vang;
            case 9:
              return -r*cos*vang*vang;
            case 10:
              return -r*sin*vang*vang;
            case 11:
              return r*vang*vang;
            case 12:
              return Math.abs(vang/(2*Math.PI));
            case 13:
              return Math.abs(2*Math.PI/vang);
            default:
              throw new IllegalArgumentException();
          }
      }

    /**
     * Returns the vector of the indices of the most interesting outputs
     * to be displayed during the simulation
     */
    public int[] getInterestingInfo()
      { int n=10;
        int idx[]=new int[n];

        idx[0]=0;
        idx[1]=1;
        idx[2]=2;
        idx[3]=3;
        idx[4]=4;
        idx[5]=5;
        idx[6]=6;
        idx[7]=8;
        idx[8]=12;
        idx[9]=13;

        return idx;
      }



    public double getCenterX(int step)
      { if (step>=steps)
          return 0;
        double vx=settings.vel_rel_x;
        return vx*t[step];
      }

    public double getCenterY(int step)
      { if (step>=steps)
          return 0;
        double vy=settings.vel_rel_y;
        return vy*t[step];
      }


  }
