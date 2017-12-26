package quakeViewer;
import java.awt.event.ActionEvent;
import  java.time.LocalDate;
import java.time.format.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
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

public class Controller implements Initializable{

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

    private final String pattern = "yyyy-MM-dd";
    private ObservableList<earthQuake> quakes = FXCollections.observableArrayList();
    private ObservableList<String> regions = FXCollections.observableArrayList();
    private DataSet ds1;
    //private TreeSet<String> allRegion;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateMagVal();
        magSlider.setShowTickLabels(true);
        regions.add("WORLDWIDE"); //default region
        regionChoice.setItems(regions);
        regionChoice.setValue("WORLDWIDE");
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

    }
    private void showDateChart(ArrayList<earthQuake> data) {

    }
    @FXML
    private void reset() {
        magSlider.setValue(0);
    }

}
