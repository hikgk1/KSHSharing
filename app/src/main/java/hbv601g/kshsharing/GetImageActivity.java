package hbv601g.kshsharing;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class GetImageActivity extends AppCompatActivity {

    public static Intent getImageIntent(Context packageContext) {
        return new Intent(packageContext, GetImageActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_image);
    }

    public void getImage(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        startViewIntent(message);
    }

    private void startViewIntent(String message) {
        Intent intent = DisplayImageActivity.displayIntent(this, message);
        startActivity(intent);
    }

}
