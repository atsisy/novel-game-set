package core;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import javafx.stage.Stage;

import java.util.ArrayList;

import static parser.JsonParser.loadWhole;

public interface SystemInitializer {

    default void StageInit(String json_path, Stage stage){
        /*
         * 大本のJSONファイルオブジェクト
         */
        final JsonObject json = Json.parse(loadWhole(json_path)).asObject();

        /*
        * ウィンドウのサイズ設定
         */
        stage.setWidth(json.get("width").asInt());
        stage.setHeight(json.get("height").asInt());

        /*
        * ウィンドウのタイトル設定
         */
        stage.setTitle(json.get("title").asString());
    }

}
