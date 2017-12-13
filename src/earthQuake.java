public class earthQuake implements Comparable{
    private int id;
    private float latitude, longitude, depth, magnitude;
    private String UTC_date, region;

    public earthQuake(String id, String date, String latitude, String langitude, String depth, String magnitude, String region) {
        String id1 = id.substring(2,id.length());
        this.id = Integer.valueOf(id1).intValue();
        String date1 = date.substring(1, date.length() - 1);
        this.UTC_date = date1;
        this.longitude = Float.valueOf(langitude).floatValue();
        this.latitude = Float.valueOf(latitude).floatValue();
        this.depth = Float.valueOf(depth).floatValue();
        this.magnitude = Float.valueOf(magnitude).floatValue();
        this.region = region .substring(1, region.length()-1);
    }
    public String toString() {
        return id + " " + UTC_date +" "+ latitude+" " + longitude +" " + depth+ " " + magnitude+" " + region;
    }
    public int compareTo(Object o){
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
    public float getDepth() {
        return depth;
    }
    public float getMagnitude() {
        return magnitude;
    }
    public String getUTC_date() {
        return UTC_date;
    }
    public String getRegion(){
        return region;
    }

}
