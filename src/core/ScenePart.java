package core;

import javafx.scene.text.Font;
import parser.JsonParser;
import parser.TextSeparator;

import java.util.ArrayList;

public class ScenePart {

    private ArrayList<String> text_array;
    private BackGroundImage backGroundImage;
    private Font font;

    public ScenePart(ArrayList<String> text_array_paths, String back_image_path, String back_display_mode, String font_name, int font_size) {
        /*
        * コンストラクタ
        * JsonParserに実行してもらう
         */
        text_array = new ArrayList<>();

        /*
        * すべてのテキストファイルから内容を読み出し、text_arrayに追加
         */
        text_array_paths.stream()
                .map(JsonParser::loadWhole)
                .map(TextSeparator::separateTextByMark)
                .forEach(text_array::addAll);

        backGroundImage = new BackGroundImage(back_image_path, BackGroundImage.DisplayType.strToMe(back_display_mode));
        font = new Font(font_name, font_size);
    }

    public String getText(int index){
        return text_array.get(index);
    }

    public BackGroundImage getBackGroundImage() {
        return backGroundImage;
    }

    public int lastIndexOfText(){
        return text_array.size() - 1;
    }

    public Font getFont() {
        return font;
    }
}
