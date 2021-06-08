package conj.Shop.events;

import conj.Shop.data.Page;
import conj.Shop.tools.Input;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerInputEvent extends Event implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private Page page;
   private Player player;
   private String id;
   private int slot;
   private String msg;
   private Input input;
   private boolean cancelled;

   public PlayerInputEvent(Player player, Page page, String id, String msg, int slot, Input input) {
      this.page = page;
      this.player = player;
      this.id = id;
      this.slot = slot;
      this.input = input;
      this.msg = msg;
   }

   public Page getPage() {
      return this.page;
   }

   public int getSlot() {
      return this.slot;
   }

   public String getID() {
      return this.id;
   }

   public Input getInput() {
      return this.input;
   }

   public String getMessage() {
      return this.msg;
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
