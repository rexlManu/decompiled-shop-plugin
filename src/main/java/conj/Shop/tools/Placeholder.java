package conj.Shop.tools;

import conj.Shop.base.Initiate;
import conj.Shop.control.Manager;
import conj.Shop.data.Page;
import conj.Shop.data.PageSlot;
import conj.Shop.enums.Config;
import conj.Shop.interaction.Editor;
import conj.Shop.interaction.Shop;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Placeholder {
   public static List<String> placehold(Player player, List<String> messages, Page page, int slot) {
      List<String> messageList = new ArrayList();
      Iterator var6 = messages.iterator();

      while(var6.hasNext()) {
         String s = (String)var6.next();
         messageList.add(placehold(player, s, page, slot));
      }

      return messageList;
   }

   public static List<String> placehold(Player player, List<String> messages, Page page, Page gui, int x, int slot, int amount, String status, boolean buy) {
      List<String> messageList = new ArrayList();
      Iterator var11 = messages.iterator();

      while(var11.hasNext()) {
         String s = (String)var11.next();
         messageList.add(placehold(player, s, page, gui, x, slot, amount, status, buy));
      }

      return messageList;
   }

   public static String placehold(Player player, String message, Page page, Page gui, int x, int slot, int amount, String status, boolean buy) {
      if (gui != null) {
         PageSlot ps = page.getPageSlot(slot);
         if (status.equalsIgnoreCase("unconfirmed")) {
            if (ps.getDataString("gui_confirm1") != null && message.contains("%confirm%")) {
               message = message.replaceAll("%confirm%", ps.getDataString("gui_confirm1"));
            }
         } else if (ps.getDataString("gui_confirm1") != null && message.contains("%confirm%")) {
            message = message.replaceAll("%confirm%", ps.getDataString("gui_confirm2"));
         }

         if (message.contains("%quantity%")) {
            message = message.replaceAll("%quantity%", new String(ps.getDataString("gui_quantity")));
         }
      }

      return placehold(player, message, page, slot, amount, status, buy);
   }

   public static List<String> placehold(Player player, List<String> messages, Page page) {
      List<String> messageList = new ArrayList();
      Iterator var5 = messages.iterator();

      while(var5.hasNext()) {
         String s = (String)var5.next();
         messageList.add(placehold(player, s, page));
      }

      return messageList;
   }

   public static List<String> placehold(Player player, List<String> messages) {
      List<String> messageList = new ArrayList();
      Iterator var4 = messages.iterator();

      while(var4.hasNext()) {
         String s = (String)var4.next();
         messageList.add(placehold(player, s));
      }

      return messageList;
   }

   public static String placehold(Player player, String message) {
      if (Initiate.placeholderapi) {
         message = PlaceholderAddon.placehold(player, message);
      }

      if (message.contains("%shop_previous%")) {
         message = message.replaceAll("%shop_previous%", (new Manager()).getPreviousPage(player));
      }

      if (message.contains("%shop_current%")) {
         message = message.replaceAll("%shop_current%", (new Manager()).getOpenPage(player));
      }

      if (message.contains("%shop_main%")) {
         message = message.replaceAll("%shop_main%", Config.MAIN_PAGE.toString());
      }

      if (message.contains("%player%")) {
         message = message.replaceAll("%player%", player.getName());
      }

      if (message.contains("%balance%")) {
         message = message.replaceAll("%balance%", DoubleUtil.toString(Initiate.econ.getBalance(player)));
      }

      return ChatColor.translateAlternateColorCodes('&', message);
   }

   public static String placehold(Player player, String message, Page page) {
      message = placehold(player, message);
      if (message.contains("%page%")) {
         message = message.replaceAll("%page%", page.getID());
      }

      if (message.contains("%page_title%")) {
         message = message.replaceAll("%page_title%", page.getTitle());
      }

      return message;
   }

   public static String placehold(Player player, String message, Page page, int slot) {
      message = placehold(player, message, page);
      if (message.contains("%cooldown%")) {
         message = message.replaceAll("%cooldown%", (new Manager()).getCooldown(player, page, slot));
      }

      if (message.contains("%cooldown_day%")) {
         message = message.replaceAll("%cooldown_day%", String.valueOf(Manager.convertMilli((new Manager()).getCooldownMilli(player, page, slot), "day")));
      }

      if (message.contains("%cooldown_hour%")) {
         message = message.replaceAll("%cooldown_hour%", String.valueOf(Manager.convertMilli((new Manager()).getCooldownMilli(player, page, slot), "hour")));
      }

      if (message.contains("%cooldown_minute%")) {
         message = message.replaceAll("%cooldown_minute%", String.valueOf(Manager.convertMilli((new Manager()).getCooldownMilli(player, page, slot), "minute")));
      }

      if (message.contains("%cooldown_second%")) {
         message = message.replaceAll("%cooldown_second%", String.valueOf(Manager.convertMilli((new Manager()).getCooldownMilli(player, page, slot), "second")));
      }

      if (message.contains("%item%")) {
         message = message.replaceAll("%item%", Editor.getItemName(page.getInventory().getItem(slot)));
      }

      PageSlot ps = page.getPageSlot(slot);
      if (ps != null) {
         if (message.contains("%item_cost%")) {
            message = message.replaceAll("%item_cost%", DoubleUtil.toString(page.getPageSlot(slot).getCost()));
         }

         if (message.contains("%item_sell%")) {
            message = message.replaceAll("%item_sell%", DoubleUtil.toString(page.getPageSlot(slot).getSell()));
         }

         if (message.contains("%item_function%")) {
            message = message.replaceAll("%item_function%", page.getPageSlot(slot).getFunction().toString());
         }
      }

      return message;
   }

   public static String placehold(Player player, String message, Page page, int slot, int amount, String status, boolean buy) {
      PageSlot ps = page.getPageSlot(slot);
      message = placehold(player, message, page, slot);
      if (message.contains("%item%")) {
         message = message.replaceAll("%item%", String.valueOf(Editor.getItemName(page.getInventory().getItem(slot))));
      }

      if (message.contains("%earnings%")) {
         message = message.replaceAll("%earnings%", DoubleUtil.toString(ps.getSell() * (double)amount));
      }

      if (message.contains("%price%")) {
         message = message.replaceAll("%price%", DoubleUtil.toString(ps.getCost() * (double)amount));
      }

      if (message.contains("%amount%")) {
         message = message.replaceAll("%amount%", String.valueOf(amount));
      }

      if (message.contains("%confirm%")) {
         message = message.replaceAll("%confirm%", ChatColor.DARK_GREEN + new String(status.equalsIgnoreCase("unconfirmed") ? new String(buy ? "Click to buy" : "Click to sell") : "Click to confirm"));
      }

      return message;
   }

   public static List<String> placehold(Player player, List<String> messages, Page page, int slot, int amount, String status, boolean buy) {
      List<String> messageList = new ArrayList();
      Iterator var9 = messages.iterator();

      while(var9.hasNext()) {
         String s = (String)var9.next();
         messageList.add(placehold(player, s, page, slot, amount, status, buy));
      }

      return messageList;
   }

   public static Inventory placehold(Player player, Inventory inv, Page page, Page gui, int slot, int amount, String status, boolean buy) {
      int x;
      ItemStack i;
      ItemCreator ic;
      for(x = 0; inv.getSize() > x; ++x) {
         i = inv.getItem(x);
         if (i != null) {
            ic = new ItemCreator(i);
            if (ic.getName().equalsIgnoreCase("%item_display%") && page.getInventory().getItem(slot) != null) {
               ItemCreator ic2 = new ItemCreator(page.getInventory().getItem(slot));
               if (ic.hasLore()) {
                  Iterator var13 = ic.getLore().iterator();

                  while(var13.hasNext()) {
                     String l = (String)var13.next();
                     ic2.addLore(l);
                  }
               }

               inv.setItem(x, ic2.getItem());
            }
         }
      }

      for(x = 0; inv.getSize() > x; ++x) {
         i = inv.getItem(x);
         if (i != null) {
            ic = new ItemCreator(i);
            long startlore = System.currentTimeMillis();
            if (ic.hasLore()) {
               ic.setLore(placehold(player, ic.getLore(), page, gui, x, slot, amount, status, buy));
            }

            Debug.log(x + " slot lore took: " + Manager.getDuration(startlore));
            long startname = System.currentTimeMillis();
            if (ic.hasDisplayName()) {
               ic.setName(placehold(player, ic.getName(), page, gui, x, slot, amount, status, buy));
            }

            Debug.log(x + " slot name took: " + Manager.getDuration(startname));
         }
      }

      return inv;
   }

   public static Inventory placehold(Player player, Inventory inv, Page page) {
      for(int x = 0; inv.getSize() > x; ++x) {
         ItemStack i = inv.getItem(x);
         if (i != null) {
            ItemCreator ic = new ItemCreator(i);
            if (ic.hasLore()) {
               ic.setLore(placehold(player, ic.getLore(), page));
            }

            if (ic.hasDisplayName()) {
               ic.setName(placehold(player, ic.getName(), page));
            }
         }
      }

      return updateWorth(player, inv, page);
   }

   public static Inventory updateWorth(Player player, Inventory inv, Page page) {
      InventoryCreator ic = new InventoryCreator(inv);
      String worth = DoubleUtil.toString(Shop.getInventoryWorth(player, inv, page));
      ic.replace("%worth%", worth);
      return ic.getInventory();
   }

   public static void sendMessage(Player player, String message) {
      player.sendMessage(placehold(player, message));
   }

   public static void sendMessage(Player player, List<String> messages) {
      Iterator var3 = messages.iterator();

      while(var3.hasNext()) {
         String s = (String)var3.next();
         player.sendMessage(placehold(player, s));
      }

   }

   public static String[] placehold(Player player, String[] args) {
      for(int x = 0; x < args.length; ++x) {
         args[x] = placehold(player, args[x]);
      }

      return args;
   }
}
