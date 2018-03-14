package parser;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import core.FontData;
import core.ScenePart;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class JsonParser {

    public static String loadWhole(String file_path){
        /*
        * ファイルをすべて読み込む関数
        * 特にこのクラスに属しているわけではない静的メソッド
         */
        try {
            return Files.lines(Paths.get(file_path), Charset.forName("UTF-8"))
                    .collect(Collectors.joining(System.getProperty("line.separator")));
        }catch (IOException e){
            return "Failed to load file";
        }
    }

    public static ArrayList<ScenePart> parseToSceneParts(String json_path){

        ArrayList<ScenePart> result = new ArrayList<>();

        /*
        * 大本のJSONファイルオブジェクト
         */
        final JsonObject json = Json.parse(loadWhole(json_path)).asObject();

        ArrayList<String> text_array = new ArrayList<>();

        /*
        * それぞれのシーンでforEachする
         */
        json.get("Scenes").asArray().forEach(jsonValue -> {
            final JsonObject local_json = jsonValue.asObject();

            /*
            * textsを取り出す
             */
            local_json.get("texts").asArray().forEach(local_json_value -> {
                text_array.add(local_json_value.asString());
            });

            /*
            * font情報を取り出す
             */

            /*
            * あとは一気にパースして新規追加
             */
            result.add(new ScenePart(
                    text_array,
                    local_json.get("back-ground").asString(),
                    local_json.get("bg-display-mode").asString(),
                    parseFontData(local_json)
            ));
        });

        return result;
    }

    /*
    * フォントデータ読み取り専用のメソッド
    * このメソッドに渡すJsonObjectは、1シーン分のjsonの塊
     */
    private static FontData parseFontData(JsonObject object){
        /*
        * 1シーン分のJsonから、font項を取り出す。
        * 構造体のような形式で手に入る
         */
        final JsonObject local_json = object.get("font").asObject();

        return new FontData(
                local_json.get("name").asString(),
                local_json.get("size").asInt(),
                local_json.get("color").asString()
        );
    }

}
