package hbv601g.kshsharing;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DisplayGalleryActivity extends AppCompatActivity {
    public final static String GEXTRA_MESSAGE = "hbv601g.kshsharing.GMESSAGE";

    public static Intent displayIntent(Context packageContext, String message) {
        Intent intent = new Intent(packageContext, DisplayGalleryActivity.class);
        intent.putExtra(GEXTRA_MESSAGE, message);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_gallery);
        JSONArray jInput = null;

        // Sækja gögn úr intent
        Intent intent = getIntent();
        if(intent.hasExtra(GEXTRA_MESSAGE)) {
            try {
                jInput = new JSONArray(intent.getStringExtra(GEXTRA_MESSAGE));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Búa til og stilla RecyclerView
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.image_gallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<UserImageContainer> createLists = prepareData(jInput);
        RecyclerAdapter adapter = new RecyclerAdapter(getApplicationContext(), createLists);
        recyclerView.setAdapter(adapter);
    }

    // Býr til lista af UserImageContainers úr array af JSON hlutum
    private ArrayList<UserImageContainer> prepareData(JSONArray input) {
        ArrayList<UserImageContainer> res = new ArrayList<>();
        for(int i = 0; i < input.length(); i++) {
            try {
                JSONObject imgJson = input.getJSONObject(i);
                UserImageContainer img = new UserImageContainer();
                img.setUuid(imgJson.getString("uuid"));
                img.setTags(imgJson.getString("tags"));
                img.setEnding(imgJson.getString("ending"));
                img.setName(imgJson.getString("name"));
                res.add(img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }
}
