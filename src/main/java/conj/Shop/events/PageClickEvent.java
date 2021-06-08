package conj.Shop.events;

import conj.Shop.data.Page;
import conj.Shop.enums.PageData;
import conj.Shop.tools.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PageClickEvent extends Event implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private Page page;
   private GUI gui;
   private Player player;
   private int slot;
   private int rawslot;
   private ItemStack item;
   private PageData action;
   private ClickType click;
   private Inventory inv;
   private Inventory topinv;
   private boolean cancelled;
   private boolean top;

   public PageClickEvent(Player player, PageData action, GUI gui, Page page, int slot, int rawslot, ItemStack item, Inventory topinv, Inventory inv, ClickType click, boolean top) {
      this.item = item;
      this.page = page;
      this.slot = slot;
      this.rawslot = rawslot;
      this.player = player;
      this.action = action;
      this.inv = inv;
      this.topinv = topinv;
      this.gui = gui;
      this.click = click;
      this.top = top;
   }

   public Page getPage() {
      return this.page;
   }

   public GUI getGUI() {
      return this.gui;
   }

   public Inventory getTopInventory() {
      return this.topinv;
   }

   public Inventory getInventory() {
      return this.inv;
   }

   public PageData getPageData() {
      return this.action;
   }

   public ClickType getClick() {
      return this.click;
   }

   public boolean isTopInventory() {
      return this.top;
   }

   public int getSlot() {
      return this.slot;
   }

   public int getRawSlot() {
      return this.rawslot;
   }

   public ItemStack getItem() {
      return this.item;
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
