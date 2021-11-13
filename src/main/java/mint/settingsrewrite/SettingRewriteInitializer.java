package mint.settingsrewrite;

import mint.modules.Module;

import java.util.ArrayList;
import java.util.List;

public class SettingRewriteInitializer {

    List<SettingRewrite> settingRewriteList;

    public SettingRewriteInitializer() {
        settingRewriteList = new ArrayList<>();
    }

    public void addSetting(SettingRewrite setting) {
        settingRewriteList.add(setting);
    }


    public List<SettingRewrite> getSettingsInModule(Module module) {
        List<SettingRewrite> settings = new ArrayList<>();
        for (SettingRewrite setting : settingRewriteList) {
            if (!setting.getModule().equals(module))
                continue;

                settings.add(setting);
        }
        return settings;
    }

}
