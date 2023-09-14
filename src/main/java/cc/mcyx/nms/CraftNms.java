package cc.mcyx.nms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Craft Net Minecraft Server 相关处理
 */
public abstract class CraftNms {

    //解析NMS版本
    public static final String nmsVersion = (Bukkit.getServer()).getClass().getPackage().getName().replace("org.bukkit.craftbukkit.", "");
    //服务器版本号
    public static int serverId;

    static {
        //计算服务器版本号
        String bukkitVersion = Bukkit.getBukkitVersion();
        //获取服务器版本号
        String serverStrId = bukkitVersion.substring(0, bukkitVersion.indexOf("-")).replace(".", "");
        //预防某些1.17后没有的单独版本，出现这种情况后面补一个0
        serverId = Integer.parseInt(
                serverStrId.length() <= 3 ? serverStrId + "0" : serverStrId
        );
    }

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
            Class<?> aClass;
            if (serverId >= 1190) {
                //>= 1.19
                aClass = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
                Method a = aClass.getMethod("a", String.class);
                return a.invoke(aClass, str);
            }
            if (serverId >= 1180) {
                //>= 1.17
                aClass = Class.forName("net.minecraft.network.chat.ChatComponentText");
            } else {
                // < 1.17
                aClass = Class.forName("net.minecraft.server." + nmsVersion + ".ChatComponentText");
            }
            Constructor<?> constructor = aClass.getConstructor(String.class);
            return constructor.newInstance(str);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取打开GUI类型
     * 1.8 - 1.13 string
     * 1.14-1.16 int
     * 1.17+ Containers Object
     *
     * @return 返回GUI打开方案对象
     */

    public static Object getOpenGuiType() {
        //如果版本小于1.13.2以下
        if (serverId <= 1132) {
            return "minecraft:anvil";
        } else if (serverId >= 1170) {
            //高版本1.17=+
            try {
                Class<?> aClass = Class.forName("net.minecraft.world.inventory.Containers");
                Field h = aClass.getDeclaredField("h");
                h.setAccessible(true);
                return h.get(aClass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else return 8;
    }

}
