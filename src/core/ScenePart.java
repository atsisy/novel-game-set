package core;

import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import parser.JsonParser;
import parser.TextSeparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class ScenePart {

    private ArrayList<String> text_array;
    private BackGroundImage backGroundImage;
    private FontData fontData;
    private AudioClip audioPlayer;

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

        audioPlayer = null;
        bgm_path.ifPresent(path -> {
            audioPlayer = new AudioClip(new File(path).toURI().toString());
        });
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
        if(audioPlayer != null){
            audioPlayer.play();
            return true;
        }

        return false;
    }

    public boolean stopAudio(){
        if(audioPlayer != null){
            audioPlayer.stop();
            return true;
        }

        return false;
    }
}
