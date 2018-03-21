package core.structure;

import com.eclipsesource.json.JsonObject;
import javafx.scene.paint.Color;

public class SceneAnimationInfo {

    private int change_speed;
    private Color change_color;
    private int text_draw_speed;

    public SceneAnimationInfo(JsonObject animation_json){
        change_speed = animation_json.get("change-speed").asInt();
        change_color = Color.web(animation_json.get("change-color").asString());
        text_draw_speed = animation_json.get("text-speed").asInt();
    }


    public int getTextDrawTime() {
        return text_draw_speed;
    }

    public int getChangeTime() {
        return change_speed;
    }

    public Color getChangingColor() {
        return change_color;
    }
}
