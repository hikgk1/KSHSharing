package hbv601g.kshsharing;

import android.graphics.Bitmap;

/**
 * Created by Kristófer Guðni Kolbeins
 */

class UserImageContainer {
    private Bitmap image;
    private String name;
    private String uuid;
    private String tags;
    private String ending;

    UserImageContainer() {
    }

    public UserImageContainer(String name, String uuid, String tags, String ending) {
        setName(name);
        setUuid(uuid);
        setTags(tags);
        setEnding(ending);
    }

    Bitmap getImage() { return this.image; }
    void setImage(Bitmap image) { this.image = image; }

    String getName() { return this.name; }
    void setName(String name) { this.name = name; }

    String getUuid() { return this.uuid; }
    void setUuid(String uuid) { this.uuid = uuid; }

    String getTags() { return this.tags; }
    void setTags(String tags) { this.tags = tags; }

    String getEnding() { return this.ending; }
    void setEnding(String ending) { this.ending = ending; }
}