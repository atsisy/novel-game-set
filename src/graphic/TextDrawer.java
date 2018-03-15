package graphic;

import javafx.geometry.Point2D;

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
    }

    /**
     * drawメソッド
     * @param layer 書き込むレイヤー
     * @param text 表示する文字列
     */
    public void draw(Layer layer, String text){
        StringBuilder builder = new StringBuilder();

        /*
        * 空白と改行で構成されたalignmentとtextを結合し、これから表示する文字列を作り出す
         */
        String display_string = builder.append(space_alignment).append(text).toString();

        /*
        * 文字列を描画
         */
        layer.getGraphicsContext().fillText(
                display_string,
                point.getX(),
                point.getY()
                );

        /*
        * buiderを初期化
         */
        builder.setLength(0);

        /*
        * createAddtionalAlignmentメソッドを使い、space_alignmentを更新
         */
        space_alignment = createAddtionalAlignment(text);
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
            }if(String.valueOf(ch).getBytes().length < 2) {
                builder.append(half_space);
            }else{
                builder.append(full_space);
            }
        }

        /*
        * 文字列を生成して終了
         */
        return builder.toString();
    }
}
