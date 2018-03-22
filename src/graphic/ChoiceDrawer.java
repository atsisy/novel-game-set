package graphic;

import core.GameController;
import text.HighGradeTextPart;
import core.scenes.ChoiceScene;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class ChoiceDrawer {

    private ArrayList<Point2D> choice_menu_point;
    private Point2D init_point;
    private Point2D left_top_point;

    public ChoiceDrawer(double x, double y){
        choice_menu_point = new ArrayList<>();
        init_point = new Point2D(x, y);
        left_top_point = new Point2D(x, y);
    }

    public void drawChoiceMenu(Layer layer, ChoiceScene scene, HighGradeTextPart highGradeTextPart){
        layer.getGraphicsContext().fillText(highGradeTextPart.getText(), left_top_point.getX(), left_top_point.getY());
        choice_menu_point.add(left_top_point);
        left_top_point = left_top_point.add(0, scene.getFont().getSize());
    }

    public void drawPointer(GameController controller, int selecting_index, BiConsumer<Layer, Point2D> draw_pointer_function){
        draw_pointer_function.accept(
                controller.getFreeLayer(),
                choice_menu_point.get(selecting_index)
        );
    }

    public void clear(){
        choice_menu_point.clear();
        left_top_point = new Point2D(init_point.getX(), init_point.getY());
    }
}
