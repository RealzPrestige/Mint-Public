package mint.newgui.hud;

import com.google.gson.*;
import mint.newgui.hud.hudcomponents.HudWatermarkComponent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class HudComponentManager {
    static HudComponentManager INSTANCE = new HudComponentManager();
    JsonObject hudObject;
    OutputStreamWriter stream;
    ArrayList<HudModule> hudModules = new ArrayList<>();

    public HudComponentManager() {
        setInstance();
    }

    public static HudComponentManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new HudComponentManager();
        return INSTANCE;
    }

    void setInstance() {
        INSTANCE = this;
    }

    public void load() {
        init();
    }

    public void init() {
       // loadHuds();
        hudModules.add(new HudWatermarkComponent());
    }

    public void unload() {
        saveHudsActive();
        saveHudsPos();
        hudModules.clear();
    }

    public ArrayList<HudModule> getHudModules() {
        return hudModules;
    }

    public void drawText() {
        for (HudModule hudModule : hudModules) {
            if (hudModule.getValue())
                hudModule.drawText();
        }
    }

    /**
     * @author kambing
     */

    void loadHuds() {
        InputStream stream = null;
        try {
            stream = Files.newInputStream(Paths.get("mint/Default/Huds" + ".json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert stream != null;
        JsonObject hudObject = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
        for (HudModule hud : hudModules) {

        }
    }

    void saveHudsActive() {
        if (!(Files.exists(Paths.get("mint/Default/Huds" + ".json"))))
            try {
                Files.createFile(Paths.get("mint/Default/Huds" + ".json"));
            } catch (IOException ignored) {
            }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            stream = new OutputStreamWriter(new FileOutputStream("mint/Default/Huds" + ".json"), StandardCharsets.UTF_8);
        } catch (FileNotFoundException ignored) {
        }
        hudObject = new JsonObject();
        for (HudModule hud : hudModules) {
            hudObject.add(hud.getName(), new JsonPrimitive(hud.getValue()));
        }
        try {
            stream.write(gson.toJson(hudObject));
            stream.close();
        } catch (IOException ignored) {
        }
    }
    void saveHudsPos() {
        if (!(Files.exists(Paths.get("mint/Default/HudsPos" + ".json"))))
            try {
                Files.createFile(Paths.get("mint/Default/HudsPos" + ".json"));
            } catch (IOException ignored) {
            }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            stream = new OutputStreamWriter(new FileOutputStream("mint/Default/HudsPos" + ".json"), StandardCharsets.UTF_8);
        } catch (FileNotFoundException ignored) {
        }
        hudObject = new JsonObject();
        for (HudModule hud : hudModules) {
            hudObject.add(hud.getName(), new JsonPrimitive(hud.getPos()));
        }
        try {
            stream.write(gson.toJson(hudObject));
            stream.close();
        } catch (IOException ignored) {
        }
    }
}