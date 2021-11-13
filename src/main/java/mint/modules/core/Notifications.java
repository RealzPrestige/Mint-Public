package mint.modules.core;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.events.ModuleToggleEvent;
import mint.events.RenderOverlayEvent;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.*;
import mint.utils.NullUtil;
import mint.utils.RenderUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@ModuleInfo(name = "Notifications", category = Module.Category.Core, description = "Notifies you when stuff happens.")
public class Notifications extends Module {

    private static Notifications INSTANCE = new Notifications();
    public HashMap<String, Integer> notification = new HashMap<>();
    public static HashMap<String, Integer> TotemPopCounter = new HashMap<>();
    public boolean hasReachedEndState;
    public int waitTime;
    public int width;
    public EnumSetting mode = new EnumSetting("Mode", Mode.CHAT, this);
    public enum Mode {CHAT, HUD, BOTH}

    public ParentSetting targetsParent = new ParentSetting("Targets", true, this);
    public BooleanSetting pops = new BooleanSetting("Pops", false, this, v -> targetsParent.getValue());
    public BooleanSetting modules = new BooleanSetting("Modules", false, this, v -> targetsParent.getValue());

    public ParentSetting othersParent = new ParentSetting("Others", false, this);
    public IntegerSetting y = new IntegerSetting("y", 255, 0, 1000, this, v -> othersParent.getValue());
    public BooleanSetting newMode = new BooleanSetting("New Mode", false, this, v -> othersParent.getValue());
    public IntegerSetting staticTime = new IntegerSetting("Static Time", 30, 0, 100, this, v -> othersParent.getValue());

    public ColorSetting startColor = new ColorSetting("Start Color", new Color(-1), this, v -> !newMode.getValue());

    public ColorSetting endColor = new ColorSetting("End Color", new Color(-1), this, v -> !newMode.getValue());

    public ColorSetting outlineColor = new ColorSetting("Outline Color", new Color(-1), this, v ->  !newMode.getValue());

    public ColorSetting backgroundColor = new ColorSetting("Background Color", new Color(-1), this);

    public ColorSetting newColor = new ColorSetting("New Color", new Color(-1), this, v -> newMode.getValue());
    public boolean lefinalewidth;

    public Notifications() {
        this.setInstance();
    }
    
    @SubscribeEvent
    public void onModuleEnable(ModuleToggleEvent.Enable event){
        TextComponentString text = new TextComponentString(ChatFormatting.AQUA + "" + ChatFormatting.AQUA + Mint.commandManager.getClientMessage() + ChatFormatting.RESET + ChatFormatting.DARK_AQUA + "" + ChatFormatting.BOLD + " " + event.getModule().getName().replace("_", " ") + ChatFormatting.RESET + " was toggled " + ChatFormatting.GREEN + "" + ChatFormatting.BOLD + "on!");
        if (isEnabled() && (mode.getValueEnum().equals(Mode.CHAT) || mode.getValueEnum().equals(Mode.BOTH))) {
            Mint.INSTANCE.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
        }
        if (isEnabled() && modules.getValue() && (mode.getValueEnum().equals(Mode.HUD) || mode.getValueEnum().equals(Mode.BOTH))) {
            notification.clear();
            hasReachedEndState = false;
            waitTime = 0;
            width = 0;
            lefinalewidth = false;
            notification.put(event.getModule().getName().replace("_", " ") + " was toggled " + ChatFormatting.GREEN + "" + ChatFormatting.BOLD + "on!", 1000);
        }
    }

    @SubscribeEvent
    public void onModuleDisable(ModuleToggleEvent.Disable event){
        TextComponentString text = new TextComponentString(ChatFormatting.AQUA + "" + ChatFormatting.AQUA + Mint.commandManager.getClientMessage() + ChatFormatting.RESET + ChatFormatting.DARK_AQUA + "" + ChatFormatting.BOLD + " " + event.getModule().getName().replace("_", " ") + ChatFormatting.RESET + " was toggled " + ChatFormatting.RED + "" + ChatFormatting.BOLD + "off!");
        if (isEnabled() && (mode.getValueEnum().equals(Mode.CHAT) || mode.getValueEnum().equals(Mode.BOTH))) {
            Mint.INSTANCE.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
        }
        if (isEnabled() && modules.getValue() && (mode.getValueEnum().equals(Mode.HUD) || mode.getValueEnum().equals(Mode.BOTH))) {
            notification.clear();
            hasReachedEndState = false;
            waitTime = 0;
            width = 0;
            lefinalewidth = false;
            notification.put(event.getModule().getName().replace("_", " ") + " was toggled " + ChatFormatting.RED + "" + ChatFormatting.BOLD + "off!", 1000);
        }
    }
    public void renderOverlayEvent(RenderOverlayEvent event) {
        for (Map.Entry<String, Integer> entry : notification.entrySet()) {
            if (modules.getValue()) {
                String moduleString = entry.getKey();
                if(width > -Mint.textManager.getStringWidth(moduleString) - 7 && !lefinalewidth) {
                    width = width - 2;
                }
                if(width == -Mint.textManager.getStringWidth(moduleString) - 7 || width == -Mint.textManager.getStringWidth(moduleString) - 8){
                    lefinalewidth = true;
                }
                if (entry.getValue() > 950 && !hasReachedEndState) {
                    notification.put(entry.getKey(), entry.getValue() - 1);
                }
                if (entry.getValue() == 951) {
                    hasReachedEndState = true;
                }
                if (hasReachedEndState && waitTime == staticTime.getValue()) {
                    if(lefinalewidth){
                        width = width + 2;
                    }
                    notification.put(entry.getKey(), entry.getValue() + 1);
                }
                if (entry.getValue() > 1100) {
                    notification.remove(entry.getKey());
                }
                if (newMode.getValue()) {
                    RenderUtil.drawRect(entry.getValue() - Mint.textManager.getStringWidth(moduleString) - 5, y.getValue() - 5, entry.getValue() - Mint.textManager.getStringWidth(moduleString) + Mint.textManager.getStringWidth(moduleString) + 5, y.getValue() + Mint.textManager.getFontHeight() + 10, backgroundColor.getColor().getRGB());
                    Mint.textManager.drawString(moduleString, entry.getValue() - Mint.textManager.getStringWidth(moduleString), y.getValue() + 2, -1, false);
                    RenderUtil.drawRect(entry.getValue() - Mint.textManager.getStringWidth(moduleString) - 5, y.getValue() - 5, entry.getValue() - Mint.textManager.getStringWidth(moduleString) + Mint.textManager.getStringWidth(moduleString) + 5 + width, y.getValue() + Mint.textManager.getFontHeight() + 10, newColor.getColor().getRGB());
                    RenderUtil.drawRect(entry.getValue() - Mint.textManager.getStringWidth(moduleString) - 5, y.getValue() + Mint.textManager.getFontHeight() + 10, entry.getValue() - Mint.textManager.getStringWidth(moduleString) + Mint.textManager.getStringWidth(moduleString) + 5 - (waitTime * Mint.textManager.getStringWidth(moduleString) / 70f), y.getValue() + Mint.textManager.getFontHeight() + 11, newColor.getColor().getRGB());
                } else {
                    RenderUtil.drawRect(entry.getValue() - Mint.textManager.getStringWidth(moduleString) - 5, y.getValue() - 5, entry.getValue() - Mint.textManager.getStringWidth(moduleString) + Mint.textManager.getStringWidth(moduleString) + 5, y.getValue() + Mint.textManager.getFontHeight() + 10, backgroundColor.getColor().getRGB());
                    RenderUtil.drawBorder(entry.getValue() - Mint.textManager.getStringWidth(moduleString) - 4, y.getValue() - 4, Mint.textManager.getStringWidth(moduleString) + 8, Mint.textManager.getFontHeight() + 13, outlineColor.getColor());
                    RenderUtil.drawGradientRect(entry.getValue() - Mint.textManager.getStringWidth(moduleString) - 4, y.getValue() - 5, entry.getValue() - Mint.textManager.getStringWidth(moduleString) + Mint.textManager.getStringWidth(moduleString) + 4, y.getValue() - 4, startColor.getColor().getRGB(), endColor.getColor().getRGB());
                    Mint.textManager.drawString(moduleString, entry.getValue() - Mint.textManager.getStringWidth(moduleString), y.getValue() + 2, -1, false);
                }
            }
        }
    }

    public void onDeath(EntityPlayer player) {
        if (TotemPopCounter.containsKey(player.getName())) {
            int totemCount = TotemPopCounter.get(player.getName());
            TotemPopCounter.remove(player.getName());
            if (totemCount == 1) {
                int id = 0;
                for (char character : player.getName().toCharArray()) {
                    id += character;
                    id *= 10;
                }
                if (pops.getValue() && (mode.getValueEnum().equals(Mode.CHAT) || mode.getValueEnum().equals(Mode.BOTH))) {
                    width = 0;
                    lefinalewidth = false;
                    MessageManager.sendRemovableMessage(ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " died after popping " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totem.", id);
                }
                if (pops.getValue() && (mode.getValueEnum().equals(Mode.HUD) || mode.getValueEnum().equals(Mode.BOTH))) {
                    width = 0;
                    lefinalewidth = false;
                    notification.clear();
                    hasReachedEndState = false;
                    waitTime = 0;
                    notification.put(ChatFormatting.WHITE + "" + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " died after popping " + ChatFormatting.WHITE + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totem.", 1000);
                }
            } else {
                int id = 0;
                for (char character : player.getName().toCharArray()) {
                    id += character;
                    id *= 10;
                }
                if (pops.getValue() && (mode.getValueEnum() == Mode.CHAT || mode.getValueEnum() == Mode.BOTH)) {
                    width = 0;
                    lefinalewidth = false;
                    MessageManager.sendRemovableMessage(ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " died after popping " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totems.", id);
                }
                if (pops.getValue() && (mode.getValueEnum().equals(Mode.HUD) || mode.getValueEnum() == Mode.BOTH)) {
                    width = 0;
                    lefinalewidth = false;
                    notification.clear();
                    hasReachedEndState = false;
                    waitTime = 0;
                    notification.put(ChatFormatting.WHITE + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " died after popping " + ChatFormatting.WHITE + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totems.", 1000);
                }
            }
        }
    }

    public void onTotemPop(EntityPlayer player) {
        if (NullUtil.fullNullCheck())
            return;

        if (mc.player.equals(player)) {
            return;
        }
        int totemCount = 1;
        if (TotemPopCounter.containsKey(player.getName())) {
            totemCount = TotemPopCounter.get(player.getName());
            TotemPopCounter.put(player.getName(), ++totemCount);
        } else {
            TotemPopCounter.put(player.getName(), totemCount);
        }
        if (totemCount == 1) {
            int id = 0;
            for (char character : player.getName().toCharArray()) {
                id += character;
                id *= 10;
            }
            if (pops.getValue() && (mode.getValueEnum().equals(Mode.CHAT) || mode.getValueEnum().equals(Mode.BOTH))) {
                width = 0;
                lefinalewidth = false;
                MessageManager.sendRemovableMessage(ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " has popped " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totem.", id);
            }
            if (pops.getValue() && (mode.getValueEnum().equals(Mode.HUD) || mode.getValueEnum().equals(Mode.BOTH))) {
                width = 0;
                lefinalewidth = false;
                notification.clear();
                hasReachedEndState = false;
                waitTime = 0;
                notification.put(ChatFormatting.WHITE + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " has popped " + ChatFormatting.WHITE + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totem.", 1000);
            }
        } else {
            int id = 0;
            for (char character : player.getName().toCharArray()) {
                id += character;
                id *= 10;
            }
            if (pops.getValue() && (mode.getValueEnum().equals(Mode.CHAT) || mode.getValueEnum().equals(Mode.BOTH))) {
                width = 0;
                lefinalewidth = false;
                MessageManager.sendRemovableMessage(ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " has popped " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totems.", id);
            }
            if (pops.getValue() && (mode.getValueEnum().equals(Mode.HUD) || mode.getValueEnum().equals(Mode.BOTH))) {
                width = 0;
                lefinalewidth = false;
                notification.clear();
                hasReachedEndState = false;
                waitTime = 0;
                notification.put(ChatFormatting.WHITE + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " has popped " + ChatFormatting.WHITE + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totems.", 1000);
            }
        }
    }

    //HAHAHAAA FUCK U KAMBING ONTICK TIMERS ON TOP EZZ
    public void onTick() {
        if (waitTime < staticTime.getValue()) {
            ++waitTime;
        }
    }

    public static Notifications getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Notifications();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

}
