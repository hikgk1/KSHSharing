package hbv601g.kshsharing;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewUserActivity extends AppCompatActivity {
    private ProfileActions mProfile;

    public static Intent viewUserIntent(Context packageContext) {
        return new Intent(packageContext, ViewUserActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);
        mProfile = ProfileActions.getInstance(this);

        final TextView textViewId = (TextView) findViewById(R.id.userIdDisplay);
        final TextView textViewName = (TextView) findViewById(R.id.userNameDisplay);
        final TextView textViewFriends = (TextView) findViewById(R.id.userFriendsDisplay);
        final TextView textViewUploads = (TextView) findViewById(R.id.userUploadsDisplay);

        textViewId.setText(mProfile.getUserId());
        textViewName.setText(mProfile.getUserName());
        textViewFriends.setText(mProfile.getFriendName(mProfile.getUserFriendsList().get(0)));
        textViewUploads.setText(mProfile.getUserUploadList().get(0));
    }
}
