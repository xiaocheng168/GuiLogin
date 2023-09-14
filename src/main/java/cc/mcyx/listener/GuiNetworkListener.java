package cc.mcyx.listener;

import cc.mcyx.FastAuth;
import cc.mcyx.config.ConfigManager;
import cc.mcyx.config.MessageSender;
import cc.mcyx.manager.AuthManager;
import cc.mcyx.nms.CraftNms;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import fr.xephi.authme.api.v3.AuthMeApi;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;

//Gui新风格登录
public class GuiNetworkListener extends PacketAdapter {
    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    public GuiNetworkListener(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (!ConfigManager.getSetting("type", "gui").toString().equalsIgnoreCase("gui")) return;
        try {
            Player player = event.getPlayer();
            //如果玩家没有登录
            if (!AuthMeApi.getInstance().isAuthenticated(player)) {
                //玩家加入服务器
                if (event.getPacketType().equals(PacketType.Play.Client.SETTINGS)) {
                    Bukkit.getScheduler().runTaskLater(FastAuth.fastAuth, () -> openGui(player, "Join"), 10L);
                }

                //玩家关闭窗口
                if (event.getPacketType().equals(PacketType.Play.Client.CLOSE_WINDOW)) {
                    //如果玩家死亡，发送重生命令
                    if (!player.isDead())
                        Bukkit.getScheduler().runTask(FastAuth.fastAuth, () -> openGui(player, "Close Window"));
                }

                //玩家点击物品(按钮)
                if (event.getPacketType().equals(PacketType.Play.Client.WINDOW_CLICK)) {
                    //取消点击事件
                    event.setCancelled(true);
                    //获取玩家登录的密码
                    ItemStack itemStack = event.getPacket().getItemModifier().getValues().get(0);
                    Integer clickSlot = event.getPacket().getIntegers().getValues().get(1);
                    if (itemStack != null && itemStack.getItemMeta() != null) {
                        String password = itemStack.getItemMeta().getDisplayName().replace("§a", "");
                        //禁止空密码
                        if (password.equalsIgnoreCase("")) {
                            MessageSender.sendMessage(player, ConfigManager.getMessage("empty", "不能提交空密码"));
                            openGui(player, "Pwd empty");
                            return;
                        }
                        //校验完后关闭窗口
                        if (AuthManager.auth(player, password, true)) {
                            protocolManager.sendServerPacket(player, protocolManager.createPacket(PacketType.Play.Server.CLOSE_WINDOW));
                            //更新玩家经验等级
                            Bukkit.getScheduler().runTask(FastAuth.fastAuth, () -> player.setExp(player.getExp()));
                        } else openGui(player, "Not login");
                    } else openGui(player, "Not login Click empty");
                }
            }

        } catch (Exception e) {
            MessageSender.sendMessage(event.getPlayer(), "§4服务器登录校验错误!请联系服务器内管理员! 异常点: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 使用 NMS 发包打开Gui界面
     *
     * @param player 操作玩家
     */
    public static void openGui(Player player, String openType) {
        openGui(player, AuthMeApi.getInstance().isRegistered(player.getName()) ? "请登录" : "请注册", openType);
    }

    /**
     * 使用 NMS 发包打开Gui界面
     *
     * @param player 操作玩家
     * @param title  Gui标题
     */

    public static void openGui(Player player, String title, String openType) {
        setLevel(player);
//        System.out.println("open GUI : " + openType);

        //创建 Gui 数据包
        PacketContainer guiPacket = protocolManager.createPacket(PacketType.Play.Server.OPEN_WINDOW);
        int guiId = CraftNms.getPlayerGuiId(player);
        guiPacket.getModifier().write(0, guiId);
        guiPacket.getModifier().write(1, CraftNms.getOpenGuiType());
        //适应多版本，尝试全面使用NMS方式发包
        guiPacket.getModifier().write(2, CraftNms.getChatComponentText(title));


        //设置玩家点击物品
        PacketContainer btnPacket = protocolManager.createPacket(PacketType.Play.Server.SET_SLOT);
        btnPacket.getIntegers().write(0, guiId);
        btnPacket.getIntegers().write(1, 0);
        btnPacket.getItemModifier().write(0, getBtnItemStack());

        try {
            //发送GUI界面数据包
            protocolManager.sendServerPacket(player, guiPacket);
            //设置点击按钮Item
            protocolManager.sendServerPacket(player, btnPacket);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取点击物品
     *
     * @return 返回物品堆 ItemStack
     */
    public static ItemStack getBtnItemStack() {
        String uiBtnMaterial = (String) ConfigManager.getSetting("gui.button_item_material", "APPLE");
        try {
            Material.valueOf(uiBtnMaterial);
        } catch (Exception e) {
            uiBtnMaterial = "APPLE";
        }
        ItemStack itemStack = new ItemStack(Material.valueOf(uiBtnMaterial));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§a");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * 主要是解决一些没有等级的玩家
     * 发送虚拟数据包，让玩家保底有这个经验
     *
     * @param player 操作的玩家
     */
    public static void setLevel(Player player) {
        try {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.EXPERIENCE);
            packet.getIntegers().write(1, 1);
            protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
