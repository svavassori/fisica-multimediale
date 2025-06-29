package elemag;

import ui.*;
import util.*;
import numeric.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The canvas which displays the simulation
 */
public class SimulationDisplay extends SpriteCanvas implements Runnable
  { StatusDisplayer status;
    Component controller;
    CoordinateMapper map;
    AxesDrawer axes;
    CursorChanger curs;

    double zoom=1.0;

    static final int MAX_CARICHE=500;
    double carica[], x_carica[], y_carica[];
    int cariche=0;

    static final int MAX_PIANI=500;
    double dens_piano[], x_piano[], y_piano[], ang_piano[];
    int piani=0;

    static final int MAX_COPPIE=500;
    double dens_coppia[], x_coppia[], y_coppia[], dist_coppia[], ext_coppia[];
    boolean horiz_coppia[];
    int coppie=0;

    double induzione_magnetica=0.0;

    Image pos_image, neg_image;
    int   pos_width, pos_height, neg_width, neg_height;

    boolean aggiungi_carica=false;
    boolean aggiungi_piano=false;
    boolean aggiungi_coppia=false;
    boolean cancella_oggetto=false;
    boolean visualizza_linee=false;
    boolean visualizza_equip=false;
    boolean visualizza_moto=false;

    int near_carica, near_carica_dist;
    int near_piano, near_piano_dist;
    int near_coppia, near_coppia_dist;
    static final int soglia_dist_cancella=3;

    boolean visualizza_campo=false;
    int arrow_last_x=-1, arrow_last_y=-1;
    double arrow_last_Ex=0, arrow_last_Ey=0;
    public static final int ARROW_LEN=54;
    public static final int ARROW_TIP=9;
    public static final double ARROW_ANG=30*Math.PI/180;
    public static final double ARROW_COS=Math.cos(ARROW_ANG);
    public static final double ARROW_SIN=Math.sin(ARROW_ANG);

    static final double epsilon0=8.854e-12;
    static final double FACTOR=1/(4*Math.PI*epsilon0);

    Animation animation=null;
    Thread thread;
    int step=0;
    static final double frameDelay=1.0/10;
    static final double duration=10.0;

    Image ball;
    int ball_width, ball_height;

    private final AtomicBoolean isRunning = new AtomicBoolean();


    public SimulationDisplay(Component controller, StatusDisplayer status)
      { this.status=status;
        this.controller=controller;
        setBackground(Color.white);
        map=new CoordinateMapper();
        map.setIsometric(true);
        axes=new AxesDrawer(map);
        recomputeWindow();
        repaintBackground();
        curs=new CursorChanger(this);

        ImageLoader il=UserInterface.getImageLoader();

        pos_image=il.load("icons/pos.gif");
        neg_image=il.load("icons/neg.gif");
        pos_width=pos_image.getWidth(this);
        pos_height=pos_image.getHeight(this);
        neg_width=neg_image.getWidth(this);
        neg_height=neg_image.getHeight(this);

        ball=il.load("icons/red-ball.gif");
        ball_width=ball.getWidth(this);
        ball_height=ball.getHeight(this);
        addSprite(ball);

        carica=new double[MAX_CARICHE];
        x_carica=new double[MAX_CARICHE];
        y_carica=new double[MAX_CARICHE];

        dens_piano=new double[MAX_PIANI];
        x_piano=new double[MAX_PIANI];
        y_piano=new double[MAX_PIANI];
        ang_piano=new double[MAX_PIANI];

        dens_coppia=new double[MAX_COPPIE];
        x_coppia=new double[MAX_COPPIE];
        y_coppia=new double[MAX_COPPIE];
        dist_coppia=new double[MAX_COPPIE];
        ext_coppia=new double[MAX_COPPIE];
        horiz_coppia=new boolean[MAX_COPPIE];
      }

    

    void recomputeWindow()
      { map.set(-30*zoom,-20*zoom,30*zoom,20*zoom);
      }

    public void setZoom(double zoom)
      { stopAnimation();
        this.zoom=zoom;
        recomputeWindow();
        updateBeforeResize();
        repaintBackground();
      }

    /**
     * Redefined; see ui.SpriteCanvas
     */
    public void updateBeforeResize()
      { map.set(dim);
        if (animation!=null)
          { Point pt=getBallPos(step);
            moveSprite(0, pt.x, pt.y);
          }
      }

    public void paint(Graphics g)
      { super.paint(g);
        if (visualizza_campo)
          xorArrow(arrow_last_x, arrow_last_y, arrow_last_Ex, arrow_last_Ey);
      }    


    /**
     * Paint the background of the canvas
     */
    public void paintBackground(Graphics g, Rectangle rect)
      { super.paintBackground(g, rect);
        g.setColor(Color.blue);

        // Disegna gli assi
        Point zero=map.realToPixel(0,0);
        g.drawLine(zero.x, 0, zero.x, dim.height);
        g.drawLine(0, zero.y, dim.width, zero.y);
        axes.drawAxes(g, zero);


        paintCampoMagnetico(g, rect);
        paintCariche(g, rect);
        paintPiani(g, rect);
        paintCoppie(g, rect);
      }



    /**
     * Disegna le cariche
     */
    void paintCariche(Graphics g, Rectangle r)
      { int i;
        for(i=0; i<cariche; i++)
          { Point pt=map.realToPixel(x_carica[i], y_carica[i]);
            if (carica[i]<0)
              g.drawImage(neg_image, pt.x-neg_width/2, pt.y-neg_height/2, this);
            else
              g.drawImage(pos_image, pt.x-pos_width/2, pt.y-pos_height/2, this);
          }
      }

    /**
     * Disegna i piani
     */
    void paintPiani(Graphics g, Rectangle r)
      { int i;
        for(i=0; i<piani; i++)
          { if (dens_piano[i]>0)
              g.setColor(Color.red);
            else
              g.setColor(Color.cyan);

            double s=Math.sin(ang_piano[i]);
            double c=Math.cos(ang_piano[i]);
            int x1,y1,x2,y2;
            Point base=map.realToPixel(x_piano[i], y_piano[i]);
            if (Math.abs(s) > Math.abs(c))
              { y1=0;
                x1=base.x+(int)((base.y-y1)*c/s);
                y2=dim.height-1;
                x2=base.x+(int)((base.y-y2)*c/s);
              }
            else
              { x1=0;
                y1 = base.y - (int)((x1-base.x)*s/c);
                x2=dim.width-1;
                y2 = base.y - (int)((x2-base.x)*s/c);
              }

            g.drawLine(x1, y1, x2, y2);
            g.drawLine(x1+1, y1, x2+1, y2);
            g.drawLine(x1, y1+1, x2, y2+1);
          }
      }

    /**
     * Disegna le coppie di lastre
     */
    void paintCoppie(Graphics g, Rectangle rect)
      { int i;
        for(i=0; i<coppie; i++)
          { Point pt1=map.realToPixel(x_coppia[i], y_coppia[i]);
            Point pt2, pt3, pt4;
            if (horiz_coppia[i])
              { pt2=map.realToPixel(x_coppia[i]+ext_coppia[i], y_coppia[i]);
                pt3=map.realToPixel(x_coppia[i], y_coppia[i]+dist_coppia[i]);
                pt4=map.realToPixel(x_coppia[i]+ext_coppia[i], 
                                    y_coppia[i]+dist_coppia[i]);
              }
            else
              { pt2=map.realToPixel(x_coppia[i], y_coppia[i]+ext_coppia[i]);
                pt3=map.realToPixel(x_coppia[i]+dist_coppia[i], y_coppia[i]);
                pt4=map.realToPixel(x_coppia[i]+dist_coppia[i], 
                                    y_coppia[i]+ext_coppia[i]);
              }
            if (dens_coppia[i]<0)
              g.setColor(Color.cyan); 
            else
              g.setColor(Color.red);

            g.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
            g.drawLine(pt1.x+1, pt1.y, pt2.x+1, pt2.y);
            g.drawLine(pt1.x, pt1.y+1, pt2.x, pt2.y+1);

            if (dens_coppia[i]<0)
              g.setColor(Color.red);
            else
              g.setColor(Color.cyan);
              
            g.drawLine(pt3.x, pt3.y, pt4.x, pt4.y);
            g.drawLine(pt3.x+1, pt3.y, pt4.x+1, pt4.y);
            g.drawLine(pt3.x, pt3.y+1, pt4.x, pt4.y+1);

          }
      }
                

    /**
     * Disegna il campo magnetico
     */
    void paintCampoMagnetico(Graphics g, Rectangle rect)
      { if (Math.abs(induzione_magnetica)<1e-19)
          return;

        int r=3;
        int dx=50;
        int dy=50;
        int x, y;
        Point zero=map.realToPixel(0,0);
        g.setColor(Color.green);

        for(x=(zero.x % dx)-dx; x<dim.width+dx; x+=dx)
          for(y=(zero.y % dy)-dy; y<dim.height+dy; y+=dy)
            { if (induzione_magnetica>0)
                { g.drawLine(x-r, y-r, x+r, y+r);
                  g.drawLine(x+r, y-r, x-r, y+r);
                }
              else
                g.drawOval(x-r, y-r, 2*r, 2*r);
            }
      }

        

    public boolean mouseEnter(Event evt, int x, int y)
      { if (aggiungi_carica || aggiungi_piano || aggiungi_coppia
            || cancella_oggetto || visualizza_linee || visualizza_equip
            || visualizza_moto)
          curs.setCursor(Frame.CROSSHAIR_CURSOR);
        else if (thread!=null)
          curs.setCursor(Frame.HAND_CURSOR);
        else
          curs.setCursor();
        return super.mouseEnter(evt, x, y);
      }

    public boolean mouseExit(Event evt, int x, int y)
      { curs.setCursor();
        status.showStatus("");
        return super.mouseEnter(evt, x, y);
      }

    public boolean mouseMove(Event evt, int x, int y)
      { if (aggiungi_carica || aggiungi_piano || aggiungi_coppia
            || cancella_oggetto || visualizza_linee || visualizza_equip
            || visualizza_moto)
          { if (curs.getCursorType() != Frame.CROSSHAIR_CURSOR) 
              curs.setCursor(Frame.CROSSHAIR_CURSOR);
          }
        else if (thread!=null)
          curs.setCursor(Frame.HAND_CURSOR);
        else
          { if (curs.getCursorType() != Frame.DEFAULT_CURSOR)
              curs.setCursor();
          }

        double z[]=map.pixelToReal(x,y);
        double E[]=computeE(z[0], z[1]);
        double e=Functions.hypot(E[0], E[1]);
        double V=computeV(z[0], z[1]);
        String str= "X="+Format.format(".3", z[0])+" m  "+
                    "Y="+Format.format(".3", z[1])+" m  "+
                    "E="+Format.format_e(".3", e)+" V/m  "+
                    "V="+Format.format_e(".3", V)+" V";
        if (visualizza_campo)
          { xorArrow(arrow_last_x, arrow_last_y, arrow_last_Ex, arrow_last_Ey);
            xorArrow(x, y, E[0], E[1]);
          }
        status.showStatus(str);

        return super.mouseEnter(evt, x, y);
      }

    public boolean mouseDown(Event evt, int x, int y)
      { if (aggiungi_carica)
          { cleanState();
            double z[]=map.pixelToReal(x,y);
            Dialog dialog=new CaricaDialog(this, z[0], z[1]);
            dialog.reshape(100, 100, 400, 270);
            dialog.show();
          }
        else if (aggiungi_piano)
          { cleanState();
            double z[]=map.pixelToReal(x,y);
            Dialog dialog=new PianoDialog(this, z[0], z[1]);
            dialog.reshape(100, 100, 400, 300);
            dialog.show();
          }
        else if (aggiungi_coppia)
          { cleanState();
            double z[]=map.pixelToReal(x,y);
            Dialog dialog=new CoppiaDialog(this, z[0], z[1]);
            dialog.reshape(100, 50, 400, 370);
            dialog.show();
          }
        else if (cancella_oggetto)
          { cleanState();
            cancellaOggetto(x, y);
          }
        else if (visualizza_linee)
          { double z[]=map.pixelToReal(x,y);
            visualizzaLineeDiForza(z[0], z[1]);
          }
        else if (visualizza_equip)
          { double z[]=map.pixelToReal(x,y);
            visualizzaLineeEquipotenziali(z[0], z[1]);
          }
        else if (visualizza_moto)
          { cleanState();
            double z[]=map.pixelToReal(x,y);
            Dialog dialog=new MotoDialog(this, z[0], z[1]);
            dialog.reshape(50, 10, 500, 465);
            dialog.show();
          }
        else if (thread!=null)
          { stopAnimation();
            curs.setCursor();
            status.showStatus("Animazione interrotta!");
          }
          
        return super.mouseDown(evt, x, y);
      }


    /**
     * Riporta nello stato iniziale il sistema, ovvero annulla l'effetto
     * di aggiungiCarica etc.
     */
    void cleanState()
      { aggiungi_carica=false;
        aggiungi_piano=false;
        aggiungi_coppia=false;
        cancella_oggetto=false;
        visualizza_linee=false;
        visualizza_equip=false;
        visualizza_moto=false;
        if (animation!=null)
          visualizzaMoto(null);
        stopAnimation();
        curs.setCursor();
      }

    /**
     * Cancella l'esperimento
     */
    public void reset()
      { cleanState();
        cariche=0;
        piani=0;
        coppie=0;
        induzione_magnetica=0.0;
        repaintBackground();
      }


    /**
     * Prepara ad accettare l'aggiunta di una nuova carica
     */
    public void aggiungiCarica()
      { cleanState();
        if (cariche<MAX_CARICHE)
          aggiungi_carica=true;
        else
          MessageBox.alert(this,"Elettromagnetismo - Aggiungi carica",
                           "E' stato raggiunto il massimo numero di\n"+
                           "cariche previsto dalla simulazione");
      }

    /**
     * Aggiunge una carica alla simulazione
     */
    void aggiungiCarica(double carica, double x, double y)
      { if (cariche>=MAX_CARICHE || Math.abs(carica)<1e-19)
          return;
        this.carica[cariche]=carica;
        x_carica[cariche]=x;
        y_carica[cariche]=y;
        cariche++;
        repaintBackground();
      }

    /**
     * Prepara ad accettare l'aggiunta di un nuovo piano
     */
    public void aggiungiPiano()
      { cleanState();
        if (piani<MAX_PIANI)
          aggiungi_piano=true;
        else
          MessageBox.alert(this,"Elettromagnetismo - Aggiungi piano",
                           "E' stato raggiunto il massimo numero di\n"+
                           "piani previsto dalla simulazione");
      }

    /**
     * Aggiunge un piano alla simulazione
     */
    void aggiungiPiano(double dens, double x, double y, double ang)
      { if (piani>=MAX_PIANI || Math.abs(dens)<1e-19)
          return;
        dens_piano[piani]=dens;
        x_piano[piani]=x;
        y_piano[piani]=y;
        ang_piano[piani]=ang;
        piani++;
        repaintBackground();
      }

    /**
     * Prepara ad accettare l'aggiunta di un nuovo piano
     */
    public void aggiungiCoppia()
      { cleanState();
        if (piani<MAX_COPPIE)
          aggiungi_coppia=true;
        else
          MessageBox.alert(this,"Elettromagnetismo - Aggiungi coppia di lastre",
                           "E' stato raggiunto il massimo numero di\n"+
                           "lastre previsto dalla simulazione");
      }

    /**
     * Aggiunge una coppia alla simulazione
     */
    void aggiungiCoppia(double dens, double x, double y, 
                        double dist, double ext, boolean horiz)
      { if (coppie>=MAX_COPPIE || Math.abs(dens)<1e-19)
          return;
        dens_coppia[coppie]=dens;
        x_coppia[coppie]=x;
        y_coppia[coppie]=y;
        dist_coppia[coppie]=dist;
        ext_coppia[coppie]=ext;
        horiz_coppia[coppie]=horiz;
        coppie++;
        repaintBackground();
      }

    /** 
     * Aggiunge il campo magnetico
     */
    public void aggiungiCampo()
      { cleanState();
        Dialog dialog=new CampoDialog(this);
        dialog.reshape(100,100,400,200);
        dialog.show();
      }


    /**
     * Prepara a cancellare un oggetto
     */
    public void cancellaOggetto()
      { cleanState();
        cancella_oggetto=true;
      }

    /**
     * Cancella l'oggetto piu' vicino alla pos. x,y
     */
    void cancellaOggetto(int x, int y)
      { trovaCarica(x,y);
        trovaPiano(x,y);
        trovaCoppia(x,y);

        int dist=Math.min(near_carica_dist, Math.min(near_piano_dist,
                                                     near_coppia_dist));
        if (dist>soglia_dist_cancella)
          return;

        int i;
        if (dist==near_carica_dist)
          { for(i=near_carica; i<cariche-1; i++)
              { carica[i]=carica[i+1];
                x_carica[i]=x_carica[i+1];
                y_carica[i]=y_carica[i+1];
              }
            cariche--;
          }
        else if (dist==near_piano_dist)
          { for(i=near_piano; i<piani-1; i++)
              { dens_piano[i]=dens_piano[i+1];
                x_piano[i]=x_piano[i+1];
                y_piano[i]=y_piano[i+1];
                ang_piano[i]=ang_piano[i+1];
              }
            piani--;
          }
        else if (dist==near_coppia_dist)
          { for(i=near_coppia; i<coppie-1; i++)
              { dens_coppia[i]=dens_coppia[i+1];
                x_coppia[i]=x_coppia[i+1];
                y_coppia[i]=y_coppia[i+1];
                dist_coppia[i]=dist_coppia[i+1];
                ext_coppia[i]=ext_coppia[i+1];
                horiz_coppia[i]=horiz_coppia[i+1];
              }
            coppie--;
          }
        
        repaintBackground();
      }


    /**
     * Trova la carica piu' vicina al punto x,y
     */
    void trovaCarica(int x, int y)
      { near_carica_dist=soglia_dist_cancella+10;

        int i;

        Point pt;

        for(i=0; i<cariche; i++)
          { pt=map.realToPixel(x_carica[i], y_carica[i]);
            int dist=(int)Functions.hypot(x-pt.x, y-pt.y)-pos_width/2;
            if (dist<0)
              dist=0;
            if (dist<near_carica_dist)
              { near_carica_dist=dist;
                near_carica=i;
              }
          }
      }

    /**
     * Trova il piano piu' vicino al punto x,y
     */
    void trovaPiano(int x, int y)
      { near_piano_dist=soglia_dist_cancella+10;

        int i;
        for(i=0; i<piani; i++)
          { double s=Math.sin(ang_piano[i]);
            double c=Math.cos(ang_piano[i]);
            double z[]=map.pixelToReal(x,y);
            double dx=z[0]-x_piano[i];
            double dy=z[1]-y_piano[i];
            double t=dx*c+dy*s;
            double xx=x_piano[i]+t*c;
            double yy=y_piano[i]+t*s;
            Point pt=map.realToPixel(xx, yy);
            int dist=(int)Functions.hypot(x-pt.x, y-pt.y);
            if (dist<0)
              dist=0;
            if (dist<near_piano_dist)
              { near_piano_dist=dist;
                near_piano=i;
              }

          }
      }

    /**
     * Trova la coppia di lastre piu' vicina al punto x,y
     */
    void trovaCoppia(int x, int y)
      { near_coppia_dist=soglia_dist_cancella+10;
        int i;
        Point zero=map.realToPixel(0,0);
        for(i=0; i<coppie; i++)
          { double z[]=map.pixelToReal(x, y);
            double d1, d2;

            if (horiz_coppia[i])
              { double xx=Math.max(x_coppia[i], Math.min(z[0],
                                         x_coppia[i]+ext_coppia[i]));
                d1=Functions.hypot(xx-z[0], y_coppia[i]-z[1]);
                d2=Functions.hypot(xx-z[0], y_coppia[i]+dist_coppia[i]-z[1]);
              }
            else
              { double yy=Math.max(y_coppia[i], Math.min(z[1],
                                         y_coppia[i]+ext_coppia[i]));
                d1=Functions.hypot(x_coppia[i]-z[0], yy-z[1]);
                d2=Functions.hypot(x_coppia[i]+dist_coppia[i]-z[0], yy-z[1]);
              }
            Point pt=map.realToPixel(Math.min(d1, d2), 0);
            int dist=pt.x-zero.x;
            if (dist<0)
              dist=0;
            if (dist<near_coppia_dist)
              { near_coppia_dist=dist;
                near_coppia=i;
              }
          }
      }


    public boolean action(Event evt, Object what)
      { if (what==null)
          return false;

        if (what instanceof CaricaDialog)
          { CaricaDialog cd=(CaricaDialog)what;
            aggiungiCarica(cd.getCarica(), cd.getXPos(), cd.getYPos());
          }
        else if (what instanceof PianoDialog)
          { PianoDialog pd=(PianoDialog)what;
            aggiungiPiano(pd.getDens(), pd.getXPos(), pd.getYPos(), pd.getAng());
          }
        else if (what instanceof CoppiaDialog)
          { CoppiaDialog cd=(CoppiaDialog)what;
            aggiungiCoppia(cd.getDens(), cd.getXPos(), cd.getYPos(),
                           cd.getDist(), cd.getExt(), cd.getHoriz());
          }
        else if (what instanceof CampoDialog)
          { induzione_magnetica=((CampoDialog)what).getCampo();
            if (Math.abs(induzione_magnetica)<1e-19)
              induzione_magnetica=0;
            repaintBackground();
          }
        else if (what instanceof Animation)
          { visualizzaMoto((Animation)what);
          }

        return true;
      }

    /**
     * Attiva/disattiva la visualizzazione del campo
     */
    public void visualizzaCampo(boolean on)
      { visualizza_campo=on;
        repaint();
      }

    /**
     * Disegna in XOR la freccia che indica la direzione del campo
     */
    void xorArrow(int x, int y, double Ex, double Ey)
      { arrow_last_x=x;
        arrow_last_y=y;
        arrow_last_Ex=Ex;
        arrow_last_Ey=Ey;

        double E=Functions.hypot(Ex, Ey);
        if (x<0 || y<0 || x>dim.width || y>dim.height || E<1e-15)
          return;

        double sin=Ey/E;
        double cos=Ex/E;
        int x1=(int)(x+cos*ARROW_LEN);
        int y1=(int)(y-sin*ARROW_LEN);

        double sa=sin*ARROW_COS+cos*ARROW_SIN;
        double ca=-cos*ARROW_COS+sin*ARROW_SIN;
        double sb=sin*ARROW_COS-cos*ARROW_SIN;
        double cb=-cos*ARROW_COS-sin*ARROW_SIN;

        int dxa=(int)(ARROW_TIP*ca+0.5);
        int dya=(int)(ARROW_TIP*sa+0.5);
        int dxb=(int)(ARROW_TIP*cb+0.5);
        int dyb=(int)(ARROW_TIP*sb+0.5);

        Graphics g=getGraphics();
        g.setColor(Color.green);
        g.setXORMode(Color.white);

        g.drawLine(x, y, x1, y1);
        g.drawLine(x1, y1, x1+dxa, y1+dya);
        g.drawLine(x1, y1, x1+dxb, y1+dyb);


        g.setPaintMode();
        g.dispose();
      }


    /**
     * Restituisce il valore del vettore induz. magnetica (B)
     */
    public double getInduzioneMagnetica()
      { return induzione_magnetica;
      }


    /**
     * Calcola il campo elettrico in un punto
     */
    public double[] computeE(double x, double y)
      { double E[]={0,0};
        computeE(x, y, E);
        return E;
      }

    /**
     * Calcola il campo elettrico in un punto
     */
    public void computeE(double x, double y, double E[])
      { double Ex=0, Ey=0;
        double z[]={0, 0};
        
        int i;
        for(i=0; i<cariche; i++)
          { computeCaricaE(i, x, y, z);
            Ex+=z[0];
            Ey+=z[1];
          }

        for(i=0; i<piani; i++)
          { computePianoE(i, x, y, z);
            Ex+=z[0];
            Ey+=z[1];
          }

        for(i=0; i<coppie; i++)
          { computeCoppiaE(i, x, y, z);
            Ex+=z[0];
            Ey+=z[1];
          }
        E[0]=Ex;
        E[1]=Ey;
      }


    /**
     * Calcola il campo generato da una carica
     */
    void computeCaricaE(int i, double x, double y, double E[])
      { double r=Functions.hypot(x-x_carica[i], y-y_carica[i]);
        double r2=r*r;
        double r3=r*r2;

        E[0]=FACTOR*carica[i]*(x-x_carica[i])/r3;
        E[1]=FACTOR*carica[i]*(y-y_carica[i])/r3;
      }

    /**
     * Calcola il campo generato da un piano infinito
     */
    void computePianoE(int i, double x, double y, double E[])
      { double s=Math.sin(ang_piano[i]);
        double c=Math.cos(ang_piano[i]);
        double e=0.5*dens_piano[i]/epsilon0;
        double p=(x-x_piano[i])*s-(y-y_piano[i])*c;
        if (p<0)
          e=-e;
        E[0]=e*s;
        E[1]=-e*c;
      }

    /**
     * Calcola il campo generato da una coppia di piastre
     */
    void computeCoppiaE(int i, double x, double y, double E[])
      { double xx, yy;
        if (horiz_coppia[i])
          { xx=x-x_coppia[i];
            yy=y-y_coppia[i];
          }
        else
          { yy=x-x_coppia[i];
            xx=y-y_coppia[i];
          }
        double Ex, Ey;

        double r1, r2;

        r1=Functions.hypot(xx, yy);
        r2=Functions.hypot(ext_coppia[i]-xx, yy);
        Ex=-1/r1+1/r2;
        Ey=(xx/r1 + (ext_coppia[i]-xx)/r2) / yy;

        r1=Functions.hypot(xx, dist_coppia[i]-yy);
        r2=Functions.hypot(ext_coppia[i]-xx, dist_coppia[i]-yy);
        Ex-=-1/r1+1/r2;
        Ey+=(xx/r1 + (ext_coppia[i]-xx)/r2) / (dist_coppia[i]-yy);

        Ex*=dens_coppia[i]*FACTOR;
        Ey*=dens_coppia[i]*FACTOR;

        if (horiz_coppia[i])
          { E[0]=Ex;
            E[1]=Ey;
          }
        else
          { E[1]=Ex;
            E[0]=Ey;
          }
          
      }
          


    /**
     * Calcola il potenziale elettrico in un punto
     */
    public double computeV(double x, double y)
      { double V=0;
        
        int i;
        for(i=0; i<cariche; i++)
          { V+=computeCaricaV(i, x, y);
          }

        for(i=0; i<piani; i++)
          { V+=computePianoV(i, x, y);
          }

        for(i=0; i<coppie; i++)
          { V+=computeCoppiaV(i, x, y);
          }
        return V;
      }


    /**
     * Calcola il potenziale generato da una carica
     */
    double computeCaricaV(int i, double x, double y)
      { double r=Functions.hypot(x-x_carica[i], y-y_carica[i]);

        return FACTOR*carica[i]/r;
      }

    /**
     * Calcola il potenziale generato da un piano infinito
     */
    double computePianoV(int i, double x, double y)
      { double s=Math.sin(ang_piano[i]);
        double c=Math.cos(ang_piano[i]);
        double e=0.5*dens_piano[i]/epsilon0;
        double p=(x-x_piano[i])*s-(y-y_piano[i])*c;
        return -e*Math.abs(p);
      }

    /**
     * Calcola il potenziale generato da una coppia di piastre
     */
    double computeCoppiaV(int i, double x, double y)
      { double xx, yy;
        if (horiz_coppia[i])
          { xx=x-x_coppia[i];
            yy=y-y_coppia[i];
          }
        else
          { yy=x-x_coppia[i];
            xx=y-y_coppia[i];
          }
        double V;

        double r1, r2;

        r1=Functions.hypot(xx, yy);
        r2=Functions.hypot(ext_coppia[i]-xx, yy);
        V=Math.log(ext_coppia[i]-xx+r2)-Math.log(-xx+r1);

        r1=Functions.hypot(xx, dist_coppia[i]-yy);
        r2=Functions.hypot(ext_coppia[i]-xx, dist_coppia[i]-yy);
        V-=Math.log(ext_coppia[i]-xx+r2)-Math.log(-xx+r1);

        V*=dens_coppia[i]*FACTOR;

        return V;
      }

    /**
     * Controlla se un punto e' singolare
     */
    public boolean singularPoint(double x, double y)
      { int i;

        for(i=0; i<cariche; i++)
          if (singularPointCarica(i, x, y))
            return true;

        for(i=0; i<piani; i++)
          if (singularPointPiano(i, x, y))
            return true;

        for(i=0; i<coppie; i++)
          if (singularPointCoppia(i, x, y))
            return true;

        return false;
      }

    /**
     * Controlla se un punto e' singolare per una carica
     */
    boolean singularPointCarica(int i, double x, double y)
      { double r=Functions.hypot(x-x_carica[i], y-y_carica[i]);

        return r<(0.5*pos_width/map.getScaleX());
      }

    /**
     * Controlla se un punto e' singolare per un piano infinito
     */
    boolean singularPointPiano(int i, double x, double y)
      { double s=Math.sin(ang_piano[i]);
        double c=Math.cos(ang_piano[i]);
        double p=(x-x_piano[i])*s-(y-y_piano[i])*c;
        return Math.abs(p)<2/map.getScaleX();
      }

    /**
     * Controlla se un punto e' singolare per una coppia di lastre 
     */
    boolean singularPointCoppia(int i, double x, double y)
      { double xx, yy;
        double eps=2/map.getScaleX();
        if (horiz_coppia[i])
          { xx=x-x_coppia[i];
            yy=y-y_coppia[i];
          }
        else
          { yy=x-x_coppia[i];
            xx=y-y_coppia[i];
          }

        return xx>=eps && 
               xx<=ext_coppia[i]+eps &&
               (Math.abs(yy)<eps || Math.abs(dist_coppia[i]-yy)<eps);
      }

          
    /**
     * Attiva la visualizzazione delle linee di forza
     */
    public void visualizzaLineeDiForza()
      { cleanState();
        visualizza_linee=true;
      }

    /**
     * Visualizza una linea di forza passante per il punto x,y
     */
    void visualizzaLineeDiForza(double x, double y)
      { curs.setCursor(Frame.WAIT_CURSOR);
        if (visualizza_campo)
          { xorArrow(arrow_last_x, arrow_last_y, arrow_last_Ex, arrow_last_Ey);
          }
        visualizzaLineeDiForza(getGraphics(), x, y);
        if (visualizza_campo)
          { xorArrow(arrow_last_x, arrow_last_y, arrow_last_Ex, arrow_last_Ey);
          }
        visualizzaLineeDiForza(getBackgroundGraphics(), x, y);
        curs.setCursor(Frame.CROSSHAIR_CURSOR);
      }

    /**
     * Visualizza una linea di forza passante per il punto x,y
     */
    void visualizzaLineeDiForza(Graphics g, double x, double y)
      { g.setColor(Color.magenta);
        int maxstep=5000;

        ODELineeDiForza ode=new ODELineeDiForza(this);
        ode.setBackwards(false);

        int k;
        for(k=0; k<2; k++)
          { double z[]={x, y};
            ODESolver os=new ODESolver(ode, 0, z, 0.5*zoom, 0.1/map.getScaleX());
            os.setTolerant(true);
            os.setMaxStep(1*zoom);
            os.setMinStep(0.05*zoom);

            int same=0;
            Point pt0=map.realToPixel(x, y);

            int step=0;
            while (step++<maxstep)
              { os.nextStep();
                if (!os.isOk())
                  break;
                Point pt=map.realToPixel(z[0],z[1]);
                if (pt.x!=pt0.x || pt.y!=pt0.y)
                  { g.drawLine(pt0.x, pt0.y, pt.x, pt.y);
                    same=0;
                    pt0=pt;
                  }
                else if (same++>100)
                      break;
                if (pt.x<0 || pt.y<0 || pt.x>dim.width || pt.y>dim.height 
                         || singularPoint(z[0], z[1]))
                  break;

                
              }
            ode.setBackwards(true);
          }

        Rectangle rect=new Rectangle(0,0, dim.width, dim.height);
        paintCariche(g, rect);
        paintPiani(g, rect);
        paintCoppie(g, rect);
      }

    /**
     * Attiva la visualizzazione delle linee equipotenziali
     */
    public void visualizzaLineeEquipotenziali()
      { cleanState();
        visualizza_equip=true;
      }

    /**
     * Visualizza una linea equipotenziale passante per il punto x,y
     */
    void visualizzaLineeEquipotenziali(double x, double y)
      { curs.setCursor(Frame.WAIT_CURSOR);
        if (visualizza_campo)
          { xorArrow(arrow_last_x, arrow_last_y, arrow_last_Ex, arrow_last_Ey);
          }
        visualizzaLineeEquipotenziali(getGraphics(), x, y);
        if (visualizza_campo)
          { xorArrow(arrow_last_x, arrow_last_y, arrow_last_Ex, arrow_last_Ey);
          }
        visualizzaLineeEquipotenziali(getBackgroundGraphics(), x, y);
        curs.setCursor(Frame.CROSSHAIR_CURSOR);
      }

    /**
     * Visualizza una linea equipotenziale passante per il punto x,y
     */
    void visualizzaLineeEquipotenziali(Graphics g, double x, double y)
      { g.setColor(Color.orange);
        int maxstep=5000;

        ODEEquipot ode=new ODEEquipot(this,x,y);
        ode.setBackwards(false);

        int k;
external_loop:
        for(k=0; k<2; k++)
          { double z[]={x, y};
            ODESolver os=new ODESolver(ode, 0, z, 0.5*zoom, 0.3/map.getScaleX());
            os.setTolerant(true);
            os.setMaxStep(1*zoom);
            os.setMinStep(0.1*zoom);

            int same=0;
            Point pt0=map.realToPixel(x, y);
            Point pt00=pt0;
            double dist0=0.0;

            int step=0;
            while (step++<maxstep)
              { os.nextStep();
                if (!os.isOk())
                  break;
                Point pt=map.realToPixel(z[0],z[1]);
                if (pt.x!=pt0.x || pt.y!=pt0.y)
                  { g.drawLine(pt0.x, pt0.y, pt.x, pt.y);
                    same=0;
                    pt0=pt;
                    double dist=Functions.hypot(pt00.x-pt.x, pt00.y-pt.y);
                    if (dist<dist0 && dist<3)
                      { g.drawLine(pt.x, pt.y, pt00.x, pt00.y);
                        break external_loop;
                      }
                    else
                      dist0=dist;
                  }
                else if (same++>100)
                      break;
                if (pt.x<0 || pt.y<0 || pt.x>dim.width || pt.y>dim.height 
                         || singularPoint(z[0], z[1]))
                  break;

                
              }
            ode.setBackwards(true);
          }

        Rectangle rect=new Rectangle(0,0, dim.width, dim.height);
        paintCariche(g, rect);
        paintPiani(g, rect);
        paintCoppie(g, rect);
      }


    /**
     * Abilita l'attesa delle condizioni iniziali del moto
     */
    public void visualizzaMoto()
      { //System.out.println("Qiooo");
        cleanState();
        visualizza_moto=true;
      }

    /** 
     * Prepara la visualizzazione del moto di una particella
     */
    void visualizzaMoto(Animation animation)
      { //System.out.println("poip");
        this.animation=null;
        if (animation!=null)
          cleanState();
        this.animation=animation;
        //System.out.println("Ci Arrivo");
        controller.deliverEvent(
                     new Event(controller, Event.ACTION_EVENT, "*Moto*"));
        //System.out.println("evento");

        showSprite(0, animation!=null);
        step=0;
        if (animation!=null)
          { updateBeforeResize();
          }
       //System.out.println("poip2");



      }

    /**
     * Controlla se c'e' un'animazione visualizzata
     */
    public boolean motoVisualizzato()
      { return animation!=null;
      }

    Point getBallPos(int step)
      { if (animation==null)
          return new Point(-100, -100);
        else
          { double x=animation.getX(step);
            double y=animation.getY(step);
            Point pt=map.realToPixel(x, y);
            pt.x-=ball_width/2;
            pt.y-=ball_height/2;
            return pt;
          }
      }


    /**
     * Avvia l'animazione del moto
     */
    public synchronized void startAnimation()
      { stopAnimation();
        step=0;
        if (animation==null)
          return;
        isRunning.set(true);
        thread=new Thread(this);
        thread.start();
      }

    /**
     * Ferma l'animazione
     */
    public synchronized void stopAnimation()
      {
        if (thread!=null) {
//          thread.stop();
          isRunning.set(false);
          thread.interrupt(); // this is to wakeup sleep()
        }
        thread=null;
        curs.setCursor();
      }


    /**
     * Esegue l'animazione
     */
    public void run()
      { step=0;
        int i;

        if (animation==null)
          return;   
        Graphics g=getGraphics();
        Graphics bg=getBackgroundGraphics();

        g.setColor(Color.lightGray);
        bg.setColor(Color.lightGray);

        double ts=animation.getTimeScale();

        animation.runUpTo(animation.getStopTime()+ts*duration, ts*frameDelay);
        Point pt0=map.realToPixel(animation.getX(0), animation.getY(0));
        for(i=0; i<animation.getStepCount() && isRunning.get(); i++)
          { step=i;
            Point pt=map.realToPixel(animation.getX(step), 
                                     animation.getY(step));
            bg.drawLine(pt0.x, pt0.y, pt.x, pt.y);
            g.drawLine(pt0.x, pt0.y, pt.x, pt.y);
            Point pb=getBallPos(step);
            moveSprite(0, pb.x, pb.y);
            pt0=pt;

            try
              { Thread.sleep((int)(1000*frameDelay));
              }
            catch (InterruptedException e)
              {
              }
          }

         g.dispose();
         bg.dispose();
         thread=null;
         curs.setCursor();
         repaint();
      }


    public Animation getAnimation()
      { return animation;
      }

  }
