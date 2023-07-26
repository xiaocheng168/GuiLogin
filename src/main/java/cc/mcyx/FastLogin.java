package cc.mcyx;

import cc.mcyx.config.MessageConfig;
import cc.mcyx.listener.NetworkListener;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class FastLogin extends JavaPlugin {

    public static FastLogin fastLogin;

    @Override
    public void onLoad() {
        fastLogin = this;
        this.saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        getLogger().info("GuiAuth 已载入");
        //注册数据包监听器
        ProtocolLibrary.getProtocolManager().addPacketListener(new NetworkListener(this, PacketType.Play.Client.CHAT));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        this.reloadConfig();
        sender.sendMessage(Component.text(MessageConfig.getMessage("reload", "配置文件已刷新!")));
        return true;
    }
}
