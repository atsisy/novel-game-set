package parser;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import core.FontData;
import core.SceneBasicInfo;
import core.scenes.ChoiceScene;
import core.scenes.PlainTextScene;
import core.scenes.ScenePart;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonParser {

    private static final String DISABLE_KEYWORD = "-NO";

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

        /*
        * それぞれのシーンでforEachする
         */
        json.get("Scenes").asArray().forEach(jsonValue -> {
            final JsonObject local_json = jsonValue.asObject();
            ArrayList<String> text_array = new ArrayList<>();

            /*
            * textsを取り出す
             */
            local_json.get("texts").asArray().forEach(local_json_value -> {
                text_array.add(local_json_value.asString());
            });

            /*
            * あとは一気にパースして新規追加
             */
            result.add(createScene(
                    local_json,
                    local_json.get("type").asString(),
                    text_array,
                    local_json.get("back-ground").asString(),
                    local_json.get("bg-display-mode").asString(),
                    parseFontData(local_json),
                    new SceneBasicInfo(
                            local_json.get("title").asString(),
                            local_json.get("hash-name").asString()
                    ),
                    judgeBGMData(local_json.get("bgm").asString())
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

    private static String judgeBGMData(String word){
        return word.equals(DISABLE_KEYWORD) ? null : word;
    }

    /**
     * createSceneメソッド
     * ScenePartを継承したクラスのインスタンスを生成する専用メソッド
     * @param json_scene_type
     * @param text_array_paths
     * @param back_image_path
     * @param back_display_mode
     * @param fontData
     * @param basicInfo
     * @param bgm_path
     * @return
     */
    private static ScenePart createScene(
            JsonObject local_json_object,
            String json_scene_type,
            ArrayList<String> text_array_paths,
            String back_image_path,
            String back_display_mode,
            FontData fontData,
            SceneBasicInfo basicInfo,
            String bgm_path)
    {
        switch(ScenePart.SceneType.strToMe(json_scene_type)){
            case PLAIN_TEXT:
                return new PlainTextScene(
                        text_array_paths,
                        back_image_path,
                        back_display_mode,
                        fontData,
                        basicInfo,
                        Optional.ofNullable(bgm_path),
                        local_json_object.get("next-scene-hash").asString().hashCode()
                );
            case CHOICE:
                return new ChoiceScene(
                        text_array_paths,
                        back_image_path,
                        back_display_mode,
                        fontData,
                        basicInfo,
                        Optional.ofNullable(bgm_path)
                );
        }

        System.err.println(
                "ERROR: UNKNOWN TYPE OF SCENE!! @parser.JsonParser#createScene"
        );
        return null;
    }
}
