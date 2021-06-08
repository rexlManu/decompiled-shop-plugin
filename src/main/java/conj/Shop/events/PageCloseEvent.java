package conj.Shop.events;

import conj.Shop.data.Page;
import conj.Shop.enums.PageData;
import conj.Shop.tools.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

public class PageCloseEvent extends Event implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private Page page;
   private GUI gui;
   private Player player;
   private Inventory inventory;
   private int slot;
   private boolean cancelled;
   private PageData pagedata;

   public PageCloseEvent(Player player, PageData pagedata, GUI gui, Page page, int slot, Inventory inventory) {
      this.page = page;
      this.slot = slot;
      this.player = player;
      this.gui = gui;
      this.pagedata = pagedata;
      this.inventory = inventory;
   }

   public Page getPage() {
      return this.page;
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   public GUI getGUI() {
      return this.gui;
   }

   public PageData getPageData() {
      return this.pagedata;
   }

   public int getSlot() {
      return this.slot;
   }

   public Player getPlayer() {
      return this.player;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }
}
