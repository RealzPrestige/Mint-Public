package mint.settingsrewrite.impl;

import mint.modules.Module;
import mint.settingsrewrite.SettingRewrite;

import java.util.function.Predicate;

public class FloatSetting extends SettingRewrite<Float> {

    float minimum;
    float maximum;

    public FloatSetting(String name, float value, float minimum, float maximum, Module module) {
        super(name, value, module);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public FloatSetting(String name, float value, float minimum, float maximum, Module module, Predicate<Float> shown) {
        super(name, value, module, shown);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public Float getValue() {
        return value;
    }

    public float getMaximum() {
        return maximum;
    }

    public float getMinimum() {
        return minimum;
    }
}
