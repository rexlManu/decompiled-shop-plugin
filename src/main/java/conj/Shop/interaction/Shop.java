package conj.Shop.interaction;

import conj.Shop.base.Initiate;
import conj.Shop.control.Manager;
import conj.Shop.data.Page;
import conj.Shop.data.PageSlot;
import conj.Shop.enums.Config;
import conj.Shop.enums.Function;
import conj.Shop.enums.GUIFunction;
import conj.Shop.enums.MessageType;
import conj.Shop.enums.PageData;
import conj.Shop.events.PageClickEvent;
import conj.Shop.events.PageCloseEvent;
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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class Shop implements Listener {
   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void pickupPrevention(PlayerPickupItemEvent event) {
      Manager manager = Manager.get();
      String pagename = manager.getOpenPage(event.getPlayer());
      Page page = manager.getPage(pagename);
      if (page != null && page.getType() == 1) {
         event.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void removeEntity(PlayerInteractEntityEvent event) {
      if (Debug.debug) {
         if (!event.getHand().equals(EquipmentSlot.HAND)) {
            return;
         }

         if (!event.getPlayer().isOp()) {
            return;
         }

         if (event.getPlayer().getInventory().getItemInMainHand() != null && event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            ItemCreator ic = new ItemCreator(item);
            if (ic.getLore().contains(ChatColor.RED + "Right-click an entity to remove it") && ic.getName().equals(ChatColor.DARK_RED + "Entity Remover")) {
               Entity e = event.getRightClicked();
               event.getRightClicked().remove();
               Debug.log(e.getName() + " : " + e.getCustomName() + " : " + new String(e instanceof LivingEntity ? "LivingEntity" : "Entity") + " : has been removed from " + e.getLocation().getWorld().getName());
            }
         }
      }

   }

   @EventHandler
   public void ItemviewClose(PageCloseEvent event) {
      if (event.getPageData().equals(PageData.SHOP)) {
         if (!event.isCancelled()) {
            Manager manage = new Manager();
            manage.removeOpenPage(event.getPlayer());
         }

         if (event.getPage().getType() == 1) {
            Inventory inv = event.getInventory();
            Inventory pinv = event.getPage().getInventory(event.getPlayer());
            List<Integer> slots = new ArrayList();

            int slot;
            for(slot = 0; pinv.getSize() > slot; ++slot) {
               if (pinv.getItem(slot) != null) {
                  slots.add(slot);
               }
            }

            Iterator var6 = slots.iterator();

            while(var6.hasNext()) {
               slot = (Integer)var6.next();
               inv.setItem(slot, (ItemStack)null);
            }

            ItemStack[] var8;
            int var7 = (var8 = inv.getContents()).length;

            for(int var13 = 0; var13 < var7; ++var13) {
               ItemStack item = var8[var13];
               if (item != null) {
                  HashMap<Integer, ItemStack> map = event.getPlayer().getInventory().addItem(new ItemStack[]{item});
                  if (!map.isEmpty()) {
                     HashMap<Integer, ItemStack> emap = event.getPlayer().getEnderChest().addItem(new ItemStack[]{(ItemStack)map.get(0)});
                     if (!emap.isEmpty()) {
                        event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), (ItemStack)emap.get(0));
                     }
                  }
               }
            }
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void ItemviewClick(PageClickEvent event) {
      Player player = event.getPlayer();
      int slot = event.getSlot();
      Page page = event.getPage();
      PageSlot ps = page.getPageSlot(slot);
      if (event.getPageData().equals(PageData.SHOP)) {
         ItemStack item;
         if (event.isTopInventory()) {
            if (!event.getItem().getType().equals(Material.AIR)) {
               if (!ps.canSee(player)) {
                  return;
               }

               if (!page.getSlots().contains(slot)) {
                  return;
               }

               Debug.log(player.getName() + " clicked slot " + event.getRawSlot() + " (" + ps.getFunction().toString() + " ) " + "on page " + page != null ? page.getID() : "null");
               if (page.getType() == 1) {
                  if (!event.getPage().getVisibleSlots(player).contains(event.getSlot())) {
                     item = event.getInventory().getItem(event.getSlot());
                     if (item != null) {
                        int first = player.getInventory().firstEmpty();
                        if (first != -1) {
                           player.getInventory().setItem(first, item);
                           event.getInventory().setItem(event.getSlot(), (ItemStack)null);
                           page.updateView(player, false);
                        }
                     }

                     return;
                  }

                  if (ps.getFunction().equals(Function.CONFIRM)) {
                     Inventory inv = Bukkit.createInventory((InventoryHolder)null, event.getInventory().getSize());
                     inv.setContents(event.getInventory().getContents());
                     double earning = sellInventory(player, inv, event.getPage());
                     if (earning > 0.0D) {
                        String complete = Config.SELL_COMPLETE.toString();
                        if (complete.length() > 0) {
                           complete = complete.replaceAll("%worth%", DoubleUtil.toString(earning));
                           player.sendMessage(complete);
                        }

                        event.getTopInventory().setContents(page.getInventory(player).getContents());
                     }

                     return;
                  }
               }

               if (ps.getFunction().equals(Function.NONE)) {
                  return;
               }

               if (ps.hasPermissions()) {
                  Iterator var7 = ps.getPermissions().iterator();

                  while(var7.hasNext()) {
                     String p = (String)var7.next();
                     if (!player.hasPermission(p)) {
                        ps.sendMessage(player, MessageType.PERMISSION.toString());
                        player.closeInventory();
                        return;
                     }
                  }
               }

               if (ps.hasCooldown() && ps.inCooldown(player)) {
                  ps.sendMessage(player, MessageType.COOLDOWN.toString());
                  player.closeInventory();
                  return;
               }

               if (ps.getFunction().equals(Function.BUY)) {
                  if (event.getClick().equals(ClickType.RIGHT)) {
                     item = page.getInventory().getItem(slot);
                     if (ps.getSell() > 0.0D && player.getInventory().containsAtLeast(item, 1)) {
                        this.sellItem(player, page, slot, 0, "unconfirmed");
                     }
                  } else if (event.getClick().equals(ClickType.LEFT) && ps.getCost() <= Initiate.econ.getBalance(player)) {
                     this.buyItem(player, page, slot, 0, "unconfirmed");
                  }
               } else if (ps.getFunction().equals(Function.SELL)) {
                  item = page.getInventory().getItem(slot);
                  if (player.getInventory().containsAtLeast(item, 1)) {
                     this.sellItem(player, page, slot, 0, "unconfirmed");
                  }
               } else if (ps.getFunction().equals(Function.TRADE)) {
                  this.tradeItem(player, page, slot);
               } else if (ps.getFunction().equals(Function.COMMAND)) {
                  VaultAddon addon = new VaultAddon(Initiate.econ);
                  if (!addon.canAfford(player, ps.getCost())) {
                     return;
                  }

                  Iterator var8 = ps.getCommands().iterator();

                  while(var8.hasNext()) {
                     String c = (String)var8.next();
                     Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Placeholder.placehold(player, c, page, slot));
                  }

                  if (ps.hasCooldown() && !ps.inCooldown(player)) {
                     ps.cooldown(player);
                  }
               }
            }
         } else if (page.getType() == 1) {
            item = player.getInventory().getItem(event.getSlot());
            if (item != null) {
               Inventory top = event.getTopInventory();
               int first = top.firstEmpty();
               double worth = Manager.get().getWorth(item);
               if (first != -1 && worth > 0.0D) {
                  top.setItem(first, item);
                  player.getInventory().setItem(event.getSlot(), (ItemStack)null);
                  page.updateView(player, false);
               }
            }
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void tradeItemClick(PageClickEvent event) {
      Player player = event.getPlayer();
      int slot = event.getSlot();
      Page page = event.getPage();
      if (event.getPageData().equals(PageData.TRADE_ITEM)) {
         if (event.getItem().getType().equals(Material.AIR)) {
            return;
         }

         if (event.isTopInventory()) {
            int itemslot = (Integer)event.getGUI().getPass().get("slot");
            String guipagename = (String)event.getGUI().getPass().get("guipage");
            Page guipage = Manager.get().getPage(guipagename);
            PageSlot ips = page.getPageSlot(itemslot);
            boolean no_gui = guipage == null ? true : !guipage.isGUI();
            if (no_gui) {
               if (ips == null) {
                  return;
               }

               if (slot == 21) {
                  page.openPage(player);
               } else if (slot == 23) {
                  ips.attemptTrade(player);
               }
            } else {
               PageSlot ps = guipage.getPageSlot(slot);
               if (ps == null) {
                  return;
               }

               if (ps.getGUIFunction().equals(GUIFunction.BACK)) {
                  page.openPage(player);
               }

               if (ps.getGUIFunction().equals(GUIFunction.CONFIRM)) {
                  ips.attemptTrade(player);
               }
            }
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void PurchaseitemClick(PageClickEvent event) {
      Player player = event.getPlayer();
      int slot = event.getSlot();
      Page page = event.getPage();
      if (event.getPageData().equals(PageData.PURCHASE_ITEM) || event.getPageData().equals(PageData.SELL_ITEM)) {
         if (event.getItem().getType().equals(Material.AIR)) {
            return;
         }

         if (event.isTopInventory()) {
            int itemslot = (Integer)event.getGUI().getPass().get("slot");
            int amount = (Integer)event.getGUI().getPass().get("amount");
            String status = (String)event.getGUI().getPass().get("status");
            String guipagename = (String)event.getGUI().getPass().get("guipage");
            Page guipage = Manager.get().getPage(guipagename);
            if (guipage != null) {
               PageSlot ps = guipage.getPageSlot(slot);
               if (guipage.isGUI()) {
                  Debug.log(player.getName() + " clicked on GUI page " + guipagename);
                  GUIFunction function = ps.getGUIFunction();
                  Debug.log("GUIFunction: " + function);
                  if (function.equals(GUIFunction.CONFIRM)) {
                     Debug.log(player.getName() + " clicked on confirm GUI page " + guipagename);
                     if (status.equalsIgnoreCase("unconfirmed")) {
                        if (amount >= 1) {
                           if (page.instantConfirms()) {
                              this.completeTransaction(event.getPageData(), page, player, itemslot, amount);
                              return;
                           }

                           if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                              this.buyItem(player, page, itemslot, amount, "confirmed");
                           }

                           if (event.getPageData().equals(PageData.SELL_ITEM)) {
                              this.sellItem(player, page, itemslot, amount, "confirmed");
                           }
                        }
                     } else {
                        this.completeTransaction(event.getPageData(), page, player, itemslot, amount);
                     }
                  } else if (function.equals(GUIFunction.QUANTITY)) {
                     Debug.log(player.getName() + " clicked on quantity GUI page " + guipagename);
                     int add = ps.getDataInt("gui_quantity");
                     if (event.getClick().equals(ClickType.LEFT)) {
                        amount += add;
                     }

                     if (event.getClick().equals(ClickType.RIGHT)) {
                        amount -= add;
                     }

                     if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                        this.buyItem(player, page, itemslot, amount, "unconfirmed");
                     }

                     if (event.getPageData().equals(PageData.SELL_ITEM)) {
                        this.sellItem(player, page, itemslot, amount, "unconfirmed");
                     }
                  } else if (function.equals(GUIFunction.BACK)) {
                     Debug.log(player.getName() + " clicked on back GUI page " + guipagename);
                     page.openPage(player);
                  }

                  return;
               }
            }

            if (slot == 4) {
               page.openPage(player);
            }

            if (slot == 49) {
               if (status.equalsIgnoreCase("unconfirmed")) {
                  if (amount >= 1) {
                     if (page.instantConfirms()) {
                        this.completeTransaction(event.getPageData(), page, player, itemslot, amount);
                        return;
                     }

                     if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                        this.buyItem(player, page, itemslot, amount, "confirmed");
                     }

                     if (event.getPageData().equals(PageData.SELL_ITEM)) {
                        this.sellItem(player, page, itemslot, amount, "confirmed");
                     }
                  }
               } else {
                  this.completeTransaction(event.getPageData(), page, player, itemslot, amount);
               }
            }

            int add;
            if (slot == 20) {
               add = 0;
               if (event.getClick().equals(ClickType.LEFT)) {
                  ++add;
               }

               if (event.getClick().equals(ClickType.RIGHT)) {
                  --add;
               }

               if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                  this.buyItem(player, page, itemslot, amount + add, "unconfirmed");
               }

               if (event.getPageData().equals(PageData.SELL_ITEM)) {
                  this.sellItem(player, page, itemslot, amount + add, "unconfirmed");
               }
            }

            if (slot == 21) {
               add = 0;
               if (event.getClick().equals(ClickType.LEFT)) {
                  add += 8;
               }

               if (event.getClick().equals(ClickType.RIGHT)) {
                  add -= 8;
               }

               if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                  this.buyItem(player, page, itemslot, amount + add, "unconfirmed");
               }

               if (event.getPageData().equals(PageData.SELL_ITEM)) {
                  this.sellItem(player, page, itemslot, amount + add, "unconfirmed");
               }
            }

            if (slot == 22) {
               add = 0;
               if (event.getClick().equals(ClickType.LEFT)) {
                  add += 16;
               }

               if (event.getClick().equals(ClickType.RIGHT)) {
                  add -= 16;
               }

               if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                  this.buyItem(player, page, itemslot, amount + add, "unconfirmed");
               }

               if (event.getPageData().equals(PageData.SELL_ITEM)) {
                  this.sellItem(player, page, itemslot, amount + add, "unconfirmed");
               }
            }

            if (slot == 23) {
               add = 0;
               if (event.getClick().equals(ClickType.LEFT)) {
                  add += 32;
               }

               if (event.getClick().equals(ClickType.RIGHT)) {
                  add -= 32;
               }

               if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                  this.buyItem(player, page, itemslot, amount + add, "unconfirmed");
               }

               if (event.getPageData().equals(PageData.SELL_ITEM)) {
                  this.sellItem(player, page, itemslot, amount + add, "unconfirmed");
               }
            }

            if (slot == 24) {
               add = 0;
               if (event.getClick().equals(ClickType.LEFT)) {
                  add += 64;
               }

               if (event.getClick().equals(ClickType.RIGHT)) {
                  add -= 64;
               }

               if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                  this.buyItem(player, page, itemslot, amount + add, "unconfirmed");
               }

               if (event.getPageData().equals(PageData.SELL_ITEM)) {
                  this.sellItem(player, page, itemslot, amount + add, "unconfirmed");
               }
            }

            if (slot == 31) {
               add = 0;
               if (event.getClick().equals(ClickType.LEFT)) {
                  add += 128;
               }

               if (event.getClick().equals(ClickType.RIGHT)) {
                  add -= 128;
               }

               if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                  this.buyItem(player, page, itemslot, amount + add, "unconfirmed");
               }

               if (event.getPageData().equals(PageData.SELL_ITEM)) {
                  this.sellItem(player, page, itemslot, amount + add, "unconfirmed");
               }
            }
         }
      }

   }

   public void completeTransaction(PageData pd, Page page, Player player, int itemslot, int amount) {
      if (pd.equals(PageData.PURCHASE_ITEM)) {
         page.buyItem(player, itemslot, amount);
      }

      if (pd.equals(PageData.SELL_ITEM)) {
         page.sellItem(player, itemslot, amount);
      }

      if (page.closesOnTransaction()) {
         player.closeInventory();
      } else {
         page.openPage(player);
      }

   }

   public void buyItem(Player player, Page page, int slot, int amount, String status) {
      PageSlot ps = page.getPageSlot(slot);
      long start = System.currentTimeMillis();
      if (amount < page.getDefaultQuantity()) {
         amount = page.getDefaultQuantity();
      }

      int affordable = (new VaultAddon(Initiate.econ)).getAffordable(player, ps.getCost(), amount);
      if (amount > affordable) {
         amount = affordable;
      }

      Debug.log("Setup BUY GUI affordable took: " + Manager.getDuration(start));
      Page buypage = Manager.get().getPage(Config.PURCHASE_GUI.toString());
      Inventory mainInv = this.getBuyInventory().getInventory();
      String title = Placeholder.placehold(player, Placeholder.placehold(player, ChatColor.DARK_GREEN + "Buy Item", page, slot, amount, status, true));
      Debug.log("Get BUY GUI inventory took: " + Manager.getDuration(start));
      Inventory viewInv = Placeholder.placehold(player, mainInv, page, buypage, slot, amount, status, true);
      Debug.log("Placehold BUY GUI inventory took: " + Manager.getDuration(start));
      GUI gui = new GUI(Initiate.getPlugin(Initiate.class), PageData.PURCHASE_ITEM, viewInv, page);
      gui.setTitle(title);
      gui.addPass("guipage", Config.PURCHASE_GUI.toString());
      gui.addPass("status", status);
      gui.addPass("slot", slot);
      gui.addPass("amount", amount);
      Debug.log("Load BUY GUI took: " + Manager.getDuration(start));
      gui.open(player);
      Debug.log("Open BUY GUI took: " + Manager.getDuration(start));
   }

   public void tradeItem(Player player, Page page, int slot) {
      long start = System.currentTimeMillis();
      Debug.log("Setup TRADE GUI affordable took: " + Manager.getDuration(start));
      Page tradepage = Manager.get().getPage(Config.TRADE_GUI.toString());
      Inventory mainInv = this.getTradeInventory().getInventory();
      String title = Placeholder.placehold(player, Placeholder.placehold(player, ChatColor.DARK_GREEN + "Trade Item", page, slot));
      PageSlot ps = page.getPageSlot(slot);
      if (ps != null) {
         Iterator var11 = ps.getItems().iterator();

         while(var11.hasNext()) {
            ItemStack i = (ItemStack)var11.next();
            if (mainInv.firstEmpty() == -1) {
               break;
            }

            mainInv.setItem(mainInv.firstEmpty(), i);
         }
      }

      Debug.log("Get TRADE GUI inventory took: " + Manager.getDuration(start));
      Inventory viewInv = Placeholder.placehold(player, mainInv, page, tradepage, slot, 0, "confirmed", true);
      Debug.log("Placehold TRADE GUI inventory took: " + Manager.getDuration(start));
      GUI gui = new GUI(Initiate.getPlugin(Initiate.class), PageData.TRADE_ITEM, viewInv, page);
      gui.setTitle(title);
      gui.addPass("guipage", Config.TRADE_GUI.toString());
      gui.addPass("slot", slot);
      Debug.log("Load TRADE GUI took: " + Manager.getDuration(start));
      gui.open(player);
      Debug.log("Open TRADE GUI took: " + Manager.getDuration(start));
   }

   public void sellItem(Player player, Page page, int slot, int amount, String status) {
      InventoryCreator pi = new InventoryCreator(player.getInventory());
      if (amount < page.getDefaultQuantity()) {
         amount = page.getDefaultQuantity();
      }

      if (amount > pi.getAmount(page.getInventory().getItem(slot))) {
         amount = pi.getAmount(page.getInventory().getItem(slot));
      }

      Page sellpage = Manager.get().getPage(Config.SELL_GUI.toString());
      Inventory mainInv = this.getSellInventory().getInventory();
      String title = Placeholder.placehold(player, Placeholder.placehold(player, ChatColor.DARK_GREEN + "Sell Item", page, slot, amount, status, false));
      Inventory viewInv = Placeholder.placehold(player, mainInv, page, sellpage, slot, amount, status, false);
      GUI gui = new GUI(Initiate.getPlugin(Initiate.class), PageData.SELL_ITEM, viewInv, page);
      gui.setTitle(title);
      gui.addPass("guipage", Config.SELL_GUI.toString());
      gui.addPass("status", status);
      gui.addPass("slot", slot);
      gui.addPass("amount", amount);
      gui.open(player);
   }

   public InventoryCreator getTradeInventory() {
      Page pgui = Manager.get().getPage(Config.TRADE_GUI.toString());
      if (pgui != null && pgui.isGUI()) {
         return new InventoryCreator(pgui.getInventory());
      } else {
         InventoryCreator inv = new InventoryCreator(ChatColor.DARK_GREEN + "Trade Item", 3);
         int[] blank = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 22, 24, 25, 26};
         inv.setBlank(blank, Material.LEGACY_STAINED_GLASS_PANE, 15);
         inv.setItem(23, Material.LEGACY_STAINED_GLASS_PANE, 5, "&aTrade");
         inv.setItem(21, Material.LEGACY_STAINED_GLASS_PANE, 14, "&4Cancel");
         inv.setItem(22, Material.BEDROCK, "%item_display%");
         return inv;
      }
   }

   public InventoryCreator getSellInventory() {
      Page pgui = Manager.get().getPage(Config.SELL_GUI.toString());
      if (pgui != null && pgui.isGUI()) {
         return new InventoryCreator(pgui.getInventory());
      } else {
         InventoryCreator inv = new InventoryCreator(ChatColor.DARK_GREEN + "Sell Item", 6);
         inv.setItem(4, Material.LEGACY_STAINED_GLASS_PANE, 14, "&4Cancel");
         int[] blank = new int[]{3, 5, 11, 12, 13, 14, 15, 18, 19, 25, 26, 29, 30, 32, 33, 39, 40, 41, 48, 50};
         inv.setBlank(blank, Material.LEGACY_STAINED_GLASS_PANE, 15);
         inv.setItem(20, Material.LEGACY_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+1");
         inv.addLore(20, "&eRight-click&7: &c-1");
         inv.setItem(21, Material.LEGACY_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+8");
         inv.addLore(21, "&eRight-click&7: &c-8");
         inv.setItem(22, Material.LEGACY_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+16");
         inv.addLore(22, "&eRight-click&7: &c-16");
         inv.setItem(23, Material.LEGACY_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+32");
         inv.addLore(23, "&eRight-click&7: &c-32");
         inv.setItem(24, Material.LEGACY_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+64");
         inv.addLore(24, "&eRight-click&7: &c-64");
         inv.setItem(31, Material.LEGACY_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+128");
         inv.addLore(31, "&eRight-click&7: &c-128");
         int[] quantity = new int[]{20, 21, 22, 23, 24, 31};
         inv.addLore(quantity, " ");
         inv.addLore(quantity, "&9Amount&7: &9%amount%");
         inv.addLore(quantity, "&aEarnings&7: &a%earnings%");
         inv.setItem(49, Material.BEDROCK, "%item_display%");
         inv.addLore(49, " ");
         inv.addLore(49, "&9Amount&7: &9%amount%");
         inv.addLore(49, "&aEarnings&7: &a%earnings%");
         inv.addLore(49, " ");
         inv.addLore(49, "&2%confirm%");
         return inv;
      }
   }

   public InventoryCreator getBuyInventory() {
      Page pgui = Manager.get().getPage(Config.PURCHASE_GUI.toString());
      if (pgui != null && pgui.isGUI()) {
         return new InventoryCreator(pgui.getInventory());
      } else {
         InventoryCreator inv = new InventoryCreator(ChatColor.DARK_GREEN + "Buy Item", 6);
         inv.setItem(4, Material.LEGACY_STAINED_GLASS_PANE, 14, "&4Cancel");
         int[] blank = new int[]{3, 5, 11, 12, 13, 14, 15, 18, 19, 25, 26, 29, 30, 32, 33, 39, 40, 41, 48, 50};
         inv.setBlank(blank, Material.LEGACY_STAINED_GLASS_PANE, 15);
         inv.setItem(20, Material.LEGACY_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+1");
         inv.addLore(20, "&eRight-click&7: &c-1");
         inv.setItem(21, Material.LEGACY_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+8");
         inv.addLore(21, "&eRight-click&7: &c-8");
         inv.setItem(22, Material.LEGACY_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+16");
         inv.addLore(22, "&eRight-click&7: &c-16");
         inv.setItem(23, Material.LEGACY_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+32");
         inv.addLore(23, "&eRight-click&7: &c-32");
         inv.setItem(24, Material.LEGACY_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+64");
         inv.addLore(24, "&eRight-click&7: &c-64");
         inv.setItem(31, Material.LEGACY_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+128");
         inv.addLore(31, "&eRight-click&7: &c-128");
         int[] quantity = new int[]{20, 21, 22, 23, 24, 31};
         inv.addLore(quantity, " ");
         inv.addLore(quantity, "&9Amount&7: &9%amount%");
         inv.addLore(quantity, "&6Balance&7: &6%balance%");
         inv.addLore(quantity, "&aPrice&7: &a%price%");
         inv.setItem(49, Material.BEDROCK, "%item_display%");
         inv.addLore(49, " ");
         inv.addLore(49, "&9Amount&7: &9%amount%");
         inv.addLore(49, "&6Balance&7: &6%balance%");
         inv.addLore(49, "&aPrice&7: &a%price%");
         inv.addLore(49, " ");
         inv.addLore(49, "&2%confirm%");
         return inv;
      }
   }

   public static List<ItemStack> getAddedItems(OfflinePlayer player, Inventory inventory, Page page) {
      List<ItemStack> added = new ArrayList();
      Inventory copy = Bukkit.createInventory((InventoryHolder)null, inventory.getSize());
      copy.setContents(inventory.getContents());
      Iterator var6 = page.getVisibleSlots(player.getPlayer()).iterator();

      while(var6.hasNext()) {
         int slot = (Integer)var6.next();
         copy.setItem(slot, (ItemStack)null);
      }

      ItemStack[] var8;
      int var7 = (var8 = copy.getContents()).length;

      for(int var10 = 0; var10 < var7; ++var10) {
         ItemStack i = var8[var10];
         if (i != null) {
            added.add(i);
         }
      }

      return added;
   }

   public static double sellInventory(OfflinePlayer player, Inventory inventory, Page page) {
      if (inventory == null) {
         return 0.0D;
      } else {
         double earnings = getInventoryWorth(player, inventory, page);
         if (earnings > 0.0D) {
            Initiate.econ.depositPlayer(player, earnings);
         }

         return earnings;
      }
   }

   public static double getInventoryWorth(OfflinePlayer player, Inventory inventory, Page page) {
      if (inventory == null) {
         return 0.0D;
      } else {
         double worth = 0.0D;
         Iterator var6 = getAddedItems(player, inventory, page).iterator();

         while(var6.hasNext()) {
            ItemStack i = (ItemStack)var6.next();
            if (i != null) {
               worth += Manager.get().getWorth(i);
            }
         }

         return worth;
      }
   }

   public static double getInventoryWorth(Inventory inventory) {
      if (inventory == null) {
         return 0.0D;
      } else {
         double worth = 0.0D;
         ItemStack[] var6;
         int var5 = (var6 = inventory.getContents()).length;

         for(int var4 = 0; var4 < var5; ++var4) {
            ItemStack i = var6[var4];
            if (i != null) {
               worth += Manager.get().getWorth(i);
            }
         }

         return worth;
      }
   }
}
