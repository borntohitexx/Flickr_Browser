package brianwan.com;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;


public class SearchActivity extends BaseActivity {

    private static final String TAG = "SearchActivity";
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        /*
         Search activity is an activity called by another activity
         Therefore we want to activate the home button on the toolbar
         */
        activateToolbar(true);
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: starts");
        // Takes the xml representation of all the widgets in menu and creates the view from them
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Provides access to system search services
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        // Get reference to the search view widget embedded into the search menu item of the toolbar
        // We added the search view using the action view class property so we use getActionView() to retrieve it
        mSearchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();

        //Get search manager to retrieve searchable info from searchable.xml by calling getSearchableInfo and passing in component name (i.e. SearchActivity)
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());

        // Set searchable info on search view to configure it
        mSearchView.setSearchableInfo(searchableInfo);
//        Log.d(TAG, "onCreateOptionsMenu: " + getComponentName().toString());
//        Log.d(TAG, "onCreateOptionsMenu: hint is " + mSearchView.getQueryHint());
//        Log.d(TAG, "onCreateOptionsMenu: searchable info is " + searchableInfo.toString());

        // Opens the search right away instead of having the widget as an icon
        mSearchView.setIconified(false);

        // Pass in anonymous class that implements OnQueryTextListener interface
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: called");
                // Create sharedPreferences object and pass in getApplicationContext, because the activity that retrieves that data may not be the same as the one that called it
                // In this case search activity will store the data and main activity will retrieve it
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                //Store the data with the key FLICKR_QUERY that was defined in BaseActivity
                sharedPreferences.edit().putString(FLICKR_QUERY, query).apply();

                /** Workaround for emulator. "Enter" on keyboard causes Search icon in main activity to
                receive a click event - causing search activity to come up again after searching.
                 **/
                mSearchView.clearFocus();
                finish(); // Closes the activity and returns to whichever that launched it (i.e. MainActivity)
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // When user closes search, bring them back to main activity
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return false;
            }
        });

        Log.d(TAG, "onCreateOptionsMenu: returned " + true);


        return true;
    }


}
