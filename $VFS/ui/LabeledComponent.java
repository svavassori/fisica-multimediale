package ui;

import java.awt.*;

/**
 * This object is used to associate a Component with its label
 * The label can be placed to the left or to the right, above or
 * below the component. The default is above.
 */
public class LabeledComponent extends Panel
  { public static final int ABOVE=0,
                            NORTH=0,
                            LEFT=1,
                            WEST=1,
                            BELOW=2,
                            SOUTH=2,
                            RIGHT=3,
                            EAST=3;

    public LabeledComponent(Component com, String lab)
      { this(com, lab, ABOVE, null);
      }

    public LabeledComponent(Component com, String lab, int where)
      { this(com, lab, where, null);
      }


    public LabeledComponent(Component com, String lab, int where, Font lab_font)
      { setLayout(new BorderLayout(3, 3));

        add("Center", com);

        String pos[]={"North", "West", "South", "East"};
        Label label=new Label(lab);
        if (lab_font!=null)
          label.setFont(lab_font);
        add(pos[where], label);
      }

    public static void main(String argv[])
      { Frame f=new Frame();
        f.move(100, 100);
        f.setFont(new Font("TimesRoman", Font.ITALIC, 18));
        f.add("Center", new LabeledComponent(
                                 new NumericInput(1, 20, 1, 0.05),
                                 "Pippo",
                                 RIGHT));
        f.show();
        f.pack();
      }
  }
