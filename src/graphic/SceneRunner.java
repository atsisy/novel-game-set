package graphic;

import core.scenes.ChoiceItem;
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

    public enum Status {
        IN_PROCESS,
        FINISH;

        public boolean eqauls(Status status){
            return this == status;
        }
    }

    public SceneRunner(AnchorPane root, int width, int height){

        /*
        * インスタンス生成
         */
        backGroundImageLayer = new Layer(width, height);
        TextLayer = new Layer(width, height);
        freeLayer = new Layer(width, height);

        /*
        * AnchorPaneに登録
         */
        root.getChildren().addAll(backGroundImageLayer.getCanvas(), TextLayer.getCanvas(), freeLayer.getCanvas());

        AnchorPane.setTopAnchor(backGroundImageLayer.getCanvas(), 0.0);
        AnchorPane.setLeftAnchor(backGroundImageLayer.getCanvas(), 0.0);
        AnchorPane.setTopAnchor(TextLayer.getCanvas(), 0.0);
        AnchorPane.setLeftAnchor(TextLayer.getCanvas(), 0.0);
        AnchorPane.setTopAnchor(freeLayer.getCanvas(), 0.0);
        AnchorPane.setLeftAnchor(freeLayer.getCanvas(), 0.0);

        backGroundImageLayer.toBack();
        TextLayer.toFront();
        freeLayer.toFront();

    }

    public void softDraw(ScenePart scene, int local_index){
        switch(scene.getSceneType()){
            case PLAIN_TEXT:
                drawPlainTextScene((PlainTextScene)scene, local_index);
                break;
            case CHOICE:
                drawChoiceScene((ChoiceScene)scene);
                break;
        }
    }

    private void drawPlainTextScene(PlainTextScene scene, int local_index){
        backGroundImageLayer.getGraphicsContext().drawImage(scene.getBackGroundImage().getImage(), 0, 0);
        TextDrawer textDrawer = new TextDrawer(20, 20);

        scene.getHighGradeText(local_index).stream().forEach(highGradeTextPart -> {
            highGradeTextPart.activeFeatureStream(featureType -> {
                switch (featureType){
                    case COLOR:
                        TextLayer.getGraphicsContext().setFill(highGradeTextPart.getColor());
                        break;
                    case RUBY:
                        break;
                    case TEXT:
                        /*
                        * ここでは文字描画処理は行わない
                         */
                        break;
                    case UNKNOWN:
                        break;
                }
            });
            textDrawer.draw(TextLayer, highGradeTextPart.getText());
        });
    }

    private void drawChoiceScene(ChoiceScene scene){
        ChoiceDrawer choiceDrawer = new ChoiceDrawer(20, 20);

        scene.getHighGradeText(0).stream().forEach(highGradeTextPart -> {
            highGradeTextPart.activeFeatureStream(featureType -> {
                switch (featureType){
                    case COLOR:
                        TextLayer.getGraphicsContext().setFill(highGradeTextPart.getColor());
                        break;
                    case RUBY:
                        break;
                    case TEXT:
                        /*
                         * ここでは文字描画処理は行わない
                         */
                        break;
                    case UNKNOWN:
                        break;
                }
            });

            choiceDrawer.drawChoiceMenu(TextLayer, scene, highGradeTextPart);
        });
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
}
