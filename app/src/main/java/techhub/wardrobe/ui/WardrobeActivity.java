package techhub.wardrobe.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import techhub.wardrobe.R;
import techhub.wardrobe.adapter.WardrobePagerAdapter;
import techhub.wardrobe.database.SQLiteHelper;
import techhub.wardrobe.dialog.AddImageDialog;
import techhub.wardrobe.dialog.ImagePickerDialog;
import techhub.wardrobe.model.Wardrobe;
import techhub.wardrobe.util.WardrobeConstants;
import techhub.wardrobe.util.WardrobeUtility;

/**
 * This class display looks
 */
public class WardrobeActivity extends AppCompatActivity implements WardrobeConstants, View.OnClickListener {

    private final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 100; //REQUEST CODE FOR PERMISSION
    private final int REQUEST_GALLERY_IMAGE = 200; //REQUEST CODE FOR GALLERY
    private final int REQUEST_CAMERA_CAPTURE = 300; //REQUEST CODE FOR CAMERA

    private Toolbar toolbar; //Toolbar
    private ViewPager viewpagerShirt, //ViewPager for displayig shirts
            viewpagerPant; //ViewPager for displaying pants
    private TextView txtviewAddShirt, //TextView for displaying "Add Shirt" message
            txtviewAddPant; //TextView for displaying "Add Pant" message
    private ProgressDialog progressDialog; //ProgressDialog
    private MenuItem menu_shuffle; //Menu Shuffle

    private WardrobePagerAdapter wardrobePagerAdapterShirt, //ViewPager Adapter for Shirt
            wardrobePagerAdapterPant; //ViewPager Adapter for Pant

    private SQLiteHelper sqLiteHelper; //SQLiteHelper Object

    private ArrayList<Wardrobe> listShirts, //Shirt Data
            listPants; //Pant Data

    private boolean isFavouriteLook = false; //Flag is Favourite Look

    private int SELECTED_WARDROBE; //SELECTED WARDROBE ID

    private String strCapturedImagePath, //Path for captured image from Camera
            strSelectedShirtId, //Selected image shirt ID
            strSelectedPantId; //Selected image Pant ID

    private int intSelectedPagerShirt = 0, //Select Shirt page position
            intSelectedPagerPant = 0; //Select Pager page position

    /**
     * This method gets called when activity is created
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);

        initComponents(savedInstanceState);
    }

    /**
     * Initialises view components used in activity
     *
     * @param savedInstanceState
     */
    private void initComponents(Bundle savedInstanceState) {

        sqLiteHelper = new SQLiteHelper(this); //Initialise SQLiteHelper object

        toolbar = (Toolbar) findViewById(R.id.toolbar); //Initialise Toolbar
        setSupportActionBar(toolbar); //Sets toolbar

        viewpagerShirt = (ViewPager) findViewById(R.id.viewpagerShirt); //Initialise Shirt View Pager
        viewpagerShirt.setVisibility(View.GONE);
        //Adding Page CHange Listener for shirt viewpager
        viewpagerShirt.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                strSelectedShirtId = listShirts.get(position).getWardrobeId();
                intSelectedPagerShirt = position;
                checkIsFavouriteLook();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewpagerPant = (ViewPager) findViewById(R.id.viewpagerPant); //Initialise Pant ViewPager
        viewpagerPant.setVisibility(View.GONE);
        //Adding Page Change Listener for pant viewpager
        viewpagerPant.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                strSelectedPantId = listPants.get(position).getWardrobeId();
                intSelectedPagerPant = position;
                checkIsFavouriteLook();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        txtviewAddShirt = (TextView) findViewById(R.id.textviewAddShirt); //Initialise Add Shirt textview
        txtviewAddShirt.setVisibility(View.GONE);
        txtviewAddShirt.setOnClickListener(this);

        txtviewAddPant = (TextView) findViewById(R.id.textviewAddPant); //Initialise Add Pant textview
        txtviewAddPant.setVisibility(View.GONE);
        txtviewAddPant.setOnClickListener(this);

        //Checks whether activity is started after configuration change / orientation change
        if (savedInstanceState == null) {
            new FetchWardrobeDataTask().execute();
        } else {
            //Gets data from bundle and set it to respective component
            listShirts = savedInstanceState.getParcelableArrayList("shirtlist");
            strSelectedShirtId = savedInstanceState.getString("selectedShirtId");
            intSelectedPagerShirt = savedInstanceState.getInt("selectedShirtPager");
            listPants = savedInstanceState.getParcelableArrayList("pantlist");
            strSelectedPantId = savedInstanceState.getString("selectedPantId");
            intSelectedPagerPant = savedInstanceState.getInt("selectedPantPager");
            isFavouriteLook = savedInstanceState.getBoolean("isFavouriteLook");

            //Checks whether shirt data is available or not
            if (listShirts.size() == 0) {
                txtviewAddShirt.setVisibility(View.VISIBLE);
                viewpagerShirt.setVisibility(View.GONE);
            } else {
                txtviewAddShirt.setVisibility(View.GONE);
                viewpagerShirt.setVisibility(View.VISIBLE);
                wardrobePagerAdapterShirt = new WardrobePagerAdapter(getApplicationContext(), listShirts);
                viewpagerShirt.setAdapter(wardrobePagerAdapterShirt);
                viewpagerShirt.setCurrentItem(intSelectedPagerShirt);
            }

            //Checks whether pant data is available or not
            if (listPants.size() == 0) {
                txtviewAddPant.setVisibility(View.VISIBLE);
                viewpagerPant.setVisibility(View.GONE);
            } else {
                txtviewAddPant.setVisibility(View.GONE);
                viewpagerPant.setVisibility(View.VISIBLE);
                wardrobePagerAdapterPant = new WardrobePagerAdapter(getApplicationContext(), listPants);
                viewpagerPant.setAdapter(wardrobePagerAdapterPant);
                viewpagerPant.setCurrentItem(intSelectedPagerPant);
            }
        }
    }

    class FetchWardrobeDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(WardrobeActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            listShirts = sqLiteHelper.getWardrobe(SHIRT);
            listPants = sqLiteHelper.getWardrobe(PANT);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            //Check whether shirt data is available or not
            if (listShirts.size() == 0) {
                txtviewAddShirt.setVisibility(View.VISIBLE);
                viewpagerShirt.setVisibility(View.GONE);
            } else {
                txtviewAddShirt.setVisibility(View.GONE);
                viewpagerShirt.setVisibility(View.VISIBLE);
                wardrobePagerAdapterShirt = new WardrobePagerAdapter(getApplicationContext(), listShirts);
                viewpagerShirt.setAdapter(wardrobePagerAdapterShirt);
                strSelectedShirtId = listShirts.get(0).getWardrobeId();
            }

            //Check whether pant data is available or not
            if (listPants.size() == 0) {
                txtviewAddPant.setVisibility(View.VISIBLE);
                viewpagerPant.setVisibility(View.GONE);
            } else {
                txtviewAddPant.setVisibility(View.GONE);
                viewpagerPant.setVisibility(View.VISIBLE);
                wardrobePagerAdapterPant = new WardrobePagerAdapter(getApplicationContext(), listPants);
                viewpagerPant.setAdapter(wardrobePagerAdapterPant);
                strSelectedPantId = listPants.get(0).getWardrobeId();
            }

            //Check whether activity is started from notification or not
            if (getIntent().hasExtra("fromNotification")) {
                shuffleWardrobe();
            }
        }
    }

    /**
     * Checks whether displayed look is favourite or not
     */
    private void checkIsFavouriteLook() {
        if (!(strSelectedShirtId == null || strSelectedPantId == null)) {
            isFavouriteLook = sqLiteHelper.isFavouriteLook(strSelectedShirtId, strSelectedPantId);
            updateFavouriteIcon(isFavouriteLook);
        }
    }

    /**
     * Gets call when destroying activity while changing orientation / configuration change
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("shirtlist", listShirts);
        outState.putString("selectedShirtId", strSelectedShirtId);
        outState.putInt("selectedShirtPager", intSelectedPagerShirt);
        outState.putParcelableArrayList("pantlist", listPants);
        outState.putString("selectedPantId", strSelectedPantId);
        outState.putInt("selectedPantPager", intSelectedPagerPant);
        outState.putBoolean("isFavouriteLook", isFavouriteLook);
        super.onSaveInstanceState(outState);
    }

    /**
     * Gets called when menu is created
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_wardrobe, menu);
        menu_shuffle = menu.findItem(R.id.action_favourite);
        checkIsFavouriteLook();
        return true;
    }

    /**
     * Gets called when menu item is selected for menu
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                showAddImageDialog();
                return true;
            case R.id.action_shuffle:
                //Checks whether both shirt and pant are selected
                if (listShirts.size() == 0 || listPants.size() == 0) {
                    Toast.makeText(this, getString(R.string.error_selectoneimage), Toast.LENGTH_SHORT).show();
                } else {
                    shuffleWardrobe();
                }
                return true;
            case R.id.action_favourite:
                //Checks whether both shirt and pant are selected
                if (listShirts.size() == 0 || listPants.size() == 0) {
                    Toast.makeText(this, getString(R.string.error_selectoneimage), Toast.LENGTH_SHORT).show();
                } else {

                    if (isFavouriteLook) {
                        sqLiteHelper.markUnfavouriteLook(strSelectedShirtId, strSelectedPantId);
                        isFavouriteLook = false;
                    } else {
                        sqLiteHelper.markFavouriteLook(strSelectedShirtId, strSelectedPantId);
                        isFavouriteLook = true;
                    }
                    updateFavouriteIcon(isFavouriteLook);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method will display a random look for existing wardrobe
     */
    private void shuffleWardrobe() {

        Random random = new Random();

        int lowOffset = 0;
        int highOffset = listShirts.size();
        int intShirtPosition = random.nextInt(highOffset - lowOffset);

        highOffset = listPants.size();
        int intPantPosition = random.nextInt(highOffset - lowOffset);

        viewpagerShirt.setCurrentItem(intShirtPosition);
        viewpagerPant.setCurrentItem(intPantPosition);
    }

    /**
     * Gets called view is clicked (Here TextViewAddShirt and TextViewAddPant)
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textviewAddShirt) {
            SELECTED_WARDROBE = SHIRT;
            if (Build.VERSION.SDK_INT >= 23) {
                checkForAppPermissions();
            } else {
                showImagePickerDialog();
            }
        } else if (view.getId() == R.id.textviewAddPant) {
            SELECTED_WARDROBE = PANT;
            if (Build.VERSION.SDK_INT >= 23) {
                checkForAppPermissions();
            } else {
                showImagePickerDialog();
            }
        }
    }

    /**
     * Update favourite icon depending upon favourite status
     *
     * @param status
     */
    private void updateFavouriteIcon(boolean status) {
        if (menu_shuffle != null) {
            if (status) {
                menu_shuffle.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_favourite, null));
            } else {
                menu_shuffle.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_unfavourite, null));
            }
        }
    }

    /**
     * Show AddImageDialog
     */
    private void showAddImageDialog() {
        AddImageDialog addImageDialog = new AddImageDialog(this);

        addImageDialog.setAdapter(null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    dialogInterface.dismiss();
                    SELECTED_WARDROBE = SHIRT;
                    if (Build.VERSION.SDK_INT >= 23) {
                        checkForAppPermissions();
                    } else {
                        showImagePickerDialog();
                    }
                } else if (i == 1) {
                    dialogInterface.dismiss();
                    SELECTED_WARDROBE = PANT;
                    if (Build.VERSION.SDK_INT >= 23) {
                        checkForAppPermissions();
                    } else {
                        showImagePickerDialog();
                    }
                }
            }
        });
        addImageDialog.show();
    }

    /**
     * Show ImagePickerDialog
     */
    private void showImagePickerDialog() {
        ImagePickerDialog imagePickerDialog = new ImagePickerDialog(this);
        imagePickerDialog.setAdapter(null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i == 0) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_GALLERY_IMAGE);
                } else if (i == 1) {

                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (photoFile != null) {
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        }

                        startActivityForResult(cameraIntent, REQUEST_CAMERA_CAPTURE);

                    } else {
                        Toast.makeText(WardrobeActivity.this, getString(R.string.error_camera), Toast.LENGTH_SHORT);
                    }

                }

            }
        });
        imagePickerDialog.show();
    }

    /**
     * Create .jpg file
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String imageFileName = String.valueOf(System.currentTimeMillis());
        File storageDir = new File(FILE_PATH_WARDROBE_DIRECTORY + File.separatorChar + imageFileName + ".jpg");
        strCapturedImagePath = storageDir.getAbsolutePath();
        return storageDir;
    }

    /**
     * Gets called when result is return for Galley and Camera application
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            String strTempPath = copyImagesToLocalDirectory(getRealPathFromURI(uri));
            if (strTempPath != null) {
                sqLiteHelper.addToWardrobe(SELECTED_WARDROBE, Uri.fromFile(new File(strTempPath)).toString());
                updateViewPager();
            } else {
                Toast.makeText(this, getString(R.string.error_adding_wardrobe), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_CAMERA_CAPTURE && resultCode == RESULT_OK) {
            //Add image to database and viwe pager
            if (strCapturedImagePath != null) {
                sqLiteHelper.addToWardrobe(SELECTED_WARDROBE, Uri.fromFile(new File(strCapturedImagePath)).toString());
                updateViewPager();
            } else {
                Toast.makeText(this, getString(R.string.error_adding_wardrobe), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Updates shirt and pant view pager with new item / image
     */
    private void updateViewPager() {
        Wardrobe wardrobe = sqLiteHelper.getLastRecord();
        if (wardrobe != null) {
            if (SELECTED_WARDROBE == SHIRT) {
                listShirts.add(wardrobe);
                //Checks whether viewpagerShirt has set adapter
                if (viewpagerShirt.getAdapter() == null) {
                    wardrobePagerAdapterShirt = new WardrobePagerAdapter(this, listShirts);
                    strSelectedShirtId = listShirts.get(0).getWardrobeId();
                    txtviewAddShirt.setVisibility(View.GONE);
                    viewpagerShirt.setVisibility(View.VISIBLE);
                    viewpagerShirt.setAdapter(wardrobePagerAdapterShirt);
                }
                wardrobePagerAdapterShirt.notifyDataSetChanged();
                viewpagerShirt.setCurrentItem(wardrobePagerAdapterShirt.getCount() - 1, true);
            } else {
                listPants.add(wardrobe);
                //Checks whether viewpagerPant has set adapter
                if (viewpagerPant.getAdapter() == null) {
                    wardrobePagerAdapterPant = new WardrobePagerAdapter(this, listPants);
                    strSelectedPantId = listPants.get(0).getWardrobeId();
                    txtviewAddPant.setVisibility(View.GONE);
                    viewpagerPant.setVisibility(View.VISIBLE);
                    viewpagerPant.setAdapter(wardrobePagerAdapterPant);
                }
                wardrobePagerAdapterPant.notifyDataSetChanged();
                viewpagerPant.setCurrentItem(wardrobePagerAdapterPant.getCount() - 1, true);
            }
        } else {
            Toast.makeText(this, getString(R.string.error_adding_wardrobe), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method returns real path of file
     *
     * @param contentUri
     * @return
     */
    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        return cursor.getString(column_index);
    }

    /**
     * This method copy image from gallery to app local directory
     *
     * @param imageFilePath
     * @return
     */
    private String copyImagesToLocalDirectory(String imageFilePath) {

        File f = new File(imageFilePath);
        try {
            String fileName = f.getName();
            String tempFileName = FILE_PATH_WARDROBE_DIRECTORY + File.separatorChar + System.currentTimeMillis() + "." + fileName.substring(fileName.lastIndexOf(".") + 1);

            copyFile(imageFilePath, tempFileName);

            ExifInterface exifInterface = new ExifInterface(tempFileName);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            /**
             * Checks orientation of image and depending upon the result it rotates image
             */

            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                //Rotate by 180
                rotateImage(tempFileName, 180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                //Rotate by 90
                rotateImage(tempFileName, 90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                //Rotate by 270
                rotateImage(tempFileName, 270);
            }

            return tempFileName;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This function copies file from source to destination
     *
     * @param str_SelectedImagePath
     * @param str_DestinationPath
     * @throws IOException
     */
    public void copyFile(String str_SelectedImagePath, String str_DestinationPath) throws IOException {
        InputStream in = new FileInputStream(str_SelectedImagePath);
        OutputStream out = new FileOutputStream(str_DestinationPath);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    /**
     * This method will rotation image according to rotation angle
     *
     * @param imagePath
     * @param rotationAngle
     */
    void rotateImage(String imagePath, float rotationAngle) {
        Bitmap bv = WardrobeUtility.decodeSampledBitmapFromUri(imagePath, WardrobeUtility.getScreenWidth(this), WardrobeUtility.getScreenHeight(this));

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);
        Bitmap processedBitmap = Bitmap.createBitmap(bv, 0, 0, bv.getWidth(), bv.getHeight(), matrix, true);


        FileOutputStream out = null;
        try {

            out = new FileOutputStream(imagePath);

            processedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            ExifInterface exifInterface = new ExifInterface(imagePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            if (processedBitmap.isRecycled())
                processedBitmap.recycle();
            processedBitmap = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method ask for permission for devices having OS Marshmallow and Nougat
     */
    private void checkForAppPermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write Storage");
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    }
                });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        } else {
            showImagePickerDialog();
        }
    }

    /**
     * @param permissionsList
     * @param permission
     * @return
     */
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    /**
     * This method show dialog for granting permission
     *
     * @param message
     * @param okListener
     */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /**
     * This method gets called when permission is granted or declined
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    showImagePickerDialog();
                } else {
                    // Permission Denied
                    Toast.makeText(WardrobeActivity.this, getString(R.string.error_permission), Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}