package core;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class FontData {

    private Font font;
    private int size;
    private Color color;

    public FontData(String font_name, int size, String html_format_color){
        font = new Font(font_name, size);
        color = Color.web(html_format_color);
    }

    public Font getFont() {
        return font;
    }

    public int getSize() {
        return size;
    }

    public Color getColor() {
        return color;
    }
}
