package parser.ngsf;

import text.HighGradeTextInterpreter;

public class NGSFUtility {

    /**
     * getInsideメソッド
     * [|.*+|]ooooooo[|END|]という文字列を受け取り、oooooooを返す
     * @param format 複合された文字列
     * @return 本文だけを取り出した文字列
     */
    public static String getInside(String format){
        return format.substring(
                format.indexOf(HighGradeTextInterpreter.SPECIFY_END_FORMAT) + HighGradeTextInterpreter.SPECIFY_END_FORMAT.length(),
                format.indexOf(HighGradeTextInterpreter.END_FORMAT)
        );
    }

    public static NGSFormatObject parseInlineNGSF(String from_beginning_line){
        return NGSFormatObject.parseNGSFormat(
                from_beginning_line.substring(
                        from_beginning_line.indexOf(HighGradeTextInterpreter.SPECIFY_BEGIN_FORMAT),
                        from_beginning_line.indexOf(HighGradeTextInterpreter.SPECIFY_END_FORMAT) + HighGradeTextInterpreter.SPECIFY_END_FORMAT.length()
                )
        );
    }
}
