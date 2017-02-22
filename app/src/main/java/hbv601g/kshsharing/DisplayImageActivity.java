package hbv601g.kshsharing;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class DisplayImageActivity extends AppCompatActivity {
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        String message;

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();


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

    private void setImage(UserImageContainer image) {
        final ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
        imageView.setImageBitmap(image.getImage());
    }

    private class GetImageTask extends AsyncTask<String, Void, UserImageContainer> {
        private String dbUrl = "https://group28hbv501g2016.herokuapp.com/imgr/";
        private String hostUrl = "http://group28hbv501g2016.s3-website-eu-west-1.amazonaws.com/images/";

        protected UserImageContainer doInBackground(String... message) {
            UserImageContainer res = new UserImageContainer();

            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, dbUrl + message[0], null, future, future);
            mRequestQueue.add(jsObjRequest);

            try {
                JSONObject response = future.get(30, TimeUnit.SECONDS);

                Log.d("JSON object", response.toString());

                res.setName(response.getString("name"));
                res.setEnding(response.getString("ending"));
                res.setTags(response.getString("tags"));
                res.setUuid(response.getString("uuid"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            InputStream in = null;
            try {
                String urlString =  hostUrl + res.getUuid() + res.getEnding();
                in = new URL(urlString).openStream();
                res.setImage(BitmapFactory.decodeStream(in));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try { if(in != null) in.close(); } catch (Exception e) { e.printStackTrace(); }
            }
            return res;
        }

        protected void onPostExecute(UserImageContainer image) {
            setImage(image);
        }
    }
}
