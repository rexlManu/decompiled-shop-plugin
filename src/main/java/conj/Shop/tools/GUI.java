package conj.Shop.tools;

import conj.Shop.data.Page;
import conj.Shop.enums.PageData;
import conj.Shop.events.PageClickEvent;
import conj.Shop.events.PageCloseEvent;
import conj.Shop.events.PageOpenEvent;
import java.util.HashMap;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class GUI implements Listener {
   private Inventory inventory;
   private String title;
   private Plugin plugin;
   private Page page;
   private PageData data;
   private String viewer;
   private HashMap<Object, Object> pass;

   public PageData getData() {
      return this.data;
   }

   public HashMap<Object, Object> getPass() {
      return this.pass;
   }

   public Plugin getPlugin() {
      return this.plugin;
   }

   public Page getPage() {
      return this.page;
   }

   public String getTitle() {
      return this.title;
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   public void addPass(Object entry, Object data) {
      if (this.pass == null) {
         this.pass = new HashMap();
      }

      this.pass.put(entry, data);
   }

   public void setInventory(Inventory inventory) {
      this.inventory = inventory;
   }

   public void setData(PageData data) {
      this.data = data;
   }

   public void setTitle(String title) {
      this.title = StringUtils.left(title, 32);
   }

   public void register() {
      PluginManager manager = this.plugin.getServer().getPluginManager();
      manager.registerEvents(this, this.plugin);
   }

   public void destroy() {
      InventoryClickEvent.getHandlerList().unregister(this);
      InventoryCloseEvent.getHandlerList().unregister(this);
      PluginDisableEvent.getHandlerList().unregister(this);
   }

   public void open(Player player) {
      this.destroy();
      if (this.getTitle() != null) {
         Inventory i = Bukkit.createInventory((InventoryHolder)null, this.inventory.getSize(), this.getTitle());
         i.setContents(this.inventory.getContents());
         this.inventory = i;
      }

      PageOpenEvent e = new PageOpenEvent(player, this.data, this, this.page, 0, this.getInventory());
      Bukkit.getServer().getPluginManager().callEvent(e);
      if (!e.isCancelled()) {
         player.openInventory(this.inventory);
         this.register();
         this.viewer = player.getUniqueId().toString();
      }

   }

   public GUI(Plugin plugin, PageData data, Inventory inventory, Page page) {
      this.plugin = plugin;
      this.data = data;
      this.page = page;
      this.inventory = inventory;
   }

   public GUI(GUI gui) {
      this.plugin = gui.getPlugin();
      this.data = gui.getData();
      this.page = gui.getPage();
      this.inventory = gui.getInventory();
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void InteractEditor(InventoryClickEvent event) {
      if (event.getWhoClicked() instanceof Player) {
         Player player = (Player)event.getWhoClicked();
         if (this.viewer.equals(player.getUniqueId().toString()) && event.getClickedInventory() != null) {
            boolean top = event.getRawSlot() < event.getView().getTopInventory().getSize();
            Debug.log(event.getWhoClicked().getName() + " clicked " + new String(top ? "top" : "bottom") + " of " + this.data + " on page " + this.page != null ? this.page.getID() : "null");
            PageClickEvent e = new PageClickEvent(player, this.data, this, this.page, event.getSlot(), event.getRawSlot(), event.getCurrentItem(), event.getInventory(), event.getClickedInventory(), event.getClick(), top);
            Bukkit.getServer().getPluginManager().callEvent(e);
            if (!e.isCancelled()) {
               event.setCancelled(true);
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void InteractEditor(PlayerQuitEvent event) {
      if (event.getPlayer() instanceof Player) {
         Player player = event.getPlayer();
         if (this.viewer.equals(player.getUniqueId().toString())) {
            this.destroy();
         }

      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void InteractEditor(InventoryCloseEvent event) {
      if (event.getPlayer() instanceof Player) {
         Player player = (Player)event.getPlayer();
         if (this.viewer.equals(player.getUniqueId().toString())) {
            PageCloseEvent e = new PageCloseEvent(player, this.data, this, this.page, 0, event.getInventory());
            Bukkit.getServer().getPluginManager().callEvent(e);
            if (!e.isCancelled()) {
               this.destroy();
            } else {
               this.open(player);
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public void InteractEditor(PluginDisableEvent event) {
      if (this.viewer != null && Bukkit.getPlayer(UUID.fromString(this.viewer)) != null) {
         Bukkit.getPlayer(UUID.fromString(this.viewer)).closeInventory();
      }

   }
}
