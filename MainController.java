import java.awt.event.ActionEvent;
import  java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Calendar;
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
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TreeSet;



public class MainController implements Initializable{

    @FXML private TableView<earthQuake> dataTable;
    @FXML private ChoiceBox<String> regionChoice;
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
    @FXML private CategoryAxis magXAxis;
    @FXML private NumberAxis magYAxis;

    @FXML private BarChart<String,Number> dateChart;
    @FXML private CategoryAxis dateXAxis;
    @FXML private NumberAxis dateYAxis;


    private XYChart.Series<String,Number> seriesMag = new XYChart.Series<>();
    private ObservableList<XYChart.Data<String,Number>> magnitudeAxis = FXCollections.observableArrayList();
    private final String[] magnitudes = {"Under 2.0","2.0 to 3.0","3.0 to 4.0","4.0 to 5.0","5.0 to 6.0", "6.0 and over"};



    private final String pattern = "yyyy-MM-dd";
    private ObservableList<earthQuake> quakes = FXCollections.observableArrayList();
    private ObservableList<String> regions = FXCollections.observableArrayList();
    private DataSet ds1;
    //private TreeSet<String> allRegion;




    private final LocalDate fromDay = datePicker1.getValue();
    private final Calendar c1 =  Calendar.getInstance();
    private final c1.set(fromDay.getYear(), fromDay.getMonthValue() - 1, fromDay.getDayOfMonth());
    private final Date from = c1.getTime();
    private final LocalDate toDay = datePicker2.getValue();
    private final Calendar c2 =  Calendar.getInstance();
    private final c2.set(toDay.getYear(), toDay.getMonthValue() - 1, toDay.getDayOfMonth());
    private final Date to = c2.getTime();



    private XYChart.Series<String,Number> seriesDate = new XYChart.Series<>();
    private ObservableList<XYChart.Data<String,Number>> dateAxis = FXCollections.observableArrayList();
    private final int dayNum=  (int) ((from.getTime() - to.getTime()) / (1000*3600*24));
    public void circle(){
        //to produce dateXAxis
        ArrayList dateX=new ArrayList();
        for(int i=0;i<dayNum;i++){
            Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date newdate = calendar.getTime();
            dateX.add(newdate);
        }
     }
    public void initialize(URL location, ResourceBundle resources) {
        updateMagVal();

//        magnitudeChart = new BarChart<String, Number>(magXAxis,magYAxis);
        magXAxis.setLabel("Magnitude");
        magYAxis.setLabel("Number of Quakes");

        dateXAxis.setLabel("Date");
        dateYAxis.setLabel("Number of Quakes");

        regions.add("WORLDWIDE"); //default region
        regionChoice.setItems(regions);
        regionChoice.setValue("WORLDWIDE");

        magSlider.setShowTickLabels(true);

        datePicker1.setShowWeekNumbers(true);

        ds1 = new DataSet();
        regions.addAll(ds1.getRegions());
        regionChoice.setItems(regions);
        initializeTable();
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
//        LocalDate from = datePicker1.getValue();
//        LocalDate to = datePicker2.getValue();
        // already private final at the beginning
        //If the chosen dates meets requirements, proceed. Otherwise, give an alert
        if(fromDay != null && toDay != null && fromDay.isAfter(toDay) ) {
            sysInfo.setFill(Color.RED);
            sysInfo.setText("Error! Make sure the time interval is leagal!");

            return;
        }
        //prepare parameters
        String fromDate = "";
        String toDate = "";
        if(fromDay == null) fromDate = "";
        if(toDay == null) toDate = "";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
        if(fromDay != null)
            fromDate = dateFormatter.format(fromDay);
        if(toDay != null)
            toDate = dateFormatter.format(toDay);
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
        magnitudeAxis.clear();

//        int size = magnitudeChart.getChildren().size();
//        magnitudeChart.getChildren().remove(1,size);
        float magnitude = 0;
        int[] magCounter = new int[6];
        for(int i = 0; i<6;i++){
            magCounter[i] = 0;
        }
        int i =0;

        for(earthQuake e:data){
            magnitude = e.getMagnitude();
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
        for(int j=0;j<6;j++){
            magnitudeAxis.add(new XYChart.Data<>(magnitudes[j],magCounter[j]));
        }
        magnitudeChart.getData().clear();
        seriesMag.setData(magnitudeAxis);
        magnitudeChart.getData().add(seriesMag);

    }




    private void showDateChart(ArrayList<earthQuake> data) {
        dateAxis.clear();
        String date="";
        int[] dateCounter = new int[dayNum];
        for(int i=0;i<dayNum;i++){
            dateCounter[i]=0;
        }
        int i=0;

        for(earthQuake e:data) {
            date=e.getUTC_date();

            for(int j=0;j<dayNum;j++){
                int n=(date.getTime()-from.getTime())/86400000;
                //find the difference between date encountered and fromDate
                dateCounter[n]++;
            }

        }
        for(int k=0;k<dayNum;k++){
           dateAxis.add(new XYChart.Data<>(magnitudes[k],dateCounter[k]));
        }
        dateChart.getData().clear();
        seriesMag.setData(dateAxis);
        dateChart.getData().add(seriesDate);
    }
    @FXML
    private void reset() {
        magSlider.setValue(0);
    }

}