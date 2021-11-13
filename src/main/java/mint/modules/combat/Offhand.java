package mint.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.events.RenderOverlayEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.modules.miscellaneous.EntityCrammer;
import mint.settingsrewrite.impl.*;
import mint.utils.*;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zPrestige
 */

@ModuleInfo(name = "Offhand", category = Module.Category.Combat, description = "Changes the item in your offhand.")
public class Offhand extends Module {

    public ParentSetting itemParent = new ParentSetting("Items", true, this);
    public BooleanSetting crystal = new BooleanSetting("Crystal", false, this, v -> itemParent.getValue());
    public BooleanSetting crystalOnSword = new BooleanSetting("Sword Crystal", false, this, v -> !crystal.getValue() && itemParent.getValue());
    public BooleanSetting crystalOnPickaxe = new BooleanSetting("Pickaxe Crystal", false, this, v -> !crystal.getValue() && itemParent.getValue());

    public ParentSetting miscParent = new ParentSetting("Misc", true, this);
    public IntegerSetting switchDelay = new IntegerSetting("Switch Delay", 50, 0, 200, this, v -> miscParent.getValue());
    public BooleanSetting fallBack = new BooleanSetting("FallBack", false, this, v -> miscParent.getValue());

    public ParentSetting healthParent = new ParentSetting("Health", true, this);
    public FloatSetting totemHealth = new FloatSetting("Totem Health", 10f, 0f, 20f, this, v -> healthParent.getValue());
    public BooleanSetting hole = new BooleanSetting("Hole Check", false, this, v -> healthParent.getValue());
    public FloatSetting holeHealth = new FloatSetting("Totem Hole Health", 10f, 0f, 20f, this, v -> healthParent.getValue() && hole.getValue());

    public ParentSetting fallDistanceParent = new ParentSetting("Fall Distance", true, this);
    public BooleanSetting fallDistance = new BooleanSetting("Fall Distance Check", false, this, v -> fallDistanceParent.getValue());
    public FloatSetting minDistance = new FloatSetting("Min Distance", 10f, 1f, 100f, this, v -> fallDistance.getValue() && fallDistanceParent.getValue());

    public ParentSetting gappleParent = new ParentSetting("Gapple", true, this);
    public BooleanSetting gapple = new BooleanSetting("Gapple Switch", false, this, v -> gappleParent.getValue());
    public BooleanSetting rightClick = new BooleanSetting("Right Click Only", false, this, v -> gapple.getValue() && gappleParent.getValue());

    public ParentSetting visualParent = new ParentSetting("Visual", true, this);
    public EnumSetting render = new EnumSetting("RenderMode", RenderMode.ALWAYS, this, v -> visualParent.getValue());

    public enum RenderMode {ALWAYS, ONSWITCH, NEVER}


    Timer switchTimer = new Timer();
    HashMap<String, Integer> renderString = new HashMap();

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        int offhandSlot = InventoryUtil.getItemSlot(getOffhandItem());
        if (mc.player.getHeldItemOffhand().getItem() != getOffhandItem() && offhandSlot != -1 && switchTimer.passedMs(switchDelay.getValue())) {
            switchItem(offhandSlot < 9 ? offhandSlot + 36 : offhandSlot);
            mc.playerController.updateController();
            switchTimer.reset();
            if (!render.getValueEnum().equals(RenderMode.NEVER)) {
                renderString.clear();
                renderString.put(getRendererString(), 255);
            }
        }
    }

    void switchItem(int slot) {
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
    }

    public void renderOverlayEvent(RenderOverlayEvent event) {
        int screenWidth = new ScaledResolution(mc).getScaledWidth();
        for (Map.Entry<String, Integer> entry : renderString.entrySet()) {
            if (render.getValueEnum().equals(RenderMode.ALWAYS)) {
                Mint.textManager.drawStringWithShadow("Current Item: " + ChatFormatting.BOLD + entry.getKey(), (screenWidth / 2f) - (Mint.textManager.getStringWidth("Current Item: " + entry.getKey()) / 2f), 0, ColorUtil.toRGBA(255, 255, 255, entry.getValue()));
            } else if (render.getValueEnum().equals(RenderMode.ONSWITCH)) {
                renderString.put(entry.getKey(), entry.getValue() - 1);
                if (entry.getValue() <= 0) {
                    renderString.remove(entry.getKey());
                    return;
                }
                Mint.textManager.drawStringWithShadow("Offhand switched to Item: " + ChatFormatting.BOLD + entry.getKey(), (screenWidth / 2f) - (Mint.textManager.getStringWidth("Offhand switched to Item: " + entry.getKey()) / 2f), 0, ColorUtil.toRGBA(255, 255, 255, entry.getValue()));
            }
        }
    }

    Item getOffhandItem() {
        if (EntityCrammer.getInstance().isEnabled() && EntityCrammer.getInstance().target != null && mc.world.getBlockState(EntityCrammer.getInstance().targetPos).getBlock().equals(Blocks.RAIL))
            return Items.MINECART;
        if (hole.getValue()) {
            if (!EntityUtil.isPlayerSafe(mc.player) && EntityUtil.getHealth(mc.player) < totemHealth.getValue())
                return Items.TOTEM_OF_UNDYING;

            if (EntityUtil.isPlayerSafe(mc.player) && EntityUtil.getHealth(mc.player) < holeHealth.getValue())
                return Items.TOTEM_OF_UNDYING;

        } else if (EntityUtil.getHealth(mc.player) < totemHealth.getValue())
            return Items.TOTEM_OF_UNDYING;

        if (gapple.getValue() && ((rightClick.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD && mc.gameSettings.keyBindUseItem.isKeyDown()) || (!rightClick.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD)))
            return Items.GOLDEN_APPLE;

        if (fallDistance.getValue() && mc.player.fallDistance > minDistance.getValue())
            return Items.TOTEM_OF_UNDYING;

        if (!crystal.getValue() || (fallBack.getValue() && InventoryUtil.getStackCount(Items.END_CRYSTAL) == 0))
            return Items.TOTEM_OF_UNDYING;

        if (!crystal.getValue() && crystalOnSword.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD)
            return Items.END_CRYSTAL;

        if (!crystal.getValue() && crystalOnPickaxe.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE)
            return Items.END_CRYSTAL;

        if (crystal.getValue())
            return Items.END_CRYSTAL;

        return Items.TOTEM_OF_UNDYING;
    }

    String getRendererString() {
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL)
            return "End Crystal";

        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
            return "Totem";

        if (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE)
            return "Golden Apple";

        if (mc.player.getHeldItemOffhand().getItem() == Items.MINECART)
            return "Minecart";

        return "";
    }
}
