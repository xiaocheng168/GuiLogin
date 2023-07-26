package cc.mcyx.config;

import cc.mcyx.FastAuth;

//消息配置处理
public class ConfigManager {
    /**
     * 获取消息节点配置节点信息
     *
     * @param key 节点路径
     * @param def 如果数据为null默认返回这个数据
     * @return 返回这个读取到的节点数据或者默认数据
     */
    public static String getMessage(String key, String... def) {
        return FastAuth.fastAuth.getConfig().getString("message." + key, def.length > 0 ? def[0] : "未知消息key数据");
    }

    /**
     * 获取设置配置节点信息
     *
     * @param key 节点路径
     * @param def 如果数据为null默认返回这个数据
     * @return 返回这个读取到的节点数据或者默认数据
     */
    public static Object getSetting(String key, Object... def) {
        return FastAuth.fastAuth.getConfig().get("setting." + key, def.length > 0 ? def[0] : "未知消息key数据");
    }
}
