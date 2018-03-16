package core;

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

    public SceneBasicInfo(String title, String hash_name){
        this.title = title;
        this.hash_name = hash_name;
        hash = this.hash_name.hashCode();
    }

    public String getTitle() {
        return title;
    }

    public int getHash() {
        return hash;
    }

}
