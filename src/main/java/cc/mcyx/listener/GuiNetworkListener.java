package cc.mcyx.listener;

import cc.mcyx.FastAuth;
import cc.mcyx.config.ConfigManager;
import cc.mcyx.config.MessageSender;
import cc.mcyx.manager.AuthManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.xephi.authme.api.v3.AuthMeApi;
import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.inventory.Containers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

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
                if (event.getPacketType().equals(PacketType.Play.Client.CUSTOM_PAYLOAD)) {
                    Bukkit.getScheduler().runTask(FastAuth.fastAuth, () -> openGui(player));
                }
                //玩家关闭窗口
                if (event.getPacketType().equals(PacketType.Play.Client.CLOSE_WINDOW)) {
                    Bukkit.getScheduler().runTask(FastAuth.fastAuth, () -> openGui(player));
                }
                //玩家点击物品(按钮)
                if (event.getPacketType().equals(PacketType.Play.Client.WINDOW_CLICK)) {
                    //取消点击事件
                    event.setCancelled(true);
                    //获取玩家登录的密码
                    ItemStack itemStack = event.getPacket().getItemModifier().getValues().get(0);
                    if (itemStack != null && itemStack.getItemMeta() != null) {
                        String password = itemStack.getItemMeta().getDisplayName();
                        //禁止空密码
                        if (password.equalsIgnoreCase("")) {
                            MessageSender.sendMessage(player, ConfigManager.getMessage("empty", "不能提交空密码"));
                            openGui(player);
                            return;
                        }
                        //校验完后关闭窗口
                        if (AuthManager.auth(player, password, true))
                            protocolManager.sendServerPacket(player, protocolManager.createPacket(PacketType.Play.Server.CLOSE_WINDOW));
                    } else MessageSender.sendMessage(player, "点哪呢?嗯?~~");
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
    public static void openGui(Player player) {
        openGui(player, AuthMeApi.getInstance().isRegistered(player.getName()) ? "请登录" : "请注册");
    }

    /**
     * 使用 NMS 发包打开Gui界面
     *
     * @param player 操作玩家
     * @param title  Gui标题
     */
    public static void openGui(Player player, String title) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer handle = craftPlayer.getHandle();
        //创建 Gui 数据包
        int guiId = handle.nextContainerCounter();
        Containers<ContainerAnvil> guiInventory = Containers.h;
        ContainerAnvil containerAnvil = guiInventory.a(guiId, handle.fN());
        //发送 Gui 数据包
        PacketPlayOutOpenWindow packetPlayOutOpenWindow = new PacketPlayOutOpenWindow(guiId, containerAnvil.a(), IChatBaseComponent.a(title));
        craftPlayer.getHandle().c.a(packetPlayOutOpenWindow);
        //设置物品
        ItemStack itemStack = new ItemStack(Material.APPLE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component.text(""));
        itemStack.setItemMeta(itemMeta);
        //发送设置物品包
        PacketPlayOutSetSlot packetPlayOutSetSlot = new PacketPlayOutSetSlot(guiId, 0, 0, CraftItemStack.asNMSCopy(itemStack));
        craftPlayer.getHandle().c.a(packetPlayOutSetSlot);
    }
}
