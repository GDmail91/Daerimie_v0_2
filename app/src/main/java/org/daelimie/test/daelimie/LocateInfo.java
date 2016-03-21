package org.daelimie.test.daelimie;

import java.net.URLEncoder;

/**
 * Created by YS on 2016-03-17.
 */
public class LocateInfo {
    String geometry;
    String icon;
    String id;
    String name;
    String place_id;
    String reference;
    String scope;
    String types;
    String vicinity;

    public LocateInfo(Object o) {

    }

    public LocateInfo(String geometry, String icon, String id, String name, String place_id, String reference, String scope, String types, String vicinity) {
        this.geometry = geometry;
        this.icon = URLEncoder.encode(icon);
        this.id = id;
        this.name = name;
        this.place_id = place_id;
        this.reference = reference;
        this.scope = scope;
        this.types = types;
        this.vicinity = vicinity;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = URLEncoder.encode(icon);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }


}