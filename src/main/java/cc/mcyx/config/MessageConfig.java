package cc.mcyx.config;

import cc.mcyx.FastLogin;

//消息配置处理
public class MessageConfig {
    //获取对应消息
    public static String getMessage(String key, String... def) {
        return FastLogin.fastLogin.getConfig().getString("message." + key, def.length > 0 ? def[0] : "未知消息key数据");
    }
}
