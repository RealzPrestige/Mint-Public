package mint.modules.movement;

import com.mojang.authlib.GameProfile;
import mint.events.PacketEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.*;
import mint.utils.EntityUtil;
import mint.utils.MathUtil;
import mint.utils.NullUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * tpc rewrite(sixet owns me and all) / tickshift type beat shit
 * some shit is pasted
 */

@ModuleInfo(name = "SSS", category = Module.Category.Movement, description = "idk man i didnt make this im just writing the descriptions")
public class SSS extends Module {

    public BooleanSetting movementParent = new BooleanSetting("Movement", false, this);

    public EnumSetting moveType = new EnumSetting("MoveType", MoveType.YPort, this, v -> movementParent.getValue());

    public BooleanSetting step = new BooleanSetting("Step", true, this, v -> movementParent.getValue());
    public DoubleSetting yPortSpeed = new DoubleSetting("YPortSpeed", 0.1d, 0.0d, 1.0d, this, v -> movementParent.getValue() && moveType.getValueEnum().equals(MoveType.YPort));
    public FloatSetting fallSpeed = new FloatSetting("FallSpeed", 0.8f, 0.1f, 9.0f, this, v -> movementParent.getValue() && moveType.getValueEnum().equals(MoveType.YPort));
    public IntegerSetting yMotion = new IntegerSetting("YMotion", 390, 350, 420, this, v -> movementParent.getValue() && moveType.getValueEnum().equals(MoveType.YPort));


    public BooleanSetting playerParent = new BooleanSetting("Player", true, this);

    public EnumSetting playerType = new EnumSetting("Type", PlayerType.Blink, this, v -> playerParent.getValue());

    //blink
    public EnumSetting mode = new EnumSetting("Mode", Mode.Both, this, v -> playerParent.getValue() && playerType.getValueEnum().equals(PlayerType.Blink));
    public BooleanSetting renderPlayer = new BooleanSetting("Visualize", false, this, v -> playerParent.getValue() && playerType.getValueEnum().equals(PlayerType.Blink));
    public EnumSetting disableMode = new EnumSetting("Disable", DisableMode.Distance, this, v -> playerParent.getValue() && playerType.getValueEnum().equals(PlayerType.Blink));
    public IntegerSetting ticksVal = new IntegerSetting("Ticks", 20, 1, 100, this, v -> playerParent.getValue() && playerType.getValueEnum().equals(PlayerType.Blink) && disableMode.getValueEnum().equals(DisableMode.Ticks));
    public DoubleSetting distanceVal = new DoubleSetting("Distance", 3.2d, 0.1d, 15.0d, this, v -> playerParent.getValue() && playerType.getValueEnum().equals(PlayerType.Blink) && disableMode.getValueEnum().equals(DisableMode.Distance));


    //something else
    EntityOtherPlayerMP fakePlayer;
    BlockPos startPos = null;
    int ticks;

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            disable();
        ticks++;
        if (disableMode.getValueEnum().equals(DisableMode.Ticks) && ticks >= ticksVal.getValue()) {
            disable();
        }
        if (disableMode.getValueEnum().equals(DisableMode.Distance) && startPos != null && mc.player.getDistanceSq(startPos) >= MathUtil.square(distanceVal.getValue())) {
            disable();
        }
        if (moveType.getValueEnum().equals(MoveType.YPort)) {
            if (mc.player.isSneaking() || EntityUtil.isInLiquid() || mc.player.isOnLadder()) {
                return;
            }
            if (step.getValue()) {
                mc.player.stepHeight = 2.0f;
            }
            if (mc.player.onGround) {
                mc.player.motionY = yMotion.getValue() / 1000.0f;
                EntityUtil.setSpeed(mc.player, EntityUtil.getDefaultMoveSpeed() + yPortSpeed.getValue());
            } else {
                for (double y = 0.0; y < 2.5 + 0.5; y += 0.01) {
                    if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                        mc.player.motionY = -fallSpeed.getValue();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        if (NullUtil.fullNullCheck())
            return;

        ticks = 0;
        startPos = mc.player.getPosition();
        mc.player.stepHeight = 0.6f;
        if (renderPlayer.getValue() && playerType.getValueEnum().equals(PlayerType.Blink)) {
            fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(mc.player.getUniqueID(), mc.session.getUsername()));
            fakePlayer.copyLocationAndAnglesFrom(mc.player);
            fakePlayer.rotationYawHead = mc.player.rotationYawHead;
            fakePlayer.inventory = mc.player.inventory;
            fakePlayer.setHealth(EntityUtil.getHealth(mc.player));
            mc.world.addEntityToWorld(-555555, fakePlayer);
        }
    }

    @Override
    public void onDisable() {
        ticks = 0;
        startPos = null;
        mc.player.stepHeight = 0.6f;
        if (renderPlayer.getValue() && playerType.getValueEnum().equals(PlayerType.Blink)) {
            try {
                mc.world.removeEntity(fakePlayer);
            } catch (Exception ignored) {
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!isEnabled())
            return;
        if (isEnabled() && playerType.getValueEnum().equals(PlayerType.Blink) && !mode.getValueEnum().equals(Mode.Server)) {
            event.setCanceled(true); // or add == client || == both
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!isEnabled())
            return;
        if (isEnabled() && playerType.getValueEnum().equals(PlayerType.Blink) && !mode.getValueEnum().equals(Mode.Client)) {
            event.setCanceled(true);
        }
    }

    //playerParent
    public enum PlayerType {
        Blink
    }

    public enum Mode {
        Both,
        Server,
        Client
    }

    public enum DisableMode {
        Ticks,
        Distance,
        None
    }

    //movementParent
    public enum MoveType {
        YPort,
        Angle
    }
}