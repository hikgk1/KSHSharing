package hbv601g.kshsharing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
import java.io.IOException;
import java.io.InputStream;

public class SelectionActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PICK_IMAGE = 2;
    Uri mCurrentPhotoPath;
    private ProfileActions mProfile;

    public static Intent selectionIntent(Context packageContext) {
        return new Intent(packageContext, SelectionActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        mProfile = ProfileActions.getInstance(this);
    }

    private void startViewIntent(String message) {
        mProfile.addUserUpload(message);
        Intent intent = DisplayImageActivity.displayIntent(this, message);
        startActivity(intent);
    }

    public void takeImage(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) { // Passa að það sé myndavél
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
            setThumbnail(mimageView); // Var að taka mynd, setja hana í preview
        } else if(requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            mCurrentPhotoPath = data.getData(); // Sækja URI fyrir völdu myndina
            setThumbnail(mimageView); // og setja í preview
        }
    }

    private void setThumbnail(ImageView mimageView) {
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(mCurrentPhotoPath);
            Bitmap tmp = BitmapFactory.decodeStream(is);
            tmp = Bitmap.createScaledBitmap(tmp, tmp.getWidth() / 2, tmp.getHeight() / 2, false);
            mimageView.setImageBitmap(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(is != null) is.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void sendImage(View view) {
        new SendImageTask().execute();
    }

    private class SendImageTask extends AsyncTask<Void, Void, String> {

        private MultiValueMap<String, Object> mFormData;

        @Override
        protected void onPreExecute() {
            Resource image = null;

            try {
                InputStream in = getContentResolver().openInputStream(mCurrentPhotoPath);

                if(in != null) {
                    image = new InputStreamResource(in); // Breyta myndinni í Spring for Android resource
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
            mFormData = new LinkedMultiValueMap<>();

            // Setja upp name hluta formsins
            HttpHeaders nameHeader = new HttpHeaders();
            nameHeader.add("Content-Disposition", "form-data; name=name");
            HttpEntity<String> nameEntity = new HttpEntity<>(name, nameHeader);
            mFormData.add("name", nameEntity);

            // Setja upp image hluta formsins
            HttpHeaders imageHeader = new HttpHeaders();
            imageHeader.add("Content-Disposition", "form-data; name=image; filename=test.jpeg");
            imageHeader.setContentType(MediaType.IMAGE_JPEG);
            HttpEntity<Object> imageEntity = new HttpEntity<Object>(image, imageHeader);
            mFormData.add("image", imageEntity);

            // Setja upp tags hluta formsins
            HttpHeaders tagsHeader = new HttpHeaders();
            nameHeader.add("Content-Disposition", "form-data; name=tags");
            HttpEntity<String> tagsEntity = new HttpEntity<>(tags, tagsHeader);
            mFormData.add("tags", tagsEntity);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                // Notar ekki volley eins og DisplayImageActivity.
                // Komu upp villur sem var nokkurnvegin ómögulegt að debugga.
                // Fengum Spring for Android til að virka, þannig notum það í staðin.

                // The URL for making the POST request
                final String url = (String)getResources().getText(R.string.url_upload);

                HttpHeaders requestHeaders = new HttpHeaders();

                // Sending multipart/form-data
                requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

                // Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
                        mFormData, requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();

                // Make the network request, posting the message, expecting a String in response from the server
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

                // Return the response body to display to the user
                return response.getBody();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Bakendi á að skila JSON hlut með meta-data um myndina
            // Kryfja úr því uuid til að hægt sé að ná í og sýna myndina
            JSONObject res;
            try {
                res = new JSONObject(result);
                if(!res.getString("uuid").equals("Error")) {
                    startViewIntent(res.getString("uuid"));
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Er ekki viss um að þetta sé að gera það sem það á að vera að gera, en forritið virkar
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
