package graphic;

import core.SceneBGM;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MusicPlayer {

    private static final int SMOOTH_AUDIO_MILLSEC = 1000;

    private MediaPlayer player;

    public MusicPlayer(){
        player = null;
    }

    public SceneBGM.BGMStatus play(SceneBGM music){

        Animation smooth_audio = new Transition() {

            private MediaPlayer local_player;

            {
                /*
                 * SMOOTH_AUDIO_MILLSECミリ秒で音量を最大にする
                 */
                setCycleDuration(Duration.millis(SMOOTH_AUDIO_MILLSEC));

                /*
                 * 一回のみで十分
                 */
                setCycleCount(1);

                local_player = new MediaPlayer(music.getSound());
                player = local_player;
            }

            @Override
            protected void interpolate(double frac) {
                /*
                 * 音量を段階的に設定
                 */
                local_player.setVolume(frac);
            }
        };

        player.play();
        smooth_audio.play();

        return music.getBgmStatus();
    }

    public void stop(){

        if(player == null)
            return;

        Animation smooth_audio = new Transition() {

            private MediaPlayer local_player;

            {
                /*
                 * SMOOTH_AUDIO_MILLSECミリ秒で音量を最大にする
                 */
                setCycleDuration(Duration.millis(SMOOTH_AUDIO_MILLSEC));

                /*
                 * 一回のみで十分
                 */
                setCycleCount(1);

                local_player = player;
            }

            @Override
            protected void interpolate(double frac) {
                /*
                 * 音量を段階的に設定
                 */
                local_player.setVolume(1.0 - frac);
            }



        };

        smooth_audio.setOnFinished(event -> {
            player.stop();
            player = null;
        });
        smooth_audio.play();

    }

}
