package mint.managers;

import mint.utils.PlayerUtil;

import java.util.ArrayList;
import java.util.List;

public class FriendManager {
    private List<friend> friends;

    public FriendManager() {
        friends = new ArrayList<>();
    }

    public void addFriend(String name) {
        if (!isFriend(name)) {
            friends.add(new friend(name));
        }
    }

    public void removeFriend(String name) {
        friends.removeIf(player -> player.getName().equalsIgnoreCase(name));
    }

    public boolean isFriend(String name) {
        for (friend player : friends) {
            if (player.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void setFriends(List<friend> list) {
        friends = list;
    }

    public List<friend> getFriends() {
        return friends;
    }

    public void clear() {
        friends.clear();
    }

    public static class friend {
        private final String name;
        private String nickName;

        public friend(String name) {
            this.name = name;
            PlayerUtil.getUUIDFromName(name);
        }

        public String getName() {
            return name;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String name) {
            nickName = name;
        }

    }
}

