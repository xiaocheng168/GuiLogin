package cc.mcyx.config;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//消息发送器
public class MessageSender {
    /**
     * 快速发送消息给予接收对象
     *
     * @param sender 接收者
     * @param msg    消息内容
     */
    public static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(Component.text(msg.replace("&", "§")));
    }

    /**
     * 快速发送消息给予接收对象
     *
     * @param player   接收者
     * @param title    大标题
     * @param subtitle 小标题
     * @param loadTime 小标题
     * @param showTime 显示时间
     * @param downTime 淡出时间
     */
    public static void sendTitle(Player player, String title, String subtitle, int loadTime, int showTime, int downTime) {
        player.sendTitle((title.replace("&", "§")),
                (subtitle.replace("&", "§")),
                loadTime, showTime, downTime);
    }
}
