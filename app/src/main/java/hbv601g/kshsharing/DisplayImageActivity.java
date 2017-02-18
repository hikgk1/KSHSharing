package hbv601g.kshsharing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class DisplayImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        String message;

        Intent intent = getIntent();
        if(intent.hasExtra(MainActivity.EXTRA_MESSAGE)) {
            message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        } else {
            message = intent.getData().getHost();
        }

        try {
            new GetImageTask().execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImage(Bitmap image) {
        final ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
        imageView.setImageBitmap(image);
    }

    private class GetImageTask extends AsyncTask<String, Void, Bitmap> {

        private String hostUrl = "http://group28hbv501g2016.s3-website-eu-west-1.amazonaws.com/images/";

        protected Bitmap doInBackground(String... message) {
            String urlString =  hostUrl + message[0] + ".jpg";
            Bitmap res = null;
            InputStream in = null;
            try {
                in = new URL(urlString).openStream();
                res = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try { if(in != null) in.close(); } catch (Exception e) { e.printStackTrace(); }
            }
            return res;
        }

        protected void onPostExecute(Bitmap image) {
            setImage(image);
        }
    }
}
