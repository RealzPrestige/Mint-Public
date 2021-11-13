package mint.settingsrewrite;

import mint.Mint;
import mint.modules.Module;

import java.util.function.Predicate;

public class SettingRewrite<T> {
    public String name;
    public Module module;
    public T value;
    public Predicate<T> shown;

    public SettingRewrite(String name, T value, Module module) {
        this.name = name;
        this.value = value;
        this.module = module;
        assert Mint.settingsRewrite != null;
        Mint.settingsRewrite.addSetting(this);
        module.settings.add(this);
    }

    public SettingRewrite(String name, T value, Module module, Predicate<T> shown) {
        this.name = name;
        this.value = value;
        this.module = module;
        this.shown = shown;
        assert Mint.settingsRewrite != null;
        Mint.settingsRewrite.addSetting(this);
        module.settings.add(this);
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public Module getModule() {
        return module;
    }

    public String getValueAsString() {
        return value.toString();
    }

    public boolean isVisible() {
        if (shown == null)
            return true;
        return shown.test(getValue());
    }
}
