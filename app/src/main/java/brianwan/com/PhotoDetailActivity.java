package brianwan.com;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;


import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;



public class PhotoDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        // PhotoDetail activity will be called by another activity. Therefore we enable the home button on the toolbar
        activateToolbar(true);

        Intent intent = getIntent(); //Get the intent that started this activity
        Photo photo = (Photo) intent.getSerializableExtra(PHOTO_TRANSFER); //Pass in the key to the stored serializable object
        if(photo != null) {
            // Get a reference to each of the widgets in the layout and set the values to display the fields of the photo object
            TextView photoTitle = (TextView) findViewById(R.id.photo_title);
            Resources resources = getResources();
            String  text = resources.getString(R.string.photo_title_text, photo.getTitle());
            photoTitle.setText(text);
//            photoTitle.setText("Title: " + photo.getTitle());

            TextView photoTags = (TextView) findViewById(R.id.photo_tags);
            //Simplified way of getting string from resources file
            photoTags.setText(resources.getString(R.string.photo_tags_text, photo.getTags()));
//            photoTags.setText("Tags: " + photo.getTags());

            TextView photoAuthor = (TextView) findViewById(R.id.photo_author);
            photoAuthor.setText(photo.getAuthor());

            ImageView photoImage = (ImageView) findViewById(R.id.photo_image);
            Picasso.get().load(photo.getLink())
                    .error(R.drawable.placeholder) // Use placeholder image when there is an error
                    .placeholder(R.drawable.placeholder) // Use placeholder image when image is still loading
                    .into(photoImage); // Store the downloaded image into the image view widget in the viewholder
        }
    }
}
