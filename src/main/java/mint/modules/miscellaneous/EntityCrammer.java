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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

@ModuleInfo(name = "Entity Crammer", category = Module.Category.Miscellaneous, description = "Crams entities to fuck ur opp")
public class EntityCrammer extends Module {
    public static EntityCrammer INSTANCE = new EntityCrammer();
    public FloatSetting targetRange = new FloatSetting("Target Range", 10.0f, 0.0f, 15.0f, this);
    public FloatSetting placeRange = new FloatSetting("Place Range", 5.0f, 0.0f, 6.0f, this);
    public IntegerSetting startDelay = new IntegerSetting("Start Rail Delay", 100, 0, 1000, this);
    public IntegerSetting cartPlaceDelay = new IntegerSetting("Cart Place Delay", 100, 0, 1000, this);
    public BooleanSetting packet = new BooleanSetting("Packet", false, this);
    public BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    public BooleanSetting swing = new BooleanSetting("Swing", false, this);
    public EnumSetting swingMode = new EnumSetting("Swing Mode", SwingHand.MAINHAND, this, v -> swing.getValue());

    public enum SwingHand {MAINHAND, OFFHAND, PACKET}

    Timer timer = new Timer();
    Timer startTimer = new Timer();
    public EntityPlayer target;
    public BlockPos targetPos;

    public EntityCrammer() {
        setInstance();
    }


    public static EntityCrammer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EntityCrammer();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }


    public void onEnable() {
        startTimer.reset();
    }

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        target = EntityUtil.getTarget(targetRange.getValue());
        if (target == null)
            return;
        targetPos = PlayerUtil.getPlayerPos(target);

        if (!startTimer.passedMs(startDelay.getValue()))
            return;

        if (mc.player.getDistanceSq(targetPos) > placeRange.getValue())
            return;

        if (mc.world.getBlockState(targetPos).getBlock().equals(Blocks.AIR)) {
            if (timer.passedMs(cartPlaceDelay.getValue())) {
                placeBlockWithSwitchRail(targetPos);
                timer.reset();
            }
        }

        if (!mc.player.getHeldItemOffhand().getItem().equals(Items.MINECART) && timer.passedMs(cartPlaceDelay.getValue())) {
            MessageManager.sendMessage(ChatFormatting.BOLD + "Entity Crammer: " + ChatFormatting.RESET + "No minecart found in your Offhand, make sure to use " + ChatFormatting.AQUA + "Mint" + ChatFormatting.RESET + " Offhand, toggling!");
            disable();
        }

        if (timer.passedMs(cartPlaceDelay.getValue()) && mc.world.getBlockState(targetPos).getBlock().equals(Blocks.RAIL)) {
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(targetPos, EnumFacing.UP, EnumHand.OFF_HAND, 0.5f, 0.5f, 0.5f));
            timer.reset();
        }
    }

    void placeBlockWithSwitchRail(BlockPos pos) {
        int currentSlot = mc.player.inventory.currentItem;
        if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.RAIL)) == -1) {
            MessageManager.sendMessage(ChatFormatting.BOLD + "Entity Crammer: " + ChatFormatting.RESET + "Out of rails, toggling!");
            disable();
        }
        InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.RAIL)));
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
}
