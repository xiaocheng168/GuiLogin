package cc.mcyx;

import cc.mcyx.api.Metrics;
import cc.mcyx.config.ConfigManager;
import cc.mcyx.config.MessageSender;
import cc.mcyx.listener.ChatNetworkListener;
import cc.mcyx.listener.GuiNetworkListener;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class FastAuth extends JavaPlugin implements Listener {

    public static FastAuth fastAuth;

    @Override
    public void onLoad() {
        fastAuth = this;
        this.saveDefaultConfig();
        try {
            //统计数据
            Metrics metrics = new Metrics(this, 19260);
            metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "Fast Auth!!!!~~ 轻、快、用"));
        } catch (Exception e) {
            getLogger().warning("统计数据API异常!" + e.getLocalizedMessage());
        }

    }

    @Override
    public void onEnable() {
        log("§c只为登录更快一步!");
        log("§aFastAuth §f已完美载入");
        log("§a版本: §f" + getDescription().getVersion());
        log("§a环境: §f" + Bukkit.getBukkitVersion());
        log("星兮兮!Zcc!");
        log("开放月夕交流群: 250181305");
        //注册数据包监听器
        ProtocolLibrary.getProtocolManager().addPacketListener(new ChatNetworkListener(this, PacketType.Play.Client.CHAT));

        ProtocolLibrary.getProtocolManager().addPacketListener(new GuiNetworkListener(this,
                PacketType.Play.Client.WINDOW_CLICK,
                PacketType.Play.Client.CLOSE_WINDOW,
                PacketType.Play.Client.SETTINGS));
    }


    public static void log(Object s) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[" + ChatColor.GRAY + "FastAuth" + ChatColor.GREEN + "] " + ChatColor.WHITE + s);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        this.reloadConfig();
        MessageSender.sendMessage(sender, ConfigManager.getMessage("reload", "配置文件已刷新!"));
        return true;
    }
}
