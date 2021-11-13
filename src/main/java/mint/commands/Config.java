package mint.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.managers.MessageManager;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Config extends Command {
    public Config() {
        super("config", new String[]{"<save/load>"});
    }

    public void execute(String[] commands) {
        if (commands.length == 1) {
            MessageManager.sendMessage("You`ll find the config files in your gameProfile directory under mint/config");
            return;
        }
        if (commands.length == 2)
            if ("list".equals(commands[0])) {
                String configs = "Configs: ";
                File file = new File("mint/");
                List<File> directories = Arrays.stream(Objects.requireNonNull(file.listFiles())).filter(File::isDirectory).filter(f -> !f.getName().equals("util")).collect(Collectors.toList());
                StringBuilder builder = new StringBuilder(configs);
                for (File file1 : directories)
                    builder.append(file1.getName()).append(", ");
                configs = builder.toString();
                MessageManager.sendMessage(configs);
            } else {
                MessageManager.sendMessage("Not a valid command... Possible usage: <list>");
            }
        if (commands.length >= 3) {
            switch (commands[0]) {
                case "save":
                    assert Mint.configManager != null;
                    Mint.configManager.saveConfig(commands[1]);
                    MessageManager.sendMessage(ChatFormatting.GREEN + "Config '" + commands[1] + "' has been saved.");
                    return;
                case "load":
                    assert Mint.configManager != null;
                    if (Mint.configManager.configExists(commands[1])) {
                        Mint.configManager.loadConfig(commands[1]);
                        MessageManager.sendMessage(ChatFormatting.GREEN + "Config '" + commands[1] + "' has been loaded.");
                    } else {
                        MessageManager.sendMessage(ChatFormatting.RED + "Config '" + commands[1] + "' does not exist.");
                    }
                    return;
            }
            MessageManager.sendMessage("Not a valid command... Possible usage: <save/load>");
        }
    }
}
