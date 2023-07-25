package cc.mcyx;

import cc.mcyx.listener.GuiListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AuthLogin extends JavaPlugin {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new GuiListener(), this);
        getLogger().info("GuiAuth 已载入");
    }
}
