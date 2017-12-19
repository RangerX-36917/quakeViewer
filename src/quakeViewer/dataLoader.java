package quakeViewer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class dataLoader {
    private ArrayList<earthQuake> quakes = new ArrayList<>();

    public dataLoader() {
        loadData();
        //printElement(quakes);
        //System.out.println(quakes.size());
    }

    public void printElement() {
        for (Object quake : quakes) {
            System.out.println(quake.toString());
        }
    }

    private void loadData() {
        //establish jdbc connection
        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        //load path to data source from .cnf file, if not, use default setting
        Properties defProp = new Properties();
        defProp.put("path", "");
        Properties dbProp = new Properties(defProp);
        String path = System.getProperty("user.dir");
        try (BufferedReader conf = new BufferedReader(new FileReader(path + "/src/preference.cnf"))) {
            dbProp.load(conf);
        } catch (IOException e) {
            System.err.println("Using default path for data source");
        }
        double mag = 0;
        String Region = "", fromDate = "", toDate = "";
        //load data from sqlite database
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbProp.getProperty("path"));
            statement = connection.createStatement();
            String q1 = sql(mag, Region, fromDate,toDate);
            resultSet = statement.executeQuery(q1);
            resultSet.next();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String date = resultSet.getString("UTC_date");
                float latitude = resultSet.getFloat("latitude");
                float longitude = resultSet.getFloat("longitude");
                int depth = resultSet.getInt("depth");
                float magnitude = resultSet.getFloat("magnitude");
                String region = resultSet.getString("region");
                int areaID = resultSet.getInt("area_id");
                //store data into arrayList
                earthQuake ek = new earthQuake(id, date, latitude, longitude, depth, magnitude, region, areaID);
                //System.out.println(ek.toString());
                quakes.add(ek);
            }
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (SQLException sqlE) {
            System.out.println(sqlE);
            sqlE.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    private String sql(double mag, String Region, String fromDate, String toDate) {
        String q1 = "SELECT * FROM quakes WHERE 1=1 ";
        q1 += queryRegion(Region);
        q1 += queryMag(mag);
        q1 += queryDate(fromDate, toDate);
        return q1;
    }
    private String queryMag(double mag) {
        if(mag > 0) {
            String s = "AND (magnitude >= " + mag + " ) ";
            return s;
        }else return "";
    }
    private String queryRegion(String Region) {
        if(!Objects.equals(Region, "")) {
            String s = "AND (region = '" + Region + "' ) ";
            return s;
        } else return "";
    }
    private String queryDate(String from, String to) {
        if(!Objects.equals(from, "") || !Objects.equals(to, "")) {
            String s = "AND (UTC_date BETWEEN '" + from + "' AND '" + to + "' ) ";
            return s;
        } else return "";
    }
    static class idComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            earthQuake e1 = (earthQuake) o1;
            earthQuake e2 = (earthQuake) o2;
            int int1 = ((earthQuake) o1).getId();
            int int2 = ((earthQuake) o2).getId();
            return int1<int2 ? 1:0;
        }
    }

    static class latitudeComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            earthQuake e1 = (earthQuake) o1;
            earthQuake e2 = (earthQuake) o2;
            float f1 = ((earthQuake) o1).getLatitude();
            float f2=  ((earthQuake) o2).getLatitude();
            return f1 < f2 ? 1:0;
        }
    }

    static class longitudeComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            earthQuake e1 = (earthQuake) o1;
            earthQuake e2 = (earthQuake) o2;
            float f1 = ((earthQuake) o1).getLongitude();
            float f2 = ((earthQuake) o2).getLongitude();
            return f1<f2 ? 1:0;
        }
    }

    static class depthComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            earthQuake e1 = (earthQuake) o1;
            earthQuake e2 = (earthQuake) o2;
            float f1 = ((earthQuake) o1).getDepth();
            float f2 = ((earthQuake) o2).getDepth();
            return f1<f2 ? 1:0;
        }
    }

    static class magnitudeComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            earthQuake e1 = (earthQuake) o1;
            earthQuake e2 = (earthQuake) o2;
            float f1 = ((earthQuake) o1).getMagnitude();
            float f2 = ((earthQuake) o2).getMagnitude();
            return f1<f2 ? 1:0;
        }
    }

    static class dateComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            earthQuake e1 = (earthQuake) o1;
            earthQuake e2 = (earthQuake) o2;
            return e1.getUTC_date().compareTo(e2.getUTC_date());
        }
    }

    static class regionComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            earthQuake e1 = (earthQuake) o1;
            earthQuake e2 = (earthQuake) o2;
            return e1.getRegion().compareTo(e2.getRegion());
        }
    }

}
