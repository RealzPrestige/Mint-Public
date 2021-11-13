package mint.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.managers.MessageManager;
import mint.modules.Module;
import org.lwjgl.input.Keyboard;

public class Bind
        extends Command {
    public Bind() {
        super("bind", new String[]{"<module>", "<bind>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            MessageManager.sendMessage("Please specify a module.");
            return;
        }
        String rkey = commands[1];
        String moduleName = commands[0];
        Module module = Mint.moduleManager.getModuleByName(moduleName);
        if (module == null) {
            MessageManager.sendMessage("Unknown module '" + module + "'!");
            return;
        }
        if (rkey == null) {
            MessageManager.sendMessage(module.getName() + " is bound to " + ChatFormatting.GRAY + module.getBind());
            return;
        }
        int key = Keyboard.getKeyIndex(rkey.toUpperCase());
        if (rkey.equalsIgnoreCase("none")) {
            key = -1;
        }
        if (key == 0) {
            MessageManager.sendMessage("Unknown key '" + rkey + "'!");
            return;
        }
        module.bind.setValue(Integer.parseInt(String.valueOf(key)));
        MessageManager.sendMessage("Bind for " + ChatFormatting.GREEN + module.getName() + ChatFormatting.WHITE + " set to " + ChatFormatting.GRAY + rkey.toUpperCase());
    }
}

