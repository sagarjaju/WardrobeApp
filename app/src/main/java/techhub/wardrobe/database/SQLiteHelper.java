package techhub.wardrobe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import techhub.wardrobe.model.Wardrobe;
import techhub.wardrobe.util.WardrobeConstants;

/**
 * This class performs all database related functionality
 */
public class SQLiteHelper extends SQLiteOpenHelper implements WardrobeConstants {

    public static final String DATABASE_NAME = "WARDROBE_DATABASE"; //Database Name
    public static final int DATABASE_VERSION = 1; //Database Version

    private final String TABLE_WARDROBE = "tbl_wardrobe"; //Wardrobe table name
    private final String COLUMN_WARDROBE_ID = "col_wardrobe_id"; //Wardrobe Id Column name in Wardrobe table
    private final String COLUMN_WARDROBE_TYPE = "col_wardrobe_type"; //Wardrobe Type Column name in Wardrobe table
    private final String COLUMN_WARDROBE_IMAGE = "col_wardrobe_image_path"; //Wardrobe Image Path Column name in Wardrobe table

    private final String TABLE_FAVOURITE_LOOK = "tbl_favourite_look"; //Favourite Look Table name
    private final String COLUMN_LOOK_ID = "col_look_id"; //Look Id Column name in Favourite Look table
    private final String COLUMN_WARDROBE_IDS = "col_wardrobe_ids"; //Wardrobe Ids Column name in Favourite Look table

    /**
     * Construtor for SQLiteHelper class
     * @param context
     */
    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This method gets call when database is created
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_WARDROBE_TABLE = "CREATE TABLE " + TABLE_WARDROBE + "("
                + COLUMN_WARDROBE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_WARDROBE_TYPE + " INTEGER, "
                + COLUMN_WARDROBE_IMAGE + " TEXT"
                + ")";
        sqLiteDatabase.execSQL(CREATE_WARDROBE_TABLE);

        String CREATE_LOOK_TABLE = "CREATE TABLE " + TABLE_FAVOURITE_LOOK + "("
                + COLUMN_LOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_WARDROBE_IDS + " TEXT"
                + ")";
        sqLiteDatabase.execSQL(CREATE_LOOK_TABLE);
    }

    /**
     * This method gets call when database is upgraded
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    /**
     * This method add image to wardrobe for particular wardrobe type
     * @param intWardrobeType
     * @param strWardrobeImagePath
     */
    public void addToWardrobe(int intWardrobeType, String strWardrobeImagePath) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_WARDROBE_TYPE, intWardrobeType);
        values.put(COLUMN_WARDROBE_IMAGE, strWardrobeImagePath);

        // Inserting Row
        db.insert(TABLE_WARDROBE, null, values);
        db.close();
    }

    /**
     * This method return list of wardrobe for particular wardrobe type
     * @param intWardrobeType
     * @return
     */
    public ArrayList<Wardrobe> getWardrobe(int intWardrobeType) {
        ArrayList<Wardrobe> listWardrobe = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String strQuery = "SELECT * FROM " + TABLE_WARDROBE + " WHERE " + COLUMN_WARDROBE_TYPE + " = " + intWardrobeType;
        Cursor cursor = db.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Wardrobe wardrobe = new Wardrobe(cursor.getString(0), cursor.getString(2));
                listWardrobe.add(wardrobe);
            } while (cursor.moveToNext());
        }
        return listWardrobe;
    }

    /**
     * This method mark a look as Favourite
     * @param strShirtId
     * @param strPantId
     */
    public void markFavouriteLook(String strShirtId, String strPantId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_WARDROBE_IDS, strShirtId + "," + strPantId);

        // Inserting Row
        db.insert(TABLE_FAVOURITE_LOOK, null, values);
        db.close();
    }

    /**
     * This method mark a look as unfavourite
     * @param strShirtId
     * @param strPantId
     */
    public void markUnfavouriteLook(String strShirtId, String strPantId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVOURITE_LOOK, COLUMN_WARDROBE_IDS + " = ?", new String[]{strShirtId + "," + strPantId});
        db.close();
    }

    /**
     * This method checks whether displayed look is favourite or not
     * @param strShirtId
     * @param strPantId
     * @return
     */
    public boolean isFavouriteLook(String strShirtId, String strPantId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT * FROM " + TABLE_FAVOURITE_LOOK + " WHERE " + COLUMN_WARDROBE_IDS + " = \"" + strShirtId + "," + strPantId+"\"";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    /**
     * This method return last inserted record to wardrobe table
     * @return
     */
    public Wardrobe getLastRecord() {
        SQLiteDatabase db = this.getReadableDatabase();
        String strQuery = "SELECT * FROM " + TABLE_WARDROBE + " ORDER BY " + COLUMN_WARDROBE_ID + " DESC LIMIT 1;";
        Cursor cursor = db.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            Wardrobe wardrobe = new Wardrobe(cursor.getString(0), cursor.getString(2));
            return wardrobe;
        }
        return null;
    }
}