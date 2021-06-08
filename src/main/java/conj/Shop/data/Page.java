package conj.Shop.data;

import conj.Shop.base.Initiate;
import conj.Shop.base.ItemSerialize;
import conj.Shop.control.Manager;
import conj.Shop.enums.Config;
import conj.Shop.enums.Function;
import conj.Shop.enums.GUIFunction;
import conj.Shop.enums.Hidemode;
import conj.Shop.enums.PageData;
import conj.Shop.events.PageCreateEvent;
import conj.Shop.events.PageDeleteEvent;
import conj.Shop.interaction.Editor;
import conj.Shop.interaction.Shop;
import conj.Shop.tools.Debug;
import conj.Shop.tools.DoubleUtil;
import conj.Shop.tools.GUI;
import conj.Shop.tools.InventoryCreator;
import conj.Shop.tools.ItemCreator;
import conj.Shop.tools.Placeholder;
import conj.Shop.tools.VaultAddon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class Page {
   private String id;
   public String title;
   public int size;
   public int type;
   public boolean gui;
   public List<HashMap<Map<String, Object>, Map<String, Object>>> items = new ArrayList();
   public List<Integer> slots = new ArrayList();
   public HashMap<String, Object> pagedata = new HashMap();
   public HashMap<Integer, PageSlot> pageslots = new HashMap();

   public Page(String id) {
      this.id = id;
   }

   public Page(Page page) {
      this.id = new String(page.getID());
      this.title = new String(page.getTitle());
      this.size = new Integer(page.getSize());
      this.type = new Integer(page.getType());
      this.gui = new Boolean(page.isGUI());
      this.items = new ArrayList(page.getItemsMap());
      this.slots = new ArrayList(page.getSlots());
      this.pagedata = new HashMap(page.getData());
      this.pageslots = new HashMap(page.pageslots);
   }

   public boolean createOverride() {
      PageCreateEvent event = new PageCreateEvent(this);
      Bukkit.getServer().getPluginManager().callEvent(event);
      Manager manager = new Manager();
      if (!event.isCancelled()) {
         Page p = manager.getPage(this.getID());
         if (p != null) {
            p.delete();
         }

         return this.create();
      } else {
         return false;
      }
   }

   public boolean create() {
      PageCreateEvent event = new PageCreateEvent(this);
      Bukkit.getServer().getPluginManager().callEvent(event);
      if (!event.isCancelled() && (new Manager()).getPage(this.getID()) == null) {
         Manager.pages.add(this);
         return true;
      } else {
         return false;
      }
   }

   public boolean delete() {
      PageDeleteEvent event = new PageDeleteEvent(this);
      Bukkit.getServer().getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         Manager manager = new Manager();
         Page p = manager.getPage(this.id);
         Manager.pages.remove(p);
         if (Initiate.sf.getPageFile(p.getID()).exists()) {
            Initiate.sf.getPageFile(p.getID()).getFile().delete();
         }

         return true;
      } else {
         return false;
      }
   }

   public void copy(Page page) {
      Manager.pages.remove(this);
      Page copypage = new Page(page);
      copypage.setID(this.id);
      Manager.pages.add(copypage);
   }

   public void clearItems() {
      this.setItems(new ArrayList(), new ArrayList());
   }

   public void setID(String id) {
      this.id = id;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public void setSize(int size) {
      this.size = size;
   }

   public void setItems(List<ItemStack> items, List<Integer> slots) {
      this.items = ItemSerialize.serialize(items);
      this.slots = slots;
   }

   public void setItems(Inventory inventory) {
      List<ItemStack> items = new ArrayList();
      List<Integer> slots = new ArrayList();

      for(int x = 0; inventory.getSize() > x; ++x) {
         ItemStack item = inventory.getItem(x);
         if (item != null) {
            items.add(item);
            slots.add(x);
         }
      }

      this.items = ItemSerialize.serialize(items);
      this.slots = slots;
   }

   public void setItem(int slot, ItemStack item) {
      Inventory i = this.getInventory();
      i.setItem(slot, item);
      this.setItems(i);
   }

   public boolean moveItemSoft(int from, int to) {
      ItemStack fromItem = this.getInventory().getItem(from);
      ItemStack toItem = this.getInventory().getItem(to);
      this.setItem(to, fromItem);
      this.setItem(from, toItem);
      return true;
   }

   public boolean moveItem(int from, int to) {
      ItemStack fromItem = this.getInventory().getItem(from);
      ItemStack toItem = this.getInventory().getItem(to);
      this.setItem(to, fromItem);
      this.setItem(from, toItem);
      this.swapProperties(from, to);
      return true;
   }

   public boolean swapProperties(int from, int to) {
      if (this.getInventory().getSize() > from && this.getInventory().getSize() > to) {
         PageSlot fromslot = this.getPageSlot(from);
         PageSlot toslot = this.getPageSlot(to);
         this.removePageSlot(fromslot);
         this.removePageSlot(toslot);
         fromslot.setSlot(to);
         toslot.setSlot(from);
         this.addPageSlot(fromslot);
         this.addPageSlot(toslot);
         return true;
      } else {
         return false;
      }
   }

   public void setType(int type) {
      this.type = type;
   }

   public PageSlot addPageSlot(PageSlot ps) {
      return (PageSlot)this.pageslots.put(ps.getSlot(), ps);
   }

   public PageSlot removePageSlot(int slot) {
      return (PageSlot)this.pageslots.remove(slot);
   }

   public PageSlot removePageSlot(PageSlot ps) {
      return (PageSlot)this.pageslots.remove(ps.getSlot());
   }

   public int getType() {
      return this.type;
   }

   public String getID() {
      return this.id;
   }

   public String getTitle() {
      return this.title != null ? StringUtils.left(this.title, 32) : "Shop";
   }

   public int getSize() {
      return this.size > 0 && this.size < 7 ? this.size * 9 : 54;
   }

   public PageSlot getPageSlot(int slot) {
      if (this.pageslots.get(slot) == null) {
         PageSlot newdata = new PageSlot(this.getID(), slot);
         this.addPageSlot(newdata);
      }

      return (PageSlot)this.pageslots.get(slot);
   }

   public HashMap<Integer, PageSlot> getPageSlots() {
      return this.pageslots;
   }

   public List<Integer> getVisibleSlots(Player player) {
      ArrayList<Integer> slots = new ArrayList();
      Iterator var4 = this.getSlots().iterator();

      while(var4.hasNext()) {
         int slot = (Integer)var4.next();
         PageSlot ps = this.getPageSlot(slot);
         if (ps.canSee(player)) {
            slots.add(slot);
         }
      }

      return slots;
   }

   public List<Integer> getSlots() {
      return this.slots;
   }

   public List<ItemStack> getItems() {
      return ItemSerialize.deserialize(this.items);
   }

   public List<HashMap<Map<String, Object>, Map<String, Object>>> getItemsMap() {
      return this.items;
   }

   public HashMap<String, Object> getData() {
      return this.pagedata;
   }

   public Object getData(Object o) {
      return this.pagedata.get(o);
   }

   public boolean hasData(Object o) {
      return this.pagedata.get(o) != null;
   }

   public Inventory getInventory() {
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, this.getSize(), this.getTitle());

      for(int x = 0; this.getItems().size() > x; ++x) {
         int slot = (Integer)this.slots.get(x);
         ItemStack item = (ItemStack)this.getItems().get(x);
         if (slot < this.getSize()) {
            inv.setItem(slot, item);
         }
      }

      return inv;
   }

   public Inventory getInventory(Player player) {
      Inventory inv = this.getInventoryFlat(player);
      return InventoryCreator.hasPlaceholder(inv) ? Placeholder.placehold(player, inv, this) : inv;
   }

   public boolean isGUI() {
      return this.gui;
   }

   public void uncooldown(Player player) {
      Iterator var3 = this.pageslots.values().iterator();

      while(var3.hasNext()) {
         PageSlot ps = (PageSlot)var3.next();
         ps.uncooldown(player);
      }

   }

   public Inventory getInventoryFlat(Player player) {
      Inventory inv = this.getInventory();

      for(int x = 0; x < inv.getSize(); ++x) {
         PageSlot ps = this.getPageSlot(x);
         ItemStack i = inv.getItem(x);
         boolean destroy = false;
         if (i != null) {
            destroy = !ps.canSee(player);
            if (!destroy) {
               ItemCreator ic;
               double balance;
               if (ps.getFunction().equals(Function.COMMAND)) {
                  ic = new ItemCreator(i);
                  balance = Initiate.econ.getBalance(player);
                  if (balance < ps.getCost() && !this.hidesAffordability()) {
                     ic.addLore(Config.COST_CANNOT_AFFORD.toString());
                  }
               } else if (ps.getFunction().equals(Function.BUY)) {
                  ic = new ItemCreator(i);
                  balance = Initiate.econ.getBalance(player);
                  ic.addLore(" ");
                  ic.addLore(Config.COST_PREFIX.toString() + DoubleUtil.toString(ps.getCost()));
                  if (ps.getSell() > 0.0D) {
                     ic.addLore(Config.SELL_PREFIX.toString() + DoubleUtil.toString(ps.getSell()));
                  }

                  if (balance < ps.getCost() && !this.hidesAffordability()) {
                     ic.addLore(Config.COST_CANNOT_AFFORD.toString());
                  }
               } else if (ps.getFunction().equals(Function.SELL)) {
                  ic = new ItemCreator(i);
                  ic.addLore("");
                  ic.addLore(Config.SELL_PREFIX.toString() + DoubleUtil.toString(ps.getSell()));
               }
            }
         }

         if (destroy) {
            inv.setItem(x, (ItemStack)null);
         }
      }

      return inv;
   }

   public void updateView(Player player, boolean hard) {
      String pagename = Manager.get().getOpenPage(player);
      if (pagename.equals(this.getID())) {
         if (hard) {
            this.openPage(player);
            return;
         }

         Inventory open = player.getOpenInventory().getTopInventory();
         List<Integer> slots = this.getVisibleSlots(player);
         Debug.log(player.getName() + " : " + this.getID() + " : " + DoubleUtil.toString(Shop.getInventoryWorth(player, open, this)));
         Iterator var7 = slots.iterator();

         while(var7.hasNext()) {
            int s = (Integer)var7.next();
            ItemStack i = this.getInventoryFlat(player).getItem(s);
            if (i != null) {
               ItemCreator ic = new ItemCreator(i);
               ic.placehold(player, this, s);
               ic.replace("%worth%", DoubleUtil.toString(Shop.getInventoryWorth(player, open, this)));
               open.setItem(s, ic.getItem());
            }
         }
      }

   }

   public Inventory openPage(Player player) {
      Manager manage = new Manager();
      if (!manage.getOpenPage(player).equalsIgnoreCase("")) {
         manage.setPreviousPage(player, manage.getOpenPage(player));
      }

      manage.setOpenPage(player, this.id);
      Inventory inv = this.getInventory(player);
      if (this.getFill() != null) {
         InventoryCreator ic = new InventoryCreator(inv);
         ic.setFill(this.getFill());
      }

      for(int i = 0; i < inv.getSize(); ++i) {
         ItemStack item = inv.getItem(i);
         if (item != null) {
            PageSlot ps = this.getPageSlot(i);
            if (ps.hasPageLore()) {
               ItemCreator ic = new ItemCreator(item);
               Iterator var9 = ps.getPageLore().iterator();

               while(var9.hasNext()) {
                  String s = (String)var9.next();
                  ic.addLore(Placeholder.placehold(player, s, this));
               }
            }
         }
      }

      GUI gui = new GUI(Initiate.getPlugin(Initiate.class), PageData.SHOP, inv, this);
      String title = Placeholder.placehold(player, this.getTitle());
      gui.setTitle(title);
      gui.open(player);
      manage.setOpenPage(player, this.id);
      return inv;
   }

   public void openEditor(Player player) {
      InventoryCreator inv = new InventoryCreator(this.getInventory());

      for(int x = 0; x < inv.getInventory().getSize(); ++x) {
         PageSlot ps = this.getPageSlot(x);
         ItemStack i = inv.getInventory().getItem(x);
         if (i != null) {
            double cost = ps.getCost();
            Function f = ps.getFunction();
            Hidemode h = ps.getHidemode();
            inv.addLore(x, " ");
            Iterator var11 = ps.getPageLore().iterator();

            while(var11.hasNext()) {
               String s = (String)var11.next();
               inv.addLore(x, ChatColor.translateAlternateColorCodes('&', s));
            }

            if (!ps.getPageLore().isEmpty()) {
               inv.addLore(x, " ");
            }

            inv.addLore(x, ChatColor.BLUE + "Function" + ChatColor.DARK_GRAY + ": " + new String(!this.isGUI() ? ChatColor.BLUE + ps.getFunction().toString() : ChatColor.BLUE + ps.getGUIFunction().toString()));
            if (this.isGUI() && ps.getGUIFunction().equals(GUIFunction.QUANTITY)) {
               inv.addLore(x, ChatColor.DARK_GREEN + "Quantity" + ChatColor.DARK_GRAY + ": " + ChatColor.DARK_GREEN + ps.getDataInt("gui_quantity"));
            }

            if (!this.isGUI()) {
               inv.addLore(x, ChatColor.YELLOW + "Visibility" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + h.toString());
            }

            if (!f.equals(Function.NONE) && !f.equals(Function.CONFIRM) && !f.equals(Function.SELL) && !f.equals(Function.TRADE) && !this.isGUI()) {
               inv.addLore(x, ChatColor.DARK_GREEN + "Cost" + ChatColor.DARK_GRAY + ": " + ChatColor.DARK_GREEN + DoubleUtil.toString(cost));
            }

            if (!f.equals(Function.NONE) && !f.equals(Function.CONFIRM) && !f.equals(Function.COMMAND) && !f.equals(Function.TRADE) && !this.isGUI()) {
               inv.addLore(x, ChatColor.GREEN + "Sell" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + DoubleUtil.toString(ps.getSell()));
            }

            if (!f.equals(Function.NONE) && !f.equals(Function.CONFIRM) && ps.hasCooldown() && !this.isGUI()) {
               inv.addLore(x, ChatColor.LIGHT_PURPLE + "Cooldown" + ChatColor.DARK_GRAY + ": " + ChatColor.LIGHT_PURPLE + ps.getCooldown());
            }
         }
      }

      GUI gui = new GUI(Initiate.getPlugin(Initiate.class), PageData.EDIT_ITEM_VIEW, inv.getInventory(), this);
      gui.setTitle(ChatColor.YELLOW + this.id);
      gui.open(player);
   }

   public void buyItem(Player player, int slot, int amount) {
      PageSlot ps = this.getPageSlot(slot);
      ItemStack item = this.getInventory().getItem(slot);
      ItemCreator itemc = new ItemCreator(item);
      item = itemc.placehold(player, this, slot);
      int affordable = (new VaultAddon(Initiate.econ)).getAffordable(player, ps.getCost(), amount);
      if (Initiate.econ.getBalance(player) >= ps.getCost() * (double)amount) {
         int failed;
         for(failed = 0; failed < amount; ++failed) {
            Map<Integer, ItemStack> map = player.getInventory().addItem(new ItemStack[]{item});
            if (!map.values().isEmpty()) {
               --affordable;
            }
         }

         failed = amount - affordable;
         double finalprice = ps.getCost() * (double)affordable;
         if (finalprice <= 0.0D) {
            finalprice = 0.0D;
         }

         if (finalprice > 0.0D) {
            Initiate.econ.withdrawPlayer(player, finalprice);
         }

         List<String> purchase = Config.SHOP_PURCHASE.getList();
         Iterator var13 = purchase.iterator();

         while(var13.hasNext()) {
            String s = (String)var13.next();
            s = Placeholder.placehold(player, s, this);
            s = s.replaceAll("%item%", Editor.getItemName(item));
            s = s.replaceAll("%quantity%", String.valueOf(amount - failed));
            s = s.replaceAll("%cost%", DoubleUtil.toString(finalprice));
            s = s.replaceAll("%failed%", String.valueOf(failed));
            player.sendMessage(s);
         }

         if (ps.hasCooldown() && !ps.inCooldown(player)) {
            ps.cooldown(player);
         }

      }
   }

   public void sellItem(Player player, int slot, int amount) {
      PageSlot ps = this.getPageSlot(slot);
      ItemStack item = this.getInventory().getItem(slot);
      int failed = 0;

      int affordable;
      for(affordable = 0; affordable < amount; ++affordable) {
         Map<Integer, ItemStack> map = player.getInventory().removeItem(new ItemStack[]{item});
         if (!map.values().isEmpty()) {
            ++failed;
         }
      }

      affordable = amount - failed;
      double finalprice = ps.getSell() * (double)affordable;
      if (finalprice <= 0.0D) {
         finalprice = 0.0D;
      }

      if (finalprice > 0.0D) {
         Initiate.econ.depositPlayer(player, finalprice);
      }

      List<String> sell = Config.SHOP_SELL.getList();
      Iterator var12 = sell.iterator();

      while(var12.hasNext()) {
         String s = (String)var12.next();
         s = Placeholder.placehold(player, s, this);
         s = s.replaceAll("%item%", Editor.getItemName(item));
         s = s.replaceAll("%quantity%", String.valueOf(amount - failed));
         s = s.replaceAll("%cost%", DoubleUtil.toString(finalprice));
         player.sendMessage(s);
      }

      if (ps.hasCooldown() && !ps.inCooldown(player)) {
         ps.cooldown(player);
      }

   }

   public void updateViewers(boolean hard) {
      Iterator var3 = Manager.get().getViewers(this).iterator();

      while(var3.hasNext()) {
         String p = (String)var3.next();
         Player player = Bukkit.getPlayer(p);
         if (player != null) {
            this.updateView(player, hard);
         }
      }

   }

   public void closeViewers() {
      Iterator var2 = Manager.get().getViewers(this).iterator();

      while(var2.hasNext()) {
         String p = (String)var2.next();
         Player player = Bukkit.getPlayer(p);
         if (player != null) {
            player.closeInventory();
         }
      }

   }

   public void saveData() {
      if (Initiate.sf != null) {
         Initiate.sf.savePageData(this.getID());
      } else {
         Debug.log("Failed to save page: " + this.getID());
      }

   }

   public ItemStack getFill() {
      if (this.pagedata.get("fill_item") != null && this.pagedata.get("fill_item") instanceof List) {
         ItemStack item = ItemSerialize.deserializeSingle((List)this.pagedata.get("fill_item"));
         if (item != null) {
            return item;
         }
      }

      return null;
   }

   public void clearFill() {
      this.pagedata.remove("fill_item");
   }

   public void setFill(ItemStack item) {
      if (item == null) {
         this.clearFill();
      } else if (item.getType().equals(Material.AIR)) {
         this.clearFill();
      } else {
         ItemStack serializeItem = new ItemStack(item);
         List<HashMap<Map<String, Object>, Map<String, Object>>> serialize = ItemSerialize.serializeSingle(serializeItem);
         this.pagedata.put("fill_item", serialize);
      }
   }

   public boolean instantConfirms() {
      if (this.pagedata.get("instant_confirm") != null) {
         Debug.log("instantConfirms(): " + Boolean.parseBoolean((String)this.pagedata.get("instant_confirm")));
         return Boolean.parseBoolean((String)this.pagedata.get("instant_confirm"));
      } else {
         return false;
      }
   }

   public boolean closesOnTransaction() {
      if (this.pagedata.get("close_on_transaction") != null) {
         Debug.log("closesOnTransaction(): " + Boolean.parseBoolean((String)this.pagedata.get("close_on_transaction")));
         return Boolean.parseBoolean((String)this.pagedata.get("close_on_transaction"));
      } else {
         return true;
      }
   }

   public boolean hidesAffordability() {
      if (this.pagedata.get("hide_affordability") != null) {
         Debug.log("hidesAffordability(): " + Boolean.parseBoolean((String)this.pagedata.get("hide_affordability")));
         return Boolean.parseBoolean((String)this.pagedata.get("hide_affordability"));
      } else {
         return false;
      }
   }

   public void setHideAffordability(boolean hide) {
      Debug.log("setHideAffordability(" + hide + "): " + this.pagedata.get("hide_affordability"));
      this.pagedata.put("hide_affordability", String.valueOf(hide));
   }

   public void setInstantConfirm(boolean confirm) {
      Debug.log("setInstantConfirm(" + confirm + "): " + this.pagedata.get("instant_confirm"));
      this.pagedata.put("instant_confirm", String.valueOf(confirm));
   }

   public void setCloses(boolean closes) {
      Debug.log("setCloses(" + closes + "): " + this.pagedata.get("close_on_transaction"));
      this.pagedata.put("close_on_transaction", String.valueOf(closes));
   }

   public void setDefaultQuantity(int quantity) {
      this.pagedata.put("default_quantity", quantity);
      Debug.log("setDefaultQuantity(" + quantity + "): " + this.pagedata.get("default_quantity"));
   }

   public int getDefaultQuantity() {
      if (this.pagedata.get("default_quantity") != null) {
         int quantity = 0;

         try {
            quantity = (Integer)this.pagedata.get("default_quantity");
         } catch (NumberFormatException var3) {
         }

         Debug.log("getDefaultQuantity(): " + quantity);
         return quantity;
      } else {
         return 0;
      }
   }
}
