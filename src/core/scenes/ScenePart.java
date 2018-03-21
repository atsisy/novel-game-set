package core.scenes;

import core.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.Optional;

public abstract class ScenePart {

    public enum SceneType {

        PLAIN_TEXT,
        CHOICE;

        private static final String JSON_PLAIN_KEY = "plain";
        private static final String JSON_CHOICE_KEY = "choice";

        /**
         * strToMeメソッド
         * @param json_key_str JSONキー文字列 シーンのタイプを定義する
         * @return キーに対応するEnum
         */
        public static SceneType strToMe(String json_key_str){
            switch (json_key_str){
                case JSON_PLAIN_KEY:
                    return PLAIN_TEXT;
                case JSON_CHOICE_KEY:
                    return CHOICE;
            }

            return PLAIN_TEXT;
        }
    }

    protected ArrayList<HighGradeText> text_array;
    private BackGroundImage backGroundImage;
    private FontData fontData;
    private AudioPlayer audioPlayer;
    private SceneBasicInfo basicInfo;
    protected SceneType scene_type;

    public ScenePart(ArrayList<String> text_array_paths, String back_image_path, String back_display_mode, FontData fontData, SceneBasicInfo basicInfo, Optional<String> bgm_path) {
        /*
        * コンストラクタ
        * JsonParserに実行してもらう
         */
        text_array = new ArrayList<>();

        backGroundImage = new BackGroundImage(back_image_path, BackGroundImage.DisplayType.strToMe(back_display_mode));

        /*
        * フォントデータ格納
         */
        this.fontData = fontData;

        /*
        * audioプレイヤーを初期化
         */
        audioPlayer = new AudioPlayer(bgm_path);

        /*
        * SceneBasicInfoを初期化
         */
        this.basicInfo = basicInfo;
    }

    public HighGradeText getHighGradeText(int index){
        return text_array.get(index);
    }

    public BackGroundImage getBackGroundImage() {
        return backGroundImage;
    }

    public int lastIndexOfText(){
        return text_array.size() - 1;
    }

    public Font getFont() {
        return fontData.getFont();
    }

    public Color getFontColor() {
        return fontData.getColor();
    }

    public boolean playAudio(){
        return audioPlayer.play();
    }

    public boolean stopAudio(){
        return audioPlayer.stop();
    }

    @Override
    public int hashCode(){
        return basicInfo.getHash();
    }

    public Point2D getPointOfTopDisplayPoint(){
        return basicInfo.getTopPoint();
    }

    public SceneType getSceneType() {
        return scene_type;
    }

    /**
     * nextSceneHashメソッド（抽象）
     * シーン切替時に呼び出される。次にジャンプするシーンは、このメソッドの返り値で決定する
     * @return
     * このシーンが終了したあとにジャンプするシーンのハッシュ値
     */
    abstract public int nextSceneHash();

    /**
     * keyHandlerメソッド
     * 自由にキーが押されて時の動作を定義できる
     * @param controller
     * @param event
     */
    public void keyHandler(GameController controller, KeyEvent event){
        controller.defaultKeyAction(event);
    }

    /**
     * initHandlerメソッド
     * シーン開始時に呼び出される
     * @param controller
     */
    abstract public void initHandler(GameController controller);

    /**
     * afterFirstDrawingHandlerメソッド
     * 初回描画処理完了後呼び出される
     * @param controller
     */
    abstract public void afterFirstDrawingHandler(GameController controller);

    /**
     * finishHandlerメソッド
     * シーン終了時に呼び出される
     * @param controller
     */
    abstract public void finishHandler(GameController controller);
}
