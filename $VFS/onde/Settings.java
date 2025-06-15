package onde;

/**
 * This class contains the settings for the experiment
 */
public class Settings
  { public static final int ONDE_PIANE=0;
    public static final int ONDE_CIRCOLARI=1;
    public static final int TIPI_DI_ONDE=2;

    public static final int ONDE=2;

    double vel=1; // velocita' delle onde nel mezzo 
    double periodo=1;
    int tipo;     // tipo di onde

    double x[]={ -10, 0 };    // X del centro dell'onda (onde circolari)
    double y[]={ 0, 10};      // Y del centro dell'onda (onde circolari)
    int dir[]={0, 30};    // Direz. dell'onda (onde piane)
    int fase[]={0, 0};    // Fase dell'onda
  }
