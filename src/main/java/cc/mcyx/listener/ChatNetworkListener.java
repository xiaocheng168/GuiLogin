package cc.mcyx.listener;

import cc.mcyx.config.ConfigManager;
import cc.mcyx.config.MessageSender;
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
            //用户没有登录的情况下
            if (!authMeApi.isAuthenticated(player)) {
                //取消消息事件
                event.setCancelled(true);
                //获取用户名与密码
                String playerName = player.getName();
                String password = event.getPacket().getStrings().getValues().get(0);
                //用户是否已注册
                if (!authMeApi.isRegistered(playerName)) {
                    //注册用户
                    MessageSender.sendMessage(player, authMeApi.registerPlayer(playerName, password) ? ConfigManager.getMessage("register.success", "注册成功") : ConfigManager.getMessage("register.error", "登录失败,可能是账号或者密码错误"));
                    //注册完后是否自动登录
                    if ((Boolean) (ConfigManager.getSetting("register_auto_login"))) authMeApi.forceLogin(player);
                } else {
                    //判断是否登录成功 成功将听过验证 否则失败！
                    if (authMeApi.checkPassword(playerName, password)) {
                        authMeApi.forceLogin(player);
                        MessageSender.sendMessage(player, ConfigManager.getMessage("login.success", "登录成功"));
                    } else
                        MessageSender.sendMessage(player, ConfigManager.getMessage("login.error", "登录失败，可能是密码不正确哦"));
                }
            }
        } catch (Exception e) {
            MessageSender.sendMessage(player, "§4服务器登录校验错误!请联系服务器内管理员! 异常点: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
