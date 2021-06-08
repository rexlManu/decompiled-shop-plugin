package conj.Shop.data;

import conj.Shop.control.Manager;
import conj.Shop.enums.Config;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Sign implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void interactSign(PlayerInteractEvent event) {
      Block block = event.getClickedBlock();
      if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && block != null && (block.getType().equals(Material.LEGACY_WALL_SIGN) || block.getType().equals(Material.LEGACY_SIGN_POST))) {
         org.bukkit.block.Sign sign = (org.bukkit.block.Sign)block.getState();
         String line = sign.getLine(0);
         String pagename = sign.getLine(1);
         if (line != null && pagename != null && ChatColor.stripColor(line).equalsIgnoreCase(ChatColor.stripColor(Config.SIGN_TAG.toString()))) {
            Manager manage = new Manager();
            Page page = manage.getPage(pagename);
            if (page != null) {
               page.openPage(event.getPlayer());
            }
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void interactSign(BlockBreakEvent event) {
      Player player = event.getPlayer();
      Block block = event.getBlock();
      if (block.getType().equals(Material.LEGACY_WALL_SIGN) || block.getType().equals(Material.LEGACY_SIGN_POST)) {
         org.bukkit.block.Sign sign = (org.bukkit.block.Sign)block.getState();
         if (sign.getLine(0) != null && sign.getLine(0).equalsIgnoreCase(Config.SIGN_TAG.toString()) && !player.hasPermission("shop.sign.break")) {
            event.setCancelled(true);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void interactSign(SignChangeEvent event) {
      if (event.getLine(0) != null) {
         Player player = event.getPlayer();
         String top = event.getLine(0);
         if (ChatColor.stripColor(top).equalsIgnoreCase(ChatColor.stripColor(Config.SIGN_TAG.toString())) && !player.hasPermission("shop.sign.create")) {
            event.setCancelled(true);
            return;
         }

         if (player.hasPermission("shop.sign.create") && top.equalsIgnoreCase("//shop//")) {
            String page = event.getLine(1);
            if (page != null && (new Manager()).getPage(page) != null) {
               event.setLine(0, Config.SIGN_TAG.toString());
               event.setLine(1, (new Manager()).getPage(page).getID());
               return;
            }
         }
      }

   }
}
