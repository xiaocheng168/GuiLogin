package cc.mcyx.nms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;


/**
 * Craft Net Minecraft Server 相关处理
 */
public abstract class CraftNms {

    //解析NMS版本
    public static final String nmsVersion = (Bukkit.getServer()).getClass().getPackage().getName().replace("org.bukkit.craftbukkit.", "");

    /**
     * 使用反射获取玩家目前GUI序号
     * 范围1-100
     *
     * @param player 查询玩家
     * @return 返回玩家GUI目前序号
     */
    public static int getPlayerGuiId(Player player) {
        try {
            Object craftPlayer = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer").cast(player);
            Object entityPlayer = craftPlayer.getClass().getMethod("getHandle").invoke(craftPlayer);
            Object nextContainerCounter = entityPlayer.getClass().getMethod("nextContainerCounter").invoke(entityPlayer);
            return Integer.parseInt(nextContainerCounter.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过反射获取文本通信信息类
     *
     * @param str 通信内容
     * @return 返回对应版本的 ChatComponentText
     */
    public static Object getChatComponentText(String str) {
        try {
            Class<?> aClass = Class.forName("net.minecraft.server.v1_16_R3.ChatComponentText");
            Constructor<?> constructor = aClass.getConstructor(String.class);
            return constructor.newInstance(str);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getOpenGuiType() {
        return 7;
    }

}
