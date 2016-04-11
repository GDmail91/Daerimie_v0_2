package org.daelimie.test.daelimie;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by YS on 2016-04-11.
 */
public interface LocateItemCallback {
    void itemChange(int position);
    void markingLocate(LatLng latLng);
}
