package cc.mcyx;

import cc.mcyx.config.MessageConfig;
import cc.mcyx.listener.ChatListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class AuthLogin extends JavaPlugin {

    public static AuthLogin authLogin;

    @Override
    public void onLoad() {
        authLogin = this;
        this.saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
//        Bukkit.getPluginManager().registerEvents(new GuiListener(), this);
        getLogger().info("GuiAuth 已载入");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        this.reloadConfig();
        sender.sendMessage(Component.text(MessageConfig.getMessage("reload", "配置文件已刷新!")));
        return true;
    }
}
