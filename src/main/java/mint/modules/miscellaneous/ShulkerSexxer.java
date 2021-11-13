package mint.modules.miscellaneous;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.settingsrewrite.impl.FloatSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.utils.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

@ModuleInfo(name = "Shulker Sexxer", category = Module.Category.Miscellaneous, description = "Sexxes Shulkers")
public class ShulkerSexxer extends Module {

    public EnumSetting sexType = new EnumSetting("Sex Type", SexType.BREAK, this);

    enum SexType {BREAK, OPEN}

    public FloatSetting range = new FloatSetting("Range", 5.0f, 0.0f, 6.0f, this);
    public IntegerSetting delay = new IntegerSetting("Delay", 100, 0, 1000, this);
    public BooleanSetting safeOnly = new BooleanSetting("Safe Only", false, this);
    public BooleanSetting debug = new BooleanSetting("Debug", false, this);

    Timer breakTimer = new Timer();
    Timer openTimer = new Timer();

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        if (safeOnly.getValue() && !EntityUtil.isPlayerSafe(mc.player))
            return;

        if (mc.currentScreen != null)
            openTimer.reset();

        int pickSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
        int currentItem = mc.player.inventory.currentItem;

        for (BlockPos pos : BlockUtil.getSphere(range.getValue(), true)) {
            if (mc.world.getBlockState(pos).getBlock().equals(Blocks.BLACK_SHULKER_BOX)
                    || mc.world.getBlockState(pos).getBlock().equals(Blocks.BLUE_SHULKER_BOX)
                    || mc.world.getBlockState(pos).getBlock().equals(Blocks.BROWN_SHULKER_BOX)
                    || mc.world.getBlockState(pos).getBlock().equals(Blocks.SILVER_SHULKER_BOX)
                    || mc.world.getBlockState(pos).getBlock().equals(Blocks.CYAN_SHULKER_BOX)
                    || mc.world.getBlockState(pos).getBlock().equals(Blocks.GRAY_SHULKER_BOX)
                    || mc.world.getBlockState(pos).getBlock().equals(Blocks.GREEN_SHULKER_BOX)
                    || mc.world.getBlockState(pos).getBlock().equals(Blocks.LIGHT_BLUE_SHULKER_BOX)
                    || mc.world.getBlockState(pos).getBlock().equals(Blocks.LIME_SHULKER_BOX)
                    || mc.world.getBlockState(pos).getBlock().equals(Blocks.MAGENTA_SHULKER_BOX)
                    || mc.world.getBlockState(pos).getBlock().equals(Blocks.PINK_SHULKER_BOX)
                    || mc.world.getBlockState(pos).getBlock().equals(Blocks.ORANGE_SHULKER_BOX)
                    || mc.world.getBlockState(pos).getBlock().equals(Blocks.PURPLE_SHULKER_BOX)
                    || mc.world.getBlockState(pos).getBlock().equals(Blocks.RED_SHULKER_BOX)
                    || mc.world.getBlockState(pos).getBlock().equals(Blocks.WHITE_SHULKER_BOX)
                    || mc.world.getBlockState(pos).getBlock().equals(Blocks.YELLOW_SHULKER_BOX)) {
                if (!sexType.getValueEnum().equals(SexType.OPEN)) {
                    if (pickSlot != -1)
                        InventoryUtil.switchToSlot(pickSlot);
                    else {
                        MessageManager.sendMessage(ChatFormatting.BOLD + " Shulker Sexxer: " + ChatFormatting.RESET + "No " + ChatFormatting.RED + "Pickaxe" + ChatFormatting.RESET + " found, toggling!");
                        disable();
                    }
                }
                if (breakTimer.passedMs(delay.getValue())) {
                    if (sexType.getValueEnum().equals(SexType.BREAK)) {
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
                        if (debug.getValue())
                            MessageManager.sendMessage(ChatFormatting.BOLD + " Shulker Sexxer: " + ChatFormatting.RESET + "Position found at:" + ChatFormatting.AQUA + " X: " + ChatFormatting.RESET + ChatFormatting.BOLD + MathUtil.round(pos.getX(), 0) + ChatFormatting.RESET + "," + ChatFormatting.AQUA + " Y: " + ChatFormatting.RESET + ChatFormatting.BOLD + MathUtil.round(pos.getY(), 0) + ChatFormatting.RESET + "," + ChatFormatting.AQUA + " Z: " + ChatFormatting.RESET + ChatFormatting.BOLD + MathUtil.round(pos.getZ(), 0) + ChatFormatting.RESET + "!");
                        breakTimer.reset();
                    } else if (sexType.getValueEnum().equals(SexType.OPEN) && openTimer.passedMs(delay.getValue())) {
                        if (mc.currentScreen == null)
                            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                    }
                    if (sexType.getValueEnum().equals(SexType.OPEN)) {
                        mc.player.inventory.currentItem = currentItem;
                        mc.playerController.updateController();
                    }
                }
            }
        }
    }
}
