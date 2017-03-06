package hbv601g.kshsharing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Bakendi er á ókeypis þjónsutu sem getur tekið allt að 2 min að ræsa, þannig að ef þið ætlið að reyna að keyra appið farið þá fyrst á
 * https://group28hbv501g2016.herokuapp.com/
 * og bíðið þangað til síðan er búin að hlaða. Annars lendið þið sennilega í timeout.
 * Allt sem þið uploadið verður aðgengilegt á netinu, þannig að passið að uploada ekki neinu viðkvæmu.
 */
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
