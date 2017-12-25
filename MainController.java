import  java.time.LocalDate;
import java.time.format.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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
    @FXML private ImageView merchator;
    @FXML private AnchorPane merchatorMap;
    private final String pattern = "yyyy-MM-dd";
    private ObservableList<earthQuake> quakes = FXCollections.observableArrayList();
    private ObservableList<String> regions = FXCollections.observableArrayList();
    private DataSet ds1;
    //private TreeSet<String> allRegion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTable();
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
        float latitude = 0;
        float longitude = 0;
        float layoutX=38;
        float layoutY=43;

        Circle cir1 = new Circle();
        cir1.setRadius(10.0);
        cir1.setStroke(Color.RED);
        cir1.setFill(Color.RED);

        for(earthQuake e:data){
            quakes.addAll(data);
            latitude = e.getLatitude();
            longitude = e.getLongitude();
            if (latitude>0){
                latitude = layoutY+(90-latitude)*375/90;
            }else {
                latitude = layoutY+(90+latitude)*375/90;
            }
            if(longitude==180){
                longitude = layoutX+longitude;
            }else if(longitude>0){
                longitude = layoutX+longitude*1200/360;
            }else {
                longitude = layoutX+(1200+longitude)*1200/360;
            }
            cir1.setCenterX(longitude);
            cir1.setCenterY(latitude);
            merchatorMap.getChildren().add(cir1);
        }



    }
    private void showEckertIVMap(ArrayList<earthQuake> data) {

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



