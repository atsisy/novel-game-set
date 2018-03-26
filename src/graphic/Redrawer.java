package graphic;

import text.HighGradeText;

public class Redrawer {

    private String local_space_alignment;

    public Redrawer(){
        local_space_alignment = "";
    }

    public String redraw(TextDrawer text_drawer, Layer textLayer, HighGradeText highGradeText){
        StringBuilder builder = new StringBuilder();

        /*
        * 指定されたHighGradeTextに属するPartを全て描画する
         */
        highGradeText.stream().forEach(highGradeTextPart -> {

            /*
            * Partの特徴、属性を適用
             */
            highGradeTextPart.applyFeatures(textLayer);

            /*
            * 描画文字列を次回以降のために保存
             */
            builder.append(highGradeTextPart.getText());

            // 表示に適した形の文字列生成
            String display_string = local_space_alignment + highGradeTextPart.getText();

            // 描画処理を行う
            text_drawer.justDraw(textLayer, display_string);

            /*
            * 次回以降の描画のために、文字列整形用文字列を再定義
             */
            local_space_alignment = text_drawer.createAddtionalAlignment(display_string);
        });

        return builder.toString();
    }

    public void reset(){
        local_space_alignment = "";
    }

    public String getLocalSpaceAlignment() {
        return local_space_alignment;
    }
}
