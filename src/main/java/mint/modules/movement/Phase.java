package mint.modules.movement;

import mint.events.MoveEvent;
import mint.events.UpdateWalkingPlayerEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.DoubleSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.utils.EntityUtil;
import mint.utils.MathUtil;
import mint.utils.NullUtil;
import mint.utils.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

@ModuleInfo(name = "Phase", category = Module.Category.Movement, description = "Lets you phase through blocks.")
public class Phase extends Module {
    static Phase INSTANCE = new Phase();

    public DoubleSetting factor = new DoubleSetting("Factor", 5.0, 0.1, 25.0, this);
    public IntegerSetting delay = new IntegerSetting("UpdateDelay", 849, 0, 2000, this);
    Timer timer = new Timer();
    Timer timer2 = new Timer();
    double x;
    double z;

    public Phase() {
        this.setInstance();
    }

    public static Phase getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Phase();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onToggle() {
        if (NullUtil.fullNullCheck())
            return;
        x = 1;
        z = 1;
        timer.reset();
        timer2.reset();
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (NullUtil.fullNullCheck() || !isEnabled() || !mc.gameSettings.keyBindSneak.isKeyDown())
            return;

        double[] dir = MathUtil.directionSpeed(factor.getValue() / 100);
        event.x = (dir[0] * 0.20000000298023224);
        event.y = (0);
        event.z = (dir[1] * 0.20000000298023224);
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (NullUtil.fullNullCheck() || !isEnabled() || mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.0625, 0.0, -0.0625)).isEmpty() || !mc.gameSettings.keyBindSneak.isKeyDown())
            return;

        if (mc.gameSettings.keyBindSneak.isKeyDown())
            EntityUtil.stopSneaking(false);


        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY + 0.2, mc.player.posZ);

        if (timer.passedMs(delay.getValue()) && (mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST)) {
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            mc.player.setLocationAndAngles(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1339.2, mc.player.posZ, true));
            timer.reset();
        }

        mc.player.setVelocity(0.0, 0.0, 0.0);
        mc.player.noClip = true;

        if (mc.player.ticksExisted % 10 != 0)
            return;

        x = Math.cos(Math.toRadians(mc.player.rotationYaw + 90.0f));
        z = Math.sin(Math.toRadians(mc.player.rotationYaw + 90.0f));
        mc.player.setEntityBoundingBox(mc.player.getEntityBoundingBox().offset(0.00625 * x, 0.0, 0.00625 * z));
        mc.player.motionY = 0;
    }
}