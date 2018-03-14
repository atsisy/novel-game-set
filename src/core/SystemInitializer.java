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

    default ArrayList<String> getJsonScenePaths(String json_path){
        ArrayList<String> json_sp_paths = new ArrayList<>();

        /*
         * 大本のJSONファイルオブジェクト
         */
        final JsonObject json = Json.parse(loadWhole(json_path)).asObject();

        /*
        * JsonArrayからシーン設定ファイルが書き込まれたJsonファイルへのパスを読み込み
         */
        json.get("paths").asArray().forEach(jsonValue -> {
            json_sp_paths.add(jsonValue.asString());
        });

        return json_sp_paths;
    }

}
