package techhub.wardrobe.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

/**
 * This dialog provide option whether to add Shirt or Pant
 */
public class AddImageDialog extends AlertDialog.Builder {

    private ArrayAdapter<String> arrayAdapter; //Arrayadapter for displaying Shirt and Pant item on dialog

    /**
     * Constructor for AddImageDialog
     * @param context
     */
    public AddImageDialog(Context context) {
        super(context);
        arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Add Shirt");
        arrayAdapter.add("Add Pant");
    }

    /**
     * This function will setadapter for displaying Shirt and Pant item on dialog
     * @param adapter
     * @param listener
     * @return
     */
    @Override
    public AlertDialog.Builder setAdapter(ListAdapter adapter, DialogInterface.OnClickListener listener) {
        return super.setAdapter(this.arrayAdapter, listener);
    }
}