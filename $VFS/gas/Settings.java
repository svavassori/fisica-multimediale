package gas;

import ui.Parameters;
import ui.NoParameters;

/**
 * This class contains the settings for the experiment
 * The following parameters can be used to construct a 
 * an object through a Parameters instance:
 *   tipo		one of "browniano", "nmolecole"
 *   dati		one of "P+T", "rho+T", "P+rho"
 *   p			1000.0	...	1e7
 *   rho		2.5E-5	...	2.5E2	
 *   t			10.0	...	1e4
 */
public class Settings
  { // Massa di una mole del gas
    public static final double M=0.002;

    // Cost. dei gas perfetti
    public static final double R=8.314;

    public static final int MOTO_BROWNIANO=0;
    public static final int MOTO_NMOLECOLE=1;
    public int tipo;

    public static final int N=10;  // Numero di molecole

    public static final int P_T=0;
    public static final int RHO_T=1;
    public static final int P_RHO=2;
    public int dati;

    public double p;    // Pressione
    public double rho;  // Densita'
    public double t;    // Temperatura


    public Settings()
      { this(new NoParameters());
      }

    public Settings(Parameters parm)
      { String lista_tipi[]={"browniano", "nmolecole"};
        tipo=parm.getFromList("tipo", lista_tipi, MOTO_BROWNIANO);


        String lista_dati[]={"P+T", "rho+T", "P+rho"};
        dati=parm.getFromList("dati", lista_dati, P_T);

        p=parm.getDouble("p", 1e5, 1000.0, 1e7);
        rho=parm.getDouble("rho", 0.08, 2.5e-6, 2.5e2);
        t=parm.getDouble("t", 300, 10, 10000);
      }
  }
