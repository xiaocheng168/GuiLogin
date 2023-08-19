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
            String password = event.getPacket().getStrings().getValues().get(0);
            if (!authMeApi.isAuthenticated(player)) {
                event.setCancelled(true);
                AuthManager.auth(player, password);
                return;
            }
            //对密码进行保护 检测密码是否泄露
            if ((Boolean) ConfigManager.getSetting("protectPassword", true) && AuthManager.authMeApi.checkPassword(player.getName(), password)) {
                MessageSender.sendTitle(player,
                        ConfigManager.getMessage("safe.error_send_password.title", "§c❌ 密码泄露风险 ❌"),
                        ConfigManager.getMessage("safe.error_send_password.subtitle", "§4请不要向任何玩家泄露自己的密码"),
                        5, 100, 5);
                event.setCancelled(true);
            }
        } catch (Exception e) {
            MessageSender.sendMessage(player, "§4服务器登录校验错误!请联系服务器内管理员! 异常点: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
