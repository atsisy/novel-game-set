package parser;

import core.HighGradeText;
import core.HighGradeTextPart;
import parser.ngsf.NGSFormatObject;

public class HighGradeTextInterpreter {

    /*
    * 文章を拡張するための指定子
     */
    public static final String SPECIFY_BEGIN_FORMAT = "[|";
    public static final String SPECIFY_END_FORMAT = "|]";
    public static final String END_FORMAT = "[|END|]";

    /**
     * parseToHighGradeTextメソッド
     * @param text 1シーンの文章
     * @return 引数のtextをHighGradeTextをパースしたもの
     */
    public HighGradeText parseToHighGradeText(String text) {
        /*
        * 結果的の返すためのHighGradeTextのインスタンスを生成
         */
        HighGradeText highGradeText = new HighGradeText();

        /*
        * 指定子が含まれていると、最初の一文がうまくパースできなくなるので、最初にやっておく
        * indexOfは、該当する文字列が存在しないと、-1を返す。なので、ここではパスすべきで、条件式を満たしている
        * indexOfで、結果が0の場合、ここで前にくっついた文字列を取り除く必要が無い（そもそも文字列自体無い）ので、条件式を満たしている
         */
        if(text.indexOf(SPECIFY_BEGIN_FORMAT) > 0){
            highGradeText.addPart(new HighGradeTextPart(text.substring(0, text.indexOf(SPECIFY_BEGIN_FORMAT))));
        }

        /*
        * パースしていくと、指定子がなくなっていくので、指定子がなくなるまでパースを続ける
         */
        while(text.contains(SPECIFY_BEGIN_FORMAT)){
            /*
            * 拡張文章開始を示す指定子が始まる文字のインデックス
             */
            int start_point = text.indexOf(SPECIFY_BEGIN_FORMAT);

            /*
            * 拡張文章終了を示す指定子が終わる文字のインデックス
             */
            int end_point = text.indexOf(END_FORMAT) + END_FORMAT.length();

            /*
            * makePartはHighGradeTextPartオブジェクトを生成するメソッド
            * それをHighGradeTextに追加する
             */
            highGradeText.addPart(makePart(text, start_point, end_point));

            /*
            * パースし終えた部分を切り離し、新しい文字列をtextに代入し直す
             */
            text = text.substring(end_point);
        }

        /*
        * 拡張されていない文章が残っている場合は、パースを行う
         */
        if(text.length() > 0){
            highGradeText.addPart(new HighGradeTextPart(text));
        }

        /*
        * それを返す
         */
        return highGradeText;
    }

    /**
     * makePartメソッド
     * @param text 取り出したい拡張文章が含まれた一つの大きな文章
     * @param start_index 取り出したい拡張領域（拡張開始指定子と拡張終了指定子に囲まれた領域）が始まる文字のインデックス
     * @param end_index 取り出したい拡張領域（拡張開始指定子と拡張終了指定子に囲まれた領域）が終わる文字のインデックス
     * @return 拡張領域をパースしたHighGradeTextPartオブジェクト
     */
    private HighGradeTextPart makePart(String text, int start_index, int end_index){

        /*
        * builderに記録中であることを示すフラグ
         */
        boolean feat_record_on;

        /*
        * 拡張終了指定子が現れる文字列のインデックスを計算している
        * 拡張領域終了のインデックス - 終了指定子の長さ
         */
        int point_of_begining_end_feat = end_index - END_FORMAT.length();

        /*
        * 結果を格納するためのインスタンスを生成
         */
        HighGradeTextPart result = new HighGradeTextPart(true);

        /*
        * 拡張領域（[|.*|] -> [|END|]）の文字列だけを取り出す
         */
        String feature_area = text.substring(start_index, end_index);

        /*
        * 拡張開始指定子兼拡張情報記載箇所だけを取り出す
         */
        feature_area = feature_area.substring(0, feature_area.indexOf(SPECIFY_END_FORMAT) + SPECIFY_END_FORMAT.length());

        /*
        * 拡張の影響を受けるプレーンテキストを取り出す
        * 拡張領域開始インデックス + 拡張開始指定子兼拡張情報記載箇所の長さ　TO 拡張終了指定子開始のインデックス　まで
         */
        String text_area = text.substring(start_index + feature_area.length(), point_of_begining_end_feat);
        // それを格納
        result.setText(text_area);

        /*
        * NGSFormat形式の式をパーサに渡し、処理を行う
         */
        NGSFormatObject ngs_object = NGSFormatObject.parseNGSFormat(feature_area);
        ngs_object.stream().forEach(ngsExpression -> {
            String right_one = ngsExpression.getRight();
            switch(what_feat(ngsExpression.getLeft())){
                case COLOR:
                    result.setColor(right_one);
                    break;
                case RUBY:
                    result.setRuby(right_one);
                    break;
            }
        });

        // 完了
        return result;
    }

    /**
     * what_featメソッド
     * 左辺を渡してください
     * @param left_value 式 XX=YY形式
     * @return 左辺はどの拡張情報のためのものだったか
     */
    private HighGradeTextPart.FeatureType what_feat(String left_value){
        /*
        * FeatureType#strToMeでFeatureTypeに変換
         */
        return HighGradeTextPart.FeatureType.strToMe(left_value);
    }

    /**
     * getRightValueメソッド
     * XX=YYのような文字列を渡してください
     * @param text 式 XX=YY形式
     * @return 右辺の情報を文字列で返す
     */
    private String getRightValue(String text){
        /*
        * '='後の文字列を切り取って返す
         */
        return text.substring(text.indexOf('=') + 1, text.length());
    }
}
