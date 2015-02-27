package com.melvin.apps.materialtests;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Melvin on 2/21/2015.
 */
public class Geometry {


    private Location location;

    private String locationType;

    private Viewport viewport;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The location
     */
    public Location getLocation() {
        return location;
    }

    /**
     *
     * @param location
     * The location
     */

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     *
     * @return
     * The locationType
     */

    public String getLocationType() {
        return locationType;
    }

    /**
     *
     * @param locationType
     * The location_type
     */

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    /**
     *
     * @return
     * The viewport
     */

    public Viewport getViewport() {
        return viewport;
    }

    /**
     *
     * @param viewport
     * The viewport
     */

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }


    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}