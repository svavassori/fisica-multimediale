package elemag;

import numeric.*;
import util.*;

/**
 * Calcola i dati per l'animazione del moto della particella
 */
public class Animation implements ODE
  { SimulationDisplay simulation;
    double massa, carica;
    double x0, y0, vx0, vy0;
    double timeScale;
    double t1=0, t2=0;
    double t[], x[], y[], vx[], vy[], ax[], ay[];
    int steps=0;
    ODESolver solver=null;
    ODEInterpolator interpolator=null;


    public Animation(SimulationDisplay simulation,
                     double massa, double carica,
                     double x, double y,
                     double vx, double vy,
                     double timeScale)
      { this.simulation=simulation;
        this.massa=massa;
        this.carica=carica;
        this.x0=x;
        this.y0=y;
        this.vx0=vx;
        this.vy0=vy;
        this.timeScale=timeScale;
      }

    public synchronized double getStartTime()
      { return t1;
      }

    public synchronized double getStopTime()
      { return t2;
      }

    /**
     * Returns the number of saved steps
     */
    public synchronized int getStepCount()
      { return steps;
      }

    public synchronized double getTime(int step)
      { if (steps>0)
          return t[step];
        else
          return t1;
      }

    public synchronized double getX(int step)
      { if (steps>0)
          return x[step];
        else
          return x0; 
      }

    public synchronized double getY(int step)
      { if (steps>0)
          return y[step];
        else
          return y0;
      }

    public double getTimeScale()
      { return timeScale;
      }

    public void runUpTo(double endTime, double timeStep)
      { 

        if (endTime<t2)
          throw new IllegalArgumentException();


        t1=t2;
        if (steps>0)
        { x0=x[steps-1];
          y0=y[steps-1];
          vx0=vx[steps-1];
          vy0=vy[steps-1];
        }

        steps=(int)Math.ceil((endTime-t1)/timeStep)+1;
        t2=t1+(steps-1)*timeStep;
        t=new double[steps];
        x=new double[steps];
        y=new double[steps];
        vx=new double[steps];
        vy=new double[steps];
        ax=new double[steps];
        ay=new double[steps];

        int i;
        for(i=0; i<steps; i++)
          t[i]=t1+i*timeStep;

        if (solver==null)
              { double yy[]=new double[4];
                yy[0]=x0;
                yy[1]=y0;
                yy[2]=vx0;
                yy[3]=vy0;
                solver=new ODESolver(this, t1, yy, timeStep/4, 1e-4);
              }
        solver.setTolerant(true);
        solver.setMaxStep(timeStep/4);
        solver.setMinStep(timeStep/150);
        solver.setMaxError(1e-4+
                       1e-4*Math.max( Functions.hypot(x0, y0),
                                       Functions.hypot(vx0, vy0) ));
        if (interpolator==null)
              interpolator=new ODEInterpolator(solver);

        double yy[]=new double[4];

        for(i=0; i<steps; i++)
              { int status=interpolator.interpolate(t[i], yy);

                if (status!=ODEInterpolator.OK)
                  throw new RuntimeException(
                          "ODEInterpolator error: status=="+status);

                x[i]=yy[0];
                y[i]=yy[1];
                vx[i]=yy[2];
                vy[i]=yy[3];
                computeAcceleration(x[i], y[i], vx[i], vy[i], yy);
                ax[i]=yy[0];
                ay[i]=yy[1];
              }

                
      }

    /**
     * Calcola l'accelerazione
     */
    void computeAcceleration(double x, double y, double vx, double vy,
                             double a[])
      { double E[]=simulation.computeE(x, y);
        double B=simulation.getInduzioneMagnetica();
        double fx=carica*(E[0]+vy*B);
        double fy=carica*(E[1]-vx*B);
        a[0]=fx/massa;
        a[1]=fy/massa;
      }

    public int getDimension()
      { return 4;
      }

    public void derive(double t, double s[], double s1[])
      { computeAcceleration(s[0], s[1], s[2], s[3], s1);
        s1[2]=s1[0];
        s1[3]=s1[1];
        s1[0]=s[2];
        s1[1]=s[3];
      }


    public String getTableHeading()
      { String s[]={"t[s]", "x[m]", "y [m]", 
                    "Vx[m/s]", "Vy[m/s]",
                    "Ax[m/s^2]", "Ay[m/s^2]"};
        int i;
        String str="";
        for(i=0; i<s.length; i++)
          str+=Format.format("^11", s[i])+" ";
        return str;
      }

    public String getTableRow(int i)
      { String str="";

        str+=Format.format_e("11.3", t[i])+" ";
        str+=Format.format_e("11.3", x[i])+" ";
        str+=Format.format_e("11.3", y[i])+" ";
        str+=Format.format_e("11.3", vx[i])+" ";
        str+=Format.format_e("11.3", vy[i])+" ";
        str+=Format.format_e("11.3", ax[i])+" ";
        str+=Format.format_e("11.3", ay[i]);

        return str;
      }

  }
