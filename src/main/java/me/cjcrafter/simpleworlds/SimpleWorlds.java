package me.cjcrafter.simpleworlds;

import me.deecaad.core.utils.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

public class SimpleWorlds extends JavaPlugin {

    private static SimpleWorlds INSTANCE;
    private List<String> loadWorlds;

    @Override
    public void onEnable() {
        INSTANCE = this;

        if (!getDataFolder().exists() || getDataFolder().listFiles() == null || getDataFolder().listFiles().length == 0) {
            getLogger().log(Level.INFO, "Copying files from jar (This process may take up to 30 seconds during the first load)");
            FileUtil.copyResourcesTo(getClassLoader().getResource("SimpleWorlds"), getDataFolder().toPath());
            reloadConfig();
        }

        loadWorlds = getConfig().getStringList("Load_Worlds");

        // Load the default worlds from file, this process may take some time.
        // The loading process automatically prints information to console.
        for (String name : loadWorlds) {
            if (Bukkit.getWorld(name) == null)
                new WorldCreator(name).createWorld();
        }

        Command.register();
    }

    @Override
    public void onDisable() {
        getConfig().set("Load_Worlds", loadWorlds);
        saveConfig();
    }

    public void addWorld(String name) {
        loadWorlds.add(name);
    }

    public static SimpleWorlds getPlugin() {
        return INSTANCE;
    }
}
