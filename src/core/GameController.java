package core;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import core.scenes.ExitEventScene;
import core.scenes.ScenePart;
import graphic.Layer;
import graphic.SceneChangeAnimation;
import graphic.SceneRunner;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import parser.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;

import static parser.JsonParser.loadWhole;

public class GameController implements SceneChangeAnimation {

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
    private boolean keyboard_is_enable;

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
        * アプリケーション終了用シーンを挿入
         */
        ExitEventScene exit_scene = new ExitEventScene();
        scenePartsMap.put(exit_scene.hashCode(), exit_scene);

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

        keyboard_is_enable = true;

        /*
         * Enterキー入力時の動作
         */
        this.scene.setOnKeyReleased(event -> {
            if(keyboard_is_enable)
                primary_scene.keyHandler(this, event);
        });

    }

    public void defaultKeyAction(KeyEvent event){
        if(isProceedKey(event.getCode())){
            if(next().eqauls(SceneRunner.Status.FINISH)){
                current_scene_hash = primary_scene.nextSceneHash();
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

    /**
     * startメソッド
     * ゲーム開始後、最初に呼ばれるメソッド
     */
    public void start(){

        /*
        * 最初のシーンを呼ぶ
         */
        nextScene();

    }

    private void nextScene(){

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
        if(primary_scene != null) {
            primary_scene.finishHandler(this);
        }

        /*
         * 最初のシーンを呼び起こし
         */
        primary_scene = scenePartsMap.get(current_scene_hash);

        /****
         *   Sceneオブジェクト側で自由に定義できる開始時メソッドを呼び出す
         ****/
        primary_scene.initHandler(this);

        if(primary_scene.getAnimationInfo().getChangeTime() < 0){
            /*
            * アニメーションは使わないので、普通にtailProcessOfNextSceneを実行
             */
            tailProcessOfNextScene();
        }else{
            if (primary_scene != null) {
                playFadeOutIn(
                        sceneRunner.getAnimationLayer(), primary_scene.getAnimationInfo().getChangeTime(),
                        this::tailProcessOfNextScene, 10);
            }
        }
    }

    /**
     * tailProcessOfNextSceneメソッド
     * nextSceneメソッドの最後で実行される。
     * シーン切り替えの間にやっておく必要がある処理を書いておく
     */
    private void tailProcessOfNextScene(){
        /*
         * 画面消去
         */
        sceneRunner.allClear();


        /*
         * このシーンで使用するフォントに設定
         */
        sceneRunner.setFont(primary_scene.getFont(), primary_scene.getFontColor());


        sceneRunner.softDraw(this, primary_scene, local_scene_text_index);
        local_scene_text_index++;

        /****
         *   Sceneオブジェクト側で自由に定義できる初回描画完了後メソッドを呼び出す
         ****/
        primary_scene.afterFirstDrawingHandler(this);

        /*
         * 新しいシーンのBGMを再生
         */
        playPrimarySceneAudio();

    }

    private SceneRunner.Status next(){

        if(primary_scene.lastIndexOfText() >= local_scene_text_index){

            /*
            * 新しい文字列の描画を行う前に
            * 最後に描画していた文字列を確定する
             */
            sceneRunner.confirmText();

            /*
            * 最期のテキストには到達していない
             */
            sceneRunner.softDraw(this, primary_scene, local_scene_text_index);
            local_scene_text_index++;
        }else{
            /*
            * このシーンは終わりで、次のシーンに移ることになる
            * この場合、シーン切り替え処理のときにテキストレイヤーは
            * 削除されることになるので、ここでの画面消去処理は行わない
            * パフォーマンスの問題もあるが、シーン切り替え前にテキストレイヤー
            * を消去してしまうと、アニメーションが不自然になってしまう事を防ぐためでもある
            * (というかそっちが優先)
             */

            return SceneRunner.Status.FINISH;
        }

        return SceneRunner.Status.IN_PROCESS;
    }

    private SceneRunner.Status back(){
        if(local_scene_text_index >= 2){
            /*
        textDrawer.stopAnimation();
             * 負の数には到達していない
             */

            /*
             * 画面消去
             */
            sceneRunner.clearTextLayer();

            /*
            * 次に表示する文字列のインデックスになっている
            * 今回、現在の文字列の一つ前まで表示したいので
            * 2を引く
             */
            local_scene_text_index -= 2;

            /*
            * 再描画処理
             */
            sceneRunner.redrawHighGradeTextByRange(
                    primary_scene,
                    primary_scene.lastRefreshText(local_scene_text_index),
                    local_scene_text_index
            );

            /*
            * 次に表示するインデックスをセットしておく必要がある
            * 今は、次表示すべき文字列のインデックス-1なので、
            * インクリメントする。
             */
            local_scene_text_index++;
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

    /**
     * extractInitScenesJsonメソッド
     * scenes.jsonの解析を行う
     * @param json_path scenes.jsonへのパス
     * @return scenes.jsonに書かれた各シーンの記述があるJSONへのパス郡
     */
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

    public void requestKeyEnable(){
        keyboard_is_enable = true;
    }

    public void requestKeyDisable(){
        keyboard_is_enable = false;
    }

    /**
     * execUnderKeyDisabledメソッド
     * キー入力無効化下で実行したい場合、このメソッドにRunnerオブジェクトを渡し実行
     * @param process その処理
     */
    public void execUnderKeyDisabled(Runnable process){
        requestKeyDisable();
        process.run();
        requestKeyEnable();
    }
}
