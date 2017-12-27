package quakeViewer;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import  java.time.LocalDate;
import java.time.format.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import javax.swing.text.html.ImageView;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable{
    @FXML private Button searchButton;
    @FXML private Button updateButton;
    @FXML private ChoiceBox<String> regionChoice;
    @FXML private TableView<earthQuake> dataTable;
    @FXML private TableColumn<earthQuake, String> ColID;
    @FXML private TableColumn<earthQuake, String> ColLatitude;
    @FXML private TableColumn<earthQuake, String> ColLongitude;
    @FXML private TableColumn<earthQuake, String> ColMag;
    @FXML private TableColumn<earthQuake, String> ColDepth;
    @FXML private TableColumn<earthQuake, String> ColDate;
    @FXML private TableColumn<earthQuake, String> ColRegion;
    @FXML private Slider magSlider;
    @FXML private DatePicker datePicker1;
    @FXML private DatePicker datePicker2;
    @FXML private Text magBarVal;
    @FXML private Text sysInfo;
    @FXML private AnchorPane mercratorMap;

    @FXML private BarChart<String,Number> magnitudeChart;
    @FXML private BarChart<String,Number> dateChart;
    @FXML private CategoryAxis magXAxis;
    @FXML private NumberAxis magYAxis;




    private XYChart.Series<String,Number> seriesMag = new XYChart.Series<>();

    private final String[] magnitudes = {"Under 2.0","2.0 to 3.0","3.0 to 4.0","4.0 to 5.0","5.0 to 6.0", "6.0 and over"};


    private final String pattern = "yyyy-MM-dd";
    private ObservableList<earthQuake> quakes = FXCollections.observableArrayList();
    private ObservableList<String> regions = FXCollections.observableArrayList();
    private DataSet ds1;
    //private TreeSet<String> allRegion;

    private final Tooltip tooltip1 = new Tooltip("Tooltip for Button");
    private final Tooltip tooltip2 = new Tooltip();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tooltip1.setText("Fetch latest earthquakes");

        tooltip2.setText("Search earthquakes you wanted");
        updateMagVal();
        updateButton.setTooltip(tooltip1);
        searchButton.setTooltip(tooltip2);

//        magnitudeChart = new BarChart<String, Number>(magXAxis,magYAxis);
        magXAxis.setLabel("Magnitude");
        magYAxis.setLabel("Number of Quakes");
        magnitudeChart.getData().add(seriesMag);
        dateChart.getData().add(seriesDate);
        magSlider.setShowTickLabels(true);

        regions.add("WORLDWIDE"); //default region
        regionChoice.setItems(regions);
        regionChoice.setValue("WORLDWIDE");

        datePicker1.setShowWeekNumbers(true);

        ds1 = new DataSet();
        regions.addAll(ds1.getRegions());
        regionChoice.setItems(regions);

        initializeTable();

        datePicker1.setValue(LocalDate.of(2015,12,12));
        datePicker2.setValue(LocalDate.now());
    }
    @FXML
    public void dataUpdate() {
        int x = ds1.update();
        sysInfo.setFill(Color.BLACK);
        sysInfo.setText("Update complete, " + x + " new earthquakes fetched");
        regions.addAll(ds1.getNewRegions());
        regionChoice.setItems(regions);

    }

    /**
     *update the value while user move the slider
     */
    public void updateMagVal(){
        double x = magSlider.getValue();
        x = ((int) (x * 10)) / 10.0; //set precision to 0.1
        magBarVal.setText(Double.toString(x));
    }
    private void initializeTable() {
        ColID.setCellValueFactory(new PropertyValueFactory<>("id"));
        ColMag.setCellValueFactory(new PropertyValueFactory<>("magnitude"));
        ColLongitude.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        ColLatitude.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        ColDepth.setCellValueFactory(new PropertyValueFactory<>("depth"));
        ColDate.setCellValueFactory(new PropertyValueFactory<>("UTC_date"));
        ColRegion.setCellValueFactory(new PropertyValueFactory<>("region"));
    }

    /**
     * acquire data based on coditions
     */
    @FXML
    private void search() {
        quakes.clear();

        double mag = magSlider.getValue();
        String region = regionChoice.getValue();
        if(region == null) region = "";
        LocalDate from = datePicker1.getValue();
        LocalDate to = datePicker2.getValue();
        //If the chosen dates meets requirements, proceed. Otherwise, give an alert
        if(from != null && to != null && from.isAfter(to) ) {
            sysInfo.setFill(Color.RED);
            sysInfo.setText("Error! Make sure the time interval is leagal!");

            return;
        }
        //prepare parameters
        String fromDate = "";
        String toDate = "";
        if(from == null) fromDate = "";
        if(to == null) toDate = "";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
        if(from != null)
            fromDate = dateFormatter.format(from);
        if(to != null)
            toDate = dateFormatter.format(to);
        ArrayList<earthQuake> ans = new ArrayList<>();

        //execute query
        ans = ds1.query(region,fromDate,toDate,mag);

        //print success info
        sysInfo.setFill(Color.BLACK);
        sysInfo.setText("Query complete, " + ans.size() + " quakes found.");

        //present result
        showTable(ans);
        showMercratorMap(ans);
        showMagChart(ans);
        showDateChart(ans);
    }
    private void showTable(ArrayList<earthQuake> data) {
        quakes.addAll(data);
        dataTable.setItems(quakes);
    }
    private void showMercratorMap(ArrayList<earthQuake> data) {
        int size = mercratorMap.getChildren().size();
        mercratorMap.getChildren().remove(1,size);
        ArrayList<Circle> circles = new ArrayList<>();
        float magnitude = 0;
        float latitude = 0;
        float longitude = 0;
        float layoutX=40;
        float layoutY=30;

        int i = 0;

        for(earthQuake e:data){
            i++;
            magnitude = e.getMagnitude();
            Circle cir1 = new Circle();
            cir1.setRadius(magnitude);
            cir1.setStroke(Color.RED);
            latitude = e.getLatitude();
            longitude = e.getLongitude();
            if (latitude>=0){
                latitude = layoutY+(90-latitude)/180*600;
            }else {
                latitude = layoutY+300-(latitude/180)*600;
            }

            if(longitude==180){
                longitude = layoutX+900/2;
            }else if(longitude>=0){
                longitude = layoutX+longitude*900/360;
            }else {
                longitude = layoutX+900+longitude*900/360;
            }

            cir1.setCenterX(longitude);
            cir1.setCenterY(latitude);

            mercratorMap.getChildren().add(cir1);
        }

    }

    private void showMagChart(ArrayList<earthQuake> data) {
        ObservableList<XYChart.Data<String,Number>> magnitudeAxis = FXCollections.observableArrayList();

//        int size = magnitudeChart.getChildren().size();
//        magnitudeChart.getChildren().remove(1,size);
        double magnitude = 0;
        int[] magCounter = new int[15];
        for(int i = 0; i<6;i++){
            magCounter[i] = 0;
        }
        int i =0;

        for(earthQuake e:data  ){
            magnitude = e.getMagnitude();
            magnitude = (((int)(magnitude*10))/10.0);
            if(magnitude<=2.0){
                magCounter[0]++;
            }else if(magnitude<=3.0){
                magCounter[1]++;
            }else if(magnitude<=4.0){
                magCounter[2]++;
            }else if(magnitude<=5.0){
                magCounter[3]++;
            }else if(magnitude<=6.0){
                magCounter[4]++;
            }else{
                magCounter[5]++;
            }
        }
        magnitudeAxis.clear();

        System.out.println();
        if(magnitudeAxis.isEmpty())
        for(int j=0;j<6;j++){
            magnitudeAxis.add(new XYChart.Data<>(magnitudes[j],magCounter[j]));
        }
        //magnitudeChart.getData().clear();
        seriesMag.setData(magnitudeAxis);
        seriesMag.setName("Number");

    }
    @FXML
    private CategoryAxis dateXAxis;
    @FXML
    private NumberAxis dateYAxis;

    private XYChart.Series<String,Number> seriesDate = new XYChart.Series<>();

    /**
     * generate chart of number of earthquakes-data
     * @param data data acquired from query
     */
    private void showDateChart(ArrayList<earthQuake> data) {
        ObservableList<XYChart.Data<String,Number>> dateAxis = FXCollections.observableArrayList();
        ArrayList<String> dateX = new ArrayList<>();
        dateAxis.clear();
        LocalDate fromDay = datePicker1.getValue();
        Calendar c1 =  Calendar.getInstance();
        c1.set(fromDay.getYear(), fromDay.getMonthValue() - 1, fromDay.getDayOfMonth());
        Date from = c1.getTime();

        LocalDate toDay = datePicker2.getValue();
        Calendar c2 =  Calendar.getInstance();
        c2.set(toDay.getYear(), toDay.getMonthValue() - 1, toDay.getDayOfMonth());
        Date to = c2.getTime();

        //calculate days between fromDay and toDay
        int dayNum=  (int) ((to.getTime() - from.getTime()) / (1000*3600*24));
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        //generate the date between fromDay and toDay
        Calendar calendar=Calendar.getInstance();

        for(int i=0;i<dayNum;i++){

            c1.add(Calendar.DAY_OF_YEAR, 1);
            Date newDate = c1.getTime();
            dateX.add(df.format(newDate));
        }
        System.out.println("dateX: " + dateX.size());
        String date="";
        int[] dateCounter = new int[dayNum];
        for(int i=0;i<dayNum;i++){
            dateCounter[i]=0;
        }
        int i=0;

        for(earthQuake e:data) {
            date=e.getUTC_date().substring(0,10);

            Date date1 = null;
            try {
                //System.out.println(date);
                date1 = df.parse(date);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            //for(int j=0;j<dayNum;j++){
                int n=(int)((date1.getTime()-from.getTime())/86400000);
               // System.out.println("n: " + n);
                //find the difference between date encountered and fromDate
                dateCounter[n]++;
            //}

        }
        for(int k=0;k<dayNum;k++){
            System.out.println("dateX[" + k + "]: " + dateX.get(k) + "  dateCounter[" + k +"]  " + dateCounter[k]);
            dateAxis.add(new XYChart.Data<>(dateX.get(k),dateCounter[k]));
        }

        seriesDate.setData(dateAxis);

    }
    @FXML
    private void reset() {
        magSlider.setValue(0);
    }

}
