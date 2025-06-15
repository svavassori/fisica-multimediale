package ui;

import java.awt.*;

import java.awt.event.MouseListener;

import java.awt.event.MouseEvent ;

import util.*;
import ui.ImageLabel;
import ui.UserInterface;

/**
 * Implements a message box
 */
public class ExitPanel extends ImageLabel implements MouseListener {
 
    private Window dest=null;
    private Object confirm=null;
    private Object not_confirm=null;

    public ExitPanel(Object _confirm, Object _not_confirm) {
        super (UserInterface.getImageLoader().load("icons/exitpnl.gif"));
        confirm=_confirm;
        not_confirm=_not_confirm;
        setSize(210,154);
        setBackground(Color.white);
        addMouseListener(this);    
    }
    
    public void mousePressed (MouseEvent e) {
        System.out.println("pressed!");
    }    
    public void mouseEntered (MouseEvent e) {
        System.out.println("entered!");
    }    
    public void mouseExited  (MouseEvent e) {                                
        System.out.println("exited!");
    }
    public void mouseReleased(MouseEvent e) {
        System.out.println("release!");
    }
    public void mouseClicked (MouseEvent e) {
        Dimension d=getSize();
        int stx=this.getImageUpperLeftCorner().x;
        int sty=this.getImageUpperLeftCorner().y;
            
        System.out.println("click!");
        if (e.getX()>35+stx && e.getX()<82+stx && e.getY()>99+sty && e.getY()<132+sty) {
            deliverEvent(new Event(dest, Event.ACTION_EVENT, confirm));
        } else if (e.getX()>128+stx && e.getX()<176+stx && e.getY()>99+sty && e.getY()<132+sty) {
            deliverEvent(new Event(dest, Event.ACTION_EVENT, not_confirm));
        }
    }
}
