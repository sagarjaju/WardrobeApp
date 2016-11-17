package techhub.wardrobe.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is model class for Wardrobe
 */
public class Wardrobe implements Parcelable {

    /**
     * Datamembers of Wardrobe
     */
    private String strWardrobeId,
            strWardrobeImagePath;

    /**
     * Constructor of Wardrobe class
     * @param strWardrobeId
     * @param strWardrobeImagePath
     */
    public Wardrobe(String strWardrobeId, String strWardrobeImagePath) {
        this.strWardrobeId = strWardrobeId;
        this.strWardrobeImagePath = strWardrobeImagePath;
    }

    /**
     * This function will return wardrobe id of shirt or pant
     * @return
     */
    public String getWardrobeId() {
        return strWardrobeId;
    }

    /**
     * This function will return image path of shirt or pant
     * @return
     */
    public String getWardrobeImagePath() {
        return strWardrobeImagePath;
    }

    /**
     * Retriving data from parcel object
     * @param in
     */
    protected Wardrobe(Parcel in) {
        strWardrobeId = in.readString();
        strWardrobeImagePath = in.readString();
    }

    /**
     * Writing data to parcel object
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strWardrobeId);
        dest.writeString(strWardrobeImagePath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Wardrobe> CREATOR = new Creator<Wardrobe>() {

        @Override
        public Wardrobe createFromParcel(Parcel in) {
            return new Wardrobe(in);
        }

        @Override
        public Wardrobe[] newArray(int size) {
            return new Wardrobe[size];
        }
    };
}