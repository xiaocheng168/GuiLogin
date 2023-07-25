package cc.mcyx.listener;

import cc.mcyx.AuthLogin;
import cc.mcyx.config.MessageConfig;
import fr.xephi.authme.api.v3.AuthMeApi;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatPreviewEvent;

public class ChatListener implements Listener {

    public static AuthMeApi authMeApi = AuthMeApi.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
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
                sendMessage(player, authMeApi.registerPlayer(playerName, password) ? MessageConfig.getMessage("register.success", "注册成功") : MessageConfig.getMessage("register.error", "登录失败,可能是账号或者密码错误"));
            } else {
                //判断是否登录成功 成功将听过验证 否则失败！
                if (authMeApi.checkPassword(playerName, password)) {
                    authMeApi.forceLogin(player);
                    sendMessage(player, MessageConfig.getMessage("login.success", "登录成功"));

                } else sendMessage(player, MessageConfig.getMessage("login.error", "登录失败，可能是密码不正确哦"));
            }
        }
    }

    public void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(Component.text(msg));
    }
}
