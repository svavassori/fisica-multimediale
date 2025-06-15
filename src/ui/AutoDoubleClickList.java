package ui;

import numeric.*;
import util.*;

import java.awt.*;

public class AutoDoubleClickList extends List {
    public AutoDoubleClickList () {
        resize(getPreferredSize());
    }

    public boolean handleEvent(Event  evt) {
        if (/*evt.getSource()==this &&*/ evt.id==Event.LIST_SELECT) {
            evt=new Event(this,Event.ACTION_EVENT, "");
            postEvent (evt);
            return true;
        }
        else
            return super.handleEvent(evt); 
    }

    public Dimension preferredSize() {
        return getPreferredSize();
    }
    
    public Dimension minimumSize() {
        return getMinimumSize();
    }

    public Dimension getMinimumSize() {
        return getMinimumSize(getItemCount());
    }
    public Dimension getMinimumSize(int rows) {
        Dimension d=super.getMinimumSize(rows);
        d.width=Math.max(d.width*2,200); 
        d.height=Math.max(d.height+8,40);
        d.height=Math.min(d.height,100);
        return d;
    }
    public Dimension getPreferredSize() {        
        return getPreferredSize(getItemCount());
    }
    public Dimension getPreferredSize(int rows) {
        Dimension d=super.getPreferredSize(rows);
        d.width=Math.max(d.width*2,200); 
        d.height=Math.max(d.height+8,40);
        d.height=Math.min(d.height,100);
        return d;
    }
}