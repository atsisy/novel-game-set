package core;

import javafx.geometry.Point2D;

public class SceneBasicInfo {

    /*
    * シーンのタイトル
     */
    private String title;

    /*
    * シーンのハッシュ名
    * 保持しておく必要は無いが、デバッグ用に残しておく
     */
    private String hash_name;

    /*
    * このシーンのハッシュコード
     */
    private int hash;

    /*
    * このシーンにおけるテキスト表示開始座標
     */
    private Point2D top_point;

    public SceneBasicInfo(String title, String hash_name, Point2D top_point){
        this.title = title;
        this.hash_name = hash_name;
        this.top_point = top_point;
        hash = this.hash_name.hashCode();
    }

    public String getTitle() {
        return title;
    }

    public int getHash() {
        return hash;
    }

    public Point2D getTopPoint() {
        return top_point;
    }
}
