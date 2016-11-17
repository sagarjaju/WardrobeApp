package techhub.wardrobe.util;

import android.os.Environment;

import java.io.File;

/**
 * This interface contains constants used in application
 */
public interface WardrobeConstants {

    /**
     * Wardrobe Type
     */
    int SHIRT = 1;
    int PANT = 2;

    /**
     * APP Data Path
     */
    String FILE_PATH_APP_ROOT_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separatorChar + "Android" + File.separatorChar + "data" + File.separatorChar + "techhub.wardrobe";
    String FILE_PATH_WARDROBE_DIRECTORY = FILE_PATH_APP_ROOT_DIRECTORY + File.separatorChar + "Wardrobe";
}
