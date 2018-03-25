package text;

import javafx.scene.paint.Color;

import javafx.geometry.Point2D;
import text.HighGradeTextPart;

import java.util.ArrayList;
import java.util.stream.Stream;

public class HighGradeText {

    /*
    * 様々な機能を提供するための高レベルテキストクラス
     */

    private ArrayList<HighGradeTextPart> whole_text;

    private boolean refresh;
    /*
    * コンストラクタはメンバの初期化だけ。具体的な値の挿入は行わない
     */
    public HighGradeText(){
        whole_text = new ArrayList<>();

        /*
         * refreshはデフォルトで有効
         */
        refresh = true;
    }

    /*
    * HighGradeTextを構成するHighGradeTextPartクラスは、
    * ArrayListで管理する。コンストラクタ時では、そのPart郡で初期化を行わず、
    * あとで、addPartメソッドを使い追加する
     */
    public void addPart(HighGradeTextPart part){
        whole_text.add(part);
    }

    /*
    * whole_textのstreamを返すメソッド。いい感じに使うこと
     */
    public Stream<HighGradeTextPart> stream(){
        return whole_text.stream();
    }

    /*
    * 拡張機能が使われていないPartがそのままになっていると、統一性が欠け、いろいろとめんどくさくなるので、
    * ここで、特定の拡張機能が使われていないPartに対し、その機能のこのHighGradeTextが属するシーンのデフォルト値を
    * 適用する。
     */
    public void setDefaultTextStatus(Color color){
        /*
        * このHighGradeTextオブジェクトが属するシーンのデフォルト値を設定
         */

        /*
        * Color
         */
        whole_text.stream().filter(part -> !part.isExtended(HighGradeTextPart.FeatureType.COLOR)).forEach(conv -> conv.setColor(color));

        /*
        * Ruby
         */
        whole_text.stream().filter(part -> !part.isExtended(HighGradeTextPart.FeatureType.RUBY)).forEach(conv -> conv.setRuby(""));
    }

    public void setRefresh(boolean status){
        this.refresh = status;
    }

    public boolean isRefreshEnabled() {
        return refresh;
    }

    public int sizeOfParts(){
        return whole_text.size();
    }
}
