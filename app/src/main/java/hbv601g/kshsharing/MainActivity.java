package hbv601g.kshsharing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "hbv601g.kshsharing.MESSAGE";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PICK_IMAGE = 2;
    Uri mCurrentPhotoPath;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getImage(View view) {
        Intent intent = new Intent(this, DisplayImageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void takeImage(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                                                        "hbv601g.kshsharing",
                                                        photoFile);
                mCurrentPhotoPath = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void pickImage(View view) {
        Intent pickPictureIntent = new Intent();
        pickPictureIntent.setType("image/*");
        pickPictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickPictureIntent, "Select Picture"), REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode ,data);
        final ImageView mimageView = (ImageView) findViewById(R.id.imageDisplay2);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setThumbnail(mimageView);
        } else if(requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            mCurrentPhotoPath = data.getData();
            setThumbnail(mimageView);
        }
    }

    private void setThumbnail(ImageView mimageView) {
        try {
            InputStream is = getContentResolver().openInputStream(mCurrentPhotoPath);
            Bitmap tmp = BitmapFactory.decodeStream(is);
            if(is != null) is.close();
            tmp = Bitmap.createScaledBitmap(tmp, tmp.getWidth() / 2, tmp.getHeight() / 2, false);
            mimageView.setImageBitmap(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendImage(View view) {
        new SendImageTask().execute();
    }

    private class SendImageTask extends AsyncTask<Void, Void, String> {

        private MultiValueMap<String, Object> formData;

        @Override
        protected void onPreExecute() {
            //Resource resource = new ClassPathResource("res/drawable/spring09_logo.png");
            Resource image = null;

            try {
                InputStream in = getContentResolver().openInputStream(mCurrentPhotoPath);

                if(in != null) {
                    image = new InputStreamResource(in);
                } else {
                    throw new NullPointerException();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            EditText editText = (EditText) findViewById(R.id.image_name);
            String name = editText.getText().toString();

            editText = (EditText) findViewById(R.id.image_tags);
            String tags = editText.getText().toString();

            // populate the data to post
            formData = new LinkedMultiValueMap<String, Object>();
            formData.add("name", name);
            formData.add("image", image);
            formData.add("tags", tags);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                // The URL for making the POST request
                final String url = "https://group28hbv501g2016.herokuapp.com/uploadr/";

                HttpHeaders requestHeaders = new HttpHeaders();

                // Sending multipart/form-data
                requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

                // Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
                        formData, requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();

                // Make the network request, posting the message, expecting a String in response from the server
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                        String.class);

                // Return the response body to display to the user
                return response.getBody();
            } catch (Exception e) {
                Log.e("Villuskilaboð", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }

    }

    /*private class SendImageTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... message) {
            uploadImage();
            return "blaf";
        }

        protected void onPostExecute(String result) {

        }
    }

    private void uploadImage(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://group28hbv501g2016.herokuapp.com/uploadr/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.d("Virkaði", s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                Bitmap bitmap = null;
                try {
                    InputStream is = getContentResolver().openInputStream(mCurrentPhotoPath);
                    bitmap = BitmapFactory.decodeStream(is);
                    if (is != null) is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String image = getStringImage(bitmap);

                EditText editText = (EditText) findViewById(R.id.image_name);
                String name = editText.getText().toString();

                editText = (EditText) findViewById(R.id.image_tags);
                String tags = editText.getText().toString();

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("image", image);
                params.put("name", name);
                params.put("tags", tags);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024 * 50);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();

        //Adding request to the queue
        mRequestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }*/

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "tmp_file_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }
}
