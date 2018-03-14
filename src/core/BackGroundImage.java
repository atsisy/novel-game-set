package core;

import javafx.scene.image.Image;

import java.io.File;

public class BackGroundImage {

    public enum DisplayType {
        CENTER,
        LEFT_TOP,
        SCALE;

        public static DisplayType strToMe(String s){
            /*
            * string -> DisplayType
             */
            switch (s){
                case "CENTER":
                    return CENTER;
                case "LT":
                    return LEFT_TOP;
                case "SCALE":
                    return SCALE;
            }

            return CENTER;
        }
    }

    private Image image;
    private DisplayType type;

    public BackGroundImage(String image_path, DisplayType type){
        image = new Image(new File(image_path).toURI().toString());
        this.type = type;
    }

    public Image getImage() {
        return image;
    }
}
