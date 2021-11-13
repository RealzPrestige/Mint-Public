package mint.managers;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class MessageManager {
    public static final String messagePrefix = ChatFormatting.AQUA + "[Mint] " + ChatFormatting.RESET;
    public static final String errorPrefix = ChatFormatting.DARK_RED + "[Mint] " + ChatFormatting.RESET;

    public static void sendRawMessage(String message) {
        if (Minecraft.getMinecraft().player != null) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message));
        }
    }

    public static void sendMessage(String message) {
        sendRawMessage(messagePrefix + message);
    }

    public static void sendError(String message) {
        sendRawMessage(errorPrefix + message);
    }

    public static void sendRemovableMessage(String message, int id) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(messagePrefix + message), id);
    }
}
