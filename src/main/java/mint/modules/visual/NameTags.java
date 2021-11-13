package mint.modules.visual;

import mint.Mint;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.ColorSetting;
import mint.utils.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

@ModuleInfo(name = "Name Tags", category = Module.Category.Visual, description = "Draws info about an entity above their head.")
public class NameTags extends Module {
    private static NameTags INSTANCE = new NameTags();
    public BooleanSetting boxParent = new BooleanSetting("Rect", false, this);
    public BooleanSetting rect = new BooleanSetting("Rectangle Setting", true, this, v -> boxParent.getValue());
    public ColorSetting rectColor = new ColorSetting("Rectangle Color", new Color(-1), this, v -> rect.getValue() && boxParent.getValue());
    public BooleanSetting healthLine = new BooleanSetting("HealthLine", true, this);
    public BooleanSetting fullHealthLine = new BooleanSetting("FullHealthLine", true, this);
    public BooleanSetting enchant = new BooleanSetting("Enchantment", true, this);

    public NameTags() {
        setInstance();
    }

    public static NameTags getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NameTags();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void renderWorldLastEvent(RenderWorldEvent event) {
        if (!NullUtil.fullNullCheck())
            return;

        for (EntityPlayer player : mc.world.playerEntities) {
            if (player == null || player.equals(mc.player) || !player.isEntityAlive() || player.isInvisible() && !EntityUtil.isInFov(player))
                continue;
            double x = interpolate(player.lastTickPosX, player.posX, event.getPartialTicks()) - mc.getRenderManager().renderPosX;
            double y = interpolate(player.lastTickPosY, player.posY, event.getPartialTicks()) - mc.getRenderManager().renderPosY;
            double z = interpolate(player.lastTickPosZ, player.posZ, event.getPartialTicks()) - mc.getRenderManager().renderPosZ;
            renderNameTag(player, x, y, z, event.getPartialTicks());

        }
    }

    void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        double tempY = y;
        tempY += player.isSneaking() ? 0.5 : 0.7;
        Entity camera = mc.getRenderViewEntity();
        assert (camera != null);
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);
        String displayTag = getDisplayTag(player);
        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        int width = Mint.textManager.getStringWidth(displayTag) / 2;
        double scale = (0.0018 + (double) 10 * (distance * 0.3)) / 1000.0;
        if (distance <= 8.0) {
            scale = 0.0245;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4f, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();
        if (rect.getValue()) {
            RenderUtil.drawRect(-width - 1, -9, (float) width + 2.0f, 0.5f, rectColor.getColor().getRGB());
        }
        if (healthLine.getValue()) {
            float healthAmount = player.getHealth() + player.getAbsorptionAmount();
            int lineColor = (healthAmount >= 33) ? ColorUtil.toRGBA(0, 255, 0, 255) : (healthAmount >= 30) ? ColorUtil.toRGBA(150, 255, 0, 255) : ((healthAmount > 25) ? ColorUtil.toRGBA(75, 255, 0, 255) : ((healthAmount > 20) ? ColorUtil.toRGBA(255, 255, 0, 255) : ((healthAmount > 15) ? ColorUtil.toRGBA(255, 200, 0, 255) : ((healthAmount > 10) ? ColorUtil.toRGBA(255, 150, 0, 255) : ((healthAmount > 5) ? ColorUtil.toRGBA(255, 50, 0, 255) : ColorUtil.toRGBA(255, 0, 0, 255))))));
            RenderUtil.drawGradientRect(-width - 1, -(mc.fontRenderer.FONT_HEIGHT - 8), (width + healthAmount), 0, lineColor, lineColor);
        } else if (fullHealthLine.getValue()) {
            float healthAmount = player.getHealth() + player.getAbsorptionAmount();
            int lineColor = (healthAmount >= 33) ? ColorUtil.toRGBA(0, 255, 0, 255) : (healthAmount >= 30) ? ColorUtil.toRGBA(150, 255, 0, 255) : ((healthAmount > 25) ? ColorUtil.toRGBA(75, 255, 0, 255) : ((healthAmount > 20) ? ColorUtil.toRGBA(255, 255, 0, 255) : ((healthAmount > 15) ? ColorUtil.toRGBA(255, 200, 0, 255) : ((healthAmount > 10) ? ColorUtil.toRGBA(255, 150, 0, 255) : ((healthAmount > 5) ? ColorUtil.toRGBA(255, 50, 0, 255) : ColorUtil.toRGBA(255, 0, 0, 255))))));
            RenderUtil.drawGradientRect(-width - 1, -(mc.fontRenderer.FONT_HEIGHT - 8), width + 2, 0, lineColor, lineColor);
        }
        GlStateManager.disableBlend();
        ItemStack renderMainHand = player.getHeldItemMainhand().copy();
        if (renderMainHand.hasEffect() && (renderMainHand.getItem() instanceof ItemTool || renderMainHand.getItem() instanceof ItemArmor)) {
            renderMainHand.stackSize = 1;
        }
        GL11.glPushMatrix();
        GL11.glScalef(0.75f, 0.75f, 0.0f);
        GL11.glScalef(1.5f, 1.5f, 1.0f);
        GL11.glPopMatrix();
        GlStateManager.pushMatrix();
        int xOffset = -8;
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack == null) continue;
            xOffset -= 8;
        }
        xOffset -= 8;
        ItemStack renderOffhand = player.getHeldItemOffhand().copy();
        if (renderOffhand.hasEffect() && (renderOffhand.getItem() instanceof ItemTool || renderOffhand.getItem() instanceof ItemArmor)) {
            renderOffhand.stackSize = 1;
        }
        renderItemStack(renderOffhand, xOffset);
        xOffset += 16;
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack == null) continue;
            ItemStack armourStack = stack.copy();
            if (armourStack.hasEffect() && (armourStack.getItem() instanceof ItemTool || armourStack.getItem() instanceof ItemArmor)) {
                armourStack.stackSize = 1;
            }
            renderItemStack(armourStack, xOffset);
            xOffset += 16;
        }
        renderItemStack(renderMainHand, xOffset);
        GlStateManager.popMatrix();
        assert Mint.friendManager != null;
        Mint.textManager.drawStringWithShadow(displayTag, -width, -10, Mint.friendManager.isFriend(player.getName()) ? ColorUtil.toRGBA(0, 255, 255) : -1);
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();
    }

    private void renderItemStack(ItemStack stack, int x) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, -28);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, -28);
        mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        renderEnchantmentText(stack, x);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.popMatrix();
    }

    private void renderEnchantmentText(ItemStack stack, int x) {
        int enchantmentY = -28 + 1;
        if (stack.getItem() == Items.GOLDEN_APPLE && stack.hasEffect()) {
            Mint.textManager.drawStringWithShadow("God", x * 2, enchantmentY, -3977919);
            enchantmentY -= 8;
        }
        if (enchant.getValue()) {
            NBTTagList enchants = stack.getEnchantmentTagList();
            for (int index = 0; index < enchants.tagCount(); ++index) {
                short id = enchants.getCompoundTagAt(index).getShort("id");
                int level = enchants.getCompoundTagAt(index).getShort("lvl");
                Enchantment enc = Enchantment.getEnchantmentByID(id);
                if (enc == null) continue;
                String encName = findStringForEnchants(enc, level);
                Mint.textManager    .drawStringWithShadow(encName, x * 2, enchantmentY, -1);
                enchantmentY -= 8;
            }
        }
        if (PlayerUtil.hasDurability(stack)) {
            int percent = PlayerUtil.getRoundedDamage(stack);
            String color = percent >= 60 ? "\u00a7a" : (percent >= 25 ? "\u00a7e" : "\u00a7c");
            Mint.textManager.drawStringWithShadow(color + percent + "%", x * 2, enchantmentY, -1);
        }
    }

    private String findStringForEnchants(Enchantment enchantment, int level) {

        ResourceLocation resourceLocation = Enchantment.REGISTRY.getNameForObject(enchantment);

        String string = resourceLocation == null ? enchantment.getName() : resourceLocation.toString();

        int charCount = (level > 1) ? 12 : 13;

        if (string.length() > charCount) {
            string = string.substring(10, charCount);
        }

        return string.substring(0, 1).toUpperCase() + string.substring(1) + TextFormatting.WHITE + ((level > 1) ? level : "");


    }

    private String getDisplayTag(EntityPlayer player) {
        String name = player.getDisplayName().getFormattedText();
        float health = Math.round(EntityUtil.getHealth(player));
        String color = health > 18.0f ? "\u00a7a" : (health > 16.0f ? "\u00a72" : (health > 12.0f ? "\u00a7e" : (health > 8.0f ? "\u00a76" : (health > 5.0f ? "\u00a7c" : "\u00a74"))));
        String pingStr = "";
        try {
            int responseTime = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime();
            pingStr = pingStr + responseTime + "ms ";
        } catch (Exception ignored) {
        }
        return name + " " + pingStr + color + health;
    }


    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }
}