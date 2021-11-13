package mint.modules.movement;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.settingsrewrite.impl.FloatSetting;
import mint.utils.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;

/**
 * @author Zenov
 * @author kambing
 * catalyst for ncp
 */

@ModuleInfo(name = "Step", category = Module.Category.Movement, description = "Allows you to step up blocks.")
public class Step extends Module {
    public int ticks;
    public static Step INSTANCE = new Step();
    public EnumSetting mode = new EnumSetting("Mode", Mode.Vanilla, this);

    public enum Mode {Vanilla, Timer, NCP}

    public BooleanSetting cancelLiquids = new BooleanSetting("Pause In Liquids", true, this);
    public FloatSetting height = new FloatSetting("Height", 2.0f, 1.0f, 4.0f, this);

    public Step() {
        this.setInstance();
    }

    public static Step getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Step();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onEnable() {
        ticks = 0;
    }

    public void onDisable() {
        if (mc.player.stepHeight != 0.6f)
            mc.player.stepHeight = 0.6f;
    }

    public void onUpdate() {
        if (cancelLiquids.getValue() && EntityUtil.isInLiquid())
            return;

        if (mode.getValueEnum().equals(Mode.Vanilla))
            mc.player.stepHeight = height.getValue();
        else if (mode.getValueEnum().equals(Mode.NCP)) {
            final double[] forward = forward(0.1);
            boolean b = false;
            boolean b2 = false;
            boolean b3 = false;
            boolean b4 = false;
            if (Step.mc.world.getCollisionBoxes(Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 2.6, forward[1])).isEmpty() && !Step.mc.world.getCollisionBoxes(Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 2.4, forward[1])).isEmpty()) {
                b = true;
            }
            if (Step.mc.world.getCollisionBoxes(Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 2.1, forward[1])).isEmpty()) {
                if (!Step.mc.world.getCollisionBoxes(Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 1.9, forward[1])).isEmpty())
                    b2 = true;

            }
            if (Step.mc.world.getCollisionBoxes(Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 1.6, forward[1])).isEmpty() && !Step.mc.world.getCollisionBoxes(Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 1.4, forward[1])).isEmpty())
                b3 = true;

            if (Step.mc.world.getCollisionBoxes(Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 1.0, forward[1])).isEmpty() && !Step.mc.world.getCollisionBoxes(Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 0.6, forward[1])).isEmpty())
                b4 = true;

            if (Step.mc.player.collidedHorizontally && (Step.mc.player.moveForward != 0.0f || Step.mc.player.moveStrafing != 0.0f)) {
                if (Step.mc.player.onGround) {
                    if (b4 && this.height.getValue() >= 1.0) {
                        final double[] array = {0.42, 0.753};
                        for (int length = array.length, i = 0; i < length; ++i) {
                            Step.mc.player.connection.sendPacket(new CPacketPlayer.Position(Step.mc.player.posX, Step.mc.player.posY + array[i], Step.mc.player.posZ, Step.mc.player.onGround));
                        }
                        Step.mc.player.setPosition(Step.mc.player.posX, Step.mc.player.posY + 1.0, Step.mc.player.posZ);
                        this.ticks = 1;
                    }
                    if (b3 && this.height.getValue() >= 1.5) {
                        final double[] array2 = {0.42, 0.75, 1.0, 1.16, 1.23, 1.2};
                        for (double v : array2) {
                            Step.mc.player.connection.sendPacket(new CPacketPlayer.Position(Step.mc.player.posX, Step.mc.player.posY + v, Step.mc.player.posZ, Step.mc.player.onGround));
                        }
                        Step.mc.player.setPosition(Step.mc.player.posX, Step.mc.player.posY + 1.5, Step.mc.player.posZ);
                        this.ticks = 1;
                    }
                    if (b2 && this.height.getValue() >= 2.0) {
                        final double[] array3 = {0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
                        for (double v : array3) {
                            Step.mc.player.connection.sendPacket(new CPacketPlayer.Position(Step.mc.player.posX, Step.mc.player.posY + v, Step.mc.player.posZ, Step.mc.player.onGround));
                        }
                        Step.mc.player.setPosition(Step.mc.player.posX, Step.mc.player.posY + 2.0, Step.mc.player.posZ);
                        this.ticks = 2;
                    }
                    if (b && this.height.getValue() >= 2.5) {
                        final double[] array4 = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
                        for (double v : array4) {
                            Step.mc.player.connection.sendPacket(new CPacketPlayer.Position(Step.mc.player.posX, Step.mc.player.posY + v, Step.mc.player.posZ, Step.mc.player.onGround));
                        }
                        Step.mc.player.setPosition(Step.mc.player.posX, Step.mc.player.posY + 2.5, Step.mc.player.posZ);
                        this.ticks = 2;
                    }
                }
            }

        } else if (mode.getValueEnum().equals(Mode.Timer)) {
            mc.player.stepHeight = height.getValue();
            if (Step.mc.player.collidedHorizontally && (Step.mc.player.moveForward != 0.0f || Step.mc.player.moveStrafing != 0.0f))
                mc.timer.tickLength = 50.0f / 0.6f;
            else
                mc.timer.tickLength = 50.0f;

        }
    }

    public static double[] forward(final double kambingdarealnigga) {
        float moveForward = Minecraft.getMinecraft().player.movementInput.moveForward;
        float moveStrafe = Minecraft.getMinecraft().player.movementInput.moveStrafe;
        float n2 = Minecraft.getMinecraft().player.prevRotationYaw + (Minecraft.getMinecraft().player.rotationYaw - Minecraft.getMinecraft().player.prevRotationYaw) * Minecraft.getMinecraft().getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                n2 += ((moveForward > 0.0f) ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                n2 += ((moveForward > 0.0f) ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(n2 + 90.0f));
        final double cos = Math.cos(Math.toRadians(n2 + 90.0f));
        return new double[]{moveForward * kambingdarealnigga * cos + moveStrafe * kambingdarealnigga * sin, moveForward * kambingdarealnigga * sin - moveStrafe * kambingdarealnigga * cos};
    }
}