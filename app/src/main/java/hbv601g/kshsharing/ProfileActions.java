package hbv601g.kshsharing;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by Kristófer Guðni Kolbeins
 */

class ProfileActions {
    private final static String GPROFILE = "hbv601g.kshsharing.GPROFILE";
    private static ProfileActions mInstance = null;
    private static SharedPreferences mSharedPref = null;
    private UserProfile mUser;

    private ProfileActions(Context context) {
        mSharedPref = context.getSharedPreferences(GPROFILE, Context.MODE_PRIVATE);
        mUser = new UserProfile(mSharedPref.getInt(GPROFILE, -1));
    }

    static ProfileActions getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new ProfileActions(context.getApplicationContext());
        }
        return mInstance;
    }

    String getUserName() {
        return mUser.userName;
    }

    int getUserId() {
        return mUser.userId;
    }

    Vector<Integer> getUserFriendsList() {
        return mUser.friendsList;
    }

    Vector<String> getUserUploadList() {
        return mUser.uploadList;
    }

    String getFriendName(int userId) {
        return MockBackend.getFriendName(userId);
    }

    private class UserProfile {
        int userId;
        String userName;
        Vector<Integer> friendsList;
        Vector<String> uploadList;

        UserProfile(int userId) {
            if(userId == -1) {
                String newName = "Awesome User"; // TODO: Fá frá notanda
                int newId = MockBackend.getNewUserId(newName);
                mSharedPref.edit().putInt(GPROFILE, newId).apply();
                this.userId = userId;
            } else {
                this.userId = userId;
            }
            this.userName = MockBackend.getUserName(userId);
            this.friendsList = MockBackend.getUserFriends(userId);
            this.uploadList = MockBackend.getUserUploads(userId);
        }
    }
}
