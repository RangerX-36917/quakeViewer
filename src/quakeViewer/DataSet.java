package quakeViewer;

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
    private ArrayList<earthQuake> quakes = new ArrayList<>();
    private TreeSet<String> regions = new TreeSet<>();
    private Statement statement = null;
    private Connection connection = null;

    static private Calendar calendar = Calendar.getInstance();
    static private Date date1 = new Date();//current time
    static private Date date2; //target time
    static private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static private ArrayList<earthQuake> newQuakes;

    public DataSet(String Region, String fromDate, String toDate, double mag) {
        setConnection();
        loadData(Region, fromDate, toDate, mag);

        String q2 = "SELECT MAX(UTC_date) FROM quakes";
        ResultSet resultSetMaxDate = null;String maxDate = null;
        try {
            resultSetMaxDate = statement.executeQuery(q2);
            maxDate = resultSetMaxDate.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("max date: " + maxDate);
        connect(1,maxDate);

        closeConnection();
        //printElement(quakes);
        //System.out.println(quakes.size());
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
    void setConnection() {
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
    void closeConnection() {
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
    void insertList(ArrayList<earthQuake> quakes) throws Exception {
        Statement stmt = connection.createStatement();
        String insert;
        for(int i = 0; i < quakes.size(); i++) {
            earthQuake e = quakes.get(i);
            insert = "INSERT INTO quakes VALUES (";
            int rID = 123 + i;
            insert += e.getId() + ", '" + e.getUTC_date() + "'," +e.getLatitude() + "," + e.getLongitude() + "," + e.getDepth() + "," +e.getMagnitude() + ",'" + e.getRegion()+"',"+ rID+")";
            //System.out.println("insert:" + insert);
            stmt.executeUpdate(insert);
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
    public int connect(int pgNum, String maxDate) {

        Date date2 = null;
        try {
            date2 = df.parse(maxDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String s = "https://www.emsc-csem.org/Earthquake/" ;

        String address = pgNum > 1? (s + "?view=" + pgNum) : s;
        System.out.println("visiting url:" + address);

        try {
            /**
             * grab page from URL
             */
            //System.out.println("investigating: " + URL);
            Document doc = Jsoup.connect(address).timeout(5000).get();
            String text = doc.toString();
            //System.out.println(text);
            /**
             * get table data and url for next page
             */
            Elements trs = doc.select("table").select("tr");
            Elements uls = doc.select("a[href]");

            for (int i = 1; i < trs.size() - 1; i++) {
                Elements tds = trs.get(i).select("td");
                if (tds.size() != 13) continue;
                earthQuake e = getEarthQuake(tds);
                //System.out.println(e.toString());
                try {
                    Date date3 = df.parse(e.getUTC_date());
                    if(date2.getTime() >= date3.getTime()) {
                        System.out.println("stop");
                        return 0;
                    } else {
                        System.out.println("add: " + e.toString());
                        newQuakes.add(e);
                        regions.add(e.getRegion());
                    }
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }


            }

            /*
            int end = uls.size();//> 47? 47:uls.size();
            for(int i = 54; i < 70; i++) {
                System.out.println("link #" + i + " "+uls.get(i).attr("abs:href"));

            }
            */
            //String txt = uls.get(64 ).attr("abs:href");

            quakes.remove(quakes.size() - 1);
            try {
                insertList(newQuakes);
                newQuakes.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(connect(pgNum + 1, maxDate) == 0) {
                return 0;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }
    private static earthQuake getEarthQuake(Elements record) {
        /**
         * construct earthQuake object from table data
         */

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
        Random r = new Random();
        int x = r.nextInt() % 1000;
        earthQuake e = new earthQuake(x,time,lat,log,dep,mag,region,32);

        return e;
    }

}
