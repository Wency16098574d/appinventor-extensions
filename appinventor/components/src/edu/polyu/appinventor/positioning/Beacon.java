package edu.polyu.appinventor.positioning;

import java.util.ArrayList;
import java.util.List;

public class Beacon {
  private String ID;
  private double X;
  private double Y;
  private List<Integer> RecordList;
  private double Rssi;
  private double Distance;

  public Beacon(String ID, double X, double Y){
    this.ID = new String(ID);
    this.X = X;
    this.Y = Y;
    RecordList = new ArrayList<Integer>();
    Rssi = 0;
    Distance = 0;
  }

  public String getBeacon(){  return ID + "," + X + "," + Y;  }

  public String getID(){  return ID;   }

  public double getX(){    return X;   }

  public double getY(){    return Y;   }

  public List<Integer> getRecordList(){    return RecordList;    };

  public double getRssi(){   return Rssi;    }

  public double getDistance(){    return Distance;    }

  public void setX(double X){    this.X = X; }

  public void setY(double Y){    this.Y = Y; }

  public void setRssi(double Rssi){   this.Rssi = Rssi;   }

  public void setDistance(double Distance){   this.Distance = Distance;   }

  public void addRecord(int record){  this.RecordList.add(record);    }
}