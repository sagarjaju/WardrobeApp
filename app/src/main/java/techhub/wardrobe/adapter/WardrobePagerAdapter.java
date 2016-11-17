package techhub.wardrobe.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

import techhub.wardrobe.R;
import techhub.wardrobe.model.Wardrobe;

/**
 * This class acts as adapter for view pager for populating data in view pager
 */
public class WardrobePagerAdapter extends PagerAdapter {

    private ArrayList<Wardrobe> listWardrobe; //Data to be populate in viewpager
    private LayoutInflater inflater; //Used for inflating a view
    private ImageLoader imageLoader; //Image loader from Universal Image Loader Library
    private DisplayImageOptions displayImageOptions; //DisplayImageOption from Universal Image Loader Library

    /**
     * Constructor for WardrobePagerAdpater
     * @param context
     * @param listWardrobe
     */
    public WardrobePagerAdapter(Context context, ArrayList listWardrobe) {
        this.listWardrobe = listWardrobe; //Initialise data to be populate in viewpager
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //Initialise LayoutInflater object
        ImageLoaderConfiguration imageLoaderConfiguration = ImageLoaderConfiguration.createDefault(context); //Create image loader configuration
        imageLoader = ImageLoader.getInstance(); //Initialise ImageLoader object
        imageLoader.init(imageLoaderConfiguration);
        displayImageOptions = new DisplayImageOptions.Builder() //Initialise DisplayImageOptions object
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
    }

    /**
     * This method will return no. of items displayed in viewpager
     * @return
     */
    @Override
    public int getCount() {
        return listWardrobe.size();
    }

    /**
     * This method check whether view is associated with the object return by @instantiateItem
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    /**
     * This method will create view for page on particular position
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = inflater.inflate(R.layout.item_wardrobe, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageviewWardrobe);

        //Displaying image in imageview
        imageLoader.displayImage(listWardrobe.get(position).getWardrobeImagePath(), imageView, displayImageOptions);
        container.addView(itemView);

        return itemView;
    }

    /**
     * This method will remove page from container for particular position
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}