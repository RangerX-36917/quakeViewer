package quakeViewer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Properties;

public class dataLoader {
    public ArrayList<earthQuake> quakes = new ArrayList<>();

    public dataLoader() {
        loadData();
        //printElement(quakes);
        //System.out.println(quakes.size());
    }

    public void printElement() {
        Iterator iter = quakes.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next().toString());
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
        //load data from sqlite database
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbProp.getProperty("path"));
            statement = connection.createStatement();
            String q1 = "SELECT id, UTC_date, latitude, longitude, depth, magnitude, region, area_id FROM quakes";
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
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
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
