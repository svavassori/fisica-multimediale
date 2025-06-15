

package dinam2;

import dinam2.*;
import numeric.*;
import util.*;


/**
 * Simulazione del moto in un campo gravitazionale
 * @author Pasquale Foggia
 * @modified Saverio De Vito
 * @version 1.00, Mar 1999
 */
public class SimulationGrav extends Simulation implements ODE
  { 
    static final double space_scale=1e6;
    static final double G=6.66E-11;
    static final double R=1;

    double vx[], vy[];
    double ax[], ay[];

    double mass, x0, y0, vx0, vy0, x00, y00, vx00, vy00;
    ODESolver solver=null;
    ODEInterpolator interpolator=null;
    Settings settings;
    //boolean Approx=false;
    public SimulationGrav(Settings settings, 
                          double mass,
                          double x0, double y0, 
                          double vx0, double vy0)
      { this.settings=settings;
        this.mass=mass;
        this.x0=x0;
        this.y0=y0;
        this.vx0=vx0;
        this.vy0=vy0;
        this.x00=x0;
        this.y00=y0;
        this.vx00=vx0;
        this.vy00=vy0;

        t1=0;
        t2=0;


        x=new double[1];
        x[0]=x0;
        y=new double[1];
        y[0]=y0;
      }


   public double getMass()
      { return mass;
      }

   public double getInitialVelX()
      { return vx00;
      }

   public double getInitialVelY()
      { return vy00;
      }


    public int getOutputCount()
      { // return 11;
        return 10;
      }

    public String getOutputName(int i)
      { final String names[]= { "Posizione X",
                          "Posizione Y",
                          "Velocità",
                          "Velocità X",
                          "Velocità Y",
                          "Accelerazione",
                          "Accelerazione X",
                          "Accelerazione Y",
                          "Energia cinetica",
                          "Energia potenziale",
                          "Energia"
                        };
        return names[i];
      }

    public String getOutputShortName(int i)
      { final String names[]= { "X[m]",
                          "Y[m]",
                          "V[m/s]",
                          "Vx[m/s]",
                          "Vy[m/s]",
                          "A[m/s^2]",
                          "Ax[m/s^2]",
                          "Ay[m/s^2]",
                          "Ek[J]",
                          "Ep[J]",
                          "E[J]"
                        };
         return names[i];
      }

    public double getOutput(int i, int step)
      { switch(i)
        { case 0:
            return x[step]*space_scale;
          case 1:
            return y[step]*space_scale;
          case 2:
            return Functions.hypot(vx[step], vy[step]);
          case 3:
            return vx[step];
          case 4:
            return vy[step];
          case 5:
            return Functions.hypot(ax[step], ay[step]);
          case 6:
            return ax[step];
          case 7:
            return ay[step];
          case 8:
            return 0.5*mass*Functions.hypot2(vx[step], vy[step]);
          case 9:
            return Epot(x[step], y[step]);
          case 10:
            return getOutput(8, step)+getOutput(9, step);
          default:
            throw new IllegalArgumentException();
        }

      }



    public void runUpTo(double endTime, double timeStep)
      { if (endTime<t2)
          throw new IllegalArgumentException();

        
        t1=t2;
 
        if ((steps>0) && (Approx==false))
        { 
          x0=x[steps-1];
          y0=y[steps-1];
          vx0=vx[steps-1];
          vy0=vy[steps-1];
          
        } 
        

        steps=(int)Math.ceil((endTime-t1)/timeStep)+1;
        //System.out.println("New"+steps+" "+timeStep+" "+t2+" "+t1);
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

        if ((solver==null) || (Approx))
              { //System.out.println("nuovo nuovo");
                double yy[]=new double[4];
                yy[0]=x0;
                yy[1]=y0;
                yy[2]=vx0;
                yy[3]=vy0;
                solver=new ODESolver(this, t1, yy, timeStep/5, 1e-5);
                solver.setTolerant(true);
              }
        solver.setMaxStep(timeStep/3); // ex /5
        solver.setMinStep(timeStep/30000);
        solver.setMaxError(1e-5+
                       1e-7*Math.max( Functions.hypot(x0, y0),
                                      Functions.hypot(vx0, vy0) ));
        if ((interpolator==null) || (Approx))
              interpolator=new ODEInterpolator(solver);

        double yy[]=new double[4];

        for(i=0; i<steps; i++)
              { int status=interpolator.interpolate(t[i], yy);

                if (status!=ODEInterpolator.OK)
                  throw new RuntimeException(
                          "ODEInterpolator error: status=="+status+" Step:"+steps);

                x[i]=yy[0];
                y[i]=yy[1];
                vx[i]=yy[2];
                vy[i]=yy[3];
                computeAcceleration(x[i], y[i], vx[i], vy[i], yy);
                ax[i]=yy[0];
                ay[i]=yy[1];
              }

            if(Approx) {t1=0;t2=0; }
               }



    public int getDimension()
      { return 4;
      }

    public void derive(double t, double yy[], double yy1[])
      { yy1[0]=yy[2]/space_scale;
        yy1[1]=yy[3]/space_scale;
        double zz[]={0,0};
        computeAcceleration(yy[0], yy[1], yy[2], yy[3], zz);
        yy1[2]=zz[0];
        yy1[3]=zz[1];
      }

    public void computeAcceleration(double x, double y, 
                                    double vx, double vy, double a[])
      { double r=Functions.hypot(x, y);
        double f, fx, fy; // Forze per unita' di massa
      //System.out.println("Aio:"+r+" "+R);
      if (r>R)
          f=G*settings.massa/
                  (Functions.sqr(r)*Functions.sqr(space_scale));
     else 
          { Approx=true;

            f=G*settings.massa*(r/R)/
                  (Functions.sqr(R)*Functions.sqr(space_scale));
          }
       if (r>0)
          { fx=-f*x/r;
            fy=-f*y/r;
          }
        else
          { fx=fy=0;
          }

        final double alfa=0.005;
        final double beta=2000;
        double E0=Epot(x00, y00)+0.5*mass*Functions.hypot2(vx00, vy00);
        double E=Epot(x, y)+0.5*mass*Functions.hypot2(vx, vy);
        double q=(E-E0)/(Math.abs(E)+Math.abs(E0));

        if (q<-alfa)
          q=-alfa;
        else if (q>alfa)
          q=alfa;
        q = Functions.sign(q)*q*q/alfa;
        
        double correction=-q*beta*f;
        double correction_x, correction_y;
        double v=Functions.hypot(vx, vy);
        if (v>0)
          { correction_x=correction*vx/v;
            correction_y=correction*vy/v;
          }
        else if (f>0)
          { correction_x=correction*fx/f;
            correction_y=correction*fx/f;
          }
        else
          { correction_x=0;
            correction_y=0;
          }


        a[0]=fx+correction_x;
        a[1]=fy+correction_y;
      }



    public double goodTimeInterval()
      { double a, E;
        double M=settings.massa;
        final double maxA=1000*space_scale;

        E=Epot(x00, y00)+0.5*mass*Functions.hypot2(vx00, vy00);

        //System.out.println("r:"+Functions.hypot(x00,y00)+" G:"+G+" M:"+settings.massa+" m:"+mass+" space_scale:"+space_scale);

       // System.out.println(Functions.hypot(vx00,vy00));

       // System.out.println("Epot:"+Epot(x00, y00)+" Ecin: "+0.5*mass*Functions.hypot2(vx00, vy00));

       // System.out.println("E:"+E);

        if (E>=0 || G*M*mass > -2*E*maxA)
          {   
            return 0.01*1.001*2*Math.PI*Math.sqrt(maxA*maxA*maxA/(G*M));
          }
        else
        // Sistema legato
          {  
            a=-G*M*mass/(2*E);
            //System.out.println("Lega: "+a+" Tempo: "+ 1.001*2*Math.PI*Math.sqrt(a*a*a/(G*M)));
            return 1.001*2*Math.PI*Math.sqrt(a*a*a/(G*M));
          }

      }


    double Epot(double x, double y)
      { double r=Functions.hypot(x, y);

        if (r>R)
          
          return -G*settings.massa*mass/(r*space_scale);
      else{Approx=true;
          //System.out.println("ma");
          return  G*settings.massa*mass*
              (0.5*Functions.sqr(r)-1.5*Functions.sqr(R))/(R*R*R*space_scale);
           }
          
      }




    public String getTableHeading()
      { int i;
        int n=getOutputCount();
        StringBuffer sb=new StringBuffer();

        sb.append(Format.format("^11", "t[s]"));
        for(i=0; i<n; i++)
          { sb.append(" ");
            sb.append(Format.format("^11", getOutputShortName(i)));
          }

        return sb.toString();

      }




    public synchronized String getTableRow(int step)
      { int i;
        int n=getOutputCount();
        StringBuffer sb=new StringBuffer();

        sb.append(Format.format_e("11.3", t[step]));
        for(i=0; i<n; i++)
          { sb.append(" ");
            sb.append(Format.format_e("11.3", getOutput(i, step)));
          }

        return sb.toString();
      }


  }
