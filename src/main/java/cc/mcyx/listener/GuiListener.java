package cc.mcyx.listener;

import cc.mcyx.AuthLogin;
import fr.xephi.authme.api.v3.AuthMeApi;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class GuiListener implements Listener {
    //监听玩家点击
    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        //获取交互界面
        Inventory clickedInventory = event.getClickedInventory();
        //判断是否为
        if (clickedInventory instanceof AnvilInventory anvilInventory && event.getSlot() == 2) {
            //获取玩家输入的密码
            String password = anvilInventory.getRenameText();
            //判断用户是否已注册
            if (!AuthMeApi.getInstance().isRegistered(player.getName())) {
                //判断密码是否正确
                if (AuthMeApi.getInstance().checkPassword(player.getName(), password)) {
                    //登录成功
                    AuthMeApi.getInstance().forceLogin(player);
                    //关闭
                    player.closeInventory();
                    return;
                }
            } else {
                //尝试注册
                if (AuthMeApi.getInstance().registerPlayer(player.getName(), password)) {
                    //注册成功
                    player.closeInventory();
                }
            }

            //如果登录或者注册不成功重新打开GUI操作
            openGui(player, AuthMeApi.getInstance().isRegistered(player.getName()) ? "请登录" : "请注册");
        }
    }

    //玩家进入服务器
    @EventHandler
    public void onJoin(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        System.out.println("halo");
        System.out.println(isLogin(player));
        openGui(player, AuthMeApi.getInstance().isRegistered(player.getName()) ? "请登录" : "请注册");

       /* if (!isLogin(player)) {
            openGui(player, AuthMeApi.getInstance().isRegistered(player.getName()) ? "请登录" : "请注册");
            System.out.println("halo");
        }*/
    }

    //玩家尝试关闭GUI
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getInventory().getType().equals(InventoryType.ANVIL) && !isLogin(player)) {
            Bukkit.getRegionScheduler().runDelayed(AuthLogin.authLogin, player.getLocation(), (scheduledTask) -> openGui(player, AuthMeApi.getInstance().isRegistered(player.getName()) ? "请登录" : "请注册"), 10);
        }
    }


    //判断玩家是否已登录
    public boolean isLogin(Player player) {
        return AuthMeApi.getInstance().isAuthenticated(player);
    }

    //判断玩家是否已登录
    public boolean isLogin(String playerName) {
        return this.isLogin(Bukkit.getPlayer(playerName));
    }

    //打开GUI界面
    public void openGui(Player player, String title) {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.ANVIL, Component.text(title));
        ItemStack applyBtn = new ItemStack(Material.APPLE);
        inventory.setItem(2, applyBtn);
        player.openInventory(inventory);
    }
}
