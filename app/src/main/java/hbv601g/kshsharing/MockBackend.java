package hbv601g.kshsharing;

import java.util.Vector;

/**
 * Created by Kristófer Guðni Kolbeins
 */

class MockBackend {
    static int getNewUserId(String newName) {
        return 5000;
    }

    static String getUserName(int userId) {
        return "Awesome User";
    }

    static Vector<Integer> getUserFriends(int userId) {
        Vector<Integer> res = new Vector<>();
        res.add(5001);
        res.add(5002);
        return res;
    }

    static Vector<String> getUserUploads(int userId) {
        Vector<String> res = new Vector<>();
        res.add("c0089a83-a9e6-4a34-af2b-57f9def93868");
        res.add("f63875c1-f23c-4add-a5cb-d02965152f62");
        return res;
    }

    static String getFriendName(int friendId) {
        switch(friendId) {
            case 5001:
                return "Jón Jónsson";
            case 5002:
                return "Guðríður Guðríðardóttir";
            default:
                return "Fann ekki nafn";
        }
    }

    static void addUpload(String uuid) {

    }

    static void addFriend(int friendId) {

    }
}
