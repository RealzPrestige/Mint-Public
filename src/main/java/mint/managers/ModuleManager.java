package mint.managers;

import mint.Mint;
import mint.events.RenderOverlayEvent;
import mint.events.RenderWorldEvent;
import mint.modules.ClassFinder;
import mint.modules.Module;
import mint.modules.miscellaneous.SignExploit;
import mint.newgui.NewGui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    public ArrayList<Module> moduleList = new ArrayList<>();
    public List<Module> sortedModules = new ArrayList<>();
    public static Boolean doneLoad = true;

    public void init() {
        if (doneLoad) {
            SignExploit.nullCheck();
            doneLoad = false;
        }
        ClassFinder.addModules("combat");
        ClassFinder.addModules("core");
        ClassFinder.addModules("miscellaneous");
        ClassFinder.addModules("movement");
        ClassFinder.addModules("player");
        ClassFinder.addModules("visual");
    }

    public Module getModuleByName(String name) {
        for (Module module : moduleList) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<>();
        for (Module module : moduleList) {
            if (!module.isEnabled())
                continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<>();
        moduleList.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add(module);
            }
        });
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        moduleList.forEach(((EventBus) MinecraftForge.EVENT_BUS)::register);
        moduleList.forEach(Module::onLoad);
    }

    public void onUpdate() {
        moduleList.stream().filter(Module::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        moduleList.stream().filter(Module::isEnabled).forEach(Module::onTick);
    }

    public void renderOverlayEvent(RenderOverlayEvent event) {
        moduleList.stream().filter(Module::isEnabled).forEach(module -> module.renderOverlayEvent(event));
    }

    public void renderWorldEvent(RenderWorldEvent event) {
        moduleList.stream().filter(Module::isEnabled).forEach(module -> module.renderWorldLastEvent(event));
    }

    public void sortModules(boolean reverse) {
        sortedModules = getEnabledModules().stream().sorted(Comparator.comparing(module -> {
            assert Mint.textManager != null;
            return Mint.textManager.getStringWidth(module.getName()) * (reverse ? -1 : 1);
        })).collect(Collectors.toList());
    }


    public void onLogout() {
        moduleList.forEach(Module::onLogout);
    }

    public void onLogin() {
        moduleList.forEach(Module::onLogin);
    }

    public void onUnload() {
        moduleList.forEach(MinecraftForge.EVENT_BUS::unregister);
    }

    public void onUnloadPost() {
        for (Module module : moduleList) {
            module.setEnabled(false);
        }
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || Mint.INSTANCE.mc.currentScreen instanceof NewGui)
            return;

        moduleList.forEach(module -> {
            if (module.bind.getKey() == eventKey) {
                if (module.isEnabled())
                    module.disable();
                else if (!module.isEnabled())
                    module.enable();
            }
        });
    }
}

