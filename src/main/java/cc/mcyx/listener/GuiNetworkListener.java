package cc.mcyx.listener;

import cc.mcyx.FastAuth;
import cc.mcyx.config.ConfigManager;
import cc.mcyx.config.MessageSender;
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

import static cc.mcyx.listener.ChatNetworkListener.authMeApi;

//Gui新风格登录
public class GuiNetworkListener extends PacketAdapter {
    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

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
                    Bukkit.getScheduler().runTask(FastAuth.fastAuth, () -> openGui(player, "请登录"));
                }
                if (event.getPacketType().equals(PacketType.Play.Client.CLOSE_WINDOW)) {
                    Bukkit.getScheduler().runTask(FastAuth.fastAuth, () -> openGui(player, "请登录"));
                }
                if (event.getPacketType().equals(PacketType.Play.Client.WINDOW_CLICK)) {

                    //取消点击事件
                    event.setCancelled(true);
                    String playerName = player.getName();

                    //获取玩家登录的密码
                    ItemStack itemStack = event.getPacket().getItemModifier().getValues().get(0);
                    if (itemStack != null) {
                        String password = itemStack.getItemMeta().getDisplayName();
                        //玩家是否已注册
                        if (AuthMeApi.getInstance().isRegistered(playerName)) {
                            //登录过程 Login
                            //判断是否登录成功 成功将听过验证 否则失败！
                            if (authMeApi.checkPassword(playerName, password)) {
                                authMeApi.forceLogin(player);
                                MessageSender.sendMessage(player, ConfigManager.getMessage("login.success", "登录成功"));
                                protocolManager.sendServerPacket(player, protocolManager.createPacket(PacketType.Play.Server.CLOSE_WINDOW));
                            } else {
                                MessageSender.sendMessage(player, ConfigManager.getMessage("login.error", "登录失败，可能是密码不正确哦"));
                                this.openGui(player, "请登录");
                            }

                        } else {
                            //注册过程 Register
                            MessageSender.sendMessage(player, authMeApi.registerPlayer(playerName, password) ? ConfigManager.getMessage("register.success", "注册成功") : ConfigManager.getMessage("register.error", "登录失败,可能是账号或者密码错误"));
                            //注册完后是否自动登录
                            if ((Boolean) (ConfigManager.getSetting("register_auto_login")))
                                authMeApi.forceLogin(player);
                        }
                    }
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
     * @param title  Gui标题
     */
    public void openGui(Player player, String title) {
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