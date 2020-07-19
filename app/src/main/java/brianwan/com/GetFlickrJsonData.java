package brianwan.com;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;


class GetFlickrJsonData extends AsyncTask<String, Void, List<Photo>> implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GetFlickrJsonData";

    private List<Photo> mPhotoList = null; //List of Photo objects
    private String mBaseURL; // Raw link to retrieve the JSON data
    private String mLanguage; // Store link parameter for language
    private boolean mMatchAll; // Flag to search items that match all tags or any one tag

    private final OnDataAvailable mCallBack;
    private boolean runningOnSameThread = false; // Determines whether GetFlickrJsonData and GetRawData are running in same thread

    interface OnDataAvailable {
        void onDataAvailable(List<Photo> data, DownloadStatus status);
    }

    public GetFlickrJsonData(OnDataAvailable callBack, boolean matchAll, String language, String baseURL) {
        Log.d(TAG, "GetFlickrJsonData: called");
        mCallBack = callBack;
        mMatchAll = matchAll;
        mLanguage = language;
        mBaseURL = baseURL;
    }

    void executeOnSameThread(String searchCriteria) {
        Log.d(TAG, "executeOnSameThread: starts ");
        runningOnSameThread = true;
        String destinationUri = createUri(searchCriteria, mLanguage, mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUri);
        Log.d(TAG, "executeOnSameThread: ends");
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: starts");
        if(mCallBack != null) {
            mCallBack.onDataAvailable(mPhotoList, DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected List<Photo> doInBackground(String... params) { // For doing the URL building and JSON parsing in a separate thread as well
        Log.d(TAG, "doInBackground: starts");
        String destinationUri = createUri(params[0], mLanguage, mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSamethread(destinationUri); // Prevents running a background thread inside of another. Makes sure data is downloaded first.
        Log.d(TAG, "doInBackground: ends");
        return mPhotoList;
    }

    private String createUri(String searchCriteria, String lang, boolean matchAll) {
        Log.d(TAG, "createUri: Starts");

//        Uri uri = Uri.parse(mBaseURL);
//        Uri.Builder builder = uri.buildUpon();
//        builder = builder.appendQueryParameter("tags", searchCriteria);
//        builder = builder.appendQueryParameter("tagmode", matchAll ? "ALL" : "ANY");
//        builder = builder.appendQueryParameter("lang", lang);
//        builder = builder.appendQueryParameter("format", "json");
//        builder = builder.appendQueryParameter("nojsoncallback", "1");
//        uri = builder.build();

        return Uri.parse(mBaseURL).buildUpon() // Construct the URL with the appropriate tags
                .appendQueryParameter("tags", searchCriteria)
                .appendQueryParameter("tagmode", matchAll ? "ALL" : "ANY")
                .appendQueryParameter("lang", lang)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build().toString();
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "onDownloadComplete: starts. Status = " + status );
        if(status == DownloadStatus.OK) {
            mPhotoList = new ArrayList<>();
            try {
                JSONObject jsonData = new JSONObject(data); // Store the downloaded data
                JSONArray itemsArray = jsonData.getJSONArray("items"); //Retrieve an array from JSON called items where each item stores info about a photo

                for(int i=0; i<itemsArray.length(); i++) {
                    JSONObject jsonPhoto = itemsArray.getJSONObject(i); //Extract each item in the array
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorId = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");

                    // To get the image url because it is nested inside an array itself called media
                    JSONObject jsonMedia = jsonPhoto.getJSONObject("media");
                    String photoUrl = jsonMedia.getString("m");

                    // Get the link to a bigger size of the image _m (small 240) versus _b (large 1024)
                    // For when user taps on the smaller image
                    String link = photoUrl.replaceFirst("_m.", "_b.");

                    Photo photoObject = new Photo(title, author, authorId, link, tags, photoUrl);
                    mPhotoList.add(photoObject);

                    Log.d(TAG, "onDownloadComplete: " + photoObject.toString());
                }
            } catch (JSONException jsone) {
                jsone.printStackTrace();
                Log.e(TAG, "onDownloadComplete: Error processing Json data" + jsone.getMessage()); // Get information that parser sends back to us
                status = DownloadStatus.FAILED_OR_EMPTY; //When exception is raised, can't guarantee data validity or if there is any data at all
            }
        }

        if(runningOnSameThread && mCallBack != null) { //Prevents callback to onDataAvailable in two places (onDownloadComplete and onPostExecute)
            // now inform the caller that processing is done - possibly returning null if there was an error
            mCallBack.onDataAvailable(mPhotoList, status);
        }

        Log.d(TAG, "onDownloadComplete: ends");
    }
}
