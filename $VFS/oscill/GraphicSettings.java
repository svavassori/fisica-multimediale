package oscill;

/**
 * A placeholder for the information on the waves to be displayed
 */
public class GraphicSettings
  { public boolean onda[];
    public boolean somma;

    public GraphicSettings()
      { onda=new boolean[Settings.ONDE];
        int i;
        for(i=0; i<Settings.ONDE; i++)
          onda[i]=true;
        somma=true;
      }
  }
