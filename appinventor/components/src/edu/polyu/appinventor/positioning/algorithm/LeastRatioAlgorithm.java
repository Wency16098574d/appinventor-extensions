package edu.polyu.appinventor.positioning.algorithm;

  import java.util.List;
  import edu.polyu.appinventor.positioning.Beacon;
  import edu.polyu.appinventor.positioning.Location;

public class LeastRatioAlgorithm implements Algorithm{

  //https://github.com/lemmingapex/trilateration
  public Location calPosition(List<Beacon> BeaconList){
    //preparing parameters
    int N = BeaconList.size(), n = 0, l = 0;
    for(int i = 0; i < N; i++)  if(BeaconList.get(i).getDistance() != 0) n++;
    double[][] positions = new double[n] [2];
    double[] distances = new double[n];
    for(int i = 0; i < N; i++){
      if(BeaconList.get(i).getDistance() == 0) continue;
      positions[l][0] = Double.parseDouble(String.valueOf(BeaconList.get(i).getX()));
      positions[l][1] = Double.parseDouble(String.valueOf(BeaconList.get(i).getY()));
      distances[l++] = Double.parseDouble(String.valueOf(BeaconList.get(i).getDistance()));
    }

    //the algorithm
    final int maxStepNum = 100;
    final double gamma = 0.01;
    final double precision = 0.00001;

    //the algorithm
    double [] errorMatrix = new double [n * (n - 1)];
    double [] [] derivationMatrix = new double [n * (n - 1)] [2];

    int m = 0;
    double x = -1, y = -1;
    double error = 0.0, lastError = 0.0, derivationX = 0.0, derivationY = 0.0;

    for (int k = 0; k < maxStepNum; k++){
      m = 0;
      error = 0;
      derivationX = 0;
      derivationY = 0;
      for (int i = 0; i < n; i++){
        //System.out.print("i: " + i + "\t");
        for (int j = 0; j < n; j++){
          //System.out.print("j: " + j + "\n");
          if (i == j) continue;
          double xi = positions[i][0], yi = positions[i][1];
          double xj = positions[j][0], yj = positions[j][1];
          double di = distances[i], dj = distances[j];
          //System.out.println("xi: " + xi + "\txj: " + xj + "\tdi: " + di + "\tdj: " + dj);
          errorMatrix [m] = (Math.pow(x - xi, 2) + Math.pow(y - yi, 2)) / (Math.pow(x - xj, 2) + Math.pow(y - yj, 2)) - (di * di) / (dj * dj);
          //System.out.println("Error: " + errorMatrix[m]);
          error += errorMatrix[m];
          double temp1 = Math.pow(Math.pow(x - xj, 2) + Math.pow(y - yj, 2), 2);
          derivationMatrix [m] [0] = (2 * x - 2 * xi) * (Math.pow(x - xj, 2) + Math.pow(y - yj, 2)) - (2 * x - 2 * xj) * (Math.pow(x - xi, 2) + Math.pow(y - yi, 2)) / temp1;
          derivationMatrix [m] [1] = (2 * y - 2 * yi) * (Math.pow(x - xj, 2) + Math.pow(y - yj, 2)) - (2 * y - 2 * yj) * (Math.pow(x - xi, 2) + Math.pow(y - yi, 2)) / temp1;;
          //System.out.println("DerivationX: " + derivationMatrix[m] [0]);
          //System.out.println("DerivationY: " + derivationMatrix[m] [1]);
          derivationX += derivationMatrix[m] [0];
          derivationY += derivationMatrix[m++] [1];
        }
      }
      error /= m;
      //System.out.println("Step " + k + ": \tError:" + error);


      if (Math.abs(error) < precision || error == lastError) break;
      lastError = error;
      derivationX /= m;
      derivationY /= m;

      x -= (gamma * derivationX);
      y -= (gamma * derivationY);

    }

    // the answer
    return new Location(x, y);
  }
}