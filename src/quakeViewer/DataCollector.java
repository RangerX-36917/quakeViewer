package quakeViewer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.*;

public class DataCollector {
    static private int page =  54;
    public DataCollector(String maxDate) {

        connect(1, maxDate);
    }
    static private Calendar calendar = Calendar.getInstance();
    static private Date date1 = new Date();//current time
    static private Date date2; //target time
    static private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private ArrayList<earthQuake> quakes = new ArrayList<>();
    private TreeSet<String> regions = new TreeSet<>();

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
            Document doc = Jsoup.connect(address).timeout(2000).get();
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
                        quakes.add(e);
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
    public ArrayList<earthQuake> getQuakes() {
        return quakes;
    }

}
