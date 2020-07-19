package brianwan.com;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
Statuses Meaning:
IDLE - Not downloading anything at this time
PROCESSING - Downloading the data
NOT_INITIALIZED - No valid URL to download
FAILED_OR_EMPTY - Failed to download or data came back empty
OK - Download successful and there is valid data
 */
enum DownloadStatus { IDLE, PROCESSING, NOT_INITIALIZED, FAILED_OR_EMPTY, OK}

class GetRawData extends AsyncTask<String, Void, String> {
    private static final String TAG = "GetRawData";

    private DownloadStatus mDownloadStatus;
    private final OnDownloadComplete mCallback;

    //Interface to make sure that the object it's been given does have the onDownloadComplete method for callback
    interface OnDownloadComplete {
        void onDownloadComplete(String data, DownloadStatus status);
    }

    public GetRawData(OnDownloadComplete callback) { //callback contains the MainActivity object
        this.mDownloadStatus = DownloadStatus.IDLE;
        mCallback = callback;
    }

    void runInSamethread(String s) { // Allows for downloading data and parsing JSON data to run in the same thread
        Log.d(TAG, "runInSamethread: starts");

        /*
         Downloading in the same thread as GetFlickrJsonData
         */
        // Prevent calling methods that use super in own code in case the behaviour changes
        // onPostExecute(doInBackground(s));
        if (mCallback != null) {
            mCallback.onDownloadComplete(doInBackground(s), mDownloadStatus);
        }
        Log.d(TAG, "runInSamethread: ends");
    }

    @Override
    protected void onPostExecute(String s) {
        //Log.d(TAG, "onPostExecute: parameter = " + s);
        if(mCallback != null) {
            mCallback.onDownloadComplete(s, mDownloadStatus); //Pass in the data (s) and the status (mDownloadStatus)
        }
        Log.d(TAG, "onPostExecute: ends");
        
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        if(strings == null) {
            mDownloadStatus = DownloadStatus.NOT_INITIALIZED;
            return null;
        }

        try {
            mDownloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(strings[0]); //get URL passed to method

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // HttpURLConnection sets GET by default
            connection.connect();
            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground: The response code was " + response);

            StringBuilder result = new StringBuilder(); // Contains data we downloaded

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));


            //Commented code does a while loop and checks if line is not null when doing readLine()

            // String line
            // while(null != (line = reader.readLine())) {

            //Alternative we can use a for loop and constrain the line variable to only when it's used
            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                //readLine removes the new line character, so we need to append it back
                result.append(line).append("\n");
            }

            mDownloadStatus = DownloadStatus.OK;
            return  result.toString();

        } catch(MalformedURLException e) {
            Log.e(TAG, "doInBackground: Invalid URL " + e.getMessage());
        } catch(IOException e) {
            Log.e(TAG, "doInBackground: IO Exception reading data: " + e.getMessage());
        } catch(SecurityException e) {
            // Forget to request permission to access the internet
            Log.e(TAG, "doInBackground: Security Exception. Needs permission?" + e.getMessage());
        } finally { //This part is guaranteed to execute whether there is an exception or not
            if (connection != null) {
                connection.disconnect(); // Releases resources
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream " + e.getMessage());
                }
            }
        }

        mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }

}
