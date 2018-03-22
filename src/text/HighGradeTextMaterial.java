package text;

import javafx.geometry.Point2D;

public class HighGradeTextMaterial {

    private String text;
    private boolean refresh;

    public HighGradeTextMaterial(String whole_text, boolean refresh){
        this.text = whole_text;
        this.refresh = refresh;
    }

    public String getText() {
        return text;
    }

    public boolean isRefreshEnabled() {
        return refresh;
    }
}
