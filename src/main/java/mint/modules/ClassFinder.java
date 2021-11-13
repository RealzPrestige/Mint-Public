package mint.modules;

import com.google.common.reflect.ClassPath;
import mint.Mint;
import net.minecraft.launchwrapper.Launch;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class ClassFinder {

    public static List<Class<?>> from(String packageName) {
        try {
            final List<Class<?>> classes = new ArrayList<>();
            for (ClassPath.ClassInfo info : ClassPath.from(Launch.classLoader).getAllClasses()) {
                if (info.getName().startsWith(packageName)) {
                    classes.add(info.load());
                }
            }
            return classes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addModules(String folder) {
        try {
            final List<Class<?>> classes = ClassFinder.from("mint.modules." + folder);
            if (classes == null)
                return;
            for (Class<?> clazz : classes) {
                if (!Modifier.isAbstract(clazz.getModifiers()) && Module.class.isAssignableFrom(clazz)) {
                    for (Constructor<?> constructor : clazz.getConstructors()) {
                        final Module instance = (Module) constructor.newInstance();
                        for (Field field : instance.getClass().getDeclaredFields())
                            if (!field.isAccessible())
                                field.setAccessible(true);

                        Mint.moduleManager.moduleList.add(instance);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
