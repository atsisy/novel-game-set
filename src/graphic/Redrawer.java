package graphic;

import text.HighGradeText;
import text.HighGradeTextPart;

public class Redrawer {

    private String local_space_alignment;

    public Redrawer(){
        local_space_alignment = "";
    }

    public String redraw(TextDrawer text_drawer, Layer textLayer, HighGradeText highGradeText){
        StringBuilder builder = new StringBuilder();
        highGradeText.stream().forEach(highGradeTextPart -> {
            builder.append(highGradeTextPart.getText());
            String display_string = local_space_alignment + highGradeTextPart.getText();
            text_drawer.justDraw(textLayer, display_string);
            local_space_alignment = text_drawer.createAddtionalAlignment(display_string);
        });
        return builder.toString();
    }

    public void reset(){
        local_space_alignment = "";
    }

    public String getLocalSpaceAlignment() {
        return local_space_alignment;
    }
}
