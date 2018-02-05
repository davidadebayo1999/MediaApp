package app.kbuild.com.mediaapp;

/**
 * Created by USER on 05-Feb-18.
 */

public class Insta {

    private String title;
    private String desc;
    private String image;

    public Insta(String title, String desc, String image) {
        this.title = title;
        this.desc = desc;
        this.image = image;
    }

    public Insta() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }






}
