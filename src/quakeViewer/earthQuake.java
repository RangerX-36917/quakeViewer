package quakeViewer;

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
