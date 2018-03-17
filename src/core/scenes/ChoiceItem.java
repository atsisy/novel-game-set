package core.scenes;

public class ChoiceItem {

    /*
    * 選択肢本文
     */
    private String text;

    /*
    * この選択肢を選択したときにジャンプするシーンのハッシュ値
     */
    private int next_scene_hash;

    /*
    * この選択肢のID
    * この値はローカルなもので、同一シーンの選択肢以外ならば重複しても良い
     */
    private int local_choice_id;

    /**
     * コンストラクタ
     * @param text 本文
     * @param choice_id ID
     * @param next_scene 次のシーンへのハッシュ値
     */
    public ChoiceItem(String text, int choice_id, String next_scene){
        this.text = text;
        this.next_scene_hash = next_scene.hashCode();
        this.local_choice_id = choice_id;
    }

    public String getText() {
        return text;
    }

    public int getNextSceneHash() {
        return next_scene_hash;
    }

    public int getChoiceID(){
        return local_choice_id;
    }
}
