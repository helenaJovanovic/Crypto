package checkr;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Controller {

    @FXML Button exitbttn;


    @FXML private void exit_action(){
        Platform.exit();
        System.exit(0);
    }
}
