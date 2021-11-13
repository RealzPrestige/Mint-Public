package mint.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.events.RenderWorldEvent;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.*;
import mint.utils.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

@ModuleInfo(name = "Waller", category = Module.Category.Combat, description = "Walls people ye")
public class Waller extends Module {
    public FloatSetting targetRange = new FloatSetting("Target Range", 10.0f, 0.0f, 15.0f, this);
    public FloatSetting posDistance = new FloatSetting("Pos Distance", 1.0f, 0.0f, 10.0f, this);
    public FloatSetting placeRange = new FloatSetting("Place Range", 5.0f, 0.0f, 6.0f, this);
    public IntegerSetting placeDelay = new IntegerSetting("Place Delay", 100, 0, 1000, this);
    public BooleanSetting packet = new BooleanSetting("Packet", false, this);
    public BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    public BooleanSetting swing = new BooleanSetting("Swing", false, this);
    public EnumSetting swingMode = new EnumSetting("Swing Mode", SwingHand.MAINHAND, this, v -> swing.getValue());

    public enum SwingHand {MAINHAND, OFFHAND, PACKET}

    public BooleanSetting render = new BooleanSetting("Render", false, this);
    public BooleanSetting text = new BooleanSetting("Text", false, this, v -> render.getValue());
    public ColorSetting color = new ColorSetting("Color", new Color(-1), this, v -> render.getValue());
    public IntegerSetting duration = new IntegerSetting("Duration", 2000, 0, 5000, this, v -> render.getValue());

    Timer timer = new Timer();
    Timer renderRemove = new Timer();
    BlockPos renderPos;
    EntityPlayer target;

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;
        target = EntityUtil.getTarget(targetRange.getValue());

        if (target == null)
            return;

        BlockPos pos = PlayerUtil.getPlayerPos(target);
        if (target.onGround) {
            if (!mc.world.getBlockState(pos.north().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.north().up().up()).getBlock().equals(Blocks.AIR) && timer.passedMs(placeDelay.getValue()) && mc.player.getDistance(pos.north().up().up().getX(), pos.north().up().up().getY(), pos.north().up().up().getZ()) < placeRange.getValue() && target.getDistanceSq(pos.north()) < posDistance.getValue()) {
                placeBlockWithSwitch(pos.north().up().up());
                renderPos = pos.north().up().up();
                timer.reset();
                renderRemove.reset();
            }
            if (!mc.world.getBlockState(pos.east().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.east().up().up()).getBlock().equals(Blocks.AIR) && timer.passedMs(placeDelay.getValue()) && mc.player.getDistance(pos.east().up().up().getX(), pos.east().up().up().getY(), pos.east().up().up().getZ()) < placeRange.getValue() && target.getDistanceSq(pos.east()) < posDistance.getValue()) {
                placeBlockWithSwitch(pos.east().up().up());
                renderPos = pos.east().up().up();
                timer.reset();
                renderRemove.reset();
            }
            if (!mc.world.getBlockState(pos.south().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.south().up().up()).getBlock().equals(Blocks.AIR) && timer.passedMs(placeDelay.getValue()) && mc.player.getDistance(pos.south().up().up().getX(), pos.south().up().up().getY(), pos.south().up().up().getZ()) < placeRange.getValue() && target.getDistanceSq(pos.south()) < posDistance.getValue()) {
                placeBlockWithSwitch(pos.south().up().up());
                renderPos = pos.south().up().up();
                timer.reset();
                renderRemove.reset();
            }
            if (!mc.world.getBlockState(pos.west().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.west().up().up()).getBlock().equals(Blocks.AIR) && timer.passedMs(placeDelay.getValue()) && mc.player.getDistance(pos.west().up().up().getX(), pos.west().up().up().getY(), pos.west().up().up().getZ()) < placeRange.getValue() && target.getDistanceSq(pos.west()) < posDistance.getValue()) {
                placeBlockWithSwitch(pos.west().up().up());
                renderPos = pos.west().up().up();
                timer.reset();
                renderRemove.reset();
            }
        }
    }

    void placeBlockWithSwitch(BlockPos pos) {
        int currentSlot = mc.player.inventory.currentItem;
        if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1)
            disableModule();
        InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)));
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
        if (swing.getValue())
            swingArm();
        mc.player.inventory.currentItem = currentSlot;
        mc.playerController.updateController();
    }

    public void swingArm() {
        if (swingMode.getValueEnum().equals(SwingHand.MAINHAND))
            mc.player.swingArm(EnumHand.MAIN_HAND);
        else if (swingMode.getValueEnum().equals(SwingHand.OFFHAND))
            mc.player.swingArm(EnumHand.OFF_HAND);
        else if (swingMode.getValueEnum().equals(SwingHand.PACKET))
            mc.player.connection.sendPacket(new CPacketAnimation(mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));

    }

    void disableModule() {
        MessageManager.sendMessage(ChatFormatting.BOLD + "Waller: " + ChatFormatting.RESET + "Disabled due to missing blocks!");
        disable();
    }

    public void renderWorldLastEvent(RenderWorldEvent event) {
        if (!render.getValue())
            return;

        if (renderPos == null)
            return;

        if (!renderRemove.passedMs(duration.getValue()))
            return;

        RenderUtil.drawBox(renderPos, color.getColor());

        if (text.getValue())
            RenderUtil.drawText(renderPos, "Waller", -1);
    }
}
