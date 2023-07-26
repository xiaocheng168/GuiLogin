package cc.mcyx.listener;

import cc.mcyx.config.MessageConfig;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.xephi.authme.api.v3.AuthMeApi;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

//数据数据包监听
public class NetworkListener extends PacketAdapter {
    public static AuthMeApi authMeApi = AuthMeApi.getInstance();

    public NetworkListener(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
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
                    sendMessage(player, authMeApi.registerPlayer(playerName, password) ? MessageConfig.getMessage("register.success", "注册成功") : MessageConfig.getMessage("register.error", "登录失败,可能是账号或者密码错误"));
                } else {
                    //判断是否登录成功 成功将听过验证 否则失败！
                    if (authMeApi.checkPassword(playerName, password)) {
                        authMeApi.forceLogin(player);
                        sendMessage(player, MessageConfig.getMessage("login.success", "登录成功"));

                    } else sendMessage(player, MessageConfig.getMessage("login.error", "登录失败，可能是密码不正确哦"));
                }
            }
        } catch (Exception e) {
            sendMessage(player, "§4服务器登录校验错误!请联系服务器内管理员! 异常点: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(Component.text(msg.replace("&", "§")));
    }
}
