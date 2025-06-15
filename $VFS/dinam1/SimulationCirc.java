package dinam1;

import dinam1.*;
import numeric.*;


/**
 * Simulazione del moto con vincolo circolare
 * @author Pasquale Foggia
 * @version 0.99, Dec 1997
 */
public class SimulationCirc extends Simulation implements ODE
  { 
    double p[], // Posizione angolare
           v[], // Velocita' angolare
           a[]; // Accelerazione angolare

    double mass, p0, v0, p00, v00;
    ODESolver solver=null;
    ODEInterpolator interpolator=null;
    Settings settings;

    /**
     * @param p0  Posizione angolare iniziale
     * @param v0  Velocita' angolare iniziale
     */
    public SimulationCirc(Settings settings, 
                          double mass,
                          double p0, double v0)  
      { this.settings=settings;
        this.mass=mass;
        this.p0=p0;
        this.v0=v0;
        this.p00=p0;
        this.v00=v0;

        double ang=settings.angolo*Math.PI/180;

        t1=0;
        t2=0;


        x=new double[1];
        x[0]=settings.raggio*Math.cos(p0);
        y=new double[1];
        y[0]=settings.raggio*Math.sin(p0);
      }


   public double getMass()
      { return mass;
      }

   public double getInitialVel()
      { return v00;
      }



   public int getOutputCount()
      { return 17;
      }

    public String getOutputName(int i)
      { final String names[]= { 
                          "Posizione angolare (gradi)",
                          "Posizione angolare (rad)",
                          "Posizione X",
                          "Posizione Y",
                          "Velocità tangenziale",
                          "Velocità X",
                          "Velocità Y",
                          "Velocità angolare",
                          "Accelerazione",
                          "Accelerazione tangenziale",
                          "Accelerazione radiale",
                          "Accelerazione X",
                          "Accelerazione Y",
                          "Accelerazione angolare",
                          "Energia cinetica",
                          "Energia potenziale",
                          "Energia"
                        };
        return names[i];
      }

    public String getOutputShortName(int i)
      { final String names[]= { 
                          "Ang[deg]",
                          "Ang[rad]",
                          "X[m]",
                          "Y[m]",
                          "Vt[m/s]",
                          "Vx[m/s]",
                          "Vy[m/s]",
                          "Vang[rad/s]",
                          "A[m/s^2]",
                          "At[m/s^2]",
                          "Ar[m/s^2]",
                          "Ax[m/s^2]",
                          "Ay[m/s^2]",
                          "Aa[rad/s^2]",
                          "Ek[J]",
                          "Ep[J]",
                          "E[J]"
                        };
         return names[i];
      }

    public double getOutput(int i, int step)
      { double ang=p[step];
        double s=Math.sin(ang);
        double c=Math.cos(ang);
        double r=settings.raggio;

        switch(i)
        { case 0:
            return p[step]*180/Math.PI;
          case 1:
            return p[step];
          case 2:
            return x[step];
          case 3:
            return y[step];
          case 4:
            return v[step]*r;
          case 5:
            return -v[step]*r*s;
          case 6:
            return v[step]*r*c;
          case 7:
            return v[step];
          case 8:
            return Functions.hypot(getOutput(9, step), getOutput(10, step));
          case 9:
            return a[step]*r;
          case 10:
            return -Functions.sqr(v[step])*r;
          case 11:
            return -r*(c*Functions.sqr(v[step])+s*a[step]);
          case 12:
            return -r*(s*Functions.sqr(v[step])-c*a[step]);
          case 13:
            return a[step];
          case 14:
            return 0.5*mass*Functions.sqr(v[step]*r);
          case 15:
            return -settings.fx*x[step]-settings.fy*y[step];
          case 16:
            return getOutput(14, step)+getOutput(15, step);
          default:
            throw new IllegalArgumentException();
        }

      }


    public void runUpTo(double endTime, double timeStep)
      { if (endTime<t2)
          throw new IllegalArgumentException();


        t1=t2;
        if (steps>0)
        { p0=p[steps-1];
          v0=v[steps-1];
        }

        steps=(int)Math.ceil((endTime-t1)/timeStep)+1;
        t2=t1+(steps-1)*timeStep;
        t=new double[steps];
        x=new double[steps];
        y=new double[steps];
        p=new double[steps];
        v=new double[steps];
        a=new double[steps];

        int i;
        for(i=0; i<steps; i++)
          t[i]=t1+i*timeStep;

        if (solver==null)
              { double yy[]=new double[getDimension()];
                yy[0]=p0;
                yy[1]=v0;
                solver=new ODESolver(this, t1, yy, timeStep/4, 1e-5);
                solver.setTolerant(true);
              }
        solver.setMaxStep(timeStep/4);
        solver.setMinStep(timeStep/1000);
        solver.setMaxError(1e-4+
                       1e-3*Math.max( Math.abs(p0),
                                      Math.abs(v0) ));
        if (interpolator==null)
              interpolator=new ODEInterpolator(solver);

        double yy[]=new double[getDimension()];

        for(i=0; i<steps; i++)
              { 

                int status=interpolator.interpolate(t[i], yy);

                if (status!=ODEInterpolator.OK)
                  throw new RuntimeException(
                          "ODEInterpolator error: status=="+status);

                
                p[i]=yy[0];
                x[i]=Math.cos(p[i])*settings.raggio;
                y[i]=Math.sin(p[i])*settings.raggio;
                v[i]=yy[1];
                a[i]=computeAcceleration(p[i], v[i]);
              }

                
      }



    public int getDimension()
      { return 2;
      }

    public void derive(double t, double yy[], double yy1[])
      { yy1[0]=yy[1];
        yy1[1]=computeAcceleration(yy[0], yy[1]);
      }

    public double computeAcceleration(double p, double v)
      { double s=Math.sin(p);
        double c=Math.cos(p);
        double r=settings.raggio;

        double ft=-s*settings.fx+c*settings.fy;

        double vv=v*r;

        ft -= vv*settings.resistenza_laminare +
              Functions.sign(vv)*vv*vv*settings.resistenza_turbolenta;

        double fn=settings.fx*c+settings.fy*s;

        fn+=mass*v*v*r;

        double attr;

        if (v!=0)
          attr=-Functions.sign(v)*Math.abs(fn)*
                     settings.attrito_coulombiano;
        else
          attr=-Functions.sign(ft)*Math.abs(fn)*
                     settings.attrito_coulombiano;

        // Per rendere continua la funzione
        double base= (ft+attr)/mass/r;
        if ( Functions.sign(ft+attr)!=Functions.sign(ft) &&
             Math.abs(base)>Math.abs(v*1000))
              return -v*1000;
        else
              return base;
        
        
      }


  }
