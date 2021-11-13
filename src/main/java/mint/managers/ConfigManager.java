package mint.managers;

import mint.Mint;
import mint.modules.Module;
import mint.settingsrewrite.SettingRewrite;
import mint.settingsrewrite.impl.*;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class ConfigManager {
    ArrayList<Module> modules = new ArrayList<>();
    File path;

    public void init() {
        path = new File(Mint.INSTANCE.mc.gameDir + File.separator + "prestigebase");
        if (!path.exists())
            path.mkdir();
        modules.addAll(Mint.moduleManager.moduleList);
        load();
    }

    public void save() {
        saveModuleFile();
    }

    public void load() {
        setModuleEnabled();
        setModuleBind();
        setModuleSettingValues();
    }

    public void saveModuleFile() {
        try {
            for (Module module : modules) {
                File categoryPath = new File(path.getAbsolutePath() + File.separator + module.getCategory().toString());
                if (!categoryPath.exists())
                    categoryPath.mkdir();
                File file = new File(categoryPath.getAbsolutePath(), module.getName() + ".txt");
                if (!file.exists())
                    file.createNewFile();
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                bufferedWriter.write("State:" + (module.isEnabled() ? "Enabled" : "Disabled"));
                bufferedWriter.write("\r\n");
                for (mint.settingsrewrite.SettingRewrite SettingRewrite : module.getSettings()) {
                    if (SettingRewrite.getName().equals("Keybind") || SettingRewrite.getName().equals("Enabled"))
                        continue;
                    if (SettingRewrite instanceof StringSetting) {
                        String str = (String) SettingRewrite.getValue();
                        String properString = str.replace(" ", "_");
                        bufferedWriter.write(SettingRewrite.getName() + ":" + properString);
                        bufferedWriter.write("\r\n");
                        continue;
                    }
                    if (SettingRewrite instanceof ColorSetting) {
                        bufferedWriter.write(SettingRewrite.getName() + ":" + ((ColorSetting) SettingRewrite).getColor().getRGB());
                        bufferedWriter.write("\r\n");
                    }
                    bufferedWriter.write(SettingRewrite.getName() + ":" + SettingRewrite.getValue());
                    bufferedWriter.write("\r\n");
                }
                bufferedWriter.write("Keybind:" + module.getBind());
                bufferedWriter.close();
            }
        } catch (Exception ignored) {
        }
    }

    public void setModuleEnabled() {
        for (Module module : modules) {
            try {
                File categoryPath = new File(path.getAbsolutePath() + File.separator + module.getCategory().toString());
                if (!categoryPath.exists())
                    continue;
                File file = new File(categoryPath.getAbsolutePath(), module.getName() + ".txt");
                if (!file.exists())
                    continue;
                FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
                bufferReader.lines().forEach(line -> {
                    String clarification = line.split(":")[0];
                    String state = line.split(":")[1];
                    if (clarification.equals("State"))
                        if (state.equals("Enabled"))
                            module.enable();
                });
            } catch (Exception ignored) {
            }
        }
    }

    public void setModuleBind() {
        for (Module module : modules) {
            try {
                File categoryPath = new File(path.getAbsolutePath() + File.separator + module.getCategory().toString());
                if (!categoryPath.exists())
                    continue;
                File file = new File(categoryPath.getAbsolutePath(), module.getName() + ".txt");
                if (!file.exists())
                    continue;
                FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
                bufferReader.lines().forEach(line -> {
                    String clarification = line.split(":")[0];
                    String state = line.split(":")[1];
                    if (clarification.equals("Keybind")) {
                        if (state.equals("0"))
                            return;
                        module.setBind(Integer.parseInt(state));
                    }
                });
            } catch (Exception ignored) {
            }
        }
    }

    public void setModuleSettingValues() {
        for (Module module : modules) {
            try {
                File categoryPath = new File(path.getAbsolutePath() + File.separator + module.getCategory().toString());
                if (!categoryPath.exists())
                    continue;
                File file = new File(categoryPath.getAbsolutePath(), module.getName() + ".txt");
                if (!file.exists())
                    continue;
                FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
                bufferReader.lines().forEach(line -> {
                    String clarification = line.split(":")[0];
                    String state = line.split(":")[1];
                    for (SettingRewrite SettingRewrite : module.getSettings()) {
                        if (SettingRewrite.getName().equals(clarification)) {
                            if (SettingRewrite instanceof StringSetting) {
                                SettingRewrite.setValue(state);
                                continue;
                            }
                            if (SettingRewrite instanceof IntegerSetting) {
                                SettingRewrite.setValue(Integer.parseInt(state));
                                continue;
                            }
                            if (SettingRewrite instanceof FloatSetting) {
                                SettingRewrite.setValue(Float.parseFloat(state));
                                continue;
                            }
                            if (SettingRewrite instanceof DoubleSetting) {
                                SettingRewrite.setValue(Double.parseDouble(state));
                                continue;
                            }
                            if (SettingRewrite instanceof BooleanSetting) {
                                SettingRewrite.setValue(Boolean.parseBoolean(state));
                                continue;
                            }
                            if (SettingRewrite instanceof KeySetting) {
                                SettingRewrite.setValue(Integer.parseInt(state));
                                continue;
                            }
                            if (SettingRewrite instanceof ColorSetting) {
                                ((ColorSetting) SettingRewrite).setColor(new Color(Integer.parseInt(state), true));
                                continue;
                            }
                            if (SettingRewrite instanceof EnumSetting) {
                                SettingRewrite.setValue(state);
                            }
                        }
                    }
                });
            } catch (Exception ignored) {
            }
        }
    }
}