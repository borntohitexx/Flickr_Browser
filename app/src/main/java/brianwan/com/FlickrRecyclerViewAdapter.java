package brianwan.com;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrRecyclerViewAdapter.FlickrImageViewHolder> {
    private static final String TAG = "FlickrRecyclerViewAdapt";
    private List<Photo> mPhotoList; //Photo list provided by main activity

    /**
     *  Context required because we will be using an external library to download thumbnails for
     *  the photos from Flickr. The library does need a context
     */
    private Context mContext;  //Store context in a field. This will be from the constructor

    public FlickrRecyclerViewAdapter(List<Photo> photoList, Context context) {
        mPhotoList = photoList;
        mContext = context;
    }

    @NonNull
    @Override
    public FlickrImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate a view from the browse.xml layout and return the view. Called by layout manager when it needs new view
        Log.d(TAG, "onCreateViewHolder: new view requested");

        // False tells inflater not to add the view to the parent layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, parent, false);
        return new FlickrImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlickrImageViewHolder holder, int position) {
        // Called by the layout manager when it wants new data in an existing row

        // Use placeholder image and text if no photos are returned
        if((mPhotoList == null) || (mPhotoList.size() == 0)) {
            holder.thumbnail.setImageResource(R.drawable.placeholder);
            holder.title.setText(R.string.empty_photo);
        } else {
            Photo photoItem = mPhotoList.get(position); //Retrieve photo from the list
            Log.d(TAG, "onBindViewHolder: " + photoItem.getTitle() + " --> " + position);
            Picasso.get().load(photoItem.getImage())
                    .error(R.drawable.placeholder) // Use placeholder image when there is an error
                    .placeholder(R.drawable.placeholder) // Use placeholder image when image is still loading
                    .into(holder.thumbnail); // Store the downloaded image into the image view widget in the viewholder

            holder.title.setText(photoItem.getTitle()); //Puts the title into the text view
        }
    }

    @Override
    public int getItemCount() {
        // Fake it to return item count of 1 in order to display empty placeholder. Tells recycler view there is 1 thing to display
        // Otherwise it would display nothing.
        return ((mPhotoList != null) && (mPhotoList.size() != 0) ? mPhotoList.size(): 1);
    }

    void loadNewData(List<Photo> newPhotos) {
        mPhotoList = newPhotos;
        notifyDataSetChanged(); //Tells the recycler view that the data has changed.
    }

    public Photo getPhoto(int position) {
        return ((mPhotoList != null) && (mPhotoList.size() != 0) ? mPhotoList.get(position) : null);
    }

    /**
     * Static class is no different than defining it in a different file. It just means that it is
     * defined here (for convenience).
     * Does not have access to fields in the outer class, however prevents holding a
     * reference to the outer class.
     */
    static class FlickrImageViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "FlickrImageViewHolder";
        ImageView thumbnail = null;
        TextView title = null;

        public FlickrImageViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder: starts");
            this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            this.title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
