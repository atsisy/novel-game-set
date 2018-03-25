package core.scenes;

import core.GameController;
import core.structure.SceneBasicInfo;
import javafx.geometry.Point2D;

public class ExitEventScene extends ScenePart {

    public ExitEventScene() {
        super(new SceneBasicInfo("EXIT", "EXIT", new Point2D(0, 0)));
    }

    @Override
    public void initHandler(GameController controller){
        System.exit(0);
    }

    @Override
    public void afterFirstDrawingHandler(GameController controller){}

    @Override
    public void finishHandler(GameController controller){}

    @Override
    public int nextSceneHash(){
        return -1;
    }
}
