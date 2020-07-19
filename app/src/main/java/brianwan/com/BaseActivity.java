package brianwan.com;

import android.util.Log;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/*
Base Activity on which PhotoDetailActivity and SearchActivity extends
Helps to keep the reused methods and constants in a single spot to improve consistent and reduce redundancy.
 */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    // Send the photo details data to the PhotoDetailActivity
    static final String FLICKR_QUERY = "FLICKR_QUERY";
    static final String PHOTO_TRANSFER = "PHOTO_TRANSFER";

    //Method to determine whether we show the home button on the toolbar or not
    void activateToolbar(boolean enableHome) {
        Log.d(TAG, "activateToolbar: starts");
        ActionBar actionBar = getSupportActionBar(); // Get a reference to the activity's action bar
        if(actionBar == null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // If there is an action bar, inflate the toolbar

            if(toolbar != null) {
                setSupportActionBar(toolbar); //Put the toolbar in place at the top of the screen
                actionBar = getSupportActionBar(); // Get reference to the new action bar
            }
        }

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(enableHome); //Enable home button on toolbar depending on information passed into method
        }
    }


}
