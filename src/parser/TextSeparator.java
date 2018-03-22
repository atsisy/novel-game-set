package parser;

import javafx.geometry.Point2D;
import parser.ngsf.NGSFUtility;
import parser.ngsf.NGSFormatObject;
import text.HighGradeTextInterpreter;
import text.HighGradeTextMaterial;

import java.util.ArrayList;

public class TextSeparator {

    private static final String SEPARATE_MESSAGE = "###SEPARATE\n";
    private static final String LATE_MESSAGE = "###LATER\n";

    public static ArrayList<HighGradeTextMaterial> separateTextByMark(String text){
        ArrayList<HighGradeTextMaterial> result = new ArrayList<>();

        while(text.contains(SEPARATE_MESSAGE)){
            /*
            * 切り分け命令がある点を探す
             */
            int separate_point = text.indexOf(SEPARATE_MESSAGE);

            String target_text = text.substring(0, separate_point);


            /*
            * 切り分け記号以前の文章を格納
             */
            result.addAll(parseLateMark(text.substring(0, separate_point)));

            /*
            * あまりの文章を再びtext変数に代入
             */
            text = text.substring(separate_point + SEPARATE_MESSAGE.length());
        }

        /*
        * どっちにしろ、textは最期に追加する必要がある
        * 含んでいない場合は、もちろん
        * 含んでいる場合、最期のtextを効率よく判定、追加するにはこのように最期を外に出すのが良い
         */
        result.addAll(parseLateMark(text));

        return result;
    }

    private static ArrayList<HighGradeTextMaterial> parseLateMark(String text){
        ArrayList<HighGradeTextMaterial> result = new ArrayList<>();
        boolean next_is_late = false;

        if(!text.contains(LATE_MESSAGE)){
            result.add(new HighGradeTextMaterial(text, true));
            return result;
        }

        while(text.contains(LATE_MESSAGE)){
            /*
             * 切り分け命令がある点を探す
             */
            int separate_point = text.indexOf(LATE_MESSAGE);

            /*
             * 切り分け記号以前の文章を格納
             */
            result.add(new HighGradeTextMaterial(text.substring(0, separate_point), !next_is_late));

            /*
             * あまりの文章を再びtext変数に代入
             */
            text = text.substring(separate_point + LATE_MESSAGE.length());
            next_is_late = true;
        }


        /*
         * どっちにしろ、textは最期に追加する必要がある
         * 含んでいない場合は、もちろん
         * 含んでいる場合、最期のtextを効率よく判定、追加するにはこのように最期を外に出すのが良い
         */
        result.add(new HighGradeTextMaterial(text, false));

        return result;
    }

}
