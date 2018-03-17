package parser.ngsf;

/**
 * [|.*+|]形式の式を表現するためのクラス
 */
public class NGSExpression {

    private String left_hand_side;
    private String right_hand_side;

    /**
     * コンストラクタ
     * @param left 左辺の文字列
     * @param right 右辺の文字列
     */
    public NGSExpression(String left, String right){
        left_hand_side = left;
        right_hand_side = right;
    }

    /**
     * getLeftメソッド
     * 左辺値を返す
     * @return 左辺値の文字列
     */
    public String getLeft() {
        return left_hand_side;
    }

    /**
     * getRightメソッド
     * 右辺値を返す
     * @return 右辺値の文字列
     */
    public String getRight() {
        return right_hand_side;
    }

    /**
     * isLeftValueメソッド
     * @param val 左辺値と引数の文字列が等しいか判定する
     * @return 判定結果 true or false
     */
    public boolean isLeftValue(String val){
        return left_hand_side.equals(val);
    }

    /**
     * isRightValueメソッド
     * @param val 右辺値と引数の文字列が等しいか判定する
     * @return 判定結果 true or false
     */
    public boolean isRightValue(String val){
        return right_hand_side.equals(val);
    }

    /**
     * hashCodeメソッド
     * ハッシュの生成方法は、左辺値、右辺値の結合文字列のハッシュ値
     * @return ハッシュ値
     */
    @Override
    public int hashCode(){
        StringBuilder builder = new StringBuilder();
        return builder.append(left_hand_side).append(right_hand_side).toString().hashCode();
    }
}
