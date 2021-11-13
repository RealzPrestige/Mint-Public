package mint.modules.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.events.BlockEvent;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.*;
import mint.utils.InventoryUtil;
import mint.utils.NullUtil;
import mint.utils.RenderUtil;
import mint.utils.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author kambing, zPrestige
 * phobos speedmine heavily modified
 * <p>
 * also this shit is p messy so lol
 */

@ModuleInfo(name = "Packetmine", category = Module.Category.Player, description = "Mines with packets.")
public class Packetmine extends Module {
    private static Packetmine INSTANCE = new Packetmine();
    int delay;
    public Timer timer = new Timer();
    public BooleanSetting silentSwitch = new BooleanSetting("Silent Switch", false, this);
    public EnumSetting silentSwitchMode = new EnumSetting("Silent Switch Mode", SilentSwitchMode.AUTO, this);

    public enum SilentSwitchMode {AUTO, KEYBIND}

    public KeySetting switchBind = new KeySetting("Switch Bind", Keyboard.KEY_NONE, this, v -> silentSwitch.getValue() && silentSwitchMode.getValueEnum().equals(SilentSwitchMode.KEYBIND));
    public BooleanSetting render = new BooleanSetting("Render", false, this);

    public EnumSetting renderMode = new EnumSetting("Render Mode", RenderMode.EXPAND, this, v -> render.getValue());
    public EnumSetting boxMode = new EnumSetting("Box Mode", BoxMode.BOTH, this, v -> render.getValue());
    public EnumSetting colorMode = new EnumSetting("Color Mode", ColorMode.READYFADE, this, v -> render.getValue());

    public enum ColorMode {READYFADE, STATUS, STATIC}

    public enum RenderMode {FADE, EXPAND, EXPAND2, STATIC}

    public enum BoxMode {FILL, OUTLINE, BOTH}

    public ColorSetting color = new ColorSetting("Color", new Color(-1), this, v -> render.getValue());
    public ColorSetting readyColor = new ColorSetting("Ready Color", new Color(-1), this, v -> render.getValue() && (colorMode.getValueEnum().equals(ColorMode.STATUS) || colorMode.getValueEnum().equals(ColorMode.READYFADE)));
    public IntegerSetting speed = new IntegerSetting("ReadySpeed", 2, 1, 5, this, v -> render.getValue() && (colorMode.getValueEnum().equals(ColorMode.STATUS) || colorMode.getValueEnum().equals(ColorMode.READYFADE)));

    int currentAlpha;
    int count;
    ItemStack item;
    int subVal = 40;
    AxisAlignedBB bb;
    public BlockPos currentPos;
    public Block currentBlock;
    public IBlockState currentBlockState;
    int pickSlot;
    int oldSlot;
    int red0 = color.getColor().getRed();
    int green0 = color.getColor().getGreen();
    int blue0 = color.getColor().getBlue();

    public Packetmine() {
        setInstance();
    }

    public static Packetmine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Packetmine();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }


    @Override
    public void onLogin() {
        if (isEnabled()) {
            disable();
            enable();
        }
    }

    @Override
    public void onTick() {
        pickSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
        if (delay > 5) {
            delay = 0;
        } else {
            ++delay;
        }
        if (currentPos != null) {
            if (!mc.world.getBlockState(currentPos).equals(currentBlockState) || mc.world.getBlockState(currentPos).getBlock() == Blocks.AIR) {
                currentPos = null;
                currentBlockState = null;
            }
        }
        if (currentAlpha < (color.getColor().getAlpha() - 2)) {
            currentAlpha = currentAlpha + 3;
        }

        if (mc.player != null && silentSwitch.getValue() && silentSwitchMode.getValueEnum().equals(SilentSwitchMode.AUTO) && timer.passedMs((int) (2000.0f * Mint.serverManager.getTpsFactor())) && getPickSlot() != -1) {
            if (pickSlot == -1) {
                TextComponentString text = new TextComponentString(Mint.commandManager.getClientMessage() + ChatFormatting.WHITE + ChatFormatting.BOLD + " Speedmine: " + ChatFormatting.RESET + ChatFormatting.GRAY + "No pickaxe found, stopped" + ChatFormatting.WHITE + ChatFormatting.BOLD + " SilentSwitch");
                Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
            } else {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(getPickSlot()));
            }
        }
        if (mc.player != null && silentSwitch.getValue() && silentSwitchMode.getValueEnum().equals(SilentSwitchMode.AUTO) && timer.passedMs((int) (2200.0f * Mint.serverManager.getTpsFactor()))) {
            oldSlot = mc.player.inventory.currentItem;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        }
        if (mc.player != null && silentSwitch.getValue() && silentSwitchMode.getValueEnum().equals(SilentSwitchMode.KEYBIND)) {
            if (switchBind.getKey() != -1) {
                if (Keyboard.isKeyDown(switchBind.getKey())) {
                    if (pickSlot == -1) {
                        TextComponentString text = new TextComponentString(Mint.commandManager.getClientMessage() + ChatFormatting.WHITE + ChatFormatting.BOLD + " Speedmine: " + ChatFormatting.RESET + ChatFormatting.GRAY + "No pickaxe found, stopped" + ChatFormatting.WHITE + ChatFormatting.BOLD + " SilentSwitch");
                        Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
                    } else {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(getPickSlot()));

                        if (delay == 5) {
                            oldSlot = mc.player.inventory.currentItem;
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                        }

                    }
                }
            }
        }
        if (currentPos != null) {
            if (currentBlock == Blocks.OBSIDIAN && getBestItem(currentBlock) != null) {
                subVal = 146;
            } else if (currentBlock == Blocks.ENDER_CHEST && getBestItem(currentBlock) != null) {
                subVal = 66;
            }
        }
        count++;
        if (colorMode.getValueEnum().equals(ColorMode.READYFADE)) {
            if (red0 != readyColor.getColor().getRed()) {
                if (red0 > readyColor.getColor().getRed()) {
                    red0 = red0 - speed.getValue();
                } else {
                    red0 = red0 + speed.getValue();
                }
            }
            if (green0 != readyColor.getColor().getGreen()) {
                if (green0 > readyColor.getColor().getGreen()) {
                    green0 = green0 - speed.getValue();
                } else {
                    green0 = green0 + speed.getValue();
                }
            }
            if (blue0 != readyColor.getColor().getBlue()) {
                if (blue0 > readyColor.getColor().getBlue()) {
                    blue0 = blue0 - speed.getValue();
                } else {
                    blue0 = blue0 + speed.getValue();
                }
            }
        }
    }


    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck()) {
            return;
        }
        mc.playerController.blockHitDelay = 0;
    }

    @Override
    public void renderWorldLastEvent(RenderWorldEvent event) {
        try {
            if (currentPos != null) {
                if (getMineTime(currentBlock, item, false) == -1)
                    return;
                bb = mc.world.getBlockState(currentPos).getSelectedBoundingBox(mc.world, currentPos);

                // i had a headache making this i hope this works - kambing
                Color color = new Color(colorMode.getValueEnum().equals(ColorMode.STATIC) ? this.color.getColor().getRed() : colorMode.getValueEnum().equals(ColorMode.READYFADE) ? red0 : colorMode.getValueEnum().equals(ColorMode.STATUS) && this.timer.passedMs((int) (2000.0f * Mint.serverManager.getTpsFactor())) ? readyColor.getColor().getRed() : this.color.getColor().getRed(),
                        colorMode.getValueEnum().equals(ColorMode.STATIC) ? this.color.getColor().getGreen() : colorMode.getValueEnum().equals(ColorMode.READYFADE) ? green0 : colorMode.getValueEnum().equals(ColorMode.STATUS) && this.timer.passedMs((int) (2000.0f * Mint.serverManager.getTpsFactor())) ? readyColor.getColor().getGreen() : this.color.getColor().getGreen(),
                        colorMode.getValueEnum().equals(ColorMode.STATIC) ? this.color.getColor().getBlue() : colorMode.getValueEnum().equals(ColorMode.READYFADE) ? blue0 : colorMode.getValueEnum().equals(ColorMode.STATUS) && this.timer.passedMs((int) (2000.0f * Mint.serverManager.getTpsFactor())) ? readyColor.getColor().getBlue() : this.color.getColor().getBlue(),
                        renderMode.getValueEnum().equals(RenderMode.FADE) ? currentAlpha : this.color.getColor().getAlpha());

                if(renderMode.getValueEnum().equals(RenderMode.EXPAND))
                    bb = bb.shrink(Math.max(Math.min(normalize(count, getMineTime(currentBlock, item, false) - subVal), 1.0), 0.0));
                else if(renderMode.getValueEnum().equals(RenderMode.EXPAND2))
                    bb = bb.setMaxY(bb.minY - 0.5 + (Math.max(Math.min(normalize(count * 2, getMineTime(currentBlock, item, false) - subVal), 1.5), 0.0)));

                if (render.getValue() && currentPos != null) {
                    if(boxMode.getValueEnum().equals(BoxMode.OUTLINE))
                        RenderUtil.drawBlockOutlineBB(bb, color, 1f);
                    else if(boxMode.getValueEnum().equals(BoxMode.FILL))
                        RenderUtil.drawBBBox(bb, color, color.getAlpha());
                    else if(boxMode.getValueEnum().equals(BoxMode.BOTH)){
                        RenderUtil.drawBBBox(bb, color, color.getAlpha());
                        RenderUtil.drawBlockOutlineBB(bb, color, 1f);
                    }
                }
            }
        } catch (NullPointerException ignored) {
        }
    }

    @SubscribeEvent
    public void onBlockEvent(BlockEvent event) {
        if (NullUtil.fullNullCheck()) {
            return;
        }
        if (event.getStage() == 3 && mc.playerController.curBlockDamageMP > 0.1f) {
            mc.playerController.isHittingBlock = true;
        }

        if (event.pos != currentPos && currentPos != null) {
            red0 = color.getColor().getRed();
            green0 = color.getColor().getGreen();
            blue0 = color.getColor().getBlue();
            currentAlpha = 0;
            count = 0;
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, currentPos, event.facing));
            mc.playerController.isHittingBlock = false;
            mc.playerController.curBlockDamageMP = 0;
            currentPos = event.pos;
        }

        if (event.getStage() == 4) {
            if (canBreak(event.pos)) {
                mc.playerController.isHittingBlock = false;
                if (currentPos == null || event.pos != currentPos) {
                    currentPos = event.pos;
                    currentBlock = mc.world.getBlockState(currentPos).getBlock();
                    currentBlockState = mc.world.getBlockState(currentPos);
                    timer.reset();
                    if (getBestItem(currentBlock) == null) {
                        item = mc.player.getHeldItem(EnumHand.MAIN_HAND);
                    } else {
                        item = getItemStackFromItem(getBestItem(currentBlock));
                    }
                }
                currentAlpha = 0;
                count = 0;
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                event.setCanceled(true);
            }
        }
    }


    private int getPickSlot() {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() != Items.DIAMOND_PICKAXE) continue;
            return i;
        }
        return -1;
    }

    public static boolean canBreak(BlockPos pos) {
        IBlockState blockState = mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, mc.world, pos) != -1.0f;
    }

    public static double getMineTime(Block block, ItemStack stack, boolean raw) {
        if (stack.item.equals(Items.AIR))
            return -1.0;

        float speedMultiplier = stack.getDestroySpeed(block.getDefaultState());
        float damage;

        if (stack.canHarvestBlock(block.getDefaultState())) {
            damage = speedMultiplier / block.blockHardness / 30.0f;
        } else {
            damage = speedMultiplier / block.blockHardness / 100.0f;
        }
        if (raw)
            return damage;
        return (float) Math.ceil(1.0 / damage);
    }

    private double normalize(final double value, final double max) {
        return (1 - 0.5) * ((value - (double) 0) / (max - (double) 0)) + 0.5;
    }

    public static Item getBestItem(Block block) {
        String tool = block.getHarvestTool(block.getDefaultState());
        if (tool != null) {
            switch (tool) {
                case "axe":
                    return Items.DIAMOND_AXE;
                case "shovel":
                    return Items.DIAMOND_SHOVEL;
                default:
                    return Items.DIAMOND_PICKAXE;
            }
        } else {
            return Items.DIAMOND_PICKAXE;
        }

    }

    public static ItemStack getItemStackFromItem(Item item) {
        if (mc.player == null) return null;
        for (int slot = 0; slot <= 9; slot++) {
            if (mc.player.inventory.getStackInSlot(slot).getItem() == item)
                return mc.player.inventory.getStackInSlot(slot);
        }
        return null;
    }
}

