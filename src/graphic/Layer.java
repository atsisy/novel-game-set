package graphic;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

/**
 * Created by Akihiro on 2017/02/25.
 */
public class Layer {
    private Canvas canvas;
    private GraphicsContext graphicsContext;

    public Layer(double width, double height){
        canvas = new Canvas(width, height);
        graphicsContext = canvas.getGraphicsContext2D();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    /*
     * 指定したグラフィックレイヤーをすべて消す関数
     */
    public void clear(){
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /*
     * アクティブレイヤーの変更を行うメソッド
     */
    public void toFront(){
        canvas.toFront();
    }

    public void toBack(){
        canvas.toBack();
    }

    public void copyFrom(Layer layer) {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage image = layer.canvas.snapshot(params, null);
        graphicsContext.drawImage(image, 0, 0);
    }


}