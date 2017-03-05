package hbv601g.kshsharing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gotoSelection(View view) {
        Intent intent = SelectionActivity.selectionIntent(this);
        startActivity(intent);
    }

    public void gotoSearch(View view) {
        Intent intent = SearchActivity.searchIntent(this);
        startActivity(intent);
    }
}
