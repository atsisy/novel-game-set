package graphic;

import core.HighGradeTextPart;
import core.scenes.ChoiceScene;
import javafx.geometry.Point2D;

import java.util.ArrayList;

public class ChoiceDrawer {

    private ArrayList<Point2D> choice_menu_point;
    private Point2D left_top_point;

    public ChoiceDrawer(double x, double y){
        choice_menu_point = new ArrayList<>();
        left_top_point = new Point2D(x, y);
    }

    public void drawChoiceMenu(Layer layer, ChoiceScene scene, HighGradeTextPart highGradeTextPart){
        layer.getGraphicsContext().fillText(highGradeTextPart.getText(), left_top_point.getX(), left_top_point.getY());
        choice_menu_point.add(left_top_point);
        left_top_point = left_top_point.add(0, scene.getFont().getSize());
    }

}
