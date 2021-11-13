package mint.modules.visual;

import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.ColorSetting;
import mint.settingsrewrite.impl.FloatSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.utils.ColorUtil;
import mint.utils.NullUtil;
import mint.utils.RenderUtil;
import mint.utils.Timer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;

@ModuleInfo(name = "Player Trails", category = Module.Category.Visual, description = "Draws a line behind you (Breadcrumbs)")
public class PlayerTrails extends Module {

    public FloatSetting lineWidth = new FloatSetting("Line Width", 2.0f, 0.1f, 5.0f, this);
    public BooleanSetting fade = new BooleanSetting("Fade", false, this);

    public IntegerSetting selfTime = new IntegerSetting("Remove Delay", 1000, 0, 2000, this);

    public ColorSetting startColor = new ColorSetting("Start Color", new Color(-1), this);

    public ColorSetting endColor = new ColorSetting("End Color", new Color(-1), this);

    Map<UUID, ItemTrail> trails = new HashMap<>();


    public void onTick() {
        if (!isEnabled() || NullUtil.fullNullCheck())
            return;

        if (trails.containsKey(mc.player.getUniqueID())) {
            final ItemTrail playerTrail = trails.get(mc.player.getUniqueID());
            playerTrail.timer.reset();
            final List<Position> toRemove = new ArrayList<>();
            for (final Position position : playerTrail.positions)
                if (System.currentTimeMillis() - position.time > selfTime.getValue().longValue())
                    toRemove.add(position);
            playerTrail.positions.removeAll(toRemove);
            playerTrail.positions.add(new Position(mc.player.getPositionVector()));
        } else trails.put(mc.player.getUniqueID(), new ItemTrail(mc.player));

    }

    @Override
    public void renderWorldLastEvent(RenderWorldEvent event) {
        if (!isEnabled() || NullUtil.fullNullCheck())
            return;

        for (final Map.Entry<UUID, ItemTrail> entry : trails.entrySet()) {
            if (entry.getValue().entity.isDead || mc.world.getEntityByID(entry.getValue().entity.getEntityId()) == null) {
                if (entry.getValue().timer.isPaused())
                    entry.getValue().timer.reset();

                entry.getValue().timer.setPaused(false);
            }
            if (!entry.getValue().timer.isPassed())
                drawTrail(entry.getValue());

        }
    }

    void drawTrail(final ItemTrail trail) {
        final Color fadeColor = endColor.getColor();
        RenderUtil.prepare();
        GL11.glLineWidth(lineWidth.getValue());
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        (RenderUtil.builder = RenderUtil.tessellator.getBuffer()).begin(3, DefaultVertexFormats.POSITION_COLOR);
        buildBuffer(RenderUtil.builder, trail, startColor.getColor(), fade.getValue() ? fadeColor : startColor.getColor());
        RenderUtil.tessellator.draw();
        RenderUtil.release();
    }

    void buildBuffer(final BufferBuilder builder, final ItemTrail trail, final Color start, final Color end) {
        for (final Position p : trail.positions) {
            final Vec3d pos = RenderUtil.updateToCamera(p.pos);
            final double value = normalize(trail.positions.indexOf(p), trail.positions.size());
            RenderUtil.addBuilderVertex(builder, pos.x, pos.y, pos.z, ColorUtil.interpolate((float) value, start, end));
        }
    }

    double normalize(final double value, final double max) {
        return (value - 0.0) / (max - 0.0);
    }

    static class ItemTrail {
        public Entity entity;
        public List<Position> positions;
        public Timer timer;

        ItemTrail(Entity entity) {
            this.entity = entity;
            positions = new ArrayList<>();
            (timer = new Timer()).setDelay(1000);
            timer.setPaused(true);
        }
    }

    static class Position {
        public Vec3d pos;
        public long time;

        public Position(Vec3d pos) {
            this.pos = pos;
            time = System.currentTimeMillis();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Position position = (Position) o;
            return time == position.time && Objects.equals(pos, position.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, time);
        }
    }
}
