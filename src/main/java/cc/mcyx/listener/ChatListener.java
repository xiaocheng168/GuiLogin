package cc.mcyx.listener;

import cc.mcyx.AuthLogin;
import fr.xephi.authme.api.v3.AuthMeApi;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatPreviewEvent;

public class ChatListener implements Listener {

    public static AuthMeApi authMeApi = AuthMeApi.getInstance();

    @EventHandler
    public void chatEvent(AsyncPlayerChatPreviewEvent event) {
        Player player = event.getPlayer();
        if (!authMeApi.isAuthenticated(player)) {
            String playerName = player.getName();
            String password = event.getMessage();
            //用户没有登录的情况下
            event.setCancelled(true);
            //用户是否已注册
            if (!authMeApi.isRegistered(playerName)) {
                //注册用户
                sendMessage(player, authMeApi.registerPlayer(playerName, password) ? "注册成功!" : "注册失败?请联系服务器内管理员!");
            } else {
                //判断是否登录成功 成功将听过验证 否则失败！
                if (authMeApi.checkPassword(playerName, password)) {
                    authMeApi.forceLogin(player);
                    sendMessage(player, "登录成功");

                } else sendMessage(player, "登录失败，密码可能不正确哦!");
            }
        }
    }

    public void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(Component.text(msg));
    }
}
