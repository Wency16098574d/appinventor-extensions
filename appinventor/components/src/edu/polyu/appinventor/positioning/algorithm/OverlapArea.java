package edu.polyu.appinventor.positioning.algorithm;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import edu.polyu.appinventor.positioning.Beacon;
import edu.polyu.appinventor.positioning.Location;

public class OverlapArea implements Algorithm{

//    double pX1;
//    double pY1;
//
//    double pX2;
//    double pY2;
//
//    double minX;
//    double maxX;
//    double minY;
//    double maxY;

    public void calPosition(List<Beacon> BeaconList, Location loc) {
        int N = BeaconList.size();
        final double interval = 0.1;
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
        for(double a = minX; a < maxX; a += interval){
            for(double b = minY; b < maxY; b += interval){
                resL.add(new Location(a, b));
            }
        }
        //remove the points which do not satisfy the range of the circles

//        double curX = BeaconList.get(0).getX(), curY = BeaconList.get(0).getY(), curR = BeaconList.get(0).getDistance();
//        if(1.4 < curR && curR < 1.6){
//            for(double a = minX; a < maxX; a += interval)
//                for(double b = minY; b < maxY; b += interval)
//                    if(Math.pow(a - curX, 2) + Math.pow(b - curY, 2) <= Math.pow(curR, 2))
//                        resL.add(new Location(a, b));
//        }
//        else if(4.4 < curR && curR < 4.6){
//            for(double a = minX; a < maxX; a += interval)
//                for(double b = minY; b < maxY; b += interval)
//                    if(Math.pow(a - curX, 2) + Math.pow(b - curY, 2) >= Math.pow(1.5, 2) && Math.pow(a - curX, 2) + Math.pow(b - curY, 2) <= Math.pow(6, 2))
//                        resL.add(new Location(a, b));
//        }
//        else if(5.9 < curR && curR < 6.1){
//            for(double a = minX; a < maxX; a += interval)
//                for(double b = minY; b < maxY; b += interval)
//                    if(Math.pow(a - curX, 2) + Math.pow(b - curY, 2) >= Math.pow(curR, 2))
//                        resL.add(new Location(a, b));
//        }
        //remove points which does not satisfy following circles
        double curX, curY, curR;
        for(int i = 0; i < N; i++){
            if(BeaconList.get(i).getDistance() == 0) continue;
            curX = BeaconList.get(i).getX();
            curY = BeaconList.get(i).getY();
            curR = BeaconList.get(i).getDistance();
            if(1.4 < curR && curR < 1.6){
                for(int j = 0; j < resL.size(); j++) {
                    if(resL.size() == 0){
                        loc.setLocation(555, 555);
                        return;
                    }
                    Location curL = resL.get(j);
                    if (Math.pow(curL.getLocX() - curX, 2) + Math.pow(curL.getLocY() - curY, 2) > Math.pow(1.5, 2))
                        resL.remove(j--);
                }
            }
            else if(4.4 < curR && curR < 4.6){
                for(int j = 0; j < resL.size(); j++) {
                    if(resL.size() == 0){
                        loc.setLocation(666, 666);
                        return;
                    }
                    Location curL = resL.get(j);
                    if (Math.pow(curL.getLocX() - curX, 2) + Math.pow(curL.getLocY() - curY, 2) < Math.pow(1.5, 2) || Math.pow(curL.getLocX() - curX, 2) + Math.pow(curL.getLocY() - curY, 2) > Math.pow(6, 2))
                        resL.remove(j--);
                }
            }
            else if(5.9 < curR && curR < 6.1){
                for(int j = 0; j < resL.size(); j++) {
                    if(resL.size() == 0){
                        loc.setLocation(777, 777);
                        return;
                    }
                    Location curL = resL.get(j);
                    if (Math.pow(curL.getLocX() - curX, 2) + Math.pow(curL.getLocY() - curY, 2) < Math.pow(curR, 2))
                        resL.remove(j--);
                }
            }
            else {loc.setLocation(444, 444);return;   }
        }
        //find the minX maxX, minY and maxY in the resL list
        if(resL.size() == 0) {loc.setLocation(666, 666);    return;}
        double resMinX = resL.get(0).getLocX(), resMaxX = resL.get(0).getLocX();
        double resMinY = resL.get(0).getLocY(), resMaxY = resL.get(0).getLocY();
        for(int i = 1; i < resL.size(); i++){
            if(resL.get(i).getLocX() < resMinX) resMinX = resL.get(i).getLocX();
            if(resL.get(i).getLocX() > resMaxX) resMaxX = resL.get(i).getLocX();
            if(resL.get(i).getLocY() < resMinY) resMinY = resL.get(i).getLocY();
            if(resL.get(i).getLocY() > resMaxY) resMaxY = resL.get(i).getLocY();
        }
        loc.setLocation((resMinX + resMaxX) / 2, (resMinY + resMaxY) / 2);
    }



//    //https://blog.csdn.net/zx3517288/article/details/53326420
//    private boolean calIntersection(double x1, double y1, double r1, double x2, double y2, double r2){
//        double L = Math.sqrt(Math.pow(x2 - x1) + Math.pow(y2 - y1));
//
//        if(L <= r1 + r2) return false;
//        double K1 = (y2 - y1) / (x2- x1);
//        double K2 = (x1 - x2) / (y2 - y1);
//        double AE = (Math.pow(r1) - Math.pow(r2) + Math.pow(L)) / (2 * L);
//        double x0 = x1 + (AE / L) * (x2 - x1);
//        double y0 = y1 + (AE / L) * (y2 - y1);
//        double CE = Math.sqrt(Math.pow(r1) - Math.pow(AE));
//        double EF = CE / Math.sqrt(1 + Math.pow(K2));
//
//        pX1 = x0 - EF;
//        pY1 = y0 + K2(resX1 - x0);
//        pX2 = x0 + EF;
//        pY2 = y0 + K2(resX2 - x0);
//        return true;
//    }
}