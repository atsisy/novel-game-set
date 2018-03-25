package graphic;

import core.scenes.PlainTextScene;
import core.scenes.ScenePart;
import core.structure.SceneAnimationInfo;
import javafx.animation.Animation;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import text.HighGradeText;
import text.HighGradeTextPart;

import java.util.function.Consumer;

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

    private Redrawer redrawer;


    private SequentialTransition draw_process;
    /*
    * space_alignmentは""だけ
    * pointも初期化
     */
    public TextDrawer(double x, double y){
        point = new Point2D(x, y);
        space_alignment = "";
        latest_text = new StringBuilder();
        draw_process = new SequentialTransition();
        redrawer = new Redrawer();
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
    public void draw(Layer layer, ScenePart scene, HighGradeTextPart text, boolean refresh, int expected_part_size){
        /*
        * 新しいテキストを追加
         */
        latest_text.append(text.getText());

        /*
        * 空白と改行で構成されたalignmentとtextを結合し、これから表示する文字列を作り出す
         */
        String display_string = space_alignment + text.getText();

        /*
        * 文字列を描画
         */
        addAnimation(layer, display_string, scene.getAnimationInfo(), text, expected_part_size);

        /*
        * createAddtionalAlignmentメソッドを使い、space_alignmentを更新
         */
        space_alignment = createAddtionalAlignment(display_string);
    }

    private void addAnimation(Layer layer, String text, SceneAnimationInfo animationInfo, HighGradeTextPart text_part, int expected_size){
        if(animationInfo.getTextDrawTime() < 0){
            /*
            * アニメーションは使用しない
             */
            justDraw(layer, text);
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


            int last_index;
            String local_text;

            {
                /*
                 * mill_secondかけてアニメーションを行う
                 */
                setCycleDuration(Duration.millis(animationInfo.getTextDrawTime() / expected_size));

                /*
                 * 一回のみで十分
                 */
                setCycleCount(1);

                local_space_alignment = space_alignment.length();
                effective_text_length = text.length() - local_space_alignment;

                last_index = 0;
                local_text = new String(text);

            }

            @Override
            protected void interpolate(double frac) {
                //layer.clear();

                last_index = local_space_alignment + (int)((double)effective_text_length * frac);

                text_part.applyFeatures(layer);

                layer.getGraphicsContext().fillText(
                        local_text.substring(0,
                                /*
                                * 空白部分を最初から入れて計算する
                                 */
                                last_index
                        ),
                        point.getX(),
                        point.getY()
                );

                local_text = replaceRange(local_text, local_space_alignment, last_index);
            }
        };
        draw_process.getChildren().add(animation);
        draw_process.setOnFinished(event -> draw_process.getChildren().clear());
    }

    public void drawRedrawingMode(Layer textLayer, PlainTextScene scene, int local_index){
        HighGradeText target_text = scene.getHighGradeText(local_index);
        latest_text.append(
                redrawer.redraw(this, textLayer, target_text)
        );
        System.out.println("->" + target_text.toString());
    }

    public void justDraw(Layer layer, String text){
        layer.getGraphicsContext().fillText(
                text,
                point.getX(),
                point.getY()
        );
    }

    public void exec_drawing(){
        draw_process.play();
    }

    private String replaceRange(String text, int begin, int end){
        StringBuilder builder = new StringBuilder();
        return builder
                .append(text.substring(0, begin))
                .append(createAlignmentByRange(text, begin, end))
                .append(text.substring(end)).toString();
    }

    /**
     * createAdditionalAlignmentメソッド
     * @param text 対応した空白の文字列を作るための文字列
     * @return 空白と改行だけで構成されたアラインメント
     */
    public String createAddtionalAlignment(String text){
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
     * @param begin begin ~ endまでの文字列が対称となる
     * @param end   begin ~ endまでの文字列が対称となる
     * @return 空白と改行だけで構成されたアラインメント
     */
    private String createAlignmentByRange(String text, int begin, int end){
        return createAddtionalAlignment(text.substring(begin, end));
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

        System.out.println(latest_text.toString());

        layer.getGraphicsContext().fillText(
                latest_text.toString(),
                point.getX(),
                point.getY()
        );
    }

    /**
     * stopAnimationメソッド
     * 文字列描画アニメーションを終了するメソッド
     * この時、draw_processに溜まっているアニメーションは破棄される
     */
    public void stopAnimation(){
        draw_process.pause();
        draw_process.stop();
        draw_process.getChildren().clear();
    }

    public void standbyRedrawing(){
        redrawer.reset();
    }

    public void finalizeRedrawing(){
        space_alignment = redrawer.getLocalSpaceAlignment();
    }
}
