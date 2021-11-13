package mint.modules.visual;

import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.ColorSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.settingsrewrite.impl.ParentSetting;
import mint.utils.NullUtil;
import mint.utils.RenderUtil;

import java.awt.*;

/**
 * @author zPrestige_
 * @since 24/09/2021
 */

@ModuleInfo(name = "Break ESP",description = "Renders block that is being severly hit until it breaks.",category = Module.Category.Visual)
public class BreakESP extends Module {
    public ParentSetting miscParent = new ParentSetting("Misc",true, this);
    public IntegerSetting range = new IntegerSetting("Range",50,0,200,this, v -> miscParent.getValue());

    public ParentSetting visual = new ParentSetting("Visuals", true, this);
    public BooleanSetting playerName = new BooleanSetting("Player Name" , true , this, v -> visual.getValue());
    public BooleanSetting breakPercentage = new BooleanSetting("Break Percentage" , true , this, v -> visual.getValue());

    public ColorSetting box = new ColorSetting("Box",new Color(-1),this);
    public ColorSetting out = new ColorSetting("Outline", new Color(-1),this);

    @Override
    public void renderWorldLastEvent(RenderWorldEvent event) {
        if(NullUtil.fullNullCheck())
            return;

        mc.renderGlobal.damagedBlocks.forEach((integer, destroyBlockProgress) -> {
            if (destroyBlockProgress.getPosition().getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= range.getValue()) {
                RenderUtil.drawBoxESP(destroyBlockProgress.getPosition(), box.getColor(), true, out.getColor(), 1, true, true, box.getColor().getAlpha(), true);
                if (mc.world.getEntityByID(integer) != null) {
                    RenderUtil.drawText(destroyBlockProgress.getPosition(), (playerName.getValue() ? mc.world.getEntityByID(integer).getName() : "") + (breakPercentage.getValue() ? " " + (destroyBlockProgress.getPartialBlockDamage() * 12.5) + "%" : ""), -1);
                }
            }
        });
    }
}
