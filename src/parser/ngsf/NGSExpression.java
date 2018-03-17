package parser.ngsf;

public class NGSExpression {

    private String left_hand_side;
    private String right_hand_side;

    public NGSExpression(String left, String right){
        left_hand_side = left;
        right_hand_side = right;
    }

    public String getLeft() {
        return left_hand_side;
    }

    public String getRight() {
        return right_hand_side;
    }

    public boolean isLeftValue(String val){
        return left_hand_side.equals(val);
    }

    public boolean isRightValue(String val){
        return right_hand_side.equals(val);
    }

    @Override
    public int hashCode(){
        StringBuilder builder = new StringBuilder();
        return builder.append(left_hand_side).append(right_hand_side).toString().hashCode();
    }
}
