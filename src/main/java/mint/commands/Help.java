package mint.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.managers.MessageManager;

public class Help extends Command {

    public Help() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        MessageManager.sendMessage(ChatFormatting.WHITE + "" + ChatFormatting.BOLD + "Commands: ");
        for (Command command : Mint.commandManager.getCommands()) {
            MessageManager.sendMessage(ChatFormatting.WHITE + " \u2022 " + command.getName());
        }
    }
}

