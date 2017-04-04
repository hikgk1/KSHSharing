package hbv601g.kshsharing;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class SearchActivity extends AppCompatActivity {

    public static Intent searchIntent(Context packageContext) {
        return new Intent(packageContext, SearchActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void searchByTags(View view) {
        new SearchByTagsTask().execute();
    }

    public void setGallery(JSONArray list) {
        Intent intent = DisplayGalleryActivity.displayIntent(this, list.toString());
        startActivity(intent);
    }

    private class SearchByTagsTask extends AsyncTask<Void , Void, JSONArray> {

        private MultiValueMap<String, Object> mFormData;

        @Override
        protected void onPreExecute() {
            // Sækja leitartextan úr boxinu
            EditText editText = (EditText) findViewById(R.id.search_message);
            String name = editText.getText().toString();

            // populate the data to post
            mFormData = new LinkedMultiValueMap<>();

            // Setja upp leitar hluta formsins
            HttpHeaders nameHeader = new HttpHeaders();
            nameHeader.add("Content-Disposition", "form-data; name=tag");
            HttpEntity<String> nameEntity = new HttpEntity<>(name, nameHeader);
            mFormData.add("name", nameEntity);
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            try {
                // Notar ekki volley eins og DisplayImageActivity.
                // Komu upp villur sem var nokkurnvegin ómögulegt að debugga.
                // Fengum Spring for Android til að virka, þannig notum það í staðin.

                // The URL for making the POST request
                final String url = (String)getResources().getText(R.string.url_search);

                HttpHeaders requestHeaders = new HttpHeaders();

                // Sending multipart/form-data
                requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

                // Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
                        mFormData, requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();

                // Make the network request, posting the message
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

                return new JSONArray(response.getBody());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            setGallery(result);
        }
    }
}
