package cc.mcyx.config;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

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
}
