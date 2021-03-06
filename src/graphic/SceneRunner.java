package graphic;

import core.GameController;
import text.HighGradeText;
import core.scenes.ChoiceScene;
import core.scenes.PlainTextScene;
import core.scenes.ScenePart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SceneRunner {

    private Layer backGroundImageLayer;
    private Layer TextLayer;
    private Layer freeLayer;
    private Layer animationLayer;
    private Layer cacheLayer;

    private TextDrawer textDrawer;

    public enum Status {
        IN_PROCESS,
        FINISH;

        public boolean eqauls(Status status){
            return this == status;
        }
    }

    /**
     * SceneRunnerクラスのコンストラクタ
     * @param root   AnchorPane 一番汎用性が高いPaneなので採用
     * @param width  ウィンドウの幅
     * @param height ウィンドウの高さ
     */
    public SceneRunner(AnchorPane root, int width, int height){

        /*
        * インスタンス生成
         */
        backGroundImageLayer = new Layer(width, height);
        TextLayer = new Layer(width, height);
        freeLayer = new Layer(width, height);
        animationLayer = new Layer(width, height);
        cacheLayer = new Layer(width, height);

        /*
        * AnchorPaneに登録
         */
        root.getChildren().addAll(
                backGroundImageLayer.getCanvas(),
                cacheLayer.getCanvas(),
                TextLayer.getCanvas(),
                freeLayer.getCanvas(),
                animationLayer.getCanvas()
        );

        /*
        * 基本的に、(x,y)=(0,0)が基本位置となるので設定
         */
        setDefaultPlace(backGroundImageLayer);
        setDefaultPlace(TextLayer);
        setDefaultPlace(freeLayer);
        setDefaultPlace(animationLayer);
        setDefaultPlace(cacheLayer);

        /*
        * レイヤーの位置を調整
         */
        backGroundImageLayer.toBack();
        cacheLayer.toFront();
        TextLayer.toFront();
        freeLayer.toFront();

        /*
        * テキスト描画用クラスのインスタンス生成
         */
        textDrawer = new TextDrawer(0, 0);
    }

    private void setDefaultPlace(Layer layer){
        AnchorPane.setTopAnchor(layer.getCanvas(), 0.0);
        AnchorPane.setLeftAnchor(layer.getCanvas(), 0.0);
    }

    public void softDraw(GameController controller, ScenePart scene, int local_index){
        switch(scene.getSceneType()){
            case PLAIN_TEXT:
                controller.execUnderKeyDisabled(() -> drawPlainTextScene((PlainTextScene)scene, local_index));
                break;
            case CHOICE:
                drawChoiceScene((ChoiceScene)scene);
                break;
        }
    }

    public void redrawHighGradeTextByRange(ScenePart scene, int begin_index, int end_index){

        textDrawer.reset(scene.getPointOfTopDisplayPoint().getX(), scene.getPointOfTopDisplayPoint().getY());

        textDrawer.standbyRedrawing();

        /*
        * begin == endのときも実行する必要がある
         */
        for(int i = begin_index;i <= end_index;i++) {
            redrawHighGradeText(scene, i);
        }

        textDrawer.finalizeRedrawing();
    }

    private void redrawHighGradeText(ScenePart scene, int local_index){
        textDrawer.drawRedrawingMode(TextLayer, scene, local_index);
    }

    /**
     * drawPlainTextSceneメソッド
     * PlainTextシーンを描画するためのメソッド
     * @param scene PlainTextSceneオブジェクト
     * @param local_index 第一引数のオブジェクトにおいて、何番目のHighGradeTextを描画するかの情報
     */
    private void drawPlainTextScene(PlainTextScene scene, int local_index){
        /*
         * 背景画像の描画
         */
        backGroundImageLayer.getGraphicsContext().drawImage(scene.getBackGroundImage().getImage(), 0, 0);

        /*
        * 描画するHighGradeTextオブジェクトを取り出し
         */
        HighGradeText target_text = scene.getHighGradeText(local_index);

        if(target_text.isRefreshEnabled()) {
            /*
            * 取り出したテキストの描画に画面消去が必要な場合
             */

            textDrawer.reset(
                    scene.getPointOfTopDisplayPoint().getX(),
                    scene.getPointOfTopDisplayPoint().getY()
            );

            cacheLayer.clear();
            TextLayer.clear();
        }

        target_text.stream().forEach(highGradeTextPart -> {
            highGradeTextPart.applyFeatures(TextLayer);
            textDrawer.draw(TextLayer, scene, highGradeTextPart, target_text.isRefreshEnabled(), target_text.sizeOfParts());
        });

        textDrawer.exec_drawing();
    }

    /**
     * drawChoiceSceneメソッド
     * 選択肢シーンの描画を行う
     * @param choice_scene ChoiceSceneオブジェクト
     */
    private void drawChoiceScene(ChoiceScene choice_scene){
        /*
        * 背景画像の描画
         */
        backGroundImageLayer.getGraphicsContext().drawImage(choice_scene.getBackGroundImage().getImage(), 0, 0);

        choice_scene.getHighGradeText(0).stream().forEach(highGradeTextPart -> {
            highGradeTextPart.applyFeatures(TextLayer);
            choice_scene.drawChoiceItem(TextLayer, highGradeTextPart);
        });
    }

    /**
     * confirmTextメソッド
     * ウィンドウに描画している文字列を確定するメソッド
     * 文字列が書ききれない状態に陥ることを防ぐためのメソッド
     */
    public void confirmText(ScenePart scene, int primary_index){
        textDrawer.stopAnimation();
        TextLayer.clear();
        cacheLayer.clear();

        switch(scene.getSceneType()){
            case PLAIN_TEXT:
                /*
                 * 指定した範囲のHighGradeTextを描画し直す
                 */
                redrawHighGradeTextByRange(
                        scene,
                        scene.lastRefreshText(primary_index),
                        primary_index
                );
                break;
            case CHOICE:
                /*
                * 選択肢描画オブジェクトを初期化し、再描画を行う
                 */
                ((ChoiceScene)scene).resetChoiceDrawer();
                drawChoiceScene((ChoiceScene)scene);
                break;
        }
    }

    public void setFont(Font font, Color color){
        TextLayer.getGraphicsContext().setFont(font);
        TextLayer.getGraphicsContext().setFill(color);
    }

    public void allClear(){
        backGroundImageLayer.clear();
        TextLayer.clear();
    }

    public void clearTextLayer(){
        TextLayer.clear();
    }

    public Layer getFreeLayer() {
        return freeLayer;
    }

    public Layer getAnimationLayer() {
        return animationLayer;
    }

    public Layer getCacheLayer() {
        return cacheLayer;
    }
}
