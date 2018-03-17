package parser.ngsf;

import parser.HighGradeTextInterpreter;

public class NGSFUtility {

    public static String getInside(String format){
        return format.substring(
                format.indexOf(HighGradeTextInterpreter.SPECIFY_END_FORMAT) + HighGradeTextInterpreter.SPECIFY_END_FORMAT.length(),
                format.indexOf(HighGradeTextInterpreter.END_FORMAT)
        );
    }

}
