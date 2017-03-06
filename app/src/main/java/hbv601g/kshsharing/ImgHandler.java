package hbv601g.kshsharing;

import android.content.ContentResolver;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kristófer Guðni Kolbeins
 *
 * !!!!!!!!!!!!  ATH  !!!!!!!!!!!!
 *
 * Erum sennilega hættir við að nota þennan klasa.
 * Allt of mikið vesen að fá hann til að virka eins og við settum þetta upp í UML diagraminu.
 * Virknin er í staðin í viðeigandi activity klössum
 */

class ImgHandler {
    private final String mDbUrl;
    private final String mImgUrl;
    private final String mUpUrl;
    private RequestQueue mRequestQueue;
    private ContentResolver mContentResolver;

    private Uri mCurrentPhotoPath;

    private UserImageContainer getResult;
    private String postResult;

    ImgHandler(String dbUrl, String imgUrl, String upUrl, File cacheDir, ContentResolver cr) {
        mDbUrl = dbUrl;
        mImgUrl = imgUrl;
        mUpUrl = upUrl;
        mContentResolver = cr;

        Cache cache = new DiskBasedCache(cacheDir, 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
    }

    public UserImageContainer getImage(String uuid) {
        new GetImageTask().execute(uuid);
        return getResult;
    }

    public String sendImage(Uri photoPath, String imgName, String imgTags) {
        mCurrentPhotoPath = photoPath;
        new SendImageTask().execute(imgName, imgTags);
        return postResult;
    }

    private class GetImageTask extends AsyncTask<String, Void, UserImageContainer> {

        protected UserImageContainer doInBackground(String... message) {
            UserImageContainer res = new UserImageContainer();

            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, mDbUrl + message[0], null, future, future);
            mRequestQueue.add(jsObjRequest);

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

                String urlString =  mImgUrl + res.getUuid() + res.getEnding();
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
            getResult = image;
        }
    }

    private class SendImageTask extends AsyncTask<String, Void, String> {

        private MultiValueMap<String, Object> mFormData;

        void onPreExecute(String... params) {
            Resource image = null;

            try {
                InputStream in = mContentResolver.openInputStream(mCurrentPhotoPath);

                if(in != null) {
                    image = new InputStreamResource(in);
                } else {
                    throw new NullPointerException();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String name = params[0];
            String tags = params[1];

            // populate the data to post
            mFormData = new LinkedMultiValueMap<>();

            HttpHeaders nameHeader = new HttpHeaders();
            nameHeader.add("Content-Disposition", "form-data; name=name");
            HttpEntity<String> nameEntity = new HttpEntity<>(name, nameHeader);
            mFormData.add("name", nameEntity);

            HttpHeaders imageHeader = new HttpHeaders();
            imageHeader.add("Content-Disposition", "form-data; name=image; filename=test.jpeg");
            imageHeader.setContentType(MediaType.IMAGE_JPEG);
            HttpEntity<Object> imageEntity = new HttpEntity<Object>(image, imageHeader);
            mFormData.add("image", imageEntity);

            HttpHeaders tagsHeader = new HttpHeaders();
            nameHeader.add("Content-Disposition", "form-data; name=tags");
            HttpEntity<String> tagsEntity = new HttpEntity<>(tags, tagsHeader);
            mFormData.add("tags", tagsEntity);
        }

        @Override
        protected String doInBackground(String... params) {
            onPreExecute(params);

            try {
                HttpHeaders requestHeaders = new HttpHeaders();

                // Sending multipart/form-data
                requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

                // Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
                        mFormData, requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();

                // Make the network request, posting the message, expecting a String in response from the server
                ResponseEntity<String> response = restTemplate.exchange(mUpUrl, HttpMethod.POST, requestEntity, String.class);

                // Return the response body to display to the user
                return response.getBody();
            } catch (Exception e) {
                Log.e("Villuskilaboð", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject res;
            try {
                res = new JSONObject(result);
                if(!res.getString("uuid").equals("Error")) {
                    postResult = res.getString("uuid");
                } else {
                    Log.d("Villa", "Fékk til baka Error");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
