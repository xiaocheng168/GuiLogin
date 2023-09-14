package cc.mcyx.manager;

import cc.mcyx.FastAuth;
import cc.mcyx.config.ConfigManager;
import cc.mcyx.config.MessageSender;
import cc.mcyx.listener.GuiNetworkListener;
import fr.xephi.authme.api.v3.AuthMeApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

//校验类
public class AuthManager {
    public static AuthMeApi authMeApi = AuthMeApi.getInstance();

    /**
     * 登录校验
     *
     * @param player   玩家
     * @param password 密码
     */
    public static boolean auth(Player player, String password, Boolean... useGui) {
        //用户没有登录的情况下
        if (!authMeApi.isAuthenticated(player)) {
            //获取用户名与密码
            String playerName = player.getName();
            //用户是否已注册
            if (!authMeApi.isRegistered(playerName)) {
                //注册用户
                MessageSender.sendMessage(player, authMeApi.registerPlayer(playerName, password) ? ConfigManager.getMessage("register.success", "注册成功") : ConfigManager.getMessage("register.error", "注册失败,可能是密码不符合要求"));
                //注册完后是否自动登录 延迟执行
                if ((Boolean) (ConfigManager.getSetting("register_auto_login"))) {
                    Bukkit.getScheduler().runTaskLater(FastAuth.fastAuth, () -> authMeApi.forceLogin(player), 1);
                } else if (useGui.length > 0 && useGui[0])
                    Bukkit.getScheduler().runTaskLater(FastAuth.fastAuth, () -> GuiNetworkListener.openGui(player, "Auto Login Box"), 1);
                return true;
            } else {
                //判断是否登录成功 成功将听过验证 否则失败！
                if (authMeApi.checkPassword(playerName, password)) {
                    authMeApi.forceLogin(player);
                    MessageSender.sendMessage(player, ConfigManager.getMessage("login.success", "登录成功"));
                    return true;
                } else
                    MessageSender.sendMessage(player, ConfigManager.getMessage("login.error", "登录失败，可能是密码不正确哦"));
            }
        }
        return false;
    }
}
