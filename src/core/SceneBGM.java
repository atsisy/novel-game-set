package core;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.Optional;

public class SceneBGM {

    /*
    * BGMの属性を表す列挙体
     */
    public enum BGMStatus {
        NEW_BGM,
        KEEP_BGM,
        UNAVAILABLE;

        public static BGMStatus strToMe(String word){
            switch(word){
                case "KEEP":
                    return KEEP_BGM;
                case "NO":
                    return UNAVAILABLE;
            }
            return NEW_BGM;
        }
    }

    private Media sound;
    private BGMStatus bgmStatus;

    public SceneBGM(String audio_path){
        bgmStatus = BGMStatus.strToMe(audio_path);

        if(bgmStatus.equals(BGMStatus.NEW_BGM)){
            /*
            * 新しいBGMを設定する
             */
            sound = new Media(new File(audio_path).toURI().toString());
        }else{
            sound = null;
        }

    }

    public BGMStatus getBgmStatus(){
        return bgmStatus;
    }

    public Media getSound() {
        return sound;
    }
}
