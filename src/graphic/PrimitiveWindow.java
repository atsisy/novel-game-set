package graphic;

import core.GameController;
import core.SystemInitializer;
import javafx.application.Application;
import javafx.stage.Stage;

public class PrimitiveWindow extends Application implements SystemInitializer {

    public static void main(String[] args){

        Application.launch(args);

    }

    @Override
    public void start(Stage primaryStage){

        /*
        * StageInitializerで定義されたStage情報JSONパース処理をここで使用
         */
        StageInit("./setting.json", primaryStage);

        GameController controller = new GameController(primaryStage, "./scenes.json");

        primaryStage.show();

        controller.start();

    }
}
