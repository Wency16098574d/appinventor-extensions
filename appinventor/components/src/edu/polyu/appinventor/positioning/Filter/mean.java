package edu.polyu.appinventor.positioning.filter;

import java.util.List;
import edu.polyu.appinventor.positioning.Beacon;

public class Mean implements Filter{
  public int filtering(List<Beacon> BeaconList) {
    int  N = BeaconList.size(), num = 0, m, temp;
    List<Integer> RecordList;
    for (int i = 0; i < N; i++) {
      RecordList = BeaconList.get(i).getRecordList();
      m = RecordList.size();
      temp = 0;
      if (m == 0) { BeaconList.get(i).setRssi(0); continue; }
      for (int j = 0; j < m; j++) temp += RecordList.get(j);
      BeaconList.get(i).setRssi(temp / m);
      num++;
    }
    return num;
  }
}
