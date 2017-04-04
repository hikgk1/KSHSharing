package hbv601g.kshsharing;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Vector;

public class ViewUserActivity extends AppCompatActivity {
    private Context mAcontext;

    public static Intent viewUserIntent(Context packageContext) {
        return new Intent(packageContext, ViewUserActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);
        ProfileActions mProfile = ProfileActions.getInstance(this);
        mAcontext = this.getApplicationContext();

        final TextView textViewId = (TextView) findViewById(R.id.userIdDisplay);
        final TextView textViewName = (TextView) findViewById(R.id.userNameDisplay);
        final TextView textViewFriends = (TextView) findViewById(R.id.userFriendsDisplay2);
        final TextView textViewUploads = (TextView) findViewById(R.id.userUploadsDisplay2);
        final ListView listViewFriends = (ListView) findViewById(R.id.userFriendsDisplay);
        final ListView listViewUploads = (ListView) findViewById(R.id.userUploadsDisplay);

        textViewId.setText(String.format((String)getResources().getText(R.string.profile_user_id), String.valueOf(mProfile.getUserId())));
        textViewName.setText(String.format((String)getResources().getText(R.string.profile_user_name), mProfile.getUserName()));
        textViewFriends.setText(getResources().getText(R.string.profile_user_friends));
        textViewUploads.setText(getResources().getText(R.string.profile_user_uploads));

        Vector<Integer> friendsList = mProfile.getUserFriendsList();
        String[] fList = new String[friendsList.size()];
        for(int i = 0; i < friendsList.size(); i++) {
            int tmp = friendsList.get(i);
            fList[i] = mProfile.getFriendName(tmp);
        }
        ArrayAdapter<String> fAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fList);
        listViewFriends.setAdapter(fAdapter);

        Vector<String> uploadList = mProfile.getUserUploadList();
        String[] uList = new String[uploadList.size()];
        for(int i = 0; i < uploadList.size(); i++) {
            uList[i] = uploadList.get(i);
        }
        ArrayAdapter<String> uAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, uList);
        listViewUploads.setAdapter(uAdapter);

        listViewUploads.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = (String)parent.getItemAtPosition(position);
                Intent intent = DisplayImageActivity.displayIntent(mAcontext, value);
                startActivity(intent);
            }
        });
    }
}
