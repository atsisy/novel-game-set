package graphic;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public interface SceneChangeAnimation {

    default void playFadeOutIn(Layer layer, int mill_second, Runnable hidden_process, int hidden_proc_time){
        SequentialTransition sequentialTransition = new SequentialTransition();

        Timeline hidden_process_line = new Timeline(
                new KeyFrame(Duration.millis(hidden_proc_time), event -> {
                    hidden_process.run();
                })
        );

        hidden_process_line.setCycleCount(1);

        sequentialTransition.getChildren().addAll(fadeOut(layer, mill_second / 2), hidden_process_line, fadeIn(layer, mill_second / 2));
        sequentialTransition.play();
    }

    private Animation fadeOut(Layer layer, int mill_second){

        Animation animation = new Transition() {

            {

                /*
                 * mill_secondかけてアニメーションを行う
                 */
                setCycleDuration(Duration.millis(mill_second));

                /*
                 * 一回のみで十分
                 */
                setCycleCount(1);


                /*
                 * 一回レイヤー全体を消去
                 */
                layer.clear();
            }

            @Override
            protected void interpolate(double frac) {
                layer.clear();
                layer.getGraphicsContext().setFill(new Color(1.0 - frac, 1.0 - frac, 1.0 - frac, frac));
                layer.getGraphicsContext().fillRect(0, 0, layer.getCanvas().getWidth(), layer.getCanvas().getHeight());
            }
        };

        return animation;
    }

    private Animation fadeIn(Layer layer, int mill_second){

        Animation animation = new Transition() {

            {

                /*
                 * mill_secondかけてアニメーションを行う
                 */
                setCycleDuration(Duration.millis(mill_second));

                /*
                 * 一回のみで十分
                 */
                setCycleCount(1);

                /*
                * 一回レイヤー全体を消去
                 */
                layer.clear();
            }

            @Override
            protected void interpolate(double frac) {
                layer.clear();
                layer.getGraphicsContext().setFill(new Color(frac, frac, frac, 1.0 - frac));
                layer.getGraphicsContext().fillRect(0, 0, layer.getCanvas().getWidth(), layer.getCanvas().getHeight());
            }
        };

        return animation;

    }
}
