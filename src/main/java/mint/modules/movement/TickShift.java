package mint.modules.movement;

import com.mojang.authlib.GameProfile;
import mint.events.MoveEvent;
import mint.events.PacketEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.DoubleSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.utils.*;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ModuleInfo(name = "Tick Shift", category = Module.Category.Movement, description = "Sets timer for certain duration")
public class TickShift extends Module {

    public BooleanSetting step = new BooleanSetting("Step", Boolean.valueOf(String.valueOf(new String(ByteBuffer.wrap(String.valueOf(true).getBytes(StandardCharsets.UTF_8)).array()).toCharArray())), this);
    public IntegerSetting timerFactor = new IntegerSetting("Factor", 1, 0, 9, this);

    public BooleanSetting instant = new BooleanSetting("Instant Test", Boolean.valueOf(String.valueOf(new String(ByteBuffer.wrap(String.valueOf(true).getBytes(StandardCharsets.UTF_8)).array()).toCharArray())), this);
    public EnumSetting disableMode = new EnumSetting("Disable", DisableMode.Distance, this);

    public enum DisableMode {Ticks, Distance, None}

    public IntegerSetting ticksVal = new IntegerSetting("Ticks", 12, 1, 100, this, v -> disableMode.getValueEnum().equals(DisableMode.Ticks));
    public DoubleSetting distanceVal = new DoubleSetting("Distance", 3.2d, 0.1d, 15.0d, this, v -> disableMode.getValueEnum().equals(DisableMode.Distance));

    public BooleanSetting blink = new BooleanSetting("Blink", false, this);
    public EnumSetting mode = new EnumSetting("Mode", D.Client, this, v -> blink.getValue());

    public enum D {Client, Server, Both}

    public BooleanSetting renderPlayer = new BooleanSetting("Visualize", false, this, v -> blink.getValue());
    public BooleanSetting test = new BooleanSetting("Phobos Test", false, this, v -> !mode.getValueEnum().equals(D.Server) && blink.getValue());

    Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    EntityOtherPlayerMP fakePlayer;
    BlockPos startPos = null;
    int packetsCanceled = 0;
    int ticks;

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            disable();

        ticks++;
        if (disableMode.getValueEnum().equals(DisableMode.Ticks) && ticks >= ticksVal.getValue())
            disable();

        if (disableMode.getValueEnum().equals(DisableMode.Distance) && startPos != null && mc.player.getDistanceSq(startPos) >= MathUtil.square(distanceVal.getValue()))
            disable();

    }

    @Override
    public void onEnable() {
        if (NullUtil.fullNullCheck())
            disable();

        if (timerFactor.getValue() != 0)
            Timer.resetTimer();

        ticks = 0;
        startPos = mc.player.getPosition();
        if (step.getValue())
            mc.player.stepHeight = 0.6f;

        packetsCanceled = 0;
        if (renderPlayer.getValue() && blink.getValue()) {
            fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(mc.player.getUniqueID(), mc.session.getUsername()));
            fakePlayer.copyLocationAndAnglesFrom(mc.player);
            fakePlayer.rotationYawHead = mc.player.rotationYawHead;
            fakePlayer.inventory = mc.player.inventory;
            mc.world.addEntityToWorld(-555555, fakePlayer);
        }
    }

    @Override
    public void onDisable() {
        if (timerFactor.getValue() != 0)
            Timer.resetTimer();

        ticks = 0;
        startPos = null;
        if (step.getValue())
            mc.player.stepHeight = 0.6f;

        if (renderPlayer.getValue() && blink.getValue())
            try {
                mc.world.removeEntity(fakePlayer);
            } catch (Exception ignored) {
            }

    }

    @SubscribeEvent
    public void onMode(MoveEvent event) {
        if (!isEnabled() || NullUtil.fullNullCheck())
            return;
        if (event.getStage() == 0 && !NullUtil.fullNullCheck() && !mc.player.isSneaking() && !EntityUtil.isInLiquid() && (mc.player.movementInput.moveForward != 0.0f || mc.player.movementInput.moveStrafe != 0.0f) || mc.player.isOnLadder()) {
            if (timerFactor.getValue() != 0) {
                if (InventoryUtil.heldItem(Items.EXPERIENCE_BOTTLE, InventoryUtil.Hand.Both) && mc.player.isHandActive()) {
                    return;
                } else {
                    if (instant.getValue()) {
                        Timer.setTimer(100.0f);
                    } else {
                        switch (timerFactor.getValue()) {
                            case 1: {
                                Timer.setTimer(1.15f);
                                break;
                            }
                            case 2: {
                                Timer.setTimer(1.3f);
                                break;
                            }
                            case 3: {
                                Timer.setTimer(1.45f);
                                break;
                            }
                            case 4: {
                                Timer.setTimer(1.6f);
                                break;
                            }
                            case 5: {
                                Timer.setTimer(1.75f);
                                break;
                            }
                            case 6: {
                                Timer.setTimer(1.9f);
                                break;
                            }
                            case 7: {
                                Timer.setTimer(2.05f);
                                break;
                            }
                            case 8: {
                                Timer.setTimer(2.2f);
                                break;
                            }
                            case 9: {
                                Timer.setTimer(2.35f);
                                break;
                            }
                        }
                    }
                }
            }
            if (step.getValue())
                mc.player.stepHeight = 2.0f;
            final MovementInput movementInput = mc.player.movementInput;
            float moveForward = movementInput.moveForward;
            float moveStrafe = movementInput.moveStrafe;
            float rotationYaw = mc.player.rotationYaw;
            if (moveForward == 0.0 && moveStrafe == 0.0) {
                event.x = (0.0);
                event.z = (0.0);
            } else {
                if (moveForward != 0.0) {
                    if (moveStrafe > 0.0)
                        rotationYaw += ((moveForward > 0.0) ? -45 : 45);
                    else if (moveStrafe < 0.0)
                        rotationYaw += ((moveForward > 0.0) ? 45 : -45);

                    moveStrafe = 0.0f;
                    moveForward = ((moveForward == 0.0f) ? moveForward : ((moveForward > 0.0) ? 1.0f : -1.0f));
                }
                moveStrafe = ((moveStrafe == 0.0f) ? moveStrafe : ((moveStrafe > 0.0) ? 1.0f : -1.0f));
                event.x = (moveForward * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)) + moveStrafe * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)));
                event.z = (moveForward * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)) - moveStrafe * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)));
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!isEnabled() || NullUtil.fullNullCheck())
            return;

        if (event.getStage() == 0 && mc.world != null && !mc.isSingleplayer() && (mode.getValue() != D.Server) && blink.getValue() && isEnabled()) {
            Packet<?> packet = event.getPacket();
            if (test.getValue() && packet instanceof CPacketPlayer) {
                event.setCanceled(true);
                packets.add(packet);
                ++packetsCanceled;
            }
            if (!test.getValue()) {
                if (packet instanceof CPacketChatMessage || packet instanceof CPacketConfirmTeleport || packet instanceof CPacketKeepAlive || packet instanceof CPacketTabComplete || packet instanceof CPacketClientStatus) {
                    return;
                }
                packets.add(packet);
                event.setCanceled(true);
                ++packetsCanceled;
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!isEnabled() || NullUtil.fullNullCheck())
            return;
        if (mc.world != null && !mc.isSingleplayer() && (mode.getValue() != D.Client) && blink.getValue() && isEnabled()) {
            event.setCanceled(true);
        }
    }
}
