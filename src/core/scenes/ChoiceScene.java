package core.scenes;

import core.FontData;
import core.GameController;
import core.HighGradeTextPart;
import core.SceneBasicInfo;
import graphic.ChoiceDrawer;
import graphic.Layer;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import parser.HighGradeTextInterpreter;
import parser.JsonParser;
import parser.ngsf.NGSFUtility;
import parser.ngsf.NGSFormatObject;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static parser.HighGradeTextInterpreter.END_FORMAT;
import static parser.HighGradeTextInterpreter.SPECIFY_BEGIN_FORMAT;
import static parser.HighGradeTextInterpreter.SPECIFY_END_FORMAT;

public class ChoiceScene extends ScenePart {

    private HashMap<Integer, Integer> next_scene_table;
    private ArrayList<ChoiceItem> choice_items;
    private ChoiceDrawer drawing_manager;

    /*
    * 選ばれたメニューのCHOICEID
     */
    private int selected_item_id;

    /*
    * 選ばれているメニューの上からのインデックス
     */
    private int selecting_id;

    public ChoiceScene(ArrayList<String> text_array_paths, String back_image_path, String back_display_mode, FontData fontData, SceneBasicInfo basicInfo, Optional<String> bgm_path){
        /*
         * スーパクラスを初期化
         * その後、シーンタイプを設定
         */
        super(text_array_paths, back_image_path, back_display_mode, fontData, basicInfo, bgm_path);

        next_scene_table = new HashMap<>();
        choice_items = new ArrayList<>();

        text_array_paths.stream()
                .map(JsonParser::loadWhole)
                .peek(whole_str -> {
                    ArrayList<ChoiceItem> tmp = collectChoiceItem(whole_str);
                    tmp.forEach(choiceItem -> {
                        next_scene_table.put(choiceItem.getChoiceID(), choiceItem.getNextSceneHash());
                        choice_items.add(choiceItem);
                    });
                }).map(value -> {
            /*
             * ここでやっていること
             * ブロックを1つのHighGradeTextで表す
             */
            HighGradeTextInterpreter interpreter = new HighGradeTextInterpreter();

            return interpreter.parseToHighGradeText(value);
        })
        .forEach(text_array::add);

        /*
        * 表示位置はJSONから読み取ったデータで指定
         */
        drawing_manager = new ChoiceDrawer(basicInfo.getTopPoint().getX(), basicInfo.getTopPoint().getY());

        selecting_id = 0;

        /*
         * シーンタイプはチョイス(選択肢)
         */
        this.scene_type = SceneType.CHOICE;
    }

    @Override
    public int nextSceneHash(){
        return next_scene_table.get(selected_item_id);
    }

    /**
     * collectChoiceItemメソッド
     * 本文全体をChoiceItemのArrayListにパースします
     * @param text 本文全体
     * @return パース後のArrayList
     */
    private static ArrayList<ChoiceItem> collectChoiceItem(String text){
        ArrayList<ChoiceItem> items = new ArrayList<>();

        /*
        * [|END|]がなくなるまでループ
        * textは段々と切り取られるため、いつかはなくなる
         */
        while(text.contains(END_FORMAT)){
            items.add(
                    /*
                    * extractChoiceItemメソッドに任せる
                    * 渡すのは[|.*+|]ooooooo[|END|]の文字列
                     */
                    extractChoiceItem(
                            text.substring(0, text.indexOf(END_FORMAT) + END_FORMAT.length())
                    )
            );

            /*
            * 処理済みのものは切り取る
             */
            text = text.substring(text.indexOf(END_FORMAT) + END_FORMAT.length());
        }

        return items;
    }

    /**
     * extractChoiceItemメソッド
     * [|.*+|]ooooooo[|END|] この形式を渡してください。 ChoiceItemに変換します。
     * @param text_part
     * @return
     */
    private static ChoiceItem extractChoiceItem(String text_part){
        NGSFormatObject ngsf_object = NGSFormatObject.parseNGSFormat(text_part);

        /*
        * ChoiceItemのインスタンス生成
        * 第一引数: 本文のみを抽出している。 ヘッダ終了インデックス TO フッタ開始インデックスまでの文字列
        * 第二引数: CHOICEID
        * 第三引数: この選択肢を選ぶとジャンプするシーンのハッシュ名
         */
        return new ChoiceItem(
                text_part.substring(text_part.indexOf(SPECIFY_END_FORMAT) + SPECIFY_END_FORMAT.length(), text_part.indexOf(END_FORMAT)),
                Integer.valueOf(ngsf_object.get("CHOICEID")),
                ngsf_object.get("GOTO")
        );
    }

    /**
     * removeChoiceメソッド
     * 本文の中から、選択肢情報が書かれたNGSFを取り除くメソッド
     * @param text 本文
     * @return 取り除かれた文字列
     */
    private static String removeChoiceNGSF(String text){
        NGSFormatObject object;
        StringBuilder builder = new StringBuilder();

        /*
        * "[|END|]"これがなくなるまでループ
        * 判定済みの文字列はStringBuilderで再構築するため、text変数の文字列はだんだん短くなっていき、
        * 最終的に0になる
         */
        while(text.contains(END_FORMAT)){
            String one_part;

            /*
            * NGSFが来るまでループ。文字はbuilderに入れる
             */
            for(char ch : text.toCharArray()){
                if(ch == '[')
                    break;
                builder.append(ch);
            }

            /*
            * NGSFヘッダ側の文字列を抽出 & 解析
             */
            object = NGSFormatObject.parseNGSFormat(text.substring(
                    text.indexOf(SPECIFY_BEGIN_FORMAT),
                    text.indexOf(SPECIFY_END_FORMAT) + SPECIFY_BEGIN_FORMAT.length()
            ));

            /*
            * ヘッダ、本文、フッタの文字列をone_partとして格納
             */
            one_part = text.substring(
                    text.indexOf(SPECIFY_BEGIN_FORMAT),
                    text.indexOf(END_FORMAT) + END_FORMAT.length());

            /*
            * NGSFヘッダの解析結果にTYPE=CHOICEの式があるか確認する
             */
            if(object.get("TYPE").equals("CHOICE")){
                /*
                * もしもあれば、本文だけとりだして格納
                 */
                builder.append(NGSFUtility.getInside(one_part));
            }else{
                /*
                * なければ、NGSFヘッダフッタごとbuilderに入れる
                 */
                builder.append(one_part);
            }

            /*
            * textは縮小
             */
            text = text.substring(text.indexOf(END_FORMAT) + END_FORMAT.length());
        }

        /*
        * 余った文字列も格納
         */
        if(text.length() > 0){
            builder.append(text);
        }

        return builder.toString();
    }

    /**
     * keyHandlerメソッド
     * 親クラスよりオーバーライド
     * 選択肢の場合、UPやDOWNの判定も行う必要があるため定義を行う
     * @param controller ゲームコントローラ こいつに定義されたメソッドを使用できるように渡されている
     * @param event キーイベント
     */
    @Override
    public void keyHandler(GameController controller, KeyEvent event){
        switch(event.getCode()){
            case UP:
                selecting_id--;
                selecting_id = (selecting_id + choice_items.size()) % choice_items.size();
                drawing_manager.drawPointer(controller, selecting_id, this::drawPointer);
                break;
            case DOWN:
                selecting_id++;
                selecting_id = (selecting_id + choice_items.size()) % choice_items.size();
                drawing_manager.drawPointer(controller, selecting_id, this::drawPointer);
                break;
            case ENTER:
                selected_item_id = choice_items.get(selecting_id).getChoiceID();
                controller.defaultKeyAction(event);
                break;
        }
    }

    private void drawPointer(Layer layer, Point2D point2D){
        layer.clear();
        layer.getGraphicsContext().setFont(new Font(20));
        layer.getGraphicsContext().fillText("____", point2D.getX(), point2D.getY());
    }

    public void drawChoiceItem(Layer layer, HighGradeTextPart highGradeTextPart){
        drawing_manager.drawChoiceMenu(layer, this, highGradeTextPart);
    }

    @Override
    public void initHandler(GameController controller){
        selecting_id = 0;
        selected_item_id = 0;
    }

    @Override
    public void finishHandler(GameController controller){
        controller.getFreeLayer().clear();
        drawing_manager.clear();
    }

    /**
     * afterFirstDrawingHandlerメソッド
     * 初回描画処理完了後呼び出される
     * @param controller
     */
    public void afterFirstDrawingHandler(GameController controller){
        drawing_manager.drawPointer(controller, selecting_id, this::drawPointer);
    }
}
