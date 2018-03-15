package core;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import parser.JsonParser;
import parser.TextSeparator;

import java.util.ArrayList;
import java.util.Optional;

public class ScenePart {

    private ArrayList<String> text_array;
    private BackGroundImage backGroundImage;
    private FontData fontData;
    private AudioPlayer audioPlayer;

    public ScenePart(ArrayList<String> text_array_paths, String back_image_path, String back_display_mode, FontData fontData, Optional<String> bgm_path) {
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

        /*
        * フォントデータ格納
         */
        this.fontData = fontData;

        /*
        * audioプレイヤーを初期化
         */
        audioPlayer = new AudioPlayer(bgm_path);
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
        return fontData.getFont();
    }

    public Color getFontColor() {
        return fontData.getColor();
    }

    public boolean playAudio(){
        return audioPlayer.play();
    }

    public boolean stopAudio(){
        return audioPlayer.stop();
    }
}
