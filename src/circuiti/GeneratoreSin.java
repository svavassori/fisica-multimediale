package circuiti;

import util.*;
import java.awt.*;

/**
 * Un generatore di tensione sinusoidale
 */
public class GeneratoreSin extends Generatore
  { double Emax, omega, phi;

    /**
     * @param phi   fase in rad.
     */
    public GeneratoreSin(double Emax, double omega, double phi, double Ri)
      { this.Emax=Emax;
        this.omega=omega;
        this.phi=phi;
        this.Ri=Ri;
      }


    public double getEmax()
      { return Emax;
      }

    public double getOmega()
      { return omega;
      }

    /** 
     * Fase, in rad.
     */
    public double getPhi()
      { return phi;
      }

    public String toString()
      { return "F.e.m. sin. "+
               "Emax="+Format.format_e(".3", Emax)+" V "+
               "Ri="+Format.format_e(".3", Ri)+" Ohm "+ 
               "f="+Format.format(".3", omega/(2*Math.PI))+ " Hz "+
               "fase="+Format.format(".1", phi*Math.PI/180)+"°";
      }

    public double getE(double t)
      { return Emax*Math.sin(omega*t+phi);
      }

    public void paint(Graphics g, int x1, int y1, int x2, int y2)
      { int xc=(x1+x2)/2;
        int yc=(y1+y2)/2;
        int d=Math.max(Math.abs(x2-x1), Math.abs(y2-y1));

        super.paint(g, x1, y1, x2, y2);
        g.setColor(Color.black);
        g.drawArc(xc-d/6, yc-d/12, d/6, d/6, 0, 180);
        g.drawArc(xc, yc-d/12, d/6, d/6, 180, 180);
      }
  }
