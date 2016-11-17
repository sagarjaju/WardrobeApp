package techhub.wardrobe.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

/**
 * This dialog provide option whether to add image from Gallery or Camera
 */
public class ImagePickerDialog extends AlertDialog.Builder {

    private ArrayAdapter<String> arrayAdapter; //Arrayadapter for displaying Gallery and Camera item on dialog

    /**
     * Constructor for ImagePickerDialog
     * @param context
     */
    public ImagePickerDialog(Context context) {
        super(context);
        arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Gallery");
        arrayAdapter.add("Camera");
    }

    /**
     * This function will setadapter for displaying Gallery and Camera item on dialog
     * @param adapter
     * @param listener
     * @return
     */
    @Override
    public AlertDialog.Builder setAdapter(ListAdapter adapter, DialogInterface.OnClickListener listener) {
        return super.setAdapter(this.arrayAdapter, listener);
    }
}