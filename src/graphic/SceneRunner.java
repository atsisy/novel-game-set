package graphic;

import core.BackGroundImage;
import javafx.scene.layout.AnchorPane;
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

    public void setFont(Font font){
        TextLayer.getGraphicsContext().setFont(font);
    }

    public void allClear(){
        backGroundImageLayer.clear();
        TextLayer.clear();
    }

    public void clearTextLayer(){
        TextLayer.clear();
    }
}
