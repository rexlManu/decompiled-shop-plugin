package conj.Shop.data;

import conj.Shop.base.ItemSerialize;
import conj.Shop.control.Manager;
import conj.Shop.enums.Config;
import conj.Shop.enums.Function;
import conj.Shop.enums.GUIFunction;
import conj.Shop.enums.Hidemode;
import conj.Shop.enums.MessageType;
import conj.Shop.tools.InventoryCreator;
import conj.Shop.tools.Placeholder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PageSlot {
   private String page;
   private int slot;
   private double cost;
   private double sell;
   private int cooldown;
   private HashMap<String, Long> cd = new HashMap();
   private Hidemode visibility;
   private Function function;
   private GUIFunction guifunction;
   private List<String> command;
   private List<String> permission;
   private List<String> hidepermission;
   private List<String> pagelore;
   public List<HashMap<Map<String, Object>, Map<String, Object>>> items;
   private HashMap<String, Object> slotdata;

   public PageSlot(String page, int slot) {
      this.visibility = Hidemode.VISIBLE;
      this.function = Function.NONE;
      this.guifunction = GUIFunction.NONE;
      this.command = new ArrayList();
      this.permission = new ArrayList();
      this.hidepermission = new ArrayList();
      this.pagelore = new ArrayList();
      this.items = new ArrayList();
      this.slotdata = new HashMap();
      this.page = page;
      this.slot = slot;
   }

   public PageSlot(String page, int slot, double cost, double sell, int cooldown, HashMap<String, Long> cd, Hidemode visibility, Function function, GUIFunction guifunction, List<String> command, List<String> permission, List<String> hidepermission, List<String> pagelore, List<HashMap<Map<String, Object>, Map<String, Object>>> items, HashMap<String, Object> slotdata) {
      this.visibility = Hidemode.VISIBLE;
      this.function = Function.NONE;
      this.guifunction = GUIFunction.NONE;
      this.command = new ArrayList();
      this.permission = new ArrayList();
      this.hidepermission = new ArrayList();
      this.pagelore = new ArrayList();
      this.items = new ArrayList();
      this.slotdata = new HashMap();
      this.page = page;
      this.slot = slot;
      this.cost = cost;
      this.sell = sell;
      this.cooldown = cooldown;
      this.cd = cd;
      this.visibility = visibility;
      this.function = function;
      this.guifunction = guifunction;
      this.command = (List)(command != null ? command : new ArrayList());
      this.permission = (List)(permission != null ? permission : new ArrayList());
      this.hidepermission = (List)(hidepermission != null ? hidepermission : new ArrayList());
      this.pagelore = (List)(pagelore != null ? pagelore : new ArrayList());
      this.items = (List)(items != null ? items : new ArrayList());
      this.slotdata = slotdata;
   }

   public String getPage() {
      return this.page;
   }

   public int getSlot() {
      return this.slot;
   }

   public void setSlot(int slot) {
      this.slot = slot;
   }

   public void setCooldown(int seconds) {
      this.cooldown = seconds;
   }

   public int getCooldown() {
      return this.cooldown;
   }

   public void setCost(double cost) {
      this.cost = cost;
   }

   public double getCost() {
      return this.cost;
   }

   public void setSell(double cost) {
      this.sell = cost;
   }

   public double getSell() {
      return this.sell;
   }

   public float getDataFloat(String key) {
      return this.getData(key) != null && this.getData(key) instanceof Float ? (Float)this.getData(key) : 0.0F;
   }

   public double getDataDouble(String key) {
      return this.getData(key) != null && this.getData(key) instanceof Double ? (Double)this.getData(key) : 0.0D;
   }

   public int getDataInt(String key) {
      return this.getData(key) != null && this.getData(key) instanceof Integer ? (Integer)this.getData(key) : 0;
   }

   public String getDataString(String key) {
      return this.getData(key) != null && this.getData(key) instanceof String ? (String)this.getData(key) : null;
   }

   public List<?> getDataList(String key) {
      return this.getData(key) != null && this.getData(key) instanceof List ? (List)this.getData(key) : null;
   }

   public HashMap<String, Object> getSlotData() {
      return this.slotdata;
   }

   public Object getData(String key) {
      return this.slotdata.get(key);
   }

   public void setData(String key, Object value) {
      if (this.slotdata == null) {
         this.slotdata = new HashMap();
      }

      this.slotdata.put(key, value);
   }

   public Object removeData(String key) {
      return this.slotdata.remove(key);
   }

   public void setFunction(Function function) {
      this.function = function;
   }

   public void setGUIFunction(GUIFunction function) {
      this.guifunction = function;
   }

   public void setHidemode(Hidemode hidemode) {
      this.visibility = hidemode;
   }

   public Hidemode getHidemode() {
      return this.visibility;
   }

   public Function getFunction() {
      return this.function;
   }

   public GUIFunction getGUIFunction() {
      return this.guifunction;
   }

   public boolean canSee(Player player) {
      boolean see = true;
      Hidemode hidemode = this.getHidemode();
      List<String> permission = this.getPermissions();
      List<String> hidepermission = this.getHidePermissions();
      if (hidemode.equals(Hidemode.HIDDEN)) {
         see = false;
      }

      if (hidemode.equals(Hidemode.OP) && !player.isOp()) {
         see = false;
      }

      if (hidemode.equals(Hidemode.PERMISSION)) {
         String s;
         Iterator var7;
         if (!permission.isEmpty()) {
            var7 = permission.iterator();

            while(var7.hasNext()) {
               s = (String)var7.next();
               if (!player.hasPermission(s)) {
                  see = false;
                  break;
               }
            }
         }

         if (!hidepermission.isEmpty()) {
            var7 = hidepermission.iterator();

            while(var7.hasNext()) {
               s = (String)var7.next();
               if (player.hasPermission(s)) {
                  see = false;
                  break;
               }
            }
         }
      }

      return see;
   }

   public void sendMessage(Player player, String type) {
      List<String> messages = this.getMessage(type);
      Iterator var5 = messages.iterator();

      while(var5.hasNext()) {
         String m = (String)var5.next();
         player.sendMessage(Placeholder.placehold(player, m, Manager.get().getPage(this.page), this.slot));
      }

   }

   public void setMessage(String type, List<String> messages) {
      this.setData(type, messages);
   }

   public boolean addMessage(String type, String message) {
      Object d = this.getData(type);
      boolean restore = false;
      if (d == null) {
         restore = true;
      } else if (((List)d).isEmpty()) {
         restore = true;
      }

      if (restore) {
         List<String> newmessages = new ArrayList();
         newmessages.add(message);
         this.setMessage(type, newmessages);
         return true;
      } else {
         return this.getMessage(type).add(message);
      }
   }

   public List<String> getMessage(String type) {
      MessageType mt = MessageType.fromString(type);
      if (mt == null) {
         return new ArrayList();
      } else {
         Object d = this.getData(type);
         if (d == null) {
            return mt.getDefault();
         } else if (!(d instanceof List)) {
            return mt.getDefault();
         } else {
            return ((List)d).isEmpty() ? mt.getDefault() : (List)d;
         }
      }
   }

   public boolean removeMessage(String type, String message) {
      String removal = null;
      List<String> messages = this.getMessage(type);
      Iterator var6 = messages.iterator();

      while(var6.hasNext()) {
         String s = (String)var6.next();
         if (s.contains(message)) {
            removal = s;
         }
      }

      return messages.remove(removal == null ? message : removal);
   }

   public List<ItemStack> getItems() {
      return ItemSerialize.deserialize(this.items);
   }

   public void setItems(List<ItemStack> items) {
      if (items != null) {
         this.items = ItemSerialize.serialize(items);
      }
   }

   public void addItem(ItemStack item) {
      ItemStack clone = new ItemStack(item);
      List<ItemStack> items = this.getItems();
      if (items.add(clone)) {
         this.setItems(items);
      }

   }

   public void removeItem(ItemStack item) {
      List<ItemStack> items = this.getItems();
      if (items.remove(item)) {
         this.setItems(items);
      }

   }

   public boolean attemptTrade(Player player) {
      Page page = Manager.get().getPage(this.page);
      if (page == null) {
         return false;
      } else if (page.getInventory().getItem(this.slot) == null) {
         return false;
      } else {
         boolean b = false;
         InventoryCreator ic = new InventoryCreator(this.getItems());
         List<ItemStack> items_only = new ArrayList();

         ItemStack item;
         for(int i = 0; i < 36; ++i) {
            item = player.getInventory().getItem(i);
            if (item != null && !item.getType().equals(Material.AIR)) {
               items_only.add(new ItemStack(item));
            }
         }

         InventoryCreator pic = new InventoryCreator(items_only, 4);
         Iterator var8 = ic.getInventory().iterator();

         while(var8.hasNext()) {
            item = (ItemStack)var8.next();
            if (item != null && !item.getType().equals(Material.AIR)) {
               int amount = ic.getAmount(item);
               if (!player.getInventory().containsAtLeast(item, amount)) {
                  b = true;
               }

               pic.getInventory().remove(item);
            }
         }

         if (!b) {
            if (pic.canAdd(page.getInventory().getItem(this.slot))) {
               var8 = this.getItems().iterator();

               while(var8.hasNext()) {
                  item = (ItemStack)var8.next();
                  player.getInventory().removeItem(new ItemStack[]{item});
               }

               player.getInventory().addItem(new ItemStack[]{page.getInventory().getItem(this.slot)});
               if (page.closesOnTransaction()) {
                  player.closeInventory();
               } else {
                  page.openPage(player);
               }
            } else {
               player.sendMessage(Config.TRADE_NEED_SPACE.toString());
            }
         }

         return !b;
      }
   }

   public List<String> getCommands() {
      return this.command;
   }

   public void setCommands(List<String> commands) {
      this.command = commands;
   }

   public void addCommand(String command) {
      if (this.command != null) {
         this.command.add(command);
      } else {
         List<String> newlist = new ArrayList();
         newlist.add(command);
         this.command = newlist;
      }

   }

   public boolean removeCommand(String command) {
      return this.command.remove(command);
   }

   public HashMap<String, Long> getCooldowns() {
      return this.cd;
   }

   public long getCooldown(Player player) {
      long cooldown = 0L;
      if (this.cd != null) {
         Iterator var5 = this.cd.entrySet().iterator();

         while(var5.hasNext()) {
            Entry<String, Long> e = (Entry)var5.next();
            if (((String)e.getKey()).equals(player.getUniqueId().toString())) {
               cooldown = (Long)e.getValue();
            }
         }
      }

      return cooldown;
   }

   public boolean inCooldown(Player player) {
      return this.inCooldown(this.getCooldown(player));
   }

   public boolean inCooldown(long cooldown) {
      long difference = (System.currentTimeMillis() - cooldown) / 1000L;
      return difference < (long)this.getCooldown();
   }

   public boolean hasCooldown() {
      return this.cooldown != 0;
   }

   public boolean uncooldown(Player player) {
      return this.cd.remove(player.getUniqueId().toString()) != null;
   }

   public Long cooldown(Player player) {
      return (Long)this.cd.put(player.getUniqueId().toString(), System.currentTimeMillis());
   }

   public List<String> getPermissions() {
      return this.permission;
   }

   public boolean hasPermissions() {
      return !this.permission.isEmpty();
   }

   public void addPermission(String permission) {
      if (this.permission != null) {
         this.permission.add(permission);
      } else {
         List<String> newlist = new ArrayList();
         newlist.add(permission);
         this.permission = newlist;
      }

   }

   public boolean removePermission(String permission) {
      return this.permission.remove(permission);
   }

   public List<String> getHidePermissions() {
      return this.hidepermission;
   }

   public boolean hasHidePermissions() {
      return !this.hidepermission.isEmpty();
   }

   public void addHidePermission(String permission) {
      if (this.hidepermission != null) {
         this.hidepermission.add(permission);
      } else {
         List<String> newlist = new ArrayList();
         newlist.add(permission);
         this.hidepermission = newlist;
      }

   }

   public boolean removeHidePermission(String permission) {
      return this.hidepermission.remove(permission);
   }

   public List<String> getPageLore() {
      return this.pagelore;
   }

   public boolean hasPageLore() {
      return !this.pagelore.isEmpty();
   }

   public void addPageLore(String lore) {
      if (this.pagelore != null) {
         this.pagelore.add(lore);
      } else {
         List<String> newlist = new ArrayList();
         newlist.add(lore);
         this.pagelore = newlist;
      }

   }

   public boolean removePageLore(String lore) {
      return this.pagelore.remove(lore);
   }
}
