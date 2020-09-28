package checkr;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    private String[] cryptos = {"BTCUSDT"};

    private String testBTC = "BTCtoUSD";

    class otherThread implements Runnable {



        @Override
        public void run() {
            while (true) {
                try {
                    long unixTimeTo = System.currentTimeMillis() / 1000L;
                    long unixTimeFrom = unixTimeTo - 50L;

                    //https://finnhub.io/api/v1/crypto/candle?symbol=BINANCE:BTCUSDT&resolution=D&from=1572651390&to=1575243390&token=bthj1e748v6vfp9pf0u0s

                    String urlString = "https://finnhub.io/api/v1/crypto/candle?symbol=BINANCE:BTCUSDT&resolution=1&from=" + unixTimeFrom + "&to=" + unixTimeTo +"&token=bthj1e748v6vfp9pf0u0&format=csv";

                    URL url = new URL(urlString);
                    HttpURLConnection st = (HttpURLConnection) url.openConnection();

                    st.setRequestMethod("GET");

                    BufferedReader ulaz = new BufferedReader(new InputStreamReader(st.getInputStream()));

                    String line;

                    //t o h l c v
                    ulaz.readLine();

                    line = ulaz.readLine();

                    String[] parts = line.split(",");

                    //Open je 1, High 2, Low 3, Close 4

                    //StringBuilder sb = new StringBuilder();
                    testBTC = "BTC/USD  - - -  Open: " + parts[1] + " | High: " + parts[2] + " | Low: " + parts[3] + " | Close: " + parts[4];

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


    @Override
    public void start(Stage primaryStage) throws Exception{

        //Get resolution of the primary monitor
        Rectangle2D screenSize = Screen.getPrimary().getBounds();
        double SCREEN_WIDTH = screenSize.getMaxX();
        double SCREEN_HEIGHT = screenSize.getMaxY();



        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 450, SCREEN_HEIGHT*0.2));
        primaryStage.show();

        primaryStage.setX(SCREEN_WIDTH - 470);
        primaryStage.setY(SCREEN_HEIGHT*0.75);


        //primaryStage.set
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);


        Scene scene = primaryStage.getScene();
        ToolBar bp = (ToolBar) scene.lookup("#toolbar");
        ListView lw = (ListView) scene.lookup("#lista");

        if(bp == null || lw == null){
            System.err.println("There is no such ID");
            System.exit(-1);
        }

        lw.setStyle("-fx-control-inner-background:  #2b2a2a; -fx-border-color:  #141313; -fx-border-width: 2px");

        bp.setOnMousePressed(event -> {
            deltax = event.getSceneX();
            deltay = event.getSceneY();
        });

        //Kada se prevlacenje zavrsi
        bp.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - deltax);
            primaryStage.setY(event.getScreenY() - deltay);
        });

        Thread thread = new Thread( new otherThread());
        thread.start();

        //BTCvals.getBTC()

        new AnimationTimer(){

            @Override
            public void handle(long now) {
                lw.setItems(FXCollections.observableArrayList(testBTC));
            }
        }.start();



    }


    public static void main(String[] args) {
        launch(args);
    }
}
