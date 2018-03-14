package core;

import graphic.SceneRunner;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import parser.JsonParser;

import java.util.ArrayList;

public class GameController {

    private ArrayList<ScenePart> sceneParts;
    private int current_scene;
    private int local_scene_text_index;
    private ScenePart primary_scene;
    SceneRunner sceneRunner;
    private Scene scene;
    private Stage stage;
    private AnchorPane root;

    public GameController(Stage stage, ArrayList<String> json_paths){

        current_scene = 0;
        local_scene_text_index = 0;
        sceneParts = new ArrayList<>();

        /*
        * jsonへのパスのリスト => ArrayList<ScenePart>にパース -> 連結
         */
        json_paths.stream().map(JsonParser::parseToSceneParts).forEach(sceneParts::addAll);

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
            if(event.getCode().equals(KeyCode.ENTER)){
                if(next().eqauls(SceneRunner.Status.FINISH)){
                    nextScene();
                }
            }
        });

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
         * 最初のシーンを呼び起こし
         */
        primary_scene = sceneParts.get(current_scene);
        current_scene++; // 二枚目のシーンに向けてインクリメント

        sceneRunner.draw(primary_scene.getText(local_scene_text_index), primary_scene.getBackGroundImage());
        local_scene_text_index++;
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
            sceneRunner.draw(primary_scene.getText(local_scene_text_index), primary_scene.getBackGroundImage());
            local_scene_text_index++;
        }else{
            return SceneRunner.Status.FINISH;
        }

        return SceneRunner.Status.IN_PROCESS;
    }
}
