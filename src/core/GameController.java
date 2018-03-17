package core;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import core.scenes.ScenePart;
import graphic.Layer;
import graphic.SceneRunner;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import parser.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static parser.JsonParser.loadWhole;

public class GameController {

    private ArrayList<ScenePart> sceneParts;
    private HashMap<Integer, ScenePart> scenePartsMap;
    private int current_scene_hash;
    private int local_scene_text_index;
    private ScenePart primary_scene;
    private SceneRunner sceneRunner;
    private Scene scene;
    private Stage stage;
    private AnchorPane root;
    private boolean audio_playing;

    public GameController(Stage stage, String scenes_path){

        local_scene_text_index = 0;
        audio_playing = false;
        sceneParts = new ArrayList<>();
        scenePartsMap = new HashMap<>();

        ArrayList<String> json_paths = extractInitScenesJson(scenes_path);

        /*
        * jsonへのパスのリスト => ArrayList<ScenePart>にパース -> 連結
         */
        json_paths.stream().map(JsonParser::parseToSceneParts).forEach(sceneParts::addAll);
        json_paths.stream().map(JsonParser::parseToSceneParts).forEach(part -> {
            part.forEach(local_scene -> scenePartsMap.put(local_scene.hashCode(), local_scene));
        });

        /*
        * rootとなるAnchorPaneのインスタンス生成
         */
        root = new AnchorPane();

        sceneRunner = new SceneRunner(root, (int)stage.getWidth(), (int)stage.getHeight());

        /*
         * SceneとStageもコピー
         */
        this.scene = new Scene(root);
        this.stage = stage;
        stage.setScene(scene);

        /*
        * Enterキー入力時の動作
         */
        this.scene.setOnKeyReleased(event -> {
            primary_scene.keyHandler(this, event);
        });

    }

    public void defaultKeyAction(KeyEvent event){
        if(isProceedKey(event.getCode())){
            if(next().eqauls(SceneRunner.Status.FINISH)){
                current_scene_hash = primary_scene.nextSceneHash();
                primary_scene.finishHandler();
                nextScene();
            }
        }else if(isBackKey(event.getCode())){
            back();
        }
    }

    private boolean isProceedKey(KeyCode code){
        return code.equals(KeyCode.ENTER) || code.equals(KeyCode.RIGHT);
    }

    private boolean isBackKey(KeyCode code){
        return code.equals(KeyCode.BACK_SPACE) || code.equals(KeyCode.LEFT);
    }

    public void start(){

        nextScene();

    }

    private void nextScene(){

        /*
        * 画面消去
         */
        sceneRunner.allClear();

        /*
        * シーンが切り替わるため、ローカルのインデックス変数は0に初期化
        * current_sceneはゲーム全部に渡って0に初期化されない
         */
        local_scene_text_index = 0;

        /*
        * 古いシーンとなるprimary_sceneのBGMを停止
         */
        stopPrimarySceneAudio();


        /****
         *   Sceneオブジェクト側で自由に定義できる終了時時メソッドを呼び出す
         ****/
        if(primary_scene != null)
            primary_scene.finishHandler();

        /*
         * 最初のシーンを呼び起こし
         */
        primary_scene = scenePartsMap.get(current_scene_hash);

        /****
         *   Sceneオブジェクト側で自由に定義できる開始時メソッドを呼び出す
         ****/
        primary_scene.initHandler();

        /*
         * このシーンで使用するフォントに設定
         */
        sceneRunner.setFont(primary_scene.getFont(), primary_scene.getFontColor());

        sceneRunner.draw(primary_scene.getHighGradeText(local_scene_text_index), primary_scene.getBackGroundImage());
        local_scene_text_index++;

        /*
        * 新しいシーンのBGMを再生
         */
        playPrimarySceneAudio();
    }

    private SceneRunner.Status next(){

        /*
         * 画面消去
         */
        sceneRunner.clearTextLayer();

        if(primary_scene.lastIndexOfText() >= local_scene_text_index){
            /*
            * 最期のテキストには到達していない
             */
            sceneRunner.draw(primary_scene.getHighGradeText(local_scene_text_index), primary_scene.getBackGroundImage());
            local_scene_text_index++;
        }else{
            return SceneRunner.Status.FINISH;
        }

        return SceneRunner.Status.IN_PROCESS;
    }

    private SceneRunner.Status back(){
        if(local_scene_text_index >= 2){
            /*
             * 負の数には到達していない
             */

            /*
             * 画面消去
             */
            sceneRunner.clearTextLayer();


            local_scene_text_index -= 2;
            sceneRunner.draw(primary_scene.getHighGradeText(local_scene_text_index), primary_scene.getBackGroundImage());
        }

        return SceneRunner.Status.IN_PROCESS;
    }

    private void playPrimarySceneAudio(){
        /*
        * 他のシーンのBGMが流れているときは、実行しない
         */
        if(!audio_playing) {
            audio_playing = primary_scene.playAudio();
        }
    }

    private void stopPrimarySceneAudio(){
        /*
        * そもそも音楽が流れていないときは実しない
         */
        if(audio_playing) {
            audio_playing = !primary_scene.stopAudio();
        }
    }

    private ArrayList<String> extractInitScenesJson(String json_path){
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

        current_scene_hash = json.get("first-scene-hash").asString().hashCode();

        return json_sp_paths;
    }

    public Layer getFreeLayer(){
        return sceneRunner.getFreeLayer();
    }
}
