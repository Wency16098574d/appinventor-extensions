package edu.polyu.appinventor.positioning.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import edu.polyu.appinventor.positioning.Beacon;
import edu.polyu.appinventor.positioning.Location;

public class OverlapArea implements Algorithm{
  public Location calPosition(List<Beacon> BeaconList) {
    int N = BeaconList.size();
    double minX = BeaconList.get(0).getX(), maxX = BeaconList.get(0).getX();
    double minY = BeaconList.get(0).getY(), maxY = BeaconList.get(0).getY();
    double strongestRssi = BeaconList.get(0).getRssi();
    int index = 0;
    List<Location> resL= new ArrayList();
    //get the square
    for(int i = 1; i < N; i++){
      if(BeaconList.get(i).getX() < minX) minX = BeaconList.get(i).getX();
      if(BeaconList.get(i).getX() > maxX) maxX = BeaconList.get(i).getX();
      if(BeaconList.get(i).getY() < minY) minY = BeaconList.get(i).getY();
      if(BeaconList.get(i).getY() > maxY) maxY = BeaconList.get(i).getY();
      if(BeaconList.get(i).getRssi() > strongestRssi) {   strongestRssi = BeaconList.get(i).getRssi();    index = i;}
    }
    //add all the points to the list
    double spaceInterval = Math.max(maxX - minX, maxY - minY) / 100;
    for(double a = minX; a < maxX; a += spaceInterval){
      for(double b = minY; b < maxY; b += spaceInterval){
        resL.add(new Location(a, b));
      }
    }
    //remove the points which do not satisfy the range of the circles
    double curX, curY, curR;
    for(int i = 0; i < N; i++){
      if(BeaconList.get(i).getDistance() == 0) continue;
      curX = BeaconList.get(i).getX();
      curY = BeaconList.get(i).getY();
      curR = BeaconList.get(i).getDistance();
      Iterator<Location> resLIterator = resL.iterator();
      Location curL;
      if(1.4 < curR && curR < 1.6){
        while(resLIterator.hasNext()){
          curL = resLIterator.next();
          if(Math.pow(curL.getLocX() - curX, 2) + Math.pow(curL.getLocY() - curY, 2) > Math.pow(1.5, 2))
            resLIterator.remove();
        }
      }
      else if(4.4 < curR && curR < 4.6){
        while(resLIterator.hasNext()){
          curL = resLIterator.next();
          if(Math.pow(curL.getLocX() - curX, 2) + Math.pow(curL.getLocY() - curY, 2) < Math.pow(1.5, 2) || Math.pow(curL.getLocX() - curX, 2) + Math.pow(curL.getLocY() - curY, 2) > Math.pow(6, 2))
            resLIterator.remove();
        }
      }
      else if(5.9 < curR && curR < 6.1){
        while(resLIterator.hasNext()){
          curL = resLIterator.next();
          if(Math.pow(curL.getLocX() - curX, 2) + Math.pow(curL.getLocY() - curY, 2) < Math.pow(curR, 2))
            resLIterator.remove();
        }
      }
      else {  return new Location(-1, -1);   }
    }
    //find the minX maxX, minY and maxY in the resL list
    if(resL.size() == 0) {return new Location(BeaconList.get(index).getX(), BeaconList.get(index).getY());}
    double resMinX = resL.get(0).getLocX(), resMaxX = resL.get(0).getLocX();
    double resMinY = resL.get(0).getLocY(), resMaxY = resL.get(0).getLocY();
    for(int i = 1; i < resL.size(); i++){
      if(resL.get(i).getLocX() < resMinX) resMinX = resL.get(i).getLocX();
      if(resL.get(i).getLocX() > resMaxX) resMaxX = resL.get(i).getLocX();
      if(resL.get(i).getLocY() < resMinY) resMinY = resL.get(i).getLocY();
      if(resL.get(i).getLocY() > resMaxY) resMaxY = resL.get(i).getLocY();
    }
    return new Location((resMinX + resMaxX) / 2, (resMinY + resMaxY) / 2);
  }
}
