package edu.polyu.appinventor.positioning.postprocessor;

import java.util.List;
import edu.polyu.appinventor.positioning.*;

public interface Postprocessor{
    boolean processing(Positioning pos, Location newLoc);
}