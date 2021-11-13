package mint.settingsrewrite.impl;

import mint.modules.Module;
import mint.settingsrewrite.SettingRewrite;

import java.util.function.Predicate;

public class StringSetting extends SettingRewrite<String> {
    boolean isOpen = false;

    public StringSetting(String name, String value, Module module) {
        super(name, value, module);
    }

    public StringSetting(String name, String value, Module module, Predicate<String> shown) {
        super(name, value, module, shown);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
