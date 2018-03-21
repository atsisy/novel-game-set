package core;

import core.structure.FontData;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HighGradeTextPart {

    /*
    * 拡張機能を列挙したEnum
     */
    public enum FeatureType {
        TEXT,
        RUBY,
        COLOR,
        UNKNOWN;

        public static FeatureType strToMe(String str){
            switch (str){
                case "COLOR":
                    return COLOR;
                case "RUBY":
                    return RUBY;
            }

            return UNKNOWN;
        }
    }

    /*
    * 意図的に拡張機能が有効化されているか
     */
    private boolean extended;

    private String text;
    private String ruby;
    private Color color;
    private FontData data;

    /*
    * 有効化されている拡張機能のFeatureTypeが格納されている
     */
    private ArrayList<FeatureType> active_feature;

    /*
    * 一発ですべての拡張機能を使用する場合
     */
    public HighGradeTextPart(String text, String ruby, String html_format_color){
        this.text = text;
        this.ruby = ruby;
        this.color = Color.web(html_format_color);

        active_feature = new ArrayList<>();

        /*
        * 拡張機能有効化
         */
        extended = true;

        /*
        * すべて有効化
         */
        active_feature.addAll(List.of(FeatureType.COLOR, FeatureType.RUBY, FeatureType.TEXT));
    }

    /*
    * 必要なtext以外の拡張は行わないコンストラクタ
     */
    public HighGradeTextPart(String text){
        this.text = text;
        active_feature = new ArrayList<>();
        extended = false;
    }

    /*
    * 拡張するかどうかだけ設定するコンストラクタ
     */
    public HighGradeTextPart(boolean extended){
        active_feature = new ArrayList<>();
        this.extended = extended;
    }

    public Color getColor() {
        return color;
    }

    public String getRuby() {
        return ruby;
    }

    public String getText() {
        return text;
    }

    /*
     * SET関連は、最期にactive_featureを設定する
     */

    public void setColor(Color color) {
        this.color = color;
        active_feature.add(FeatureType.COLOR);
    }

    public void setColor(String html_color) {
        this.color = Color.web(html_color);
        active_feature.add(FeatureType.COLOR);
    }

    public void setData(FontData data) {
        this.data = data;
    }

    public void setRuby(String ruby) {
        this.ruby = ruby;
        active_feature.add(FeatureType.RUBY);
    }

    public void setText(String text) {
        this.text = text;
        active_feature.add(FeatureType.TEXT);
    }

    /*
    * そもそも意図的な拡張されていますか?
     */
    public boolean isAnyExtended() {
        return extended;
    }

    /*
    * 個別に拡張されているか調べる
     */
    public boolean isExtended(FeatureType type){
        return active_feature.contains(type);
    }

    /*
    * 関数を渡してもらい、それを実行する
     */
    public void activeFeatureStream(Consumer<FeatureType> function){
        active_feature.forEach(function);
    }
}
