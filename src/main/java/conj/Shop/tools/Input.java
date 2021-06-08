package conj.Shop.tools;

import conj.Shop.base.Initiate;
import conj.Shop.control.Manager;
import conj.Shop.data.Page;
import conj.Shop.events.PlayerInputEvent;
import conj.Shop.interaction.Editor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class Input implements Listener {
   private String page;
   private String id;
   private Player player;
   private int slot;
   private Plugin plugin;

   public Input(Player player, String page, int slot, String id) {
      this.player = player;
      this.page = page;
      this.id = id;
      this.slot = slot;
      this.plugin = Initiate.getPlugin(Initiate.class);
   }

   public Player getPlayer() {
      return this.player;
   }

   public Page getPage() {
      return (new Manager()).getPage(this.page);
   }

   public String getID() {
      return this.id;
   }

   public int getSlot() {
      return this.slot;
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void enterInput(PlayerCommandPreprocessEvent event) {
      Player player = event.getPlayer();
      if (this.player != null) {
         if (this.player.getUniqueId().equals(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Input required");
         }

      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void enterInput(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      if (this.player != null) {
         if (this.player.getUniqueId().equals(player.getUniqueId())) {
            event.setCancelled(true);
            String fullmsg = event.getMessage();
            String msg = ChatColor.stripColor(event.getMessage());
            if (msg.equalsIgnoreCase("-cancel")) {
               Editor.editItem(player, (new Manager()).getPage(this.page), this.slot);
               this.destroy();
               return;
            }

            if (msg.equalsIgnoreCase("&&")) {
               fullmsg = " ";
            }

            Page page = this.getPage();
            if (page != null) {
               PlayerInputEvent e = new PlayerInputEvent(player, page, this.id, fullmsg, this.slot, this);
               Bukkit.getServer().getPluginManager().callEvent(e);
               if (e.isCancelled()) {
                  event.setCancelled(true);
               }
            } else {
               this.destroy();
            }
         }

      }
   }

   public void register() {
      PluginManager manager = this.plugin.getServer().getPluginManager();
      manager.registerEvents(this, this.plugin);
   }

   public void unregister() {
      AsyncPlayerChatEvent.getHandlerList().unregister(this);
      PluginDisableEvent.getHandlerList().unregister(this);
      PlayerCommandPreprocessEvent.getHandlerList().unregister(this);
      InventoryClickEvent.getHandlerList().unregister(this);
      InventoryCloseEvent.getHandlerList().unregister(this);
   }

   public void destroyData() {
      this.page = null;
      this.id = null;
      this.player = null;
      this.plugin = null;
   }

   public void destroy() {
      this.destroyData();
      this.unregister();
   }
}
