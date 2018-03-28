package core.scenes;

import core.structure.FontData;
import core.GameController;
import text.HighGradeText;
import core.structure.SceneAnimationInfo;
import core.structure.SceneBasicInfo;
import text.HighGradeTextInterpreter;
import parser.JsonParser;
import parser.TextSeparator;

import java.util.ArrayList;
import java.util.Optional;

public class PlainTextScene extends ScenePart {

    private int next_scene_hash;

    public PlainTextScene(ArrayList<String> text_array_paths, String back_image_path, String back_display_mode, FontData fontData, SceneBasicInfo basicInfo, SceneAnimationInfo animationInfo, String bgm_path, int next_scene_hash){
        /*
        * スーパクラスを初期化
        * その後、シーンタイプを設定
         */
        super(text_array_paths, back_image_path, back_display_mode, fontData, basicInfo, animationInfo, bgm_path);

        /*
         * すべてのテキストファイルから内容を読み出し、text_arrayに追加
         */
        text_array_paths.stream()
                .map(JsonParser::loadWhole)
                .map(TextSeparator::separateTextByMark)
                .map(value -> {
                    /*
                     * ここでやっていること
                     * セパレータによって分割された文字列のシーンごとに区切られたものが次々と放られてくる
                     * valueはArrayList(1シーン分の複数のセパレートされたブロック)
                     * セパレートされたブロック一つを1つのHighGradeTextで表す
                     * これをArrayListにして返す
                     */
                    ArrayList<HighGradeText> result = new ArrayList<>();
                    HighGradeTextInterpreter interpreter = new HighGradeTextInterpreter();

                    value.forEach(material -> {
                        HighGradeText text = interpreter.parseToHighGradeText(material.getText());
                        text.setRefresh(material.isRefreshEnabled());
                        result.add(text);
                    });
                    return result;
                })
                .forEach(text_array::addAll);

        /*
         * 拡張されてないものを、デフォルトの状態に設定する
         */
        text_array.forEach(highGradeText -> highGradeText.setDefaultTextStatus(fontData.getColor()));

        /*
        * このシーンが終わったあとを定義
         */
        this.next_scene_hash = next_scene_hash;

        /*
        * シーンタイプはプレーンテキスト
         */
        this.scene_type = SceneType.PLAIN_TEXT;
    }

    @Override
    public int nextSceneHash(){
        return next_scene_hash;
    }

    @Override
    public void initHandler(GameController controller){}

    @Override
    public void finishHandler(GameController controller){}

    @Override
    public void afterFirstDrawingHandler(GameController controller){}
}
