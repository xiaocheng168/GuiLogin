package cc.mcyx;

import cc.mcyx.config.ConfigManager;
import cc.mcyx.listener.GuiNetworkListener;
import cc.mcyx.listener.ChatNetworkListener;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class FastAuth extends JavaPlugin {

    public static FastAuth fastAuth;

    @Override
    public void onLoad() {
        fastAuth = this;
        this.saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        getLogger().info("只为登录更快一步!");
        getLogger().info("FastAuth 已载入");
        getLogger().info("星兮兮!Zcc!");
        //注册数据包监听器
        ProtocolLibrary.getProtocolManager().addPacketListener(new ChatNetworkListener(this, PacketType.Play.Client.CHAT));
        ProtocolLibrary.getProtocolManager().addPacketListener(new GuiNetworkListener(this, PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.CUSTOM_PAYLOAD, PacketType.Play.Client.CLOSE_WINDOW));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        this.reloadConfig();
        sender.sendMessage(Component.text(ConfigManager.getMessage("reload", "配置文件已刷新!")));
        return true;
    }
}