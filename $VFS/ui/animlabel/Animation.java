package ui.animlabel;

import java.awt.*;

/**
 * Represents an animation for an animated label
 */ 
public interface Animation
  { /**
     * Performs the animation
     * @param g     the Graphics on which the char will be drawn
     * @param frame the number of the animation frame (prop. to time)
     * @param idx   the pos. of the char in the string
     * @param tok   modifiable informations on the character
     */
    void perform(Graphics g, int frame, int idx, Token tok);
  }
