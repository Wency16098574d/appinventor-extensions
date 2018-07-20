package edu.polyu.appinventor.positioning;

public class Location{
  double locX;
  double locY;

  public Location(double locX, double locY){
    this.locX = locX;
    this.locY = locY;
  }

  public void setLocation(double locX, double locY){
    this.locX = locX;
    this.locY = locY;   }

  public double getLocX(){
    return locX;
  }

  public double getLocY(){
    return locY;
  }
}