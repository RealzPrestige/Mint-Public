package mint.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.managers.MessageManager;

public class Prefix
        extends Command {
    public Prefix() {
        super("prefix", new String[]{"<prefix>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            MessageManager.sendMessage(ChatFormatting.WHITE + "Current prefix is " + Mint.commandManager.getPrefix());
            return;
        }
        Mint.commandManager.setPrefix(commands[0]);
        MessageManager.sendMessage(ChatFormatting.WHITE + "Prefix has been set to " + commands[0]);
    }
}

