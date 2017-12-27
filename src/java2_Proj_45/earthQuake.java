package java2_Proj_45;

/**
 * Description: The class for constructing earthquake objects
 * @author 11612028 CHEN Shijie
 */

public class earthQuake {
    private int id, areaID, depth;
    private float latitude, longitude, magnitude;
    private String UTC_date, region;

    /**
     *
     * @param id earthquake id
     * @param date earthquake time
     * @param latitude earthquake latitude
     * @param longitude earthquake longitude
     * @param depth earthquake depth
     * @param magnitude earthquake magnitude
     * @param region earthquake region
     * @param areaID area ID for region
     */
    public earthQuake(int id, String date, float latitude, float longitude, int depth, float magnitude, String region, int areaID) {
        this.id = id;
        this.UTC_date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.depth = depth;
        this.magnitude = magnitude;
        this.region = region;
        this.areaID = areaID;
    }

    public String toString() {
        return id + " " + UTC_date + " " + latitude + " " + longitude + " " + depth + " " + magnitude + " " + region;
    }

    public int getId() {
        return id;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public int getDepth() {
        return depth;
    }

    public float getMagnitude() {
        return magnitude;
    }

    public String getUTC_date() {
        return UTC_date;
    }

    public String getRegion() {
        return region;
    }

}
