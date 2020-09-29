package checkr;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Main extends Application {


    /*
     * Information that needs to be global and accessible to all threads
     */
    private String[] cryptos = {"BTCUSDT", "ETHUSDT", "XRPUSDT", "DOTUSDT", "LINKUSDT", "EOSUSDT", "TRXUSDT", "MKRUSDT", "YFIUSDT"};
    private String[] ret_vals =  {"BTC/USDT", "ETH/USDT", "XRP/USDT", "DOT/USDT", "LINK/USDT", "EOS/USDT", "TRX/USDT", "MKR/USDT", "YFI/USDT"};


    /*
    *   Inner class for purposes of threading
    */
    class otherThread implements Runnable {

        private int ind;

        otherThread(int index){
            ind = index;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    /*
                    *  Unix time which represents the interval for data we want
                    */
                    long unixTimeTo = System.currentTimeMillis() / 1000L;
                    long unixTimeFrom = unixTimeTo - 50L;

                    /*
                    * Accessing the data via HTTP GET request
                    */
                    String urlString = "https://finnhub.io/api/v1/crypto/candle?symbol=BINANCE:" + cryptos[ind] +
                    "&resolution=1&from=" + unixTimeFrom + "&to=" + unixTimeTo +"&token=bthj1e748v6vfp9pf0u0&format=csv";

                    URL url = new URL(urlString);
                    HttpURLConnection st = (HttpURLConnection) url.openConnection();
                    st.setRequestMethod("GET");

                    /*
                    *   Interpret the csv data in a more appropriate form
                    */

                    BufferedReader ulaz = new BufferedReader(new InputStreamReader(st.getInputStream()));
                    String line;
                    ulaz.readLine();
                    line = ulaz.readLine();
                    String[] parts;

                    /*
                    *   If we get a null pointer here the HTTP request didn't go well.
                    *   Sleep one second and try again.
                    */
                    try{
                        parts = line.split(",");
                    }
                    catch(NullPointerException e){
                        Thread.sleep(1000);
                        continue;
                    }

                    //Open 1, High 2, Low 3, Close 4
                    ret_vals[ind] =  cryptos[ind] + "  - - -  Open: " + parts[1] + " | High: " + parts[2] + " | Low: " + parts[3] + " | Close: " + parts[4];


                    ulaz.close();
                    st.disconnect();
                    Thread.sleep(60000); //Pause for 60 seconds

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private double deltax = 0;
    private double deltay = 0;

    private boolean alwaysOnTopBool = false;

    private Scene scene;
    private ToolBar bp;
    private ListView lw;
    private Button alwaysOTbttn;

    private void initObjects(){
        /*
            Get FXML created objects from the scene by their ID
         */
        bp = (ToolBar) scene.lookup("#toolbar");
        lw = (ListView) scene.lookup("#lista");
        alwaysOTbttn = (Button) scene.lookup("#onTopButton");

        if(bp == null || lw == null){
            System.err.println("There is no such ID");
            System.exit(-1);
        }

        //Setting style for the ListView
        lw.setStyle("-fx-control-inner-background:  #2b2a2a; -fx-border-color:  #141313; -fx-border-width: 2px;" +
                " -fx-selection-bar: grey; -fx-selection-bar-non-focused: grey; -fx-focus-color: transparent;");
    }

    private void startThreads(){
        for(int i=0; i<9; i++) {
            Thread thread = new Thread(new otherThread(i));
            thread.start();
        }

        new AnimationTimer(){

            @Override
            public void handle(long now) {
                lw.setItems(FXCollections.observableArrayList(ret_vals));
            }
        }.start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        /*
        * Get the resolution of the primary monitor
        */
        Rectangle2D screenSize = Screen.getPrimary().getBounds();
        double SCREEN_WIDTH = screenSize.getMaxX();
        double SCREEN_HEIGHT = screenSize.getMaxY();

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("StockCheck");
        primaryStage.setScene(new Scene(root, 450, 270));
        primaryStage.show();

        primaryStage.setX(SCREEN_WIDTH - 470);
        primaryStage.setY(SCREEN_HEIGHT - 300);
        primaryStage.setResizable(false);

        scene = primaryStage.getScene();

        initObjects();

        /*
        *  Window dragging events
        */
        bp.setOnMousePressed(event -> {
            deltax = event.getSceneX();
            deltay = event.getSceneY();
        });

        bp.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - deltax);
            primaryStage.setY(event.getScreenY() - deltay);
        });

        alwaysOTbttn.setOnMouseClicked(event -> {
            alwaysOnTopBool = !alwaysOnTopBool;
            primaryStage.setAlwaysOnTop(alwaysOnTopBool);
        });

        startThreads();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
