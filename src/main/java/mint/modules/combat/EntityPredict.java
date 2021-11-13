package mint.modules.combat;

import mint.events.PacketEvent;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.utils.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Entity Predict", category = Module.Category.Combat, description = "Predicts entityids and attacks them.")
public class EntityPredict extends Module {
    public IntegerSetting attackAmount = new IntegerSetting("Attack Amount", 1, 1, 5, this);
    public BooleanSetting holdingCrystalOnly = new BooleanSetting("Holding Crystal Only", false, this);
    public BooleanSetting render = new BooleanSetting("Render", false, this);
    public BooleanSetting down = new BooleanSetting("Down", false, this, v -> render.getValue());

    int currentId;
    BlockPos currentPos;
    Entity entity;

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!isEnabled())
            return;

        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            int entityId = 0;
            CPacketPlayerTryUseItemOnBlock packet = event.getPacket();

            if (!mc.world.getBlockState(packet.position).getBlock().equals(Blocks.OBSIDIAN) && !mc.world.getBlockState(packet.position).getBlock().equals(Blocks.BEDROCK))
                return;

            if (holdingCrystalOnly.getValue() && !(mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) || mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL)))
                return;

            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityEnderCrystal) {
                    if (entity.entityId > entityId)
                        entityId = entity.entityId;

                    this.entity = entity;
                }
            }
            currentPos = packet.getPos();
            if (entity != null) {
                switch (attackAmount.getValue()) {
                    case 1:
                        attackEntity(entityId + 1);
                        break;
                    case 2:
                        attackEntity(entityId + 1);
                        attackEntity(entityId + 2);
                        break;
                    case 3:
                        attackEntity(entityId + 1);
                        attackEntity(entityId + 2);
                        attackEntity(entityId + 3);
                        break;
                    case 4:
                        attackEntity(entityId + 1);
                        attackEntity(entityId + 2);
                        attackEntity(entityId + 3);
                        attackEntity(entityId + 4);
                        break;
                    case 5:
                        attackEntity(entityId + 1);
                        attackEntity(entityId + 2);
                        attackEntity(entityId + 3);
                        attackEntity(entityId + 4);
                        attackEntity(entityId + 5);
                        break;

                }
            }
        }
    }

    void attackEntity(int entityId) {
        CPacketUseEntity packet = new CPacketUseEntity();
        packet.entityId = entityId;
        packet.action = CPacketUseEntity.Action.ATTACK;
        mc.player.connection.sendPacket(packet);
        currentId = entityId;
    }

    public void renderWorldLastEvent(RenderWorldEvent event) {
        if (currentPos == null || !render.getValue())
            return;

        RenderUtil.drawText(down.getValue() ? currentPos : currentPos.up(), "Id: " + currentId, -1);
    }
}
