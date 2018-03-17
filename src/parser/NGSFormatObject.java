package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class NGSFormatObject {

    /*
    * この２つが所持する内容は同等
    * メモリ効率的にはほぼ変わらないはずなので、まあいい
     */
    private HashMap<String, String> data_table;
    private ArrayList<NGSExpression> expressions;

    /**
     * parseNGSFormatメソッド
     * @param feature_area [||]で囲まれる文字列
     * @return パース後のオブジェクト
     */
    public static NGSFormatObject parseNGSFormat(String feature_area){
        HashMap<String, String> data_table = new HashMap<>();
        ArrayList<NGSExpression> expressions = new ArrayList<>();
        ArrayList<String> features = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        boolean feat_record_on;
        /*
         * 一文字目は、ループ外で判定
         * '|'であれば、記録開始
         */
        feat_record_on = feature_area.charAt(0) == '|';
        for(int i = 1;i < feature_area.length();i++){

            /*
             * 記録中かつ、現在の文字が'|'だった場合、記録を完了し、式をfeaturesに追加
             */
            if(feat_record_on && feature_area.charAt(i) == '|'){
                /*
                 * builderから文字列生成
                 * その後、builderをリセット
                 */
                features.add(builder.toString());
                builder.setLength(0);

                // 記録完了
                feat_record_on = false;
                continue;
            }

            if(feat_record_on){
                /*
                 * 記録中フラグが立っているので、現在の文字を記録する
                 */
                builder.append(feature_area.charAt(i));
            }else{
                /*
                 * 記録を開始すべきか確認する
                 */
                feat_record_on = feature_area.charAt(i) == '|';
            }
        }

        /*
        * データ格納処理
         */
        features.stream().map(format -> {
            /*
            * NGSExpressionのインスタンス生成
            * 新しいstream作成
             */
            return new NGSExpression(getLeftHandSide(format), getRightHandSide(format));
        }).forEach(ngsExpression -> {
            expressions.add(ngsExpression);
            data_table.put(ngsExpression.getLeft(), ngsExpression.getRight());
        });

        return new NGSFormatObject(data_table, expressions);

    }

    private NGSFormatObject(HashMap<String, String> data_table, ArrayList<NGSExpression> expressions){
        this.data_table = data_table;
        this.expressions = expressions;
    }

    /**
     * getLeftHandSideメソッド
     * XX=YYのような文字列を渡してください
     * @param expression 式 XX=YY形式
     * @return 左辺の情報を文字列で返す
     */
    private static String getLeftHandSide(String expression){
        StringBuilder builder = new StringBuilder();

        /*
         * '='が来るまでの文字列を記録することで、左辺のみを取り出す
         */
        for(int i = 0;i < expression.length() && expression.charAt(i) != '=';i++){
            builder.append(expression.charAt(i));
        }

        /*
         * 文字列生成
         */
        return builder.toString();
    }

    /**
     * getRightHandSideメソッド
     * XX=YYのような文字列を渡してください
     * @param expression 式 XX=YY形式
     * @return 右辺の情報を文字列で返す
     */
    private static String getRightHandSide(String expression){
        /*
         * '='後の文字列を切り取って返す
         */
        return expression.substring(expression.indexOf('=') + 1, expression.length());
    }

    /**
     * hasメソッド
     * @param lhs_value 保持しているか確認したい左辺値
     * @return 持っているか否か
     */
    public boolean has(String lhs_value){
        return data_table.containsKey(lhs_value);
    }

    /**
     * streamメソッド
     * @return 式のストリーム
     */
    public Stream<NGSExpression> stream(){
        return expressions.stream();
    }

}
