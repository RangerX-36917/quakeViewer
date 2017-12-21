package quakeViewer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DataSet {
    private ArrayList<earthQuake> quakes = new ArrayList<>();
    private TreeSet<String> regions = new TreeSet<>();
    private Statement statement = null;
    private Connection connection = null;

    public DataSet(String Region, String fromDate, String toDate, double mag) {
        loadData(Region, fromDate, toDate, mag);
        //printElement(quakes);
        //System.out.println(quakes.size());
    }

    public void printElement() {
        for (Object quake : quakes) {
            System.out.println(quake.toString());
        }
    }
    private void setConnection() {
        Properties defProp = new Properties();
        defProp.put("path", "");
        Properties dbProp = new Properties(defProp);
        String path = System.getProperty("user.dir");
        try (BufferedReader conf = new BufferedReader(new FileReader(path + "/src/preference.cnf"))) {
            dbProp.load(conf);
        } catch (IOException e) {
            System.err.println("Using default path for data source");
        }
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbProp.getProperty("path"));
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (SQLException sqlE) {
            System.out.println(sqlE);
            sqlE.printStackTrace();
        }

    }
    private void closeConnection() {
        try {

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
    private void loadData(String Region, String fromDate, String toDate, double mag) {
        //establish jdbc connection

        ResultSet resultSet = null;


        //load data from sqlite database
        try {
            setConnection();

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


                earthQuake ek = new earthQuake(id, date, latitude, longitude, depth, magnitude, region, areaID);
                regions.add(region);
                quakes.add(ek);

            }
        } catch (SQLException sqlE) {
            System.out.println(sqlE);
            sqlE.printStackTrace();
        } finally {
            closeConnection();
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


}
