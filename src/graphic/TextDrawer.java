package graphic;

import core.scenes.ScenePart;
import core.structure.SceneAnimationInfo;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class TextDrawer {

    private static final char half_space = ' ';
    private static final char full_space = '　';

    /*
    * space_alignment
    * かぶせながら表示していくアルゴリズムに必要
    *
     */
    private String space_alignment;

    /*
    * テキスト切り替え（SEPARATE）までの描画した文字列を
    * 保存しておく
     */
    private StringBuilder latest_text;

    /*
    * 文字描画のアルゴリズム的に、常に同じ位置から書き始める必要があるため、
    * 位置を記憶しておく
     */
    private Point2D point;

    /*
    * space_alignmentは""だけ
    * pointも初期化
     */
    public TextDrawer(double x, double y){
        point = new Point2D(x, y);
        space_alignment = "";
        latest_text = new StringBuilder();
    }

    /**
     * TextDrawerオブジェクトを初期化するメソッド
     * @param x テキスト描画のX座標
     * @param y テキスト描画のY座標
     */
    public void reset(double x, double y){
        point = new Point2D(x, y);
        space_alignment = "";
        latest_text.setLength(0);
    }

    /**
     * drawメソッド
     * @param layer 書き込むレイヤー
     * @param scene 書き込みを行うシーン
     * @param text 表示する文字列
     */
    public void draw(Layer layer, ScenePart scene, String text, boolean refresh){
        /*
        * 新しいテキストを追加
         */
        latest_text.append(text);

        /*
        * 空白と改行で構成されたalignmentとtextを結合し、これから表示する文字列を作り出す
         */
        String display_string = space_alignment + text;

        /*
        * 文字列を描画
         */
        drawAnimation(layer, display_string, scene.getAnimationInfo());

        /*
        * createAddtionalAlignmentメソッドを使い、space_alignmentを更新
         */
        space_alignment = createAddtionalAlignment(display_string);
    }

    private void drawAnimation(Layer layer, String text, SceneAnimationInfo animationInfo){
        if(animationInfo.getTextDrawTime() < 0){
            /*
            * アニメーションは使用しない
             */
            layer.getGraphicsContext().fillText(
                    text,
                    point.getX(),
                    point.getY()
            );
            return;
        }
        Animation animation = new Transition() {

            /*
            * 先頭の空白部分を覗いた、本来表示するべき有効文字列長を格納する
             */
            private int effective_text_length;

            /*
            * このアニメーションが定義された当時の先頭空白部分の長さ
            * アニメーションは定義後即座に実行されるものではないので、
            * このように、当時の空白部分の長さを保持しておく
             */
            private int local_space_alignment;

            {

                /*
                 * mill_secondかけてアニメーションを行う
                 */
                setCycleDuration(Duration.millis(animationInfo.getTextDrawTime()));

                /*
                 * 一回のみで十分
                 */
                setCycleCount(1);

                local_space_alignment = space_alignment.length();
                effective_text_length = text.length() - local_space_alignment;

            }

            @Override
            protected void interpolate(double frac) {
                layer.clear();
                layer.getGraphicsContext().fillText(
                        text.substring(0,
                                /*
                                * 空白部分を最初から入れて計算する
                                 */
                                local_space_alignment + (int)((double)effective_text_length * frac)
                        ),
                        point.getX(),
                        point.getY()
                );
            }
        };
        animation.play();
    }

    /**
     * createAdditionalAlignmentメソッド
     * @param text 対応した空白の文字列を作るための文字列
     * @return 空白と改行だけで構成されたアラインメント
     */
    private String createAddtionalAlignment(String text){
        /*
        * 文字列をStringから文字の配列に直す
        * これから配列でループするため、変換を行う
         */
        char[] array = text.toCharArray();
        StringBuilder  builder = new StringBuilder();

        /*
        * 受け取った文字列の空白バージョンを生成
        * 改行: \n
        * 半角空白: " "
        * 全角空白: "　"
         */
        for(char ch : array){
            if(ch == '\n'){
                builder.append('\n');
            }else{
                builder.append(getPropSpace(ch));
            }
        }

        /*
        * 文字列を生成して終了
         */
        return builder.toString();
    }


    /**
     * createAdditionalAlignmentメソッド
     * @param text 対応した空白の文字列を作るための文字列
     * @param length 0 ~ lengthまでの文字列が対称となる
     * @return 空白と改行だけで構成されたアラインメント
     */
    private String createAddtionalAlignment(String text, int length){
        return createAddtionalAlignment(text.substring(0, length));
    }

    /**
     * getPropSpaceメソッド
     * 引数で受けた文字にふさわしいスペースを返すメソッド
     * @param ch 検証する文字
     * @return ふさわしいスペース
     */
    private char getPropSpace(char ch){
        if(String.valueOf(ch).getBytes().length < 2) {
            return half_space;
        }else{
            return full_space;
        }
    }

    /**
     * drawLatestTextメソッド
     * 現在ウィンドウに表示されているであろう文字列を描画する
     * @param layer テキストを描画するレイヤー
     */
    public void drawLatestText(Layer layer){
        layer.getGraphicsContext().fillText(
                latest_text.toString(),
                point.getX(),
                point.getY()
        );
    }
}
