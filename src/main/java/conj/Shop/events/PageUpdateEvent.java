package conj.Shop.events;

import conj.Shop.data.Page;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PageUpdateEvent extends Event implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private Page page;
   private boolean cancelled;

   public PageUpdateEvent(Page page) {
      this.page = page;
   }

   public Page getPage() {
      return this.page;
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
