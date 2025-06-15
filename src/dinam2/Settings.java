package dinam2;

/**
 * This class contains the settings for the experiment
 */
public class Settings
  { 
    // Tipi di campo
    public static final int ELASTICO=0;
    public static final int GRAVITAZIONALE=1;

    public static final int TIPI_DI_CAMPO=2;

    public static final int Pianeti=10;
    public static final int iMassaScelta=0;

    public static final int iMassaMercurio=1;
    public static final int iMassaVenere=2;
    public static final int iMassaTerrestre=3;
    public static final int iMassaMarte=4;
    public static final int iMassaGiove=5;
    public static final int iMassaSaturno=6;
    public static final int iMassaUrano=7;
    public static final int iMassaNettuno=8;
    public static final int iMassaPlutone=9;
    public static final double MassaPianeti[]={ 5.9800E24,
                                                0.3238E24,
                                                4.8300E24,
                                                5.9800E24,
                                                0.6370E24,
                                                1.9000E27,
                                                5.6700E26,
                                                8.8000E25,
                                                1.0300E26,
                                                1.0700E24};

    public int campo=0;

    public double massa=5.98E24;
  

    public double costante_elastica=1;
    public double resistenza_laminare=0.0;
    public double resistenza_turbolenta=0.0;
    public double fx=0, fy=0;

  }
