package org.daelimie.test.daelimie;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by YS on 2016-04-09.
 */
public interface SubwayInfoCallback {
    void subwayDegree(final int confDegree);
    void subwayArrivalList(final JSONArray subArrivalInfo, final JSONObject targetSubInfo);
}

