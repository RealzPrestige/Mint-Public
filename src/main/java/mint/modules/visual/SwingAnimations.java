package mint.modules.visual;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.utils.NullUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumHand;

@ModuleInfo(name = "Swing", category = Module.Category.Visual, description = "Tweaks the way your swing looks.")
public class SwingAnimations extends Module {

    public EnumSetting switchSetting = new EnumSetting("Switch", Switch.ONEDOTEIGHT, this);

    public enum Switch {ONEDOTNINE, ONEDOTEIGHT}

    public EnumSetting swing = new EnumSetting("Swing", Swing.MAINHAND, this);

    public enum Swing {MAINHAND, OFFHAND, CANCEL}

    public BooleanSetting speed = new BooleanSetting("Speed", false, this);
    public IntegerSetting amplifier = new IntegerSetting("SpeedVal", 1, 1, 1000, this);

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck()) {
            return;
        }

        if (switchSetting.getValueEnum().equals(Switch.ONEDOTEIGHT) && (double) mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
        }
    }

    public void onTick() {
        if (swing.getValueEnum().equals(Swing.OFFHAND)) {
            mc.player.swingingHand = EnumHand.OFF_HAND;
        } else if (swing.getValueEnum().equals(Swing.MAINHAND)) {
            mc.player.swingingHand = EnumHand.MAIN_HAND;
        } else if (swing.getValueEnum().equals(Swing.CANCEL)) {
            mc.player.isSwingInProgress = false;
            mc.player.swingProgressInt = 0;
            mc.player.swingProgress = 0.0f;
            mc.player.prevSwingProgress = 0.0f;
        }
    }

    @Override
    public void onDisable() {
        mc.player.removePotionEffect(MobEffects.MINING_FATIGUE);
        mc.player.removePotionEffect(MobEffects.HASTE);
    }
}