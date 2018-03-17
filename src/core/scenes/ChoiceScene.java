package core.scenes;

import core.FontData;
import core.SceneBasicInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class ChoiceScene extends ScenePart {

    private HashMap<Integer, Integer> next_scene_table;
    private int selected_item_id;

    public ChoiceScene(ArrayList<String> text_array_paths, String back_image_path, String back_display_mode, FontData fontData, SceneBasicInfo basicInfo, Optional<String> bgm_path){
        /*
         * スーパクラスを初期化
         * その後、シーンタイプを設定
         */
        super(text_array_paths, back_image_path, back_display_mode, fontData, basicInfo, bgm_path);

        /*
         * シーンタイプはチョイス(選択肢)
         */
        this.scene_type = SceneType.CHOICE;
    }

    @Override
    public int nextSceneHash(){
        return next_scene_table.get(selected_item_id);
    }

}
