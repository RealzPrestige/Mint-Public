package mint.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.managers.FriendManager;
import mint.managers.MessageManager;

public class Friend
        extends Command {
    public Friend() {
        super("friend", new String[]{"add/del/name/clear", "name"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            if (Mint.friendManager.getFriends().isEmpty()) {
                MessageManager.sendMessage("Friend list empty.");
            } else {
                String f = "Friends: ";
                for (FriendManager.friend friend : Mint.friendManager.getFriends()) {
                    try {
                        f = f + friend.getName() + ", ";
                    } catch (Exception exception) {
                    }
                }
                MessageManager.sendMessage(f);
            }
            return;
        }
        if (commands.length == 2) {
            if ("reset".equals(commands[0])) {
                MessageManager.sendMessage("Friends got reset.");
                return;
            }
            MessageManager.sendMessage(commands[0] + (Mint.friendManager.isFriend(commands[0]) ? " is friended." : " isn't friended."));
            return;
        }
        if (commands.length >= 2) {
            switch (commands[0]) {
                case "add": {
                    Mint.friendManager.addFriend(commands[1]);
                    MessageManager.sendMessage(ChatFormatting.WHITE + commands[1] + " has been friended");
                    return;
                }
                case "del": {
                    Mint.friendManager.removeFriend(commands[1]);
                    MessageManager.sendMessage(ChatFormatting.WHITE + commands[1] + " has been unfriended");
                    return;
                }
            }
            MessageManager.sendMessage("Unknown Command, try friend add/del (name)");
        }
    }
}

