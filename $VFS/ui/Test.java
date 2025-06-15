package ui;

import java.awt.*;

class Test extends Frame
  { public static void main(String argv[])
      { Frame f=new Test();
        f.resize(500, 300);
        f.move(200, 200);
        f.show();
      }

    Test()
      { super("Test");
        PropLayout lm=new PropLayout();
        setLayout(lm);
        PropConstraints c;

        Button b1=new Button("Bottone 1");
        add(b1);
        c=new PropConstraints();
        c.left.set(30);
        c.top.set(30);
        c.width.setLeave();
        lm.setConstraints(b1, c);
        b1.resize(30,30);

        Button b2=new Button("Bottone 2");
        add(b2);
        c=new PropConstraints();
        c.below(b1, 5, PropConstraint.LEFT);
        c.width.set(b1, PropConstraint.WIDTH, 200);
        lm.setConstraints(b2, c);
        b2.resize(30,30);


        Button b3=new Button("Bottone 3");
        add(b3);
        c=new PropConstraints();
        c.rightOf(b2, 20, PropConstraint.BOTTOM);
        c.top.set(b1, PropConstraint.TOP);
        c.width.set(20, b1, PropConstraint.WIDTH);
        lm.setConstraints(b3, c);
        b3.resize(30,30);

        c=new PropConstraints();
        c.width.set(30, b3, PropConstraint.RIGHT);
        c.height.set(30, b3, PropConstraint.BOTTOM);
        lm.setConstraints(this, c);

      }
  }
