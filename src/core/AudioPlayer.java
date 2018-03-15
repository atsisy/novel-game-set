package core;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.Optional;

public class AudioPlayer {

    private static final int SMOOTH_AUDIO_MILLSEC = 1000;

    private Media sound;
    private MediaPlayer player;
    private boolean available;

    public AudioPlayer(Optional<String> audio_path){

        /*
        * audio_pathが有効な値を持っているときだけ、playerを初期化
        * 無効な場合、available変数にその情報を格納。playerにはnullを代入
         */
        if(audio_path.isPresent()){
            sound = new Media(new File(audio_path.get()).toURI().toString());
            player = new MediaPlayer(sound);
            available = true;
        }else{
            sound = null;
            player = null;
            available = false;
        }

    }

    public boolean play(){

        Animation smooth_audio = new Transition() {

            {
                /*
                * SMOOTH_AUDIO_MILLSECミリ秒で音量を最大にする
                 */
                setCycleDuration(Duration.millis(SMOOTH_AUDIO_MILLSEC));

                /*
                * 一回のみで十分
                 */
                setCycleCount(1);
            }

            @Override
            protected void interpolate(double frac) {
                /*
                * 音量を段階的に設定
                 */
                player.setVolume(frac);
            }
        };

        if(available){
            player.play();
            smooth_audio.play();
        }

        return available;
    }

    public boolean stop(){

        Animation smooth_audio = new Transition() {

            {
                /*
                 * SMOOTH_AUDIO_MILLSECミリ秒で音量を最大にする
                 */
                setCycleDuration(Duration.millis(SMOOTH_AUDIO_MILLSEC));

                /*
                 * 一回のみで十分
                 */
                setCycleCount(1);
            }

            @Override
            protected void interpolate(double frac) {
                /*
                 * 音量を段階的に設定
                 */
                player.setVolume(1.0 - frac);
            }

        };

        smooth_audio.setOnFinished(event -> player.stop());

        smooth_audio.play();

        return available;
    }

}
