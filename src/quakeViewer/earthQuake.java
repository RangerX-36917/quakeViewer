package quakeViewer;

import java.util.Random;

public class earthQuake {
    private int id, areaID, depth;
    private float latitude, longitude, magnitude;
    private String UTC_date, region;

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
    public earthQuake(String id, String time, String latitude, String longitude, String depth, String magnitude, String region, String areaID) {
        float lat = Float.valueOf(latitude);
        float log = Float.valueOf(longitude);
        int dep = Integer.valueOf(depth);
        float mag = Float.valueOf(magnitude);
        Random r = new Random();
        int x = r.nextInt() % 1000;
        this.id = Integer.valueOf(id);
        this.UTC_date = time;
        this.latitude = lat;
        this.longitude = log;
        this.depth = dep;
        this.magnitude = mag;
        this.region = region;
        this.areaID = 32;


    }

    public String toString() {
        return id + " " + UTC_date + " " + latitude + " " + longitude + " " + depth + " " + magnitude + " " + region;
    }

    public int compareTo(Object o) {
        return 1;
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
