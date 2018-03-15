package graphic;

import core.BackGroundImage;
import core.HighGradeText;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SceneRunner {

    private Layer backGroundImageLayer;
    private Layer TextLayer;

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

        /*
        * AnchorPaneに登録
         */
        root.getChildren().addAll(backGroundImageLayer.getCanvas(), TextLayer.getCanvas());

        AnchorPane.setTopAnchor(backGroundImageLayer.getCanvas(), 0.0);
        AnchorPane.setLeftAnchor(backGroundImageLayer.getCanvas(), 0.0);
        AnchorPane.setTopAnchor(TextLayer.getCanvas(), 0.0);
        AnchorPane.setLeftAnchor(TextLayer.getCanvas(), 0.0);

        backGroundImageLayer.toBack();
        TextLayer.toFront();

    }

    public void draw(String text, BackGroundImage bg_image){
        backGroundImageLayer.getGraphicsContext().drawImage(bg_image.getImage(), 0, 0);
        TextLayer.getGraphicsContext().fillText(text, 20, 20);
    }

    public void draw(HighGradeText high_text, BackGroundImage bg_image){
        backGroundImageLayer.getGraphicsContext().drawImage(bg_image.getImage(), 0, 0);
        TextDrawer textDrawer = new TextDrawer(20, 20);

        //TextLayer.getGraphicsContext().fillText(text, 20, 20);

        high_text.stream().forEach(highGradeTextPart -> {
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

}
