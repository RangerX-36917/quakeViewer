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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.swing.text.html.ImageView;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

    @FXML private AnchorPane mercratorMap;
    @FXML private AnchorPane eckertIVMap;
//    @FXML private AnchorPane magnitudeChart;
    @FXML private BarChart<String,Number> magnitudeChart;
    @FXML private CategoryAxis magXAxis;
    @FXML private NumberAxis magYAxis;

    private XYChart.Series<String,Number> seriesMag = new XYChart.Series<>();
    private ObservableList<XYChart<String,Number>> magnitudeAxis = FXCollections.observableArrayList();
    private final String[] magnitudes = {"Under 2.0","2.0 to 3.0","3.0 to 4.0","4.0 to 5.0","5.0 to 6.0", "6.0 and over"};


    private final String pattern = "yyyy-MM-dd";
    private ObservableList<earthQuake> quakes = FXCollections.observableArrayList();
    private ObservableList<String> regions = FXCollections.observableArrayList();
    private DataSet ds1;
    //private TreeSet<String> allRegion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTable();
//        initializeMagChart();
        ds1 = new DataSet();
        regions.addAll(ds1.getRegions());
        regionChoice.setItems(regions);
    }
    private void initializeTable() {
        ColID.setCellValueFactory(new PropertyValueFactory<>("id"));
        ColMag.setCellValueFactory(new PropertyValueFactory<>("magnitude"));
        ColLongitude.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        ColLatitude.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        ColDepth.setCellValueFactory(new PropertyValueFactory<>("depth"));
        ColDate.setCellValueFactory(new PropertyValueFactory<>("UTC_date"));
        ColRegion.setCellValueFactory(new PropertyValueFactory<>("region"));
        //showTable();
    }
//    private void initializeMagChart(){
//        String[] magnitudes = {"Under 2.0","2.0 to 3.0","3.0 to 4.0","4.0 to 5.0","5.0 to 6.0", "6.0 and over"};
////        magnitudeAxis.addAll(Arrays.asList(magnitudes));
////
////        magXAxis.setCategories(magnitudeAxis);
//    }
    @FXML
    private void search() {
        //acquire data set based on conditions
        quakes.clear();
        //regions.clear();
        double mag = magSlider.getValue();
        String region = regionChoice.getValue();
        if(region == null) region = "";
        LocalDate from = datePicker1.getValue();
        LocalDate to = datePicker2.getValue();
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
        ans = ds1.query(region,fromDate,toDate,mag);

        showTable(ans);
        showMercratorMap(ans);
        showEckertIVMap(ans);
        showMagChart(ans);
        showDateChart(ans);
    }
    private void showTable(ArrayList<earthQuake> data) {
        for(earthQuake e:data) {
            System.out.println("result: " + e.toString());
        }
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
        float layoutX=150;
        float layoutY=71;

        int i = 0;

        for(earthQuake e:data){
            i++;
            magnitude = e.getMagnitude();
            Circle cir1 = new Circle();
            cir1.setRadius(magnitude);
            cir1.setStroke(Color.RED);
            cir1.setFill(Color.WHITE);

            System.out.println("add point");
            //quakes.addAll(data);
            latitude = e.getLatitude();
            longitude = e.getLongitude();
            //cir1.setCenterX(68 + 30* (i + 1));
            //cir1.setCenterY(44 + 30 * (i + 1));

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
    private void showEckertIVMap(ArrayList<earthQuake> data) {
//
//        int size = eckertIVMap.getChildren().size();
//        eckertIVMap.getChildren().remove(1,size);
//        ArrayList<Circle> circles = new ArrayList<>();
//        float latitude = 0;
//        float longitude = 0;
//        float layoutX=100;
//        float layoutY=120;
//
//        int i = 0;
//
//        for(earthQuake e:data){
//            i++;
//            Circle cir1 = new Circle();
//            cir1.setRadius(2.0);
//            cir1.setStroke(Color.RED);
//            cir1.setFill(Color.RED);
//
//            System.out.println("add point");
//            //quakes.addAll(data);
//            latitude = e.getLatitude();
//            longitude = e.getLongitude();
//            //cir1.setCenterX(68 + 30* (i + 1));
//            //cir1.setCenterY(44 + 30 * (i + 1));
//
//            if (latitude>=0){
//                latitude = layoutY+((90-latitude)/90)*600/2;
//            }else {
//                latitude = layoutY+600/2-(latitude/90)*600/2;
//            }
//            if(longitude==180){
//                longitude = layoutX+900/2;
//            }else if(longitude>=0){
//                longitude = layoutX+(longitude/180)*900/2;
//            }else {
//                longitude = layoutX+900/2+((900+longitude)/180)*900/2;
//            }
//
//            cir1.setCenterX(longitude);
//            cir1.setCenterY(latitude);
//
//            eckertIVMap.getChildren().add(cir1);
//        }

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
        magnitudeChart = new BarChart<String, Number>(magXAxis,magYAxis);
        magXAxis.setLabel("Magnitude");
        magYAxis.setLabel("Number of Quakes");
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

        seriesMag.setData(magnitudeAxis);
        magnitudeChart.getData().add(seriesMag);
    }
    private void showDateChart(ArrayList<earthQuake> data) {

    }
    @FXML
    private void reset() {
        magSlider.setValue(0);
    }

}
