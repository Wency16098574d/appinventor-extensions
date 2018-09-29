package edu.polyu.appinventor.positioning.postprocessor;
import java.util.Calendar;
import java.util.List;
import edu.polyu.appinventor.positioning.*;

public class Validation implements Postprocessor{
    public boolean processing(Positioning pos, Location newLoc) {
        if(newLoc.getLocX() > pos.MaxX() || newLoc.getLocX() < pos.MinX() || newLoc.getLocY() > pos.MaxY() || newLoc.getLocY() > pos.MinX())
            return false;

        long curTime = Calendar.getInstance().getTimeInMillis();
        double distance = Math.pow(Math.pow(newLoc.getLocX() - pos.locX(), 2) + Math.pow(newLoc.getLocY() - pos.locY(), 2), 0.5);
        if(distance / ((pos.getLastUpdateTime() - curTime) / 2000) > 2 )
            return false;

        return true;
    }
}
