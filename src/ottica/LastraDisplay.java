package ottica;

import java.awt.*;
import ui.*;
import numeric.*;
import util.*;

/**
 * Simulazione di una lastra rifrangente
 */
public class LastraDisplay extends SimulationDisplay 
  { boolean raggio_definito;
    double raggio_x, raggio_y, raggio_ang;
    double angolo_incidente, angolo_rifratto;

    

    public LastraDisplay(Settings settings, PaintableCanvas legenda,
                             StatusDisplayer status)
      { super(settings, legenda, status);
      }

    /**
     * Riaggiorna la finestra a fronte di un cambiamento dei
     * parametri
     */
    protected void cleanUp()
      { raggio_definito=false;
      }


    /**
     * Disegna la legenda
     */
    public void paint(Component com, Graphics g)
      {
        Dimension d=com.size();
        FontMetrics fm=g.getFontMetrics();
        int asc=fm.getAscent();
        int h=fm.getHeight();
        String str="Rifrazione attraverso una lastra";
        int w=fm.stringWidth(str);
        g.setColor(Color.lightGray);
        g.drawString(str, (d.width-w)/2+1, asc+3+1);
        g.setColor(Color.blue);
        g.drawString(str, (d.width-w)/2-2, asc+3-2);
        if (!raggio_definito)
          return;
        g.setColor(Color.black);
        g.drawString("Angolo del raggio incidente: "+
                      Format.format("5.1", angolo_incidente)+"°",
                      20, asc+h+6);
        g.drawString("Angolo del raggio rifratto:  "+
                      Format.format("5.1", angolo_rifratto)+"°",
                      20, asc+2*h+6);
      }

    /**
     * vero se puo' visualizzare un oggetto e la sua immagine
     */
    public boolean canDisplayOggetto()
      { return false;
      }

    /**
     * vero se puo' visualizzare il percorso di un raggio luminoso
     */
    public boolean canDisplayRaggio()
      { return true;
      }

    /**
     * Restituisce true se il punto (in coordinate reali)
     * e' attivo, ovvero puo' essere usato per il posizionamento
     * di un oggetto o un raggio
     */
    protected boolean activePoint(double x, double y)
      { double spessore=settings.lastra_spessore;
        return y>(spessore*1.1*0.5);
      }

    /**
     * Inserisce un nuovo raggio
     */
    protected void inserisciRaggio(double x, double y, double ang)
      { 
        // Riduce l'angolo a [-PI, PI[
        ang=Functions.reduceAngle(ang);

        // Se non e' diretto verso il basso, non disegna nulla
        double sin=Math.sin(ang);
        if (sin>-5e-3)
          return;
     
        raggio_definito=true;
        angolo_incidente=ang+0.5*Math.PI;
        angolo_rifratto=angoloRifrazione(angolo_incidente);
        angolo_incidente*=180/Math.PI;
        angolo_rifratto*=180/Math.PI;
        raggio_x=x;
        raggio_y=y;
        raggio_ang=ang;
        Graphics g=getGraphics();
        Rectangle old=clip(g);
        disegnaRaggio(g);
        clip(g, old);
        legenda.repaint();
      }

    public void paint(Graphics g)
    {   super.paint(g);

        // Disegna la lastra
        double h=settings.lastra_spessore;
        Point pt1=realToPixel(0, h/2);
        Point pt2=realToPixel(0, -h/2);
        Rectangle old=clip(g);
        g.setColor(glassColor);
        g.fillRect(insets.left, pt1.y, dim.width-insets.left-insets.right,
                   pt2.y-pt1.y+1);

        // disegna l'ultimo raggio
        if (raggio_definito)
          disegnaRaggio(g); 
        
        clip(g, old);
      }

      
    void disegnaRaggio(Graphics g)
      { double h=settings.lastra_spessore;
        double ang1=raggio_ang+0.5*Math.PI;
        double ang2=angoloRifrazione(ang1);
        double sin1=Math.sin(ang1);
        double cos1=Math.cos(ang1);
        double sin2=Math.sin(ang2);
        double cos2=Math.cos(ang2);
        double ya=h/2;
        double xa=raggio_x+sin1/cos1*(raggio_y-ya);
        double yb=-h/2;
        double xb=xa+sin2/cos2*h;
        double z[]={0, 0};
        pixelToReal(0, dim.height-insets.bottom, z);
        double yc=z[1];
        double xc=xb+sin1/cos1*(yb-yc);
      
        Point pt0=realToPixel(raggio_x, raggio_y);
        Point pta=realToPixel(xa, ya);
        Point ptb=realToPixel(xb, yb);
        Point ptc=realToPixel(xc, yc);
        g.setColor(colore[coloreRaggio]);
        g.drawLine(pt0.x, pt0.y, pta.x, pta.y);
        g.drawLine(pta.x, pta.y, ptb.x, ptb.y);
        g.drawLine(ptb.x, ptb.y, ptc.x, ptc.y);
      }  

  }
