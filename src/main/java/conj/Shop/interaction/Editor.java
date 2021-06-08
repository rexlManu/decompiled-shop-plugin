package conj.Shop.interaction;

import conj.Shop.base.Initiate;
import conj.Shop.data.Page;
import conj.Shop.data.PageSlot;
import conj.Shop.enums.Function;
import conj.Shop.enums.GUIFunction;
import conj.Shop.enums.Hidemode;
import conj.Shop.enums.MessageType;
import conj.Shop.enums.PageData;
import conj.Shop.events.PageClickEvent;
import conj.Shop.events.PlayerInputEvent;
import conj.Shop.tools.Debug;
import conj.Shop.tools.DoubleUtil;
import conj.Shop.tools.GUI;
import conj.Shop.tools.Input;
import conj.Shop.tools.InventoryCreator;
import conj.Shop.tools.ItemCreator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Editor implements Listener {
   @EventHandler
   public void PageviewClick(PageClickEvent event) {
      Player player = event.getPlayer();
      int slot = event.getSlot();
      Page page = event.getPage();
      ItemStack item = event.getItem();
      if (event.getPageData().equals(PageData.EDIT_ITEM_VIEW)) {
         if (event.getClick().equals(ClickType.RIGHT)) {
            if (event.isTopInventory()) {
               if (item != null && !item.getType().equals(Material.AIR)) {
                  this.moveItem(player, event.getGUI(), slot, true);
               }
            } else if (item != null && !item.getType().equals(Material.AIR)) {
               this.moveItem(player, event.getGUI(), slot, false);
            }
         } else if (event.getClick().equals(ClickType.LEFT)) {
            if (event.isTopInventory() && item != null && !item.getType().equals(Material.AIR)) {
               editItem(player, event.getPage(), slot);
            }
         } else if (event.getClick().equals(ClickType.SHIFT_RIGHT)) {
            this.moveItems(player, page);
         }
      } else if (event.getPageData().equals(PageData.MOVE_ITEM)) {
         if (event.getClick().equals(ClickType.SHIFT_RIGHT)) {
            page.setItems(event.getGUI().getInventory());
            page.openEditor(player);
         } else {
            event.setCancelled(true);
         }
      } else {
         int itemslot;
         if (event.getPageData().equals(PageData.EDIT_ITEM_MOVE)) {
            itemslot = (Integer)event.getGUI().getPass().get("old");
            Inventory i = page.getInventory();
            Inventory pi = player.getInventory();
            boolean wasTop = (Boolean)event.getGUI().getPass().get("top");
            ItemStack selecteditem;
            ItemStack clickeditem;
            if (event.isTopInventory()) {
               if (wasTop) {
                  if (i.getItem(itemslot) == null) {
                     page.openEditor(player);
                     return;
                  }

                  selecteditem = new ItemStack(i.getItem(itemslot));
                  clickeditem = i.getItem(slot);
                  i.setItem(slot, selecteditem);
                  i.setItem(itemslot, clickeditem);
                  page.swapProperties(itemslot, slot);
                  page.setItems(i);
                  page.openEditor(player);
                  return;
               }

               if (pi.getItem(itemslot) == null) {
                  page.openEditor(player);
                  return;
               }

               selecteditem = new ItemStack(pi.getItem(itemslot));
               clickeditem = i.getItem(slot);
               i.setItem(slot, selecteditem);
               pi.setItem(itemslot, clickeditem);
               page.setItems(i);
               page.openEditor(player);
               return;
            }

            if (wasTop) {
               if (i.getItem(itemslot) == null) {
                  page.openEditor(player);
                  return;
               }

               selecteditem = new ItemStack(i.getItem(itemslot));
               clickeditem = pi.getItem(slot);
               pi.setItem(slot, selecteditem);
               i.setItem(itemslot, clickeditem);
               page.setItems(i);
               page.openEditor(player);
               return;
            }

            if (pi.getItem(itemslot) == null) {
               page.openEditor(player);
               return;
            }

            selecteditem = new ItemStack(pi.getItem(itemslot));
            clickeditem = pi.getItem(slot);
            pi.setItem(slot, selecteditem);
            pi.setItem(itemslot, clickeditem);
            page.openEditor(player);
            return;
         }

         PageSlot ps;
         List cmd;
         String removal;
         if (event.getPageData().equals(PageData.EDIT_ITEM_MANAGE)) {
            itemslot = (Integer)event.getGUI().getPass().get("slot");
            ps = page.getPageSlot(itemslot);
            if (event.isTopInventory()) {
               if (event.getSlot() == 1) {
                  this.editFunction(player, page, itemslot);
               } else if (event.getSlot() == 8) {
                  if (ps.getFunction().equals(Function.NONE) || page.isGUI() || ps.getFunction().equals(Function.CONFIRM)) {
                     return;
                  }

                  this.editMessages(player, page, itemslot);
               } else if (event.getSlot() == 13) {
                  this.editItemDisplay(player, page, itemslot);
               } else if (event.getSlot() == 9) {
                  if (ps.getFunction().equals(Function.NONE) || page.isGUI() || ps.getFunction().equals(Function.CONFIRM)) {
                     return;
                  }

                  if (event.getClick().equals(ClickType.LEFT)) {
                     this.editInput(player, page, itemslot, "cooldown");
                     player.sendMessage(ChatColor.GREEN + "Enter cooldown in seconds into chat");
                  } else if (event.getClick().equals(ClickType.RIGHT)) {
                     ps.setCooldown(0);
                     editItem(player, page, itemslot);
                  }
               } else if (event.getSlot() == 10) {
                  if (page.isGUI()) {
                     if (event.getClick().equals(ClickType.LEFT)) {
                        if (ps.getGUIFunction().equals(GUIFunction.QUANTITY)) {
                           this.editInput(player, page, itemslot, "quantity");
                           player.sendMessage(ChatColor.GREEN + "Enter quantity into chat");
                        }
                     } else if (event.getClick().equals(ClickType.RIGHT)) {
                        ps.setData("gui_quantity", 0);
                        editItem(player, page, itemslot);
                     }

                     return;
                  }

                  this.editHidemode(player, page, itemslot);
               } else if (event.getSlot() == 15) {
                  if (ps.getFunction().equals(Function.NONE) || page.isGUI() || ps.getFunction().equals(Function.CONFIRM)) {
                     return;
                  }

                  if (event.getClick().equals(ClickType.LEFT)) {
                     this.editInput(player, page, itemslot, "permission");
                     player.sendMessage(ChatColor.GREEN + "Enter permission into chat");
                  } else if (event.getClick().equals(ClickType.RIGHT)) {
                     cmd = ps.getPermissions();
                     if (cmd.size() > 0) {
                        removal = (String)cmd.get(cmd.size() - 1);
                        ps.removePermission(removal);
                     }

                     editItem(player, page, itemslot);
                  }
               } else if (event.getSlot() == 16) {
                  if (page.isGUI() && ps.getGUIFunction().equals(GUIFunction.CONFIRM)) {
                     if (event.getClick().equals(ClickType.LEFT)) {
                        this.editInput(player, page, itemslot, "confirm1");
                        player.sendMessage(ChatColor.GREEN + "Enter message into chat");
                     } else if (event.getClick().equals(ClickType.RIGHT)) {
                        this.editInput(player, page, itemslot, "confirm2");
                        player.sendMessage(ChatColor.GREEN + "Enter message into chat");
                     } else if (event.getClick().equals(ClickType.SHIFT_LEFT)) {
                        ps.removeData("gui_confirm1");
                        editItem(player, page, itemslot);
                     } else if (event.getClick().equals(ClickType.SHIFT_RIGHT)) {
                        ps.removeData("gui_confirm2");
                        editItem(player, page, itemslot);
                     }

                     return;
                  }
               } else if (event.getSlot() == 24) {
                  if (page.isGUI()) {
                     return;
                  }

                  if (event.getClick().equals(ClickType.LEFT)) {
                     this.editInput(player, page, itemslot, "hidepermission");
                     player.sendMessage(ChatColor.GREEN + "Enter hide permission into chat");
                  } else if (event.getClick().equals(ClickType.RIGHT)) {
                     cmd = ps.getHidePermissions();
                     if (cmd.size() > 0) {
                        removal = (String)cmd.get(cmd.size() - 1);
                        ps.removeHidePermission(removal);
                     }

                     editItem(player, page, itemslot);
                  }
               } else if (event.getSlot() == 18) {
                  if (ps.getFunction().equals(Function.NONE) || ps.getFunction().equals(Function.COMMAND) || page.isGUI() || ps.getFunction().equals(Function.CONFIRM) || ps.getFunction().equals(Function.TRADE)) {
                     return;
                  }

                  if (event.getClick().equals(ClickType.LEFT)) {
                     this.editInput(player, page, itemslot, "sell");
                     player.sendMessage(ChatColor.GREEN + "Enter sell into chat");
                     player.sendMessage(ChatColor.GREEN + "This is how much it sells per 1 of the item");
                  } else if (event.getClick().equals(ClickType.RIGHT)) {
                     ps.setSell(0.0D);
                     editItem(player, page, itemslot);
                  }
               } else if (event.getSlot() == 19) {
                  if (ps.getFunction().equals(Function.NONE) || ps.getFunction().equals(Function.SELL) || ps.getFunction().equals(Function.CONFIRM) || ps.getFunction().equals(Function.TRADE)) {
                     return;
                  }

                  if (event.getClick().equals(ClickType.LEFT)) {
                     this.editInput(player, page, itemslot, "cost");
                     player.sendMessage(ChatColor.GREEN + "Enter cost into chat");
                  } else if (event.getClick().equals(ClickType.RIGHT)) {
                     ps.setCost(0.0D);
                     editItem(player, page, itemslot);
                  }
               } else if (event.getSlot() == 22) {
                  page.openEditor(player);
               } else if (event.getSlot() == 26 && ps.getFunction().equals(Function.COMMAND)) {
                  if (event.getClick().equals(ClickType.LEFT)) {
                     this.editInput(player, page, itemslot, "command");
                     player.sendMessage(ChatColor.GREEN + "Enter command into chat with no slash");
                  } else if (event.getClick().equals(ClickType.RIGHT)) {
                     cmd = ps.getCommands();
                     if (cmd.size() > 0) {
                        removal = (String)cmd.get(cmd.size() - 1);
                        ps.removeCommand(removal);
                     }

                     editItem(player, page, itemslot);
                  }
               }
            }
         } else {
            String name;
            if (event.getPageData().equals(PageData.EDIT_ITEM_FUNCTION)) {
               itemslot = (Integer)event.getGUI().getPass().get("slot");
               ps = page.getPageSlot(itemslot);
               if (event.isTopInventory() && item != null && !item.getType().equals(Material.AIR)) {
                  if (!page.isGUI()) {
                     if (event.getSlot() == 8) {
                        editItem(player, page, itemslot);
                        return;
                     }

                     name = ChatColor.stripColor(item.getItemMeta().getDisplayName().toUpperCase().replaceAll(" ", "_"));
                     Function function = Function.NONE;

                     try {
                        function = Function.valueOf(name);
                     } catch (IllegalArgumentException var13) {
                     }

                     ps.setFunction(function);
                     editItem(player, page, itemslot);
                  } else {
                     if (event.getSlot() == 8) {
                        editItem(player, page, itemslot);
                        return;
                     }

                     name = ChatColor.stripColor(item.getItemMeta().getDisplayName().toUpperCase().replaceAll(" ", "_"));
                     GUIFunction function = GUIFunction.NONE;

                     try {
                        function = GUIFunction.valueOf(name);
                     } catch (IllegalArgumentException var12) {
                     }

                     ps.setGUIFunction(function);
                     editItem(player, page, itemslot);
                  }
               }
            } else if (event.getPageData().equals(PageData.EDIT_ITEM_HIDEMODE)) {
               itemslot = (Integer)event.getGUI().getPass().get("slot");
               ps = page.getPageSlot(itemslot);
               if (event.isTopInventory() && item != null && !item.getType().equals(Material.AIR)) {
                  if (event.getSlot() == 4) {
                     editItem(player, page, itemslot);
                     return;
                  }

                  name = ChatColor.stripColor(item.getItemMeta().getDisplayName().toUpperCase().replaceAll(" ", "_"));
                  Hidemode hidemode = Hidemode.valueOf(name);
                  ps.setHidemode(hidemode);
                  editItem(player, page, itemslot);
               }
            } else if (event.getPageData().equals(PageData.EDIT_ITEM_MESSAGES)) {
               itemslot = (Integer)event.getGUI().getPass().get("slot");
               ps = page.getPageSlot(itemslot);
               if (event.getSlot() == 1) {
                  if (event.getClick().equals(ClickType.LEFT)) {
                     this.editInput(player, page, itemslot, "permissionmessage");
                     player.sendMessage(ChatColor.GREEN + "Enter message you want to add into chat");
                  } else if (event.getClick().equals(ClickType.RIGHT)) {
                     cmd = ps.getMessage(MessageType.PERMISSION.toString());
                     if (cmd.size() > 0) {
                        removal = (String)cmd.get(cmd.size() - 1);
                        ps.removeMessage(MessageType.PERMISSION.toString(), removal);
                     }

                     this.editMessages(player, page, itemslot);
                  }
               } else if (event.getSlot() == 7) {
                  if (event.getClick().equals(ClickType.LEFT)) {
                     this.editInput(player, page, itemslot, "cooldownmessage");
                     player.sendMessage(ChatColor.GREEN + "Enter message you want to add into chat");
                  } else if (event.getClick().equals(ClickType.RIGHT)) {
                     cmd = ps.getMessage(MessageType.COOLDOWN.toString());
                     if (cmd.size() > 0) {
                        removal = (String)cmd.get(cmd.size() - 1);
                        ps.removeMessage(MessageType.COOLDOWN.toString(), removal);
                     }

                     this.editMessages(player, page, itemslot);
                  }
               } else if (event.getSlot() == 22) {
                  editItem(player, page, itemslot);
               }
            } else if (event.getPageData().equals(PageData.EDIT_ITEM)) {
               itemslot = (Integer)event.getGUI().getPass().get("slot");
               ItemStack i;
               if (event.getSlot() == 3) {
                  if (event.getClick().equals(ClickType.LEFT)) {
                     this.editInput(player, page, itemslot, "itemname");
                     player.sendMessage(ChatColor.GREEN + "Enter name you want to set into chat");
                  } else if (event.getClick().equals(ClickType.RIGHT)) {
                     i = page.getInventory().getItem(itemslot);
                     if (i != null) {
                        ItemCreator ic = new ItemCreator(i);
                        Inventory inv = page.getInventory();
                        inv.setItem(itemslot, ic.resetName());
                        page.setItems(inv);
                     }

                     this.editItemDisplay(player, page, itemslot);
                  }
               } else if (event.getSlot() == 5) {
                  if (event.getClick().equals(ClickType.LEFT)) {
                     this.editInput(player, page, itemslot, "itemlore");
                     player.sendMessage(ChatColor.GREEN + "Enter lore you want to add into chat");
                  } else if (event.getClick().equals(ClickType.RIGHT)) {
                     i = page.getInventory().getItem(itemslot);
                     if (i != null) {
                        cmd = getLore(i);
                        Debug.log("Beginning attempt to remove lore from slot " + itemslot + " on page " + page.getID());
                        if (cmd.size() > 0) {
                           removal = (String)cmd.get(cmd.size() - 1);
                           ItemCreator ic = new ItemCreator(i);
                           Inventory inv = page.getInventory();
                           inv.setItem(itemslot, ic.removeLore(removal));
                           page.setItems(inv);
                           Debug.log("Lore has been removed from slot " + itemslot + " on page " + page.getID());
                        }
                     }

                     this.editItemDisplay(player, page, itemslot);
                  }
               } else if (event.getSlot() == 6) {
                  ps = page.getPageSlot(itemslot);
                  if (event.getClick().equals(ClickType.LEFT)) {
                     this.editInput(player, page, itemslot, "pagelore");
                     player.sendMessage(ChatColor.GREEN + "Enter page lore into chat");
                  } else if (event.getClick().equals(ClickType.RIGHT)) {
                     cmd = ps.getPageLore();
                     if (cmd.size() > 0) {
                        removal = (String)cmd.get(cmd.size() - 1);
                        ps.removePageLore(removal);
                     }

                     this.editItemDisplay(player, page, itemslot);
                  }
               } else if (event.getSlot() == 13) {
                  if (event.getClick().equals(ClickType.LEFT)) {
                     this.editInput(player, page, itemslot, "itemquantity");
                     player.sendMessage(ChatColor.GREEN + "Enter amount into chat");
                  } else if (event.getClick().equals(ClickType.RIGHT)) {
                     i = page.getInventory().getItem(itemslot);
                     if (i != null) {
                        i.setAmount(1);
                     }

                     page.setItem(itemslot, i);
                     this.editItemDisplay(player, page, itemslot);
                  }
               } else if (event.getSlot() == 22) {
                  editItem(player, page, itemslot);
               }
            }
         }
      }

   }

   @EventHandler
   public void inputValue(PlayerInputEvent event) {
      Player player = event.getPlayer();
      int slot = event.getSlot();
      Page page = event.getPage();
      PageSlot ps = page.getPageSlot(slot);
      String id = event.getID();
      String msg = event.getMessage();
      Input input = event.getInput();
      event.setCancelled(true);
      double value;
      if (id.equalsIgnoreCase("cost")) {
         try {
            value = Double.parseDouble(ChatColor.stripColor(msg));
            if (value >= 0.0D) {
               ps.setCost(value);
               input.destroy();
               editItem(player, page, slot);
            } else {
               player.sendMessage(ChatColor.RED + "Value must be greater than or equal to 0");
            }
         } catch (NumberFormatException var16) {
            player.sendMessage(ChatColor.RED + "Expected number");
         }
      } else if (id.equalsIgnoreCase("sell")) {
         try {
            value = Double.parseDouble(ChatColor.stripColor(msg));
            if (value >= 0.0D) {
               ps.setSell(value);
               input.destroy();
               editItem(player, page, slot);
            } else {
               player.sendMessage(ChatColor.RED + "Value must be greater than or equal to 0");
            }
         } catch (NumberFormatException var15) {
            player.sendMessage(ChatColor.RED + "Expected number");
         }
      } else {
         int value2;
         if (id.equalsIgnoreCase("cooldown")) {
            try {
               value2 = Integer.parseInt(ChatColor.stripColor(msg));
               if (value2 >= 0) {
                  ps.setCooldown(value2);
                  input.destroy();
                  editItem(player, page, slot);
               } else {
                  player.sendMessage(ChatColor.RED + "Value must be greater than or equal to 0");
               }
            } catch (NumberFormatException var14) {
               player.sendMessage(ChatColor.RED + "Expected number");
            }
         } else if (id.equalsIgnoreCase("itemquantity")) {
            try {
               value = Integer.parseInt(ChatColor.stripColor(msg));
               if (value > 0) {
                  ItemStack i = page.getInventory().getItem(slot);
                  if (i != null) {
                     i.setAmount((int) value);
                  }

                  if (value > i.getMaxStackSize()) {
                     value = i.getMaxStackSize();
                  }

                  page.setItem(slot, i);
                  input.destroy();
                  this.editItemDisplay(player, page, slot);
               } else {
                  player.sendMessage(ChatColor.RED + "Value must be greater than 0");
               }
            } catch (NumberFormatException var13) {
               player.sendMessage(ChatColor.RED + "Expected number");
            }
         } else if (id.equalsIgnoreCase("command")) {
            ps.addCommand(msg);
            input.destroy();
            editItem(player, page, slot);
         } else if (id.equalsIgnoreCase("permission")) {
            ps.addPermission(msg);
            input.destroy();
            editItem(player, page, slot);
         } else if (id.equalsIgnoreCase("hidepermission")) {
            ps.addHidePermission(msg);
            input.destroy();
            editItem(player, page, slot);
         } else if (id.equalsIgnoreCase("permissionmessage")) {
            ps.addMessage(MessageType.PERMISSION.toString(), msg);
            input.destroy();
            this.editMessages(player, page, slot);
         } else if (id.equalsIgnoreCase("cooldownmessage")) {
            ps.addMessage(MessageType.COOLDOWN.toString(), msg);
            input.destroy();
            this.editMessages(player, page, slot);
         } else {
            Inventory i;
            ItemCreator ic;
            ItemStack item;
            if (id.equalsIgnoreCase("itemname")) {
               item = page.getInventory().getItem(slot);
               if (item != null) {
                  ic = new ItemCreator(item);
                  i = page.getInventory();
                  i.setItem(slot, ic.setName(ChatColor.translateAlternateColorCodes('&', msg)));
                  page.setItems(i);
               }

               input.destroy();
               this.editItemDisplay(player, page, slot);
            } else if (id.equalsIgnoreCase("itemlore")) {
               item = page.getInventory().getItem(slot);
               if (item != null) {
                  ic = new ItemCreator(item);
                  i = page.getInventory();
                  i.setItem(slot, ic.addLore(ChatColor.translateAlternateColorCodes('&', msg)));
                  page.setItems(i);
               }

               input.destroy();
               this.editItemDisplay(player, page, slot);
            } else if (id.equalsIgnoreCase("pagelore")) {
               ps.addPageLore(msg);
               input.destroy();
               this.editItemDisplay(player, page, slot);
            } else if (id.equalsIgnoreCase("quantity")) {
               try {
                  value = Integer.parseInt(ChatColor.stripColor(msg));
                  if (value >= 0) {
                     ps.setData("gui_quantity", value);
                     input.destroy();
                     editItem(player, page, slot);
                  } else {
                     player.sendMessage(ChatColor.RED + "Value must be greater than or equal to 0");
                  }
               } catch (NumberFormatException var12) {
                  player.sendMessage(ChatColor.RED + "Expected number");
               }
            } else if (id.equalsIgnoreCase("confirm1")) {
               ps.setData("gui_confirm1", msg);
               input.destroy();
               editItem(player, page, slot);
            } else if (id.equalsIgnoreCase("confirm2")) {
               ps.setData("gui_confirm2", msg);
               input.destroy();
               editItem(player, page, slot);
            } else {
               input.destroy();
            }
         }
      }

   }

   public void moveItem(Player player, GUI ogui, int slot, boolean top) {
      GUI gui = new GUI(ogui);
      gui.setTitle("Click slot to move item to");
      gui.setData(PageData.EDIT_ITEM_MOVE);
      gui.addPass("old", slot);
      gui.addPass("top", top);
      gui.open(player);
   }

   public void editInput(Player player, Page page, int slot, String id) {
      Input input = new Input(player, page.getID(), slot, id);
      input.register();
      player.sendMessage(ChatColor.YELLOW + "Type \"-cancel\" in the chat to cancel");
      player.sendMessage(ChatColor.YELLOW + "Type \"&&\" in the chat for a blank");
      player.closeInventory();
   }

   public static void editItem(Player player, Page page, int slot) {
      PageSlot ps = page.getPageSlot(slot);
      InventoryCreator inv = new InventoryCreator("" + ChatColor.BLUE + slot + ChatColor.DARK_GRAY + "▐ Properties", 3);
      if (page.getInventory().getItem(slot) == null) {
         page.openEditor(player);
      } else {
         inv.getInventory().setItem(4, page.getInventory().getItem(slot));
         inv.addLore(4, " ");
         inv.addLore(4, ChatColor.WHITE + "Page" + ChatColor.GRAY + ": " + ChatColor.WHITE + ChatColor.BOLD + page.getID());
         inv.addLore(4, ChatColor.BLUE + "Function" + ChatColor.DARK_GRAY + ": " + new String(!page.isGUI() ? ChatColor.BLUE + ps.getFunction().toString() : ChatColor.BLUE + ps.getGUIFunction().toString()));
         if (page.isGUI() && ps.getGUIFunction().equals(GUIFunction.QUANTITY)) {
            inv.addLore(4, ChatColor.DARK_GREEN + "Quantity" + ChatColor.DARK_GRAY + ": " + ChatColor.DARK_GREEN + ps.getDataInt("gui_quantity"));
         }

         if (!page.isGUI()) {
            inv.addLore(4, ChatColor.YELLOW + "Visibility" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + ps.getHidemode().toString());
         }

         if (!ps.getFunction().equals(Function.NONE) && !ps.getFunction().equals(Function.CONFIRM) && !ps.getFunction().equals(Function.SELL) && !ps.getFunction().equals(Function.TRADE) && !page.isGUI()) {
            inv.addLore(4, ChatColor.DARK_GREEN + "Cost" + ChatColor.DARK_GRAY + ": " + ChatColor.DARK_GREEN + DoubleUtil.toString(ps.getCost()));
         }

         if (!ps.getFunction().equals(Function.NONE) && !ps.getFunction().equals(Function.CONFIRM) && !ps.getFunction().equals(Function.COMMAND) && !ps.getFunction().equals(Function.TRADE) && !page.isGUI()) {
            inv.addLore(4, ChatColor.GREEN + "Sell" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + DoubleUtil.toString(ps.getSell()));
         }

         if (!ps.getFunction().equals(Function.NONE) && !ps.getFunction().equals(Function.CONFIRM) && ps.hasCooldown() && !page.isGUI()) {
            inv.addLore(4, ChatColor.LIGHT_PURPLE + "Cooldown" + ChatColor.DARK_GRAY + ": " + ChatColor.LIGHT_PURPLE + ps.getCooldown());
         }

         inv.getInventory().setItem(1, new ItemStack(Material.EMERALD));
         inv.setDisplay(1, ChatColor.GREEN + "Change Function");
         inv.addLore(1, ChatColor.WHITE + "Function" + ChatColor.GRAY + ": " + new String(!page.isGUI() ? ChatColor.BLUE + ps.getFunction().toString() : ChatColor.BLUE + ps.getGUIFunction().toString()));
         inv.addLore(1, " ");
         inv.addLore(1, ChatColor.GRAY + "Left-click to change function");
         if (!page.isGUI()) {
            inv.getInventory().setItem(10, new ItemStack(Material.GLASS));
            inv.setDisplay(10, ChatColor.GREEN + "Change Visibility");
            inv.addLore(10, ChatColor.WHITE + "Visibility" + ChatColor.GRAY + ": " + ChatColor.YELLOW + ps.getHidemode().toString());
            inv.addLore(10, " ");
            inv.addLore(10, ChatColor.GRAY + "Left-click to change visibility");
            if (ps.getFunction().equals(Function.TRADE)) {
               inv.getInventory().setItem(19, new ItemStack(Material.CHEST));
               inv.setDisplay(19, ChatColor.GREEN + "Item Inventory");
               inv.addLore(19, ChatColor.GRAY + "Manage item's inventory");
            }

            String s;
            Iterator var6;
            if (!ps.getFunction().equals(Function.NONE) && !ps.getFunction().equals(Function.CONFIRM)) {
               if (!ps.getFunction().equals(Function.SELL) && !ps.getFunction().equals(Function.TRADE)) {
                  inv.getInventory().setItem(19, new ItemStack(Material.GOLD_INGOT));
                  inv.setDisplay(19, ChatColor.GREEN + "Change Cost");
                  inv.addLore(19, ChatColor.WHITE + "Cost" + ChatColor.GRAY + ": " + ChatColor.DARK_GREEN + DoubleUtil.toString(ps.getCost()));
                  inv.addLore(19, " ");
                  inv.addLore(19, ChatColor.GRAY + "Left-click to set cost");
                  inv.addLore(19, ChatColor.GRAY + "Right-click to remove cost");
               }

               if (!ps.getFunction().equals(Function.COMMAND) && !ps.getFunction().equals(Function.TRADE)) {
                  inv.getInventory().setItem(18, new ItemStack(Material.DIAMOND));
                  inv.setDisplay(18, ChatColor.GREEN + "Change Sell");
                  inv.addLore(18, ChatColor.WHITE + "Sell" + ChatColor.GRAY + ": " + ChatColor.GREEN + DoubleUtil.toString(ps.getSell()));
                  inv.addLore(18, " ");
                  inv.addLore(18, ChatColor.GRAY + "Left-click to set sell");
                  inv.addLore(18, ChatColor.GRAY + "Right-click to remove sell");
               }

               inv.getInventory().setItem(9, new ItemStack(Material.LEGACY_WATCH));
               inv.setDisplay(9, ChatColor.GREEN + "Change Cooldown");
               inv.addLore(9, ChatColor.WHITE + "Cooldown" + ChatColor.GRAY + ": " + ChatColor.LIGHT_PURPLE + ps.getCooldown());
               inv.addLore(9, " ");
               inv.addLore(9, ChatColor.GRAY + "Left-click to set");
               inv.addLore(9, ChatColor.GRAY + "Right-click to remove");
               inv.setItem(15, Material.LEGACY_WOOL, 14, ChatColor.GREEN + "Manage Permissions");
               inv.addLore(15, " ");
               var6 = ps.getPermissions().iterator();

               while(var6.hasNext()) {
                  s = (String)var6.next();
                  inv.addLore(15, ChatColor.WHITE + s);
               }

               inv.addLore(15, " ");
               inv.addLore(15, ChatColor.GRAY + "Left-click to add");
               inv.addLore(15, ChatColor.GRAY + "Right-click to remove");
               inv.getInventory().setItem(8, new ItemStack(Material.PAPER));
               inv.setDisplay(8, ChatColor.GOLD + "Messages");
               inv.addLore(8, ChatColor.GRAY + "Manage the messages your item sends");
            }

            inv.setItem(24, Material.LEGACY_WOOL, 7, ChatColor.GREEN + "Manage Hide Permissions");
            inv.addLore(24, " ");
            var6 = ps.getHidePermissions().iterator();

            while(var6.hasNext()) {
               s = (String)var6.next();
               inv.addLore(24, ChatColor.WHITE + s);
            }

            inv.addLore(24, " ");
            inv.addLore(24, ChatColor.GRAY + "Left-click to add");
            inv.addLore(24, ChatColor.GRAY + "Right-click to remove");
            if (ps.getFunction().equals(Function.COMMAND)) {
               inv.setItem(26, Material.PAPER, ChatColor.GREEN + "Manage Commands");
               inv.addLore(26, " ");
               var6 = ps.getCommands().iterator();

               while(var6.hasNext()) {
                  s = (String)var6.next();
                  inv.addLore(26, ChatColor.WHITE + s);
               }

               inv.addLore(26, " ");
               inv.addLore(26, ChatColor.GRAY + "Left-click to add");
               inv.addLore(26, ChatColor.GRAY + "Right-click to remove");
            }
         }

         inv.getInventory().setItem(22, new ItemStack(Material.COMPASS));
         inv.setDisplay(22, ChatColor.RED + "Back");
         inv.addLore(22, ChatColor.GRAY + "Return to item manager");
         inv.getInventory().setItem(13, new ItemStack(Material.PAPER));
         inv.setDisplay(13, ChatColor.GOLD + "Item Display");
         inv.addLore(13, ChatColor.GRAY + "Modify your item's display");
         if (page.isGUI()) {
            if (ps.getGUIFunction().equals(GUIFunction.QUANTITY)) {
               inv.setItem(10, Material.DIAMOND, ChatColor.GREEN + "Change Quantity");
               int amount = ps.getDataInt("gui_quantity");
               inv.addLore(10, ChatColor.WHITE + "Quantity" + ChatColor.GRAY + ": " + ChatColor.DARK_GREEN + amount);
               inv.addLore(10, " ");
               inv.addLore(10, ChatColor.GRAY + "Left-click to set quantity");
               inv.addLore(10, ChatColor.GRAY + "Right-click to remove quantity");
            }

            inv.getInventory().setItem(15, new ItemStack(Material.LEGACY_SIGN));
            inv.setDisplay(15, ChatColor.GOLD + "Placeholders");
            inv.addLore(15, ChatColor.GRAY + "%item% - item being sold/bought");
            inv.addLore(15, ChatColor.GRAY + "%earnings% - sell GUI");
            inv.addLore(15, ChatColor.GRAY + "%price% - buy GUI");
            inv.addLore(15, ChatColor.GRAY + "%amount% - selected amount");
            inv.addLore(15, ChatColor.GRAY + "%confirm% - for confirm message");
            inv.addLore(15, ChatColor.GRAY + "%quantity% - for quantity function");
            inv.addLore(15, ChatColor.GRAY + "%balance% - player's money");
            inv.addLore(15, ChatColor.GRAY + "%player% - player's name");
            inv.addLore(15, ChatColor.GRAY + "%item_display% - turns item's name");
            inv.addLore(15, ChatColor.GRAY + "and material to the item that the");
            inv.addLore(15, ChatColor.GRAY + "player is selling/buying");
            if (ps.getGUIFunction().equals(GUIFunction.CONFIRM)) {
               inv.setItem(16, Material.PAPER, ChatColor.GREEN + "Change Confirm Message");
               inv.addLore(16, " ");
               inv.addLore(16, ChatColor.WHITE + "1: " + new String(ps.getDataString("gui_confirm1") != null ? ps.getDataString("gui_confirm1") : ""));
               inv.addLore(16, ChatColor.WHITE + "2: " + new String(ps.getDataString("gui_confirm2") != null ? ps.getDataString("gui_confirm2") : ""));
               inv.addLore(16, " ");
               inv.addLore(16, ChatColor.GRAY + "Left-click to set confirm text 1");
               inv.addLore(16, ChatColor.GRAY + "Right-click to set confirm text 2");
               inv.addLore(16, ChatColor.GRAY + "Shift left-click to clear text 1");
               inv.addLore(16, ChatColor.GRAY + "Shift right-click to clear text 2");
            }
         }

         inv.setBlank(Material.LEGACY_STAINED_GLASS_PANE, 11);
         GUI gui = new GUI(Initiate.getPlugin(Initiate.class), PageData.EDIT_ITEM_MANAGE, inv.getInventory(), page);
         gui.addPass("slot", slot);
         gui.open(player);
      }
   }

   public void editFunction(Player player, Page page, int slot) {
      InventoryCreator inv = new InventoryCreator("" + ChatColor.BLUE + slot + ChatColor.DARK_GRAY + "▐ Functions", 1);
      int var6;
      int var7;
      ItemStack item;
      ItemMeta itemM;
      ArrayList lore;
      if (!page.isGUI()) {
         Function[] var8;
         var7 = (var8 = Function.values()).length;

         for(var6 = 0; var6 < var7; ++var6) {
            Function f = var8[var6];
            if (page.getType() != 0 || f != Function.CONFIRM) {
               item = new ItemStack(Material.EMERALD);
               itemM = item.getItemMeta();
               itemM.setDisplayName(ChatColor.GREEN + f.toString());
               lore = new ArrayList();
               lore.add(ChatColor.WHITE + f.getDescription());
               itemM.setLore(lore);
               item.setItemMeta(itemM);
               inv.getInventory().addItem(new ItemStack[]{item});
            }
         }

         inv.getInventory().setItem(8, new ItemStack(Material.COMPASS));
         inv.setDisplay(8, ChatColor.RED + "Back");
         inv.addLore(8, ChatColor.GRAY + "Return to item properties");
      } else {
         GUIFunction[] var14;
         var7 = (var14 = GUIFunction.values()).length;

         for(var6 = 0; var6 < var7; ++var6) {
            GUIFunction f = var14[var6];
            item = new ItemStack(Material.EMERALD);
            itemM = item.getItemMeta();
            itemM.setDisplayName(ChatColor.GREEN + f.toString());
            lore = new ArrayList();
            lore.add(ChatColor.WHITE + f.getDescription());
            itemM.setLore(lore);
            item.setItemMeta(itemM);
            inv.getInventory().addItem(new ItemStack[]{item});
         }

         inv.getInventory().setItem(8, new ItemStack(Material.COMPASS));
         inv.setDisplay(8, ChatColor.RED + "Back");
         inv.addLore(8, ChatColor.GRAY + "Return to item properties");
      }

      GUI gui = new GUI(Initiate.getPlugin(Initiate.class), PageData.EDIT_ITEM_FUNCTION, inv.getInventory(), page);
      gui.addPass("slot", slot);
      gui.open(player);
   }

   public void editHidemode(Player player, Page page, int slot) {
      InventoryCreator inv = new InventoryCreator("" + ChatColor.BLUE + slot + ChatColor.DARK_GRAY + "▐ Visibility", 1);
      Hidemode[] var8;
      int var7 = (var8 = Hidemode.values()).length;

      for(int var6 = 0; var6 < var7; ++var6) {
         Hidemode hide = var8[var6];
         ItemStack item = new ItemStack(Material.EMERALD);
         ItemMeta itemM = item.getItemMeta();
         itemM.setDisplayName(ChatColor.GREEN + hide.toString());
         ArrayList<String> lore = new ArrayList();
         lore.add(ChatColor.WHITE + hide.getDescription());
         itemM.setLore(lore);
         item.setItemMeta(itemM);
         inv.getInventory().addItem(new ItemStack[]{item});
      }

      inv.getInventory().setItem(4, new ItemStack(Material.COMPASS));
      inv.setDisplay(4, ChatColor.RED + "Back");
      inv.addLore(4, ChatColor.GRAY + "Return to item properties");
      GUI gui = new GUI(Initiate.getPlugin(Initiate.class), PageData.EDIT_ITEM_HIDEMODE, inv.getInventory(), page);
      gui.addPass("slot", slot);
      gui.open(player);
   }

   public void editMessages(Player player, Page page, int slot) {
      PageSlot ps = page.getPageSlot(slot);
      InventoryCreator inv = new InventoryCreator("" + ChatColor.BLUE + slot + ChatColor.DARK_GRAY + "▐ Messages", 3);
      GUI gui = new GUI(Initiate.getPlugin(Initiate.class), PageData.EDIT_ITEM_MESSAGES, inv.getInventory(), page);
      inv.getInventory().setItem(22, new ItemStack(Material.COMPASS));
      inv.setDisplay(22, ChatColor.RED + "Back");
      inv.addLore(22, ChatColor.GRAY + "Return to item properties");
      inv.setItem(1, Material.PAPER, ChatColor.GREEN + "Permission Error");
      inv.addLore(1, " ");
      Iterator var8 = ps.getMessage(MessageType.PERMISSION.toString()).iterator();

      String s;
      while(var8.hasNext()) {
         s = (String)var8.next();
         inv.addLore(1, ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', s));
      }

      inv.addLore(1, " ");
      inv.addLore(1, ChatColor.GRAY + "Left-click to add");
      inv.addLore(1, ChatColor.GRAY + "Right-click to remove");
      inv.setItem(7, Material.PAPER, ChatColor.GREEN + "Cooldown Error");
      inv.addLore(7, " ");
      var8 = ps.getMessage(MessageType.COOLDOWN.toString()).iterator();

      while(var8.hasNext()) {
         s = (String)var8.next();
         inv.addLore(7, ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', s));
      }

      inv.addLore(7, " ");
      inv.addLore(7, ChatColor.GRAY + "Left-click to add");
      inv.addLore(7, ChatColor.GRAY + "Right-click to remove");
      inv.setBlank(Material.LEGACY_STAINED_GLASS_PANE, 11);
      gui.addPass("slot", slot);
      gui.open(player);
   }

   public void editItemDisplay(Player player, Page page, int slot) {
      if (page.getInventory().getItem(slot) == null) {
         player.closeInventory();
      } else {
         InventoryCreator inv = new InventoryCreator("" + ChatColor.BLUE + slot + ChatColor.DARK_GRAY + "▐ Item Display", 3);
         GUI gui = new GUI(Initiate.getPlugin(Initiate.class), PageData.EDIT_ITEM, inv.getInventory(), page);
         inv.getInventory().setItem(4, page.getInventory().getItem(slot));
         inv.getInventory().setItem(22, new ItemStack(Material.COMPASS));
         inv.setDisplay(22, ChatColor.RED + "Back");
         inv.addLore(22, ChatColor.GRAY + "Return to item properties");
         inv.setItem(3, Material.NAME_TAG, ChatColor.GREEN + "Change Display Name");
         inv.addLore(3, " ");
         inv.addLore(3, ChatColor.WHITE + getItemName(page.getInventory().getItem(slot)));
         inv.addLore(3, " ");
         inv.addLore(3, ChatColor.GRAY + "Left-click to set");
         inv.addLore(3, ChatColor.GRAY + "Right-click to reset");
         inv.setItem(5, Material.BOOK, ChatColor.GREEN + "Add Lore");
         inv.addLore(5, " ");
         Iterator var7 = getLore(page.getInventory().getItem(slot)).iterator();

         while(var7.hasNext()) {
            String s = (String)var7.next();
            inv.addLore(5, ChatColor.WHITE + s);
         }

         if (!getLore(page.getInventory().getItem(slot)).isEmpty()) {
            inv.addLore(5, " ");
         }

         inv.addLore(5, ChatColor.GRAY + "Left-click to add");
         inv.addLore(5, ChatColor.GRAY + "Right-click to remove");
         PageSlot ps = page.getPageSlot(slot);
         if (ps != null) {
            inv.getInventory().setItem(6, new ItemStack(Material.ENCHANTED_BOOK));
            inv.setDisplay(6, ChatColor.GREEN + "Add Page Lore");
            inv.addLore(6, " ");
            Iterator var8 = ps.getPageLore().iterator();

            while(var8.hasNext()) {
               String s = (String)var8.next();
               inv.addLore(6, ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', s));
            }

            if (!ps.getPageLore().isEmpty()) {
               inv.addLore(6, " ");
            }

            inv.addLore(6, ChatColor.GRAY + "Left-click to add");
            inv.addLore(6, ChatColor.GRAY + "Right-click to remove");
         }

         inv.setItem(13, Material.ANVIL, ChatColor.GREEN + "Change Amount");
         inv.addLore(13, " ");
         inv.addLore(13, ChatColor.WHITE + String.valueOf(page.getInventory().getItem(slot).getAmount()));
         inv.addLore(13, " ");
         inv.addLore(13, ChatColor.GRAY + "Left-click to set");
         inv.addLore(13, ChatColor.GRAY + "Right-click to reset");
         inv.setBlank(Material.LEGACY_STAINED_GLASS_PANE, 11);
         gui.addPass("slot", slot);
         gui.open(player);
      }
   }

   public void moveItems(Player player, Page page) {
      GUI gui = new GUI(Initiate.getPlugin(Initiate.class), PageData.MOVE_ITEM, page.getInventory(), page);
      gui.setTitle(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Manual" + ChatColor.DARK_GRAY + "] Move items");
      gui.open(player);
   }

   public static String getItemName(ItemStack item) {
      return item != null ? (new ItemCreator(item)).getName() : ChatColor.RED + "null";
   }

   public static List<String> getLore(ItemStack item) {
      List<String> lore = new ArrayList();
      if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
         lore = item.getItemMeta().getLore();
      }

      return (List)lore;
   }
}
