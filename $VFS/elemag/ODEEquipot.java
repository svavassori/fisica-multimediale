package elemag;

import numeric.*;

/**
 * Sist. di equazioni differenziali usato per le linee equipotenziali
 */
public class ODEEquipot implements ODE
  { SimulationDisplay simulation;
    boolean backwards=false;
    double V0;


    public ODEEquipot(SimulationDisplay simulation, double x0, double y0)
      { this.simulation=simulation;
        V0=simulation.computeV(x0, y0);
      }

    public void setBackwards(boolean on)
      { backwards=on;
      }

    public int getDimension()
      { return 2;
      }

    public void derive(double t, double s[], double s1[])
      { double e[]={0, 0};
        simulation.computeE(s[0], s[1], e);
        double E=Functions.hypot(e[0], e[1]);
        if (E>1e-15)
          { e[0]/=E;
            e[1]/=E;
          }

        s1[0]=e[1];
        s1[1]=-e[0];

        if (backwards)
          { s1[0]=-s1[0];
            s1[1]=-s1[1];
          }

        // Correzione per mantenere V costante
        double V=simulation.computeV(s[0],s[1]);
        if (E>1e-15)
          { s1[0]+=1e-4*(V-V0)*e[0]/E;
            s1[1]+=1e-4*(V-V0)*e[1]/E;
          }
      }

  }

