package edu.polyu.appinventor.positioning.algorithm;

import java.util.List;
import edu.polyu.appinventor.positioning.Beacon;
import edu.polyu.appinventor.positioning.Location;
import edu.polyu.appinventor.positioning.algorithm.trilateration.*;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Trilateration implements Algorithm{

  //https://github.com/lemmingapex/trilateration
  public void calPosition(List<Beacon> BeaconList, Location loc){
    //preparing parameters
    int N = BeaconList.size(), n = 0, j = 0;
    for(int i = 0; i < N; i++)  if(BeaconList.get(i).getDistance() != 0) n++;
    double[][] positions = new double[n] [2];
    double[] distances = new double[n];
    for(int i = 0; i < N; i++){
      if(BeaconList.get(i).getDistance() == 0) continue;
      positions[j][0] = Double.parseDouble(String.valueOf(BeaconList.get(i).getX()));
      positions[j][1] = Double.parseDouble(String.valueOf(BeaconList.get(i).getY()));
      distances[j++] = Double.parseDouble(String.valueOf(BeaconList.get(i).getDistance()));
    }

    //the algorithm
    NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
    Optimum optimum = solver.solve();

    // error and geometry information; may throw SingularMatrixException depending the threshold argument provided
    RealVector standardDeviation = optimum.getSigma(0);
    RealMatrix covarianceMatrix = optimum.getCovariances(0);

    // the answer
    double[] centroid = optimum.getPoint().toArray();
    loc.setLocation(centroid[0], centroid[1]);
  }
}