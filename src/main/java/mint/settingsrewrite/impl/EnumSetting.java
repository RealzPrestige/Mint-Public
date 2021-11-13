package mint.settingsrewrite.impl;

import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mint.modules.Module;
import mint.settingsrewrite.SettingRewrite;

import java.util.function.Predicate;

public final class EnumSetting extends SettingRewrite<Enum> {

    Enum<?>[] constants;
    int modeIndex;
    int valueIndex;

    public EnumSetting(String name, Enum<?> value, Module module) {
        super(name, value, module);
        constants = value.getDeclaringClass().getEnumConstants();
        valueIndex = modeIndex = indexOf(value);
    }

    public EnumSetting(String name, Enum<?> value, Module module, Predicate<Enum> shown) {
        super(name, value, module, shown);
        constants = value.getDeclaringClass().getEnumConstants();
        valueIndex = modeIndex = indexOf(value);
    }

    public Enum<?>[] getConstants(){
        return constants;
    }

    public int getModeIndex() {
        return modeIndex;
    }

    public void setModeIndex(int value) {
        modeIndex = value;
    }

    public void increase() {
        if (modeIndex == constants.length - 1)
            modeIndex = 0;
        else modeIndex++;
    }

    public void decrease() {
        if (modeIndex == 0)
            modeIndex = constants.length - 1;
        else modeIndex--;
    }



    public Enum<?> getValueEnum() {
        return constants[modeIndex];
    }

    public int indexOf(Enum<?> value) {
        for (int i = 0; i < constants.length; i++)
            if (constants[i] == value)
                return i;
        return -1;
    }

    public void setEnum(String enumString) {
        EnumUtil converter = new EnumUtil(getValue().getClass());
        Enum value = converter.doBackward(enumString);
        setValue(value == null ? getValue() : value);
    }

    public static class EnumUtil extends Converter<Enum, JsonElement> {
        private final Class<? extends Enum> clazz;

        public EnumUtil(Class<? extends Enum> clazz) {
            this.clazz = clazz;
        }

        public static int currentEnum(Enum clazz) {
            for (int i = 0; i < clazz.getClass().getEnumConstants().length; ++i) {
                Enum e = clazz.getClass().getEnumConstants()[i];
                if (!e.name().equalsIgnoreCase(clazz.name())) continue;
                return i;
            }
            return -1;
        }

        public static String getProperName(Enum clazz) {
            return Character.toUpperCase(clazz.name().charAt(0)) + clazz.name().toLowerCase().substring(1);
        }

        public JsonElement doForward(Enum anEnum) {
            return new JsonPrimitive(anEnum.toString());
        }

        public Enum doBackward(JsonElement jsonElement) {
            try {
                return Enum.valueOf(this.clazz, jsonElement.getAsString());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        public Enum doBackward(String string) {
            try {
                return Enum.valueOf(this.clazz, string);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
