package mint.settingsrewrite.impl;

import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mint.modules.Module;
import mint.settingsrewrite.SettingRewrite;
import org.lwjgl.input.Keyboard;

import java.util.function.Predicate;

public class KeySetting extends SettingRewrite<Integer> {

    public boolean isTyping = false;

    public KeySetting(String name, int value, Module module) {
        super(name, value, module);
    }

    public KeySetting(String name, int value, Module module, Predicate<Integer> shown) {
        super(name, value, module, shown);
    }

    public int getKey() {
        return value;
    }

    public void setBind(int bind) {
        value = bind;
    }

    public static class BindUtil {
        private final int key;

        public BindUtil(int key) {
            this.key = key;
        }

        public static BindUtil none() {
            return new BindUtil(-1);
        }

        public int getKey() {
            return key;
        }

        public boolean isEmpty() {
            return key < 0;
        }

        public String toString() {
            return isEmpty() ? "None" : (key < 0 ? "None" : Keyboard.getKeyName(key));
        }

        public boolean isDown() {
            return !isEmpty() && Keyboard.isKeyDown(getKey());
        }

        public static class BindConverter extends Converter<BindUtil, JsonElement> {

            public JsonElement doForward(BindUtil bindUtil) {
                return new JsonPrimitive(bindUtil.toString());
            }

            public BindUtil doBackward(JsonElement jsonElement) {
                String s = jsonElement.getAsString();

                if (s.equalsIgnoreCase("None"))
                    return BindUtil.none();

                int key = -1;

                try {
                    key = Keyboard.getKeyIndex(s.toUpperCase());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (key == 0)
                    return BindUtil.none();

                return new BindUtil(key);
            }

            public int doBackwardInt(JsonElement element) {
                String s = element.getAsString();

                if (s.equalsIgnoreCase("None"))
                    return -1;

                int key = -1;

                try {
                    key = Keyboard.getKeyIndex(s.toUpperCase());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (key == 0)
                    return -1;

                return new BindUtil(key).key;

            }
        }
    }
}
