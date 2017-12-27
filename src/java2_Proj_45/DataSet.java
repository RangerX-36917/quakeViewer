package java2_Proj_45;
/**
 * Description: The class used to load and update data and perform query statements
 * @author 11612028 CHEN Shijie
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class DataSet {
    /**
     * regions get by query
     */
    private TreeSet<String> regions = new TreeSet<>(); //regions get by query
    /**
     * new regions fetched by update
     */
    private TreeSet<String> newRegions = new TreeSet<>(); //new regions fetched by update
    /**
     * new quakes fetched by update
     */
    private ArrayList<earthQuake> newQuakes = new ArrayList<>();
    /**
     * the number of new quakes updated
     */
    private int numNew;
    private Statement statement = null;
    private Connection connection = null;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * constructor
     * establish connection to database, fetch new data from the internet and update the database file
     */
    DataSet() {
        query(1,"","","",0); //update region for choiceBox

        //setup connection to database and fetch latest data from the web
        setConnection();
        String q2 = "SELECT MAX(UTC_date) FROM quakes";
        ResultSet resultSetMaxDate;
        String maxDate;
        try {
            resultSetMaxDate = statement.executeQuery(q2);
            maxDate = resultSetMaxDate.getString(1);
            onlineUpdate(maxDate);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        closeConnection();
    }

    /**
     * fetch latest data from the web
     * @return the number of records fetched
     */
    public int update() {
        numNew = 0;
        setConnection();
        String q2 = "SELECT MAX(UTC_date) FROM quakes";
        ResultSet resultSetMaxDate;
        String maxDate;
        try {
            resultSetMaxDate = statement.executeQuery(q2);
            maxDate = resultSetMaxDate.getString(1);
            onlineUpdate(maxDate);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        closeConnection();
        return numNew;
    }

    /**
     * return regions for the choice box
     * @return regions
     */
    public TreeSet<String> getRegions() {
        return regions;
    }

    /**
     * return new regions for update info
     * @return new regions
     */
    public TreeSet<String> getNewRegions() {return newRegions;}
    /**
     * execute query based on the given conditions
     * @param Region region
     * @param fromDate start of date interval
     * @param toDate end of date interval
     * @param mag minimum magnitude
     * @return an ArrayList of earthquakes wanted
     */
    public ArrayList<earthQuake> query(int refreshChoice, String Region, String fromDate, String toDate, double mag) {
        setConnection();
        if (Region.equals("WORLDWIDE")) {
            Region = ""; //by default, search worldwide earthquakes
        }
        //establish jdbc connection
        ArrayList<earthQuake> ans = new ArrayList<>();
        ResultSet resultSet;


        //load data from sqlite database
        try {

            String q1 = sql(mag, Region, fromDate,toDate);
            resultSet = statement.executeQuery(q1);

            while (resultSet.next()) {
                //get data from result set
                int id = resultSet.getInt("id");
                String date = resultSet.getString("UTC_date");
                float latitude = resultSet.getFloat("latitude");
                float longitude = resultSet.getFloat("longitude");
                int depth = resultSet.getInt("depth");
                float magnitude = resultSet.getFloat("magnitude");
                String region = resultSet.getString("region");
                int areaID = resultSet.getInt("area_id");

                earthQuake ek = new earthQuake(id, date, latitude, longitude, depth, magnitude, region, areaID);
                if(refreshChoice == 1) regions.add(region);
                ans.add(ek);

            }
        } catch (SQLException sqlE) {
            System.err.println(sqlE.getMessage());
        }
        closeConnection();
        return ans;
    }

    /**
     * set jdbc connection
     */
    private void setConnection() {
        //read configuration file
        Properties defProp = new Properties();
        defProp.put("path", "");
        Properties dbProp = new Properties(defProp);
        String path = System.getProperty("user.dir");
        try (BufferedReader conf = new BufferedReader(new FileReader(path + "/src/preference.cnf"))) {
            dbProp.load(conf);
        } catch (IOException e) {
            System.err.println("Using default path for data source");
        }
        //establish database connection
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbProp.getProperty("path"));
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    /**
     * close jdbc connection
     */
    private void closeConnection() {
        try {

            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * insert quake data fetched from the web page into database, region id will be auto-generated by the database
     * @param quakes data fetched
     * @throws Exception throws sql insertion exception
     */
    private void insertList(ArrayList<earthQuake> quakes) throws Exception {
        Statement stmt = connection.createStatement();
        String insert;
        for(int i = 0; i < quakes.size(); i++) {
            earthQuake e = quakes.get(i);
            insert = "INSERT INTO quakes VALUES (";
            insert +=  e.getId() + ", '" + e.getUTC_date() + "'," +e.getLatitude() + "," + e.getLongitude() + "," + e.getDepth() + "," +e.getMagnitude() + ",'" + e.getRegion().replace('\'','"')+"',"+ "NULL)";
            stmt.executeUpdate(insert);
        }

    }

    /**
     * create sql query command based on given parameters
     * @param mag magnitude
     * @param Region region
     * @param fromDate start of date interval
     * @param toDate end of date interval
     * @return sql query command
     */
    private String sql(double mag, String Region, String fromDate, String toDate) {
        String q1 = "SELECT * FROM quakes WHERE 1=1 ";
        q1 += queryRegion(Region);
        q1 += queryMag(mag);
        q1 += queryDate(fromDate, toDate);
        return q1;
    }
    private String queryMag(double mag) {
        if(mag > 0) {
            return "AND (magnitude >= " + mag + " ) ";
        }else return "";
    }
    private String queryRegion(String Region) {
        if(!Objects.equals(Region, "")) {
            return "AND (region = '" + Region.replace("'","''") + "' ) ";
        } else return "";
    }
    private String queryDate(String from, String to) {
        if(!Objects.equals(from, "") || !Objects.equals(to, "")) {
            return "AND (UTC_date BETWEEN '" +from + " 00:00:00.1 " + "' AND '" + to + " 00:00:01 " + "' ) ";
        } else return "";
    }

    /**
     * fetch newest online data for the database
     * @param maxDate the maximum data in the database before update
     */
    private void onlineUpdate( String maxDate) {
        int pgNum = 1;

        Date date2 = null;
        try {
            date2 = df.parse(maxDate);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }


        String s = "https://www.emsc-csem.org/Earthquake/" ;
        while(true) {
            String address = pgNum > 1 ? (s + "?view=" + pgNum) : s;
            System.out.println("visiting: " + address);
            try {
                // grab page from URL
                Document doc = Jsoup.connect(address).timeout(5000).get();
                String text = doc.toString();

                //  get table data and url for next page
                Elements trs = doc.select("table").select("tr");

                for (int i = 1; i < trs.size() - 1; i++) {
                    Elements tds = trs.get(i).select("td");
                    String tr = trs.get(i).attr("id");

                    if (tds.size() != 13) continue;
                    earthQuake e = getEarthQuake(tr, tds);
                    try {
                        Date date3 = df.parse(e.getUTC_date());
                        //if reach current latest time, stop
                        assert date2 != null;
                        if (date2.getTime() >= date3.getTime()) {
                            numNew += newQuakes.size();
                            try {
                                insertList(newQuakes);
                            } catch (Exception e1) {
                                System.err.println(e1.getMessage());
                            }
                            newQuakes.clear();
                            return ;
                        } else {
                            newQuakes.add(e);
                            newRegions.add(e.getRegion());
                        }
                    } catch (ParseException e1) {
                        System.err.println(e1.getMessage());
                    }


                }

                //delete duplicate record at the end of each page
                newQuakes.remove(newQuakes.size() - 1);
                try {
                    numNew += newQuakes.size();
                    insertList(newQuakes);
                    newQuakes.clear();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                //go to next page
                pgNum++;

            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * construct earthQuake object by given tr and record grabbed from the html file
     * @param tr contains earthQuake id
     * @param record contains earthQuake attributes
     * @return an earthQuake object
     */
    private static earthQuake getEarthQuake(String tr, Elements record) {
        String data = record.get(3).text();
        String time = data.substring(10, 31);

        String Half = record.get(5).text();
        String latitude = "";
        if(Half.equals("S")) latitude = "-";
        latitude += record.get(4).text();
        float lat = Float.valueOf(latitude);

        Half = record.get(7).text();
        String longitude = "";
        if(Half.equals("W")) longitude = "-";
        longitude += record.get(6).text();
        float log = Float.valueOf(longitude);

        String depth = record.get(8).text();
        int dep = Integer.valueOf(depth);

        String magnitude = record.get(10).text();
        float mag = Float.valueOf(magnitude);

        String region = record.get(11).text();

        return new earthQuake(Integer.valueOf(tr),time,lat,log,dep,mag,region,32);
    }


}
