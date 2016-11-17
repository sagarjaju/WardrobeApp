package techhub.wardrobe.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;

import techhub.wardrobe.R;
import techhub.wardrobe.database.SQLiteHelper;
import techhub.wardrobe.receivers.AlarmReceiver;
import techhub.wardrobe.util.WardrobeConstants;

/**
 * This class displays Splashscreen
 */
public class SplashscreenActivity extends AppCompatActivity implements WardrobeConstants {

    private static int SPLASHSCREEN_TIME = 3000; //Splashscreen wait time

    /**
     * This method gets called when activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        /**
         * Handler for waiting splashscreen
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                createDirectoryStructure();
                createDatabase();
                setAlarm();

                Intent intent = new Intent(SplashscreenActivity.this, WardrobeActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASHSCREEN_TIME);
    }

    /**
     * This method will create database
     */
    private void createDatabase() {
        SQLiteHelper sqLiteHelper = new SQLiteHelper(this);
        sqLiteHelper.getWritableDatabase();
    }

    /**
     * This method will create directory structure for storing images
     */
    private void createDirectoryStructure() {
        File file = new File(FILE_PATH_APP_ROOT_DIRECTORY);
        if(!file.exists())
        {
            file.mkdir();
        }

        file = new File(FILE_PATH_WARDROBE_DIRECTORY);
        if(!file.exists())
        {
            file.mkdir();
        }
    }

    /**
     * This method will set alarm @ 6:00 AM
     */
    private void setAlarm() {
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.setAlarm(this);
    }
}