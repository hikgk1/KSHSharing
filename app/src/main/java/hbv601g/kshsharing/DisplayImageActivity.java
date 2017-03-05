package hbv601g.kshsharing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class DisplayImageActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "hbv601g.kshsharing.MESSAGE";
    private RequestQueue mRequestQueue;

    public static Intent displayIntent(Context packageContext, String message) {
        Intent intent = new Intent(packageContext, DisplayImageActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        String message;

        // Setja upp og ræsa Volley http request queue
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();

        // Sækja uuid úr extra ef innan úr appinu eða URI ef smellt var á ksh:// link
        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_MESSAGE)) {
            message = intent.getStringExtra(EXTRA_MESSAGE);
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
        if(image == null || image.getUuid().equals("Error")) return;
        final ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
        Bitmap tmp = image.getImage();
        // Getur ekki skalað myndina inn í ImageView ef víddir myndarinnar eru meira en 4096
        if(tmp.getWidth() > 4096 || tmp.getHeight() > 4096) {
            // Veðjum á að myndin sé ekki stærri en 8192
            // Þarf betri langtímalausn, en þetta dugar í fyrstu útgáfu
            tmp = Bitmap.createScaledBitmap(tmp, tmp.getWidth() / 2, tmp.getHeight() / 2, false);
        }
        imageView.setImageBitmap(tmp);
    }

    private class GetImageTask extends AsyncTask<String, Void, UserImageContainer> {
        private final String mDbUrl = (String)getResources().getText(R.string.url_db);
        private final String mImgUrl = (String)getResources().getText(R.string.url_downloadImg);

        protected UserImageContainer doInBackground(String... message) {
            UserImageContainer res = new UserImageContainer();

            // RequestFuture til að það sé ekkert mögulegt vesen með hvenær það er búið að sækja myndina
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            // Sækja meta-data um myndina
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, mDbUrl + message[0], null, future, future);
            mRequestQueue.add(jsObjRequest); // Senda þetta inn í volley queue

            InputStream in = null;
            try {
                JSONObject response = future.get(30, TimeUnit.SECONDS);

                if(!response.getString("uuid").equals("Error")) {
                    res.setName(response.getString("name"));
                    res.setEnding(response.getString("ending"));
                    res.setTags(response.getString("tags"));
                    res.setUuid(response.getString("uuid"));
                } else {
                    res.setUuid("Error");
                    throw new Exception();
                }

                // Sækja myndina sjálfa
                Toast.makeText(getApplicationContext(), "Downloading image", Toast.LENGTH_LONG).show();
                String urlString =  mImgUrl + res.getUuid() + res.getEnding();
                in = new URL(urlString).openStream();
                res.setImage(BitmapFactory.decodeStream(in));
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Encountered an error", Toast.LENGTH_LONG).show();
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
