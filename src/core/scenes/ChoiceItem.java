package core.scenes;

public class ChoiceItem {

    private String text;
    private int next_scene_hash;
    private int local_choice_id;

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
