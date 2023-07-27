package cc.mcyx.listener;

import cc.mcyx.config.ConfigManager;
import cc.mcyx.config.MessageSender;
import cc.mcyx.manager.AuthManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.xephi.authme.api.v3.AuthMeApi;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

//聊天快速登录
public class ChatNetworkListener extends PacketAdapter {
    public static AuthMeApi authMeApi = AuthMeApi.getInstance();

    public ChatNetworkListener(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (!ConfigManager.getSetting("type", "gui").toString().equalsIgnoreCase("chat")) return;
        Player player = event.getPlayer();
        try {
            if (!authMeApi.isAuthenticated(player)) {
                event.setCancelled(true);
                String password = event.getPacket().getStrings().getValues().get(0);
                AuthManager.auth(player, password);
            }
        } catch (Exception e) {
            MessageSender.sendMessage(player, "§4服务器登录校验错误!请联系服务器内管理员! 异常点: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
