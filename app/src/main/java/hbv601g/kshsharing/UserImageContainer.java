package hbv601g.kshsharing;

import android.graphics.Bitmap;

/**
 * Created by Mogget on 18.2.2017.
 */

class UserImageContainer {
    private Long id;

    private Bitmap image;
    private String name;
    private String uuid;
    private String tags;
    private String ending;

    public UserImageContainer() {
    }

    public UserImageContainer(String name, String uuid, String tags, String ending) {
        setImage(image);
        setName(name);
        setUuid(uuid);
        setTags(tags);
        setEnding(ending);
    }

    public Bitmap getImage() { return this.image; }
    public void setImage(Bitmap image) { this.image = image; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getUuid() { return this.uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getTags() { return this.tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getEnding() { return this.ending; }
    public void setEnding(String ending) { this.ending = ending; }
}