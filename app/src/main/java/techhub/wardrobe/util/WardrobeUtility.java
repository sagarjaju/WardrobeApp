package techhub.wardrobe.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;

/**
 * This class contains common functions used in application
 */
public class WardrobeUtility {

    /**
     * This function will return screen width
     *
     * @param activity
     * @return
     */
    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point.x;
    }

    /**
     * This function will return screen height
     *
     * @param activity
     * @return
     */
    public static int getScreenHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point.y;
    }

    /**
     * This function will return bitmap from path
     *
     * @param path
     * @param intReqWidth
     * @param intReqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromUri(String path, int intReqWidth, int intReqHeight) {
        Bitmap bitmap = null;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, intReqWidth, intReqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, options);

        return bitmap;
    }

    /**
     * This function will return sample size according bitmap width and height
     *
     * @param options
     * @param intReqWidth
     * @param intReqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int intReqWidth, int intReqHeight) {
        // Raw height and width of image
        final int intHeight = options.outHeight;
        final int intWidth = options.outWidth;
        int intSampleSize = 1;

        if (intHeight > intReqHeight || intWidth > intReqWidth) {
            if (intWidth > intHeight) {
                intSampleSize = Math.round((float) intHeight / (float) intReqHeight);
            } else {
                intSampleSize = Math.round((float) intWidth / (float) intReqWidth);
            }
        }
        return intSampleSize;
    }
}
