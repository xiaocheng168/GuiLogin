package cc.mcyx.listener;

import fr.xephi.authme.api.v3.AuthMeApi;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiListener implements Listener {
    //监听玩家点击
    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        //获取交互界面
        Inventory clickedInventory = event.getClickedInventory();
        //判断是否为
        if (clickedInventory instanceof AnvilInventory anvilInventory) {
            //获取玩家输入的密码
            String password = anvilInventory.getRenameText();
            //判断密码是否正确
            if (AuthMeApi.getInstance().checkPassword(player.getName(), password)) {
                //登录成功
                AuthMeApi.getInstance().forceLogin(player);
                //关闭
                player.closeInventory();
            } else {
                //如果登录不成功重新打开GUI登录
                this.openGui(player);
            }
        }
    }

    //玩家进入服务器
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        openGui(player);
    }

    //打开GUI界面
    public void openGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.ANVIL, Component.text("请输入"));
        ItemStack applyBtn = new ItemStack(Material.APPLE);
        inventory.setItem(2, applyBtn);
        player.openInventory(inventory);
    }
}
