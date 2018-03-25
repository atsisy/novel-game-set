package graphic;

import core.GameController;
import text.HighGradeTextPart;
import core.scenes.ChoiceScene;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class ChoiceDrawer {

    private ArrayList<Point2D> choice_menu_point;
    private Point2D init_point;
    private Point2D left_top_point;

    /**
     * ChoiceDrawerクラスのコンストラクタ
     * @param x 選択肢を描画する左上側のX座標
     * @param y 選択肢を描画する左上側のY座標
     */
    public ChoiceDrawer(double x, double y){
        choice_menu_point = new ArrayList<>();

        /*
        * init_pointは変更禁止
        * 保存用として持っておく
         */
        init_point = new Point2D(x, y);
        left_top_point = new Point2D(x, y);
    }

    /**
     * drawChoiceMenuメソッド
     * 選択肢一つひとつごとに一回ずつ呼び出される
     * @param layer 選択肢を描画するレイヤー
     * @param scene 描画するHighGradeTextPartを保持するChoiceSceneオブジェクト
     * @param highGradeTextPart 描画するHighGradeTextPart
     */
    public void drawChoiceMenu(Layer layer, ChoiceScene scene, HighGradeTextPart highGradeTextPart){
        /*
        * 選択肢描画
         */
        layer.getGraphicsContext().fillText(highGradeTextPart.getText(), left_top_point.getX(), left_top_point.getY());


        /*
        * 今回の選択肢を描画する座標をchoice_menu_pointに追加
        * アクセスには選択中のindexが使われる
        * このメソッドは、各選択肢項目ごとに、順番に呼び出されるため、
        * 選択中の項目のindexでアクセスできるわけです
         */
        choice_menu_point.add(left_top_point);

        /*
        * 次に呼び出されるときのために、left_top_pointを編集しておく
        * scene.getFont().getSize()でフォントの高さが得られるので、
        * それを加算する。
         */
        left_top_point = left_top_point.add(0, scene.getFont().getSize());
    }

    /**
     * drawPointerメソッド
     * @param controller ゲーム管理オブジェクト 第三引数の高階関数に渡す
     * @param selecting_index 選択している項目のインデックス 第三引数の高階関数に渡す
     * @param draw_pointer_function シーンごとに定義できるポインター描画メソッド
     */
    public void drawPointer(GameController controller, int selecting_index, BiConsumer<Layer, Point2D> draw_pointer_function){
        draw_pointer_function.accept(
                controller.getFreeLayer(),
                choice_menu_point.get(selecting_index)
        );
    }

    /**
     * clearメソッド
     * このオブジェクトをリセットするメソッド
     */
    public void clear(){
        choice_menu_point.clear();
        left_top_point = new Point2D(init_point.getX(), init_point.getY());
    }
}
