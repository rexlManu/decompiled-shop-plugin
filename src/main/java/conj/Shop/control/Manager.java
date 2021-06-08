package conj.Shop.control;

import conj.Shop.base.Initiate;
import conj.Shop.base.ItemSerialize;
import conj.Shop.data.Page;
import conj.Shop.data.PageSlot;
import conj.Shop.tools.NPCAddon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Manager {
   public static HashMap<String, String> openpage = new HashMap();
   public static HashMap<String, String> previouspage = new HashMap();
   public static HashMap<String, String> edit = new HashMap();
   public static ArrayList<String> blacklist = new ArrayList();
   public static ArrayList<Page> pages = new ArrayList();
   public static HashMap<Integer, String> cnpcs = new HashMap();
   public static HashMap<Integer, List<String>> cnpcpermissions = new HashMap();
   public static HashMap<String, Double> worth = new HashMap();
   public static HashMap<String, Page> pagerecovery = new HashMap();

   public double getWorth(ItemStack item) {
      return this.getFlatWorth(item) * (double)item.getAmount();
   }

   public double getFlatWorth(ItemStack item) {
      String serialize = ItemSerialize.serializeSoft(item);
      double value = worth.get(serialize) != null ? (Double)worth.get(serialize) : 0.0D;
      if (value == 0.0D) {
         ItemStack i = new ItemStack(item.getType());
         String serialize2 = ItemSerialize.serializeSoft(i);
         value = worth.get(serialize2) != null ? (Double)worth.get(serialize2) : 0.0D;
      }

      return value;
   }

   public void setWorth(ItemStack item, double w, boolean save) {
      String serialize = ItemSerialize.serializeSoft(item);
      worth.put(serialize, w);
      if (save) {
         Initiate.sf.saveWorthData();
      }

   }

   public List<String> getBlacklist() {
      return blacklist;
   }

   public boolean blacklistContains(String world) {
      return blacklist.contains(world);
   }

   public void blacklistAdd(String world) {
      if (!blacklist.contains(world)) {
         blacklist.add(world);
      }

   }

   public void blacklistRemove(String world) {
      if (blacklist.contains(world)) {
         blacklist.remove(world);
      }

   }

   public Page getRecoveryPage(Player player) {
      return (Page)pagerecovery.get(player.getUniqueId().toString());
   }

   public boolean hasRecoveryPage(Player player) {
      return pagerecovery.get(player.getUniqueId().toString()) != null;
   }

   public List<Page> getPages() {
      return pages;
   }

   public Page getPage(String page) {
      if (page == null) {
         return null;
      } else {
         Iterator var3 = pages.iterator();

         while(var3.hasNext()) {
            Page p = (Page)var3.next();
            if (p.getID().equalsIgnoreCase(page)) {
               return p;
            }
         }

         return null;
      }
   }

   public Page getPage(NPC npc) {
      return npc.hasTrait(NPCAddon.class) ? this.getPage((String)cnpcs.get(npc.getId())) : null;
   }

   public static String convertMilli(long millis) {
      long hours = TimeUnit.MILLISECONDS.toHours(millis);
      long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1L);
      long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1L);
      String time = "";
      if (seconds > 0L) {
         time = seconds + (seconds > 1L ? " seconds" : " second");
      }

      if (minutes > 0L) {
         time = minutes + (minutes > 1L ? " minutes" : " minute") + (seconds > 0L ? " " : "") + time;
      }

      if (hours > 0L) {
         time = hours + (hours > 1L ? " hours" : " hour") + (minutes <= 0L && seconds <= 0L ? "" : " ") + time;
      }

      return time == "" ? "0 seconds" : time;
   }

   public static long convertMilli(long millis, String type) {
      if (type.equalsIgnoreCase("hour")) {
         return TimeUnit.MILLISECONDS.toHours(millis);
      } else if (type.equalsIgnoreCase("day")) {
         return TimeUnit.MILLISECONDS.toDays(millis);
      } else if (type.equalsIgnoreCase("minute")) {
         return TimeUnit.MILLISECONDS.toMinutes(millis);
      } else {
         return type.equalsIgnoreCase("second") ? TimeUnit.MILLISECONDS.toSeconds(millis) : 0L;
      }
   }

   public static String getDuration(long start) {
      long seconds = System.currentTimeMillis() / 1000L - start / 1000L;
      String remaining = convertMilli(TimeUnit.SECONDS.toMillis(seconds));
      return remaining;
   }

   public String getCooldown(Player player, Page page, int slot) {
      PageSlot ps = page.getPageSlot(slot);
      long seconds = (long)ps.getCooldown() - (System.currentTimeMillis() / 1000L - ps.getCooldown(player) / 1000L);
      String remaining = convertMilli(TimeUnit.SECONDS.toMillis(seconds));
      return remaining;
   }

   public long getCooldownMilli(Player player, Page page, int slot) {
      PageSlot ps = page.getPageSlot(slot);
      long milli = (long)ps.getCooldown() - (System.currentTimeMillis() - ps.getCooldown(player));
      return milli;
   }

   public List<String> getViewers(Page page) {
      List<String> viewers = new ArrayList();
      Iterator var4 = openpage.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<String, String> value = (Entry)var4.next();
         if (((String)value.getValue()).equalsIgnoreCase(page.getID())) {
            viewers.add((String)value.getKey());
         }
      }

      return viewers;
   }

   public void setOpenPage(Player player, String page) {
      openpage.put(player.getName(), page);
   }

   public void setPreviousPage(Player player, String page) {
      previouspage.put(player.getName(), page);
   }

   public void removePreviousPage(Player player) {
      if (previouspage.containsKey(player.getName())) {
         previouspage.remove(player.getName());
      }

   }

   public void removeOpenPage(Player player) {
      if (openpage.containsKey(player.getName())) {
         openpage.remove(player.getName());
      }

   }

   public String getOpenPage(Player player) {
      if (openpage == null) {
         return "";
      } else {
         return openpage.containsKey(player.getName()) ? (String)openpage.get(player.getName()) : "";
      }
   }

   public String getPreviousPage(Player player) {
      if (previouspage == null) {
         return "";
      } else {
         return previouspage.containsKey(player.getName()) ? (String)previouspage.get(player.getName()) : "";
      }
   }

   public String getEditorPage(Player player) {
      return edit.containsKey(player.getName()) ? (String)edit.get(player.getName()) : "";
   }

   public static List<String> getAvailableCommands(Player player, String type) {
      List<String> commands = new ArrayList();
      if (type.equalsIgnoreCase("page")) {
         if (player.hasPermission("shop.page.create")) {
            commands.add(ChatColor.GREEN + "/shop page create " + ChatColor.GRAY + "<entry>");
         }

         if (player.hasPermission("shop.page.delete")) {
            commands.add(ChatColor.GREEN + "/shop page delete " + ChatColor.GRAY + "<page>");
         }

         if (player.hasPermission("shop.page.manage")) {
            commands.add(ChatColor.GREEN + "/shop page manage " + ChatColor.GRAY + "<page>");
         }

         if (player.hasPermission("shop.page.edit")) {
            commands.add(ChatColor.GREEN + "/shop page edit " + ChatColor.GRAY + "[<page>]");
         }

         if (player.hasPermission("shop.page.size")) {
            commands.add(ChatColor.GREEN + "/shop page size " + ChatColor.GRAY + "<1-6>");
         }

         if (player.hasPermission("shop.page.add")) {
            commands.add(ChatColor.GREEN + "/shop page add " + ChatColor.GRAY + "<cost> <sell>");
         }

         if (player.hasPermission("shop.page.copy")) {
            commands.add(ChatColor.GREEN + "/shop page copy " + ChatColor.GRAY + "<page>");
         }

         if (player.hasPermission("shop.page.title")) {
            commands.add(ChatColor.GREEN + "/shop page title " + ChatColor.GRAY + "<title>");
         }

         if (player.hasPermission("shop.page.type")) {
            commands.add(ChatColor.GREEN + "/shop page type " + ChatColor.GRAY + "<normal/sell>");
         }

         if (player.hasPermission("shop.page.open")) {
            commands.add(ChatColor.GREEN + "/shop page open " + ChatColor.GRAY + "<page>");
         }

         if (player.hasPermission("shop.page.properties")) {
            commands.add(ChatColor.GREEN + "/shop page properties");
         }

         if (player.hasPermission("shop.page.recover")) {
            commands.add(ChatColor.GREEN + "/shop page recover");
         }

         if (player.hasPermission("shop.page.clear")) {
            commands.add(ChatColor.GREEN + "/shop page clear");
         }

         if (player.hasPermission("shop.page.list")) {
            commands.add(ChatColor.GREEN + "/shop page list");
         }
      } else if (type.equalsIgnoreCase("citizen")) {
         if (player.hasPermission("shop.citizen.page")) {
            commands.add(ChatColor.GREEN + "/shop citizen page " + ChatColor.GRAY + "<page>");
         }

         if (player.hasPermission("shop.citizen.permission.add")) {
            commands.add(ChatColor.GREEN + "/shop citizen permission add " + ChatColor.GRAY + "<permission>");
         }

         if (player.hasPermission("shop.citizen.permission.remove")) {
            commands.add(ChatColor.GREEN + "/shop citizen permission remove " + ChatColor.GRAY + "<permission>");
         }

         if (player.hasPermission("shop.citizen.permission.clear")) {
            commands.add(ChatColor.GREEN + "/shop citizen permission clear");
         }

         if (player.hasPermission("shop.citizen.permission.clear") || player.hasPermission("shop.citizen.permission.add") || player.hasPermission("shop.citizen.permission.remove")) {
            commands.add(ChatColor.GREEN + "/shop citizen permission");
         }
      } else if (type.equalsIgnoreCase("console")) {
         if (player.hasPermission("shop.console")) {
            commands.add(ChatColor.GREEN + "/shop open page " + ChatColor.GRAY + "<page> <player>");
            commands.add(ChatColor.GREEN + "/shop page open " + ChatColor.GRAY + "<page> <player>");
            commands.add(ChatColor.GREEN + "/shop page move " + ChatColor.GRAY + "<page> <from> <to> [<soft> or <hard>]");
            commands.add(ChatColor.GREEN + "/shop close inventory " + ChatColor.GRAY + "<player>");
            commands.add(ChatColor.GREEN + "/shop send broadcast " + ChatColor.GRAY + "<message>");
            commands.add(ChatColor.GREEN + "/shop send message " + ChatColor.GRAY + "<player> <message>");
            commands.add(ChatColor.GREEN + "/shop take money " + ChatColor.GRAY + "<amount> <player>");
            commands.add(ChatColor.GREEN + "/shop cooldown clear " + ChatColor.GRAY + "<player>");
            commands.add(ChatColor.GREEN + "/shop teleport " + ChatColor.GRAY + "<player> <world> <x> <y> <z> [<yaw> <pitch>]");
         }
      } else if (type.equalsIgnoreCase("blacklist")) {
         if (player.hasPermission("shop.blacklist.add")) {
            commands.add(ChatColor.GREEN + "/shop blacklist add");
         }

         if (player.hasPermission("shop.blacklist.remove")) {
            commands.add(ChatColor.GREEN + "/shop blacklist remove");
         }

         if (player.hasPermission("shop.blacklist.list")) {
            commands.add(ChatColor.GREEN + "/shop blacklist list");
         }
      } else if (type.equalsIgnoreCase("worth")) {
         if (player.hasPermission("shop.worth.set")) {
            commands.add(ChatColor.GREEN + "/shop worth set " + ChatColor.GRAY + "<amount>");
         }

         if (player.hasPermission("shop.worth.list")) {
            commands.add(ChatColor.GREEN + "/shop worth list");
         }

         if (player.hasPermission("shop.worth.item")) {
            commands.add(ChatColor.GREEN + "/shop worth");
         }
      }

      return commands;
   }

   public static Manager get() {
      return new Manager();
   }

   public void setCitizenPage(int id, String page) {
      if (page == null) {
         if (cnpcs.containsKey(id)) {
            cnpcs.remove(id);
         }

      } else {
         cnpcs.put(id, page);
         Initiate.sf.saveCitizensData();
      }
   }

   public boolean addCitizenPermission(int id, String permission) {
      List<String> permissions = new ArrayList();
      if (cnpcpermissions.get(id) != null) {
         permissions = (List)cnpcpermissions.get(id);
      }

      boolean add = ((List)permissions).add(permission);
      cnpcpermissions.put(id, permissions);
      Initiate.sf.saveCitizensData();
      return add;
   }

   public boolean removeCitizenPermission(int id, String permission) {
      List<String> permissions = new ArrayList();
      if (cnpcpermissions.get(id) != null) {
         permissions = (List)cnpcpermissions.get(id);
      }

      boolean remove = ((List)permissions).remove(permission);
      cnpcpermissions.put(id, permissions);
      Initiate.sf.saveCitizensData();
      return remove;
   }

   public void clearCitizenPermissions(int id) {
      if (cnpcpermissions.get(id) != null) {
         cnpcpermissions.remove(id);
      }

   }

   public List<String> getCitizenPermissions(int id) {
      return (List)(cnpcpermissions.get(id) != null ? (List)cnpcpermissions.get(id) : new ArrayList());
   }
}
