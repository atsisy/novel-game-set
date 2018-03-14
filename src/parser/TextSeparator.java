package parser;

import java.util.ArrayList;

public class TextSeparator {

    private static final String SEPARATE_MESSAGE = "###SEPARATE\n";

    public static ArrayList<String> separateTextByMark(String text){
        ArrayList<String> result = new ArrayList<>();

        while(text.contains(SEPARATE_MESSAGE)){
            /*
            * 切り分け命令がある点を探す
             */
            int separate_point = text.indexOf(SEPARATE_MESSAGE);

            /*
            * 切り分け記号以前の文章を格納
             */
            result.add(text.substring(0, separate_point));

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
        result.add(text);

        return result;
    }

}
