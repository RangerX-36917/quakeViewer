package quakeViewer;

import java.io.BufferedReader;
import java.io.File;
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
        Properties defProp = new Properties();
        defProp.put("db path", "");
        defProp.put("csv path", "");
        defProp.put("source","sqlite");
        Properties dataProp = new Properties(defProp);
        Properties sysProp = new Properties(defProp);
        String path = System.getProperty("user.dir");try (BufferedReader conf = new BufferedReader(new FileReader(path + "/src/preference.cnf"))) {
            sysProp.load(conf);
        } catch (IOException e) {
            System.err.println("Using default path for data source");
        }
        if(sysProp.getProperty("source").equals("csv")) {

            createCSVDB(path, dataProp);
        } else {
            setDBConnection(path, dataProp);
            //printElement(quakes);
            //System.out.println(quakes.size());
        }

        DataCollector dataCollector = new DataCollector();

        try {
            insertList(dataCollector.getQuakes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadData(Region, fromDate, toDate, mag);
        closeConnection();
        File file = new File(path + "/src/tempSqlite.sqlite");
        file.delete();
    }
    public TreeSet<String> getRegions() {
        return regions;
    }
    public ArrayList<earthQuake> getQuakes() {
        return quakes;
    }
    public void printElement() {
        for (Object quake : quakes) {
            System.out.println(quake.toString());
        }
    }
    private void setDBConnection(String path, Properties dbProp){
        try (BufferedReader conf = new BufferedReader(new FileReader(path + "/src/preference.cnf"))) {
            dbProp.load(conf);
        } catch (IOException e) {
            System.err.println("Using default path for data source");
        }
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbProp.getProperty("dbPath"));
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
        }
    }
    public void insertList(ArrayList<earthQuake> _quakes) throws Exception {
        System.out.println("********insert " + _quakes.size() + " elements *********");
        Statement stmt = connection.createStatement();
        String insert;
        statement.clearBatch();
        for(int i = 0; i < _quakes.size(); i++) {
            earthQuake e = _quakes.get(i);
            //System.out.println("insert: # " +i+" " + e.toString());
            insert = "INSERT INTO quakes VALUES (";
            int rID = 123 + i;
            insert += e.getId() + ", '" + e.getUTC_date() + "'," +e.getLatitude() + "," + e.getLongitude() + "," + e.getDepth() + "," +e.getMagnitude() + ",'" + e.getRegion()+"',"+rID+")";
            stmt.addBatch(insert);
            if(i % 100 == 0) {
                try{
                    System.out.println("execute batch");
                    stmt.executeBatch();
                    stmt.clearBatch();
                } catch (Exception x) {

                }
            }
        }
        try{
            System.out.println("execute batch");
            stmt.executeBatch();
            stmt.clearBatch();
        } catch (Exception x) {

        }

    }

    public void createCSVDB(String path, Properties dbProp){
        System.out.println("using csv");
        try {
            //System.out.println(path);
            BufferedReader conf = new BufferedReader(new FileReader(path + "/src/preference.cnf"));
            dbProp.load(conf);
            //System.out.println(dbProp.getProperty("csvPath"));
        } catch (IOException e) {
            System.err.println("Using default path for data source");
        }

        try {

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + path + "/src/tempSqlite.sqlite");
            statement = connection.createStatement();
            try{
                statement.executeUpdate(" CREATE TABLE quakes (id int ,UTC_date VARCHAR (30), latitude float , " +
                        "longitude float ,depth int ,magnitude float ,region varchar(100), area_id int)");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }

            File dataFile = new File(dbProp.getProperty("csvPath"));

            try {
                BufferedReader br = new BufferedReader(new FileReader(dataFile));
                String line;
                br.readLine();
                ArrayList<earthQuake> list = new ArrayList<>();
                int counter = 0;
                while((line = br.readLine()) != null) {
                    counter++;
                    String[] elements = line.split(",");
                    earthQuake e = new earthQuake(elements[0] , elements[1].substring(1, elements[1].length() - 1) , elements[2],
                            elements[3] ,elements[4],elements[5], elements[6].substring(1,elements[6].length() - 1),"323");

                    list.add(e);

                }
                insertList(list);


            } catch (Exception e) {

            }

            String filePath = dbProp.getProperty("csv path");
            //System.out.println(filePath);

        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (SQLException sqlE) {
            System.out.println(sqlE);
            sqlE.printStackTrace();
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
            String s = "AND (UTC_date BETWEEN '" +from + " 00:00:00.1 " + "' AND '" + to + " 00:00:01 " + "' ) ";
            return s;
        } else return "";
    }


}
