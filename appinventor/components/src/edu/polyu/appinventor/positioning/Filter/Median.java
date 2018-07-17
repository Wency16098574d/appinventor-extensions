package edu.polyu.appinventor.positioning.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import edu.polyu.appinventor.positioning.Beacon;

public class Median implements Filter{

    public int filtering(List<Beacon> BeaconList) {
        int N = BeaconList.size(), num = 0, m;
        List<Integer> RecordList;

        for(int i = 0; i < N; i++) {
            RecordList = BeaconList.get(i).getRecordList();
            m = RecordList.size();

            if(m == 0)  {   BeaconList.get(i).setRssi(0); continue;   }
            Collections.sort(RecordList);
            if(m % 2 != 0) BeaconList.get(i).setRssi(RecordList.get(m/2));
            else    BeaconList.get(i).setRssi((RecordList.get(m/2) + RecordList.get(m/2 + 1)) / 2);
            num++;
        }
        return num;
    }
}