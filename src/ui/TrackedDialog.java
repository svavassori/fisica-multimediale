package ui;

import java.awt.*;

public class TrackedDialog extends Dialog {
    Window p;
    public TrackedDialog(Window  parent, boolean  modal) {
        //super(parent,modal);
        super(WindowsTracker.firstwin,false);        
        if (modal) parent.enable(false);        
        p=parent;
        WindowsTracker.add(this);
    }

    public TrackedDialog(Window  parent, String  title, boolean  modal) {
        //super(parent,title,modal);
        super(WindowsTracker.firstwin,title,false);
//        if (modal) parent.enable(false);
        p=parent;
        WindowsTracker.add(this);
    }

    public void dispose() {
        WindowsTracker.delete(this);
        p.enable(true);
        p.requestFocus();
        super.dispose();
    }
    public void centerOnScreen() {
		Dimension s=getToolkit().getScreenSize();		if (WindowsTracker.fullscreen) {
			setSize(s.width,s.height);
			System.out.print ("Screen size: ");			System.out.println(s);				
		}		
		else
			setSize(512,440);		Dimension d=size();
		move( (s.width-d.width)/2, (s.height-d.height)/2 );    }
    
}