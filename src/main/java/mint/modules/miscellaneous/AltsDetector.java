package mint.modules.miscellaneous;

import mint.Mint;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.utils.NullUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author kambing
 * @since 1/11/2021
 */

@ModuleInfo(name = "AltsDetector", category = Module.Category.Miscellaneous, description = "sum thing that works yea")
public class AltsDetector extends Module {

    private final BooleanSetting friend = new BooleanSetting("Ignore Friends", true, this);
    List<String> list;
    List<String> list1 = new ArrayList<>();

    public AltsDetector() {
    }

    public List<EntityPlayer> players;



    @Override
    public void onEnable() {
        players = new ArrayList<>(); // literally just #clear lololololol
        try {
            list = Files.readAllLines(getAltsDetectorFile().toPath());
        }catch (Exception ignored) {
        }
        for (String strings : list) {
            String[] string = strings.split(" : ");
            list1.add(string[0]);
        }
    }

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck()) return;

        List<EntityPlayer> players1 = new ArrayList<>();

        for (EntityPlayer player : mc.world.playerEntities) {
            if (player == mc.player) continue;
            if (Mint.friendManager.isFriend(player.getName()) && friend.getValue()) continue;
            if (list1.contains(player.getName())) continue;
            players1.add(player);
        }
        if (players1.size() >= 1) {
            for (EntityPlayer player : players1) {
                if (players.contains(player)) continue;

                String pingStr = "";

                try {
                    int responseTime = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime();
                    pingStr = pingStr + responseTime + "ms ";
                } catch (Exception ignored) {
                }
                saveFile(player.getName(), pingStr);
                players.add(player);
            }
        }
    }

    @Override
    public void onLogin() {
        if (this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }

    public void saveFile(String name, String ping) {
        try {
            File file = new File("mint/altsdetector.txt");
            try {
                if (!file.exists())
                    file.createNewFile();
            } catch (Exception ignored) {
            }
            file.getParentFile().mkdirs();
            PrintWriter writer = new PrintWriter(new FileWriter(file, true));
            writer.println(name + " : " + ping);
            writer.close();
        } catch (Exception ignored) {
        }

    }

    private File getAltsDetectorFile() {
        File file = new File("mint/altsdetector.txt");
        try {
            if (!file.exists())
                file.createNewFile();
        } catch (Exception ignored) {
        }
        return file;
    }
}
