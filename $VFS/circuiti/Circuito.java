package circuiti;

import java.awt.Point;
import numeric.*;
import ui.*;


/**
 * Questa classe rappresenta un circuito elettrico 
 */
public class Circuito implements ODE
  { public static final int GRID=6;
    public static final int NODES=GRID*GRID;
    public static final int STEPS=200;

    Componente comp[][];
    boolean    reversed[][];

    int n_componenti, n_induttori, n_condensatori;
    int from[], to[];
    Componente comp1[];
    
    int n_nodi;
    int node_label[];
    int node[];
    int n_stati;
    int stato_label[], stato[];
    int n_generatori;
    int gen_label[], generatore[];

    int first_node, first_comp, first_stato, first_gen;
    double M[][];

    boolean singular=true;

    boolean simulation_available=false;

    double time[]={0};
    double sim_state[][];

    public Circuito()
      { int n=NODES;

        comp=new Componente[n][];
        reversed=new boolean[n][];
        int i;
        for(i=1; i<n; i++)
          { comp[i]=new Componente[i];
            reversed[i]=new boolean[i];
          }
      }

    public void add(int i, int j, Componente c)
      { simulation_available=false;
        if (i>j)
          { comp[i][j]=c;
            reversed[i][j]=false;
          }
        else
          { comp[j][i]=c;
            reversed[j][i]=true;
          }
      }

    public Componente get(int i, int j)
      { 
        if (i>j)
          return comp[i][j];
        else
          return comp[j][i];
      }

    public boolean isReversed(int i, int j)
      { if (i>j)
          return reversed[i][j];
        else
          return !reversed[j][i];
      }

    public int posToNode(int x, int y)
      { return x+GRID*y;
      }

    public Point nodeToPos(int n)
      { return new Point(n % GRID, n/GRID);
      }

    /**
     * Restituisce i nodi del 4-intorno di n
     */
    public int[] adjacentNodes(int n)
      { int num=4;
        if (n<GRID || n>=NODES-GRID)
          num--;
        if (n%GRID ==0 || n%GRID==GRID-1)
          num--;

        int adj[]=new int[num];

        int i=0;
        if (n%GRID > 0)
          adj[i++]=n-1;
        if (n>=GRID)
          adj[i++]=n-GRID;
        if (n%GRID<GRID-1)
          adj[i++]=n+1;
        if (n<NODES-GRID)
          adj[i++]=n+GRID;

        return adj;
      }

    public boolean adjacent(int i, int j)
      { return  (i % GRID > 0 && j==i-1)
             || (i>=GRID && j==i-GRID)
             || (i % GRID < GRID-1 && j==i+1)
             || (i<NODES-GRID && j==i+GRID) ;
      }


    /**
     * Assegna un'etichetta unica a ciascun componente
     * da 0 a n-1, con n numero di componenti.
     * Contestualmente, conta il numero di generatori,
     * induttori, condensatori.
     * Etichetta anche i nodi.
     * Modifica i membri: n_componenti, n_generatori, n_induttori,
     *                    n_condensatori, from, to, n_nodi, node_label, node
     */
    public void etichettaComponenti()
      { n_componenti=0;
        n_generatori=0;
        n_induttori=0;
        n_condensatori=0;

        int i,j;
        for(i=1; i<NODES; i++)
          for(j=0; j<i; j++)
            { if (comp[i][j]!=null)
                comp[i][j].setLabel(n_componenti++);
              if (comp[i][j] instanceof Induttore)
                n_induttori++;
              else if (comp[i][j] instanceof Condensatore)
                n_condensatori++;
              else if (comp[i][j] instanceof Generatore)
                n_generatori++;
            }


        from=new int[n_componenti];
        to=new int[n_componenti];
        comp1=new Componente[n_componenti];

        node_label=new int[NODES];
        for(i=0; i<NODES; i++)
          node_label[i]=-1;
        n_nodi=0;

        n_stati=n_induttori+n_condensatori;
        stato_label=new int[n_componenti];
        for(i=0; i<n_componenti; i++)
          stato_label[i]=-1;
        stato=new int[n_stati];
        n_stati=0;
        gen_label=new int[n_componenti];
        for(i=0; i<n_componenti; i++)
          gen_label[i]=-1;
        generatore=new int[n_generatori];
        n_generatori=0;

        for(i=1; i<NODES; i++)
          for(j=0; j<i; j++)
            { if (comp[i][j]==null)
                continue;
              int l=comp[i][j].getLabel();
              from[l]=i;
              to[l]=j;
              comp1[l]=comp[i][j];
              if (node_label[i]<0)
                node_label[i]=n_nodi++;
              if (node_label[j]<0)
                node_label[j]=n_nodi++;

              if (comp[i][j] instanceof Induttore ||
                  comp[i][j] instanceof Condensatore)
                { stato[n_stati]=l;
                  stato_label[l]=n_stati++;
                }
              else if (comp[i][j] instanceof Generatore)
                { generatore[n_generatori]=l;
                  gen_label[l]=n_generatori++;
                }
            }

        node=new int[n_nodi];
        for(i=0; i<NODES; i++)
          if (node_label[i]>=0)
            node[node_label[i]]=i;

      }

    /**
     * Genera la matrice corrispondente al sistema di equazioni.
     * Le colonne della matrice rappresentano, in ordine,
     * i pot. ai nodi del sistema, le correnti nei componenti, 
     * gli stati e i generatori. Per le correnti che passano nei generatori,
     * usa la convenzione del generatore.
     */
    public void generaMatrice()
      { first_node=0;
        first_comp=n_nodi;
        first_stato=first_comp+n_componenti;
        first_gen=first_stato+n_stati;
        int Mm=first_gen+n_generatori;
        int Mn=first_stato;
        M=new double[Mn][];
        int i;
        for(i=0; i<Mn; i++)
          { M[i]=new double[Mm];
          }

        //---------- Genera le equazioni per i nodi ------------------//
        singular=true;
        if (Mm==0 || Mn==0)
          return;

        // Al primo nodo assegna potenziale 0
        M[0][0]=1;

        // Per gli altri nodi, usa la legge di Kirchhoff ai nodi.
        for(i=1; i<n_nodi; i++)
          { int n=node[i];
            int adj[]=adjacentNodes(n);
            int j;
            for(j=0; j<adj.length; j++)
              { 
                Componente c=get(n, adj[j]);
                if (c==null)
                  continue;
                int lab=c.getLabel();
                int sign=+1;
                if (isReversed(n, adj[j]))
                  sign=-sign;
                if (c instanceof Generatore)
                  sign=-sign;
                M[i][first_comp+lab]=sign;
              }
          }

        //--------- Genera le equazioni per i componenti ------------//
        for(i=0; i<n_componenti; i++)
          { int n1=from[i];
            int n2=to[i];
            Componente c=get(n1, n2);
            if (c==null)
              continue;
            int l1=node_label[n1];
            int l2=node_label[n2];
            boolean rev=isReversed(n1, n2);
            int l=c.getLabel();
            int eq=first_comp+i;
            int sign=1;
            if (rev)
              sign=-sign;

            if (c instanceof CortoCircuito)
              { M[eq][first_node+l1]=1;
                M[eq][first_node+l2]=-1;
              }
            else if (c instanceof Resistore)
              { double R=((Resistore)c).getR();
                M[eq][first_node+l1]=1;
                M[eq][first_node+l2]=-1;
                M[eq][first_comp+l]=-sign*R;
              }
            else if (c instanceof Generatore)
              { double Ri=((Generatore)c).getRi();
                int lg=gen_label[l];
                M[eq][first_node+l1]=1;
                M[eq][first_node+l2]=-1;
                M[eq][first_comp+l]=+sign*Ri;
                M[eq][first_gen+lg]=-sign;
              }
            else if (c instanceof Induttore)
              { int ls=stato_label[l];
                M[eq][first_comp+l]=1;
                M[eq][first_stato+ls]=-1;
              }
            else if (c instanceof Condensatore)
              { int ls=stato_label[l];
                M[eq][first_node+l1]=1;
                M[eq][first_node+l2]=-1;
                M[eq][first_stato+ls]=-sign;
              }

          }



        //-------- Ora prova a diagonalizzare la matrice  ------------//
        singular=Gauss.diagonalize(M) != Gauss.OK;


      }

    public boolean isSimulationAvailable()
      { return simulation_available;
      }

    /**
     * Calcola i risultati della simulazione
     */
    public void execute(double start, double duration, StatusDisplayer status)
      { int i;
        simulation_available=false;
        if (status!=null)
          status.showStatus("0 %"); 

        etichettaComponenti();
        generaMatrice();
        if (singular)
          { MessageBox.alert(null,"Circuiti elettrici - esegui simulazione",
                "Il circuito non è corretto.");
            return;
          }


        double end=start+duration;
        time=new double[STEPS+1];
        sim_state=new double[STEPS+1][];
        for(i=0; i<=STEPS; i++)
          time[i]=start+i*duration/STEPS;
        if (n_stati==0)
          { simulation_available=true;
            if (status!=null)
              status.showStatus("Simulazione completata"); 
            return;
          }

        double initial[]=getInitialState();

        ODESolver os=new ODESolver(this, 0, initial, duration/400, 1e-4);
        os.setMaxStep(Math.min(end/40, duration/5));
        os.setMinStep(duration/2000);
        os.setTolerant(true);


        ODEInterpolator interp=new ODEInterpolator(os);
       
        // Esegue la simulazione fino all'istante iniziale
        if (start>end/100)
          { double yy[]=new double[n_stati];
            for(i=0; i<19; i++)
              { if (interp.interpolate(i*start/20, yy)!=ODEInterpolator.OK)
                { MessageBox.alert(null,"Circuiti elettrici - esegui simulazione",
                      "Simulazione numericamente divergente! Prova a usare\n"+
                      "un intervallo temporale più breve");
                  return;
                }
                int perc=(int)(i*start/20/end*100);
                if (status!=null)
                  status.showStatus(""+perc+" %");
              }
          }

        int old_perc=0;
        for(i=0; i<=STEPS; i++)
          { sim_state[i]=new double[n_stati];
            if (interp.interpolate(time[i], sim_state[i])
                    !=ODEInterpolator.OK)
                { MessageBox.alert(null,"Circuiti elettrici - esegui simulazione",
                      "Simulazione numericamente divergente! Prova a usare\n"+
                      "un intervallo temporale più breve");
                  return;
                }
             int perc=(int)(time[i]/end*100);
             if (status!=null && perc!=old_perc)
               { status.showStatus(""+perc+" %");
                 old_perc=perc;
               }
          }
          
        if (status!=null)
          status.showStatus("Simulazione completata");  
        simulation_available=true;
      }

    double[] getInitialState()
      { double s[]=new double[n_stati];

        int i;
        for(i=0; i<n_stati; i++)
          { int l=stato[i];
            Componente c=comp1[l];
            if (c instanceof Condensatore)
              { Condensatore cond=(Condensatore)c;
                s[i]=cond.getQ0()/cond.getC();
              }
            else
              s[i]=0;
          }
        return s;
      }


    public int getDimension()
      { return n_stati;
      }

    public void derive(double t, double s[], double s1[])
      { int i,j,k;

        for(i=0; i<n_stati; i++)
          { int l=stato[i];
            Componente c=comp1[l];
            if (c instanceof Condensatore)
              { int eq=first_comp+l;
                double curr=getDependentValue(eq, t, s);
                s1[i]=curr/((Condensatore)c).getC();
              }
            else if (c instanceof Induttore)
              { int n1=from[l];
                int n2=to[l];
                int sign= isReversed(n1, n2)? -1: 1;
                int eq1=first_node+node_label[n1];
                int eq2=first_node+node_label[n2];
                double V1=getDependentValue(eq1, t, s);
                double V2=getDependentValue(eq2, t, s);
                s1[i]=sign*(V1-V2)/((Induttore)c).getL();
              }
          }
      }


    /**
     * Calcola il valore di una grandezza dipendente
     * Richiede che la matrice sia diagonalizzata, e che siano
     * noti i valori degli stati e il tempo.
     */
    double getDependentValue(int eq, double t, double st[])
      { double val=0;
        int j;
        for(j=0; j<n_stati; j++)
          { val -= M[eq][first_stato+j]*st[j]; 
          }
        for(j=0; j<n_generatori; j++)
          { Generatore g=(Generatore)comp1[generatore[j]];
            val -= M[eq][first_gen+j]*g.getE(t); 
          }
        return val;
      }


    /**
     * Restituisce il n. di passi della simulazione
     */
    public int getStepCount()
      { return STEPS;
      }

    /**
     * Restituisce il tempo ad uno specifico passo
     */
    public double getStepTime(int step)
      { return time[step];
      }

    /**
     * Restituisce la tensione di un nodo
     */
    public double getV(int step, int n)
      { int l=node_label[n];
        if (l<0)
          throw new IllegalArgumentException();
        return getDependentValue(first_node+l, time[step], sim_state[step]);
      }
    
    /**
     * Restituisce la ddp tra due nodi 
     */
    public double getV(int step, int n1, int n2)
      { return getV(step, n1)-getV(step, n2);
      }

    /**
     * Restituisce la corrente tra due nodi, considerando la conv.
     * del generatore o dell'utilizzatore a seconda del componente
     */
    public double getI(int step, int n1, int n2)
      { Componente c=get(n1, n2);
        int l=c.getLabel();
        int sign= isReversed(n1, n2)? -1: 1;

        double I=getDependentValue(first_comp+l, time[step], sim_state[step]);
        return sign*I;
      }
  }

