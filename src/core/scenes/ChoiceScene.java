package core.scenes;

import core.FontData;
import core.GameController;
import core.HighGradeText;
import core.SceneBasicInfo;
import javafx.scene.input.KeyEvent;
import parser.HighGradeTextInterpreter;
import parser.JsonParser;
import parser.ngsf.NGSFUtility;
import parser.ngsf.NGSFormatObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static parser.HighGradeTextInterpreter.END_FORMAT;
import static parser.HighGradeTextInterpreter.SPECIFY_BEGIN_FORMAT;
import static parser.HighGradeTextInterpreter.SPECIFY_END_FORMAT;

public class ChoiceScene extends ScenePart {

    private HashMap<Integer, Integer> next_scene_table;
    private ArrayList<ChoiceItem> choice_items;

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
                .map(whole_str -> {
                    ArrayList<ChoiceItem> tmp = collectChoiceItem(whole_str);
                    tmp.forEach(choiceItem -> {
                        next_scene_table.put(choiceItem.getChoiceID(), choiceItem.getNextSceneHash());
                        choice_items.add(choiceItem);
                    });
                    return removeChoiceNGSF(whole_str);
                }).map(value -> {
            /*
             * ここでやっていること
             * ブロックを1つのHighGradeTextで表す
             */
            HighGradeTextInterpreter interpreter = new HighGradeTextInterpreter();

            return interpreter.parseToHighGradeText(value);
        })
        .forEach(text_array::add);

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

    private static ArrayList<ChoiceItem> collectChoiceItem(String text){
        ArrayList<ChoiceItem> items = new ArrayList<>();
        while(text.contains(END_FORMAT)){
            int begin_text = text.indexOf(SPECIFY_END_FORMAT) + SPECIFY_END_FORMAT.length();
            items.add(
                    extractChoiceItem(text)
            );
            text = text.substring(text.indexOf(END_FORMAT) + END_FORMAT.length());
        }

        return items;
    }

    private static ChoiceItem extractChoiceItem(String text_part){
        int begin_text = text_part.indexOf(SPECIFY_END_FORMAT) + SPECIFY_END_FORMAT.length();
        NGSFormatObject ngsf_object = NGSFormatObject.parseNGSFormat(text_part.substring(0, begin_text));
        return new ChoiceItem(
                text_part.substring(begin_text, text_part.indexOf(END_FORMAT)),
                Integer.valueOf(ngsf_object.get("CHOICEID")),
                ngsf_object.get("GOTO")
        );
    }

    private static String removeChoiceNGSF(String text){
        NGSFormatObject object;
        StringBuilder builder = new StringBuilder();

        while(text.contains(END_FORMAT)){
            String one_part;

            object = NGSFormatObject.parseNGSFormat(text.substring(
                    text.indexOf(SPECIFY_BEGIN_FORMAT),
                    text.indexOf(SPECIFY_END_FORMAT) + SPECIFY_BEGIN_FORMAT.length()
            ));

            one_part = text.substring(
                    text.indexOf(SPECIFY_BEGIN_FORMAT),
                    text.indexOf(END_FORMAT) + END_FORMAT.length());

            if(object.get("TYPE").equals("CHOICE")){
                builder.append(NGSFUtility.getInside(one_part));
            }else{
                builder.append(one_part);
            }

            text = text.substring(one_part.length());
        }

        return builder.toString();
    }

    @Override
    public void keyHandler(GameController controller, KeyEvent event){
        switch(event.getCode()){
            case UP:
                selecting_id--;
                selecting_id = (selecting_id + choice_items.size()) % choice_items.size();
                break;
            case DOWN:
                selecting_id++;
                selecting_id = (selecting_id + choice_items.size()) % choice_items.size();
                break;
            case ENTER:
                selected_item_id = choice_items.get(selecting_id).getChoiceID();
                controller.defaultKeyAction(event);
                break;
        }
    }

}
