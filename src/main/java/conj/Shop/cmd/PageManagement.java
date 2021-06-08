package conj.Shop.cmd;

import conj.Shop.control.Control;
import conj.Shop.control.Manager;
import conj.Shop.data.Page;
import conj.Shop.data.PageSlot;
import conj.Shop.enums.Config;
import conj.Shop.enums.Function;
import conj.Shop.interaction.Editor;
import conj.Shop.interaction.PageProperties;
import conj.Shop.tools.DoubleUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class PageManagement {
   public void run(Player player, Command cmd, String label, String[] args) {
      Manager manager = new Manager();
      if (args.length == 1) {
         if (!player.hasPermission("shop.page.manage")) {
            player.sendMessage(Config.PERMISSION_ERROR.toString());
         } else {
            if (isManaging(player, false)) {
               player.sendMessage(ChatColor.GREEN + "You are managing " + manager.getEditorPage(player));
            }

         }
      } else if (args.length < 2) {
         this.help(player);
      } else {
         String command = args[1];
         String pagename;
         Page page;
         if (command.equalsIgnoreCase("edit")) {
            if (!player.hasPermission("shop.page.edit")) {
               player.sendMessage(Config.PERMISSION_ERROR.toString());
               return;
            }

            pagename = null;
            if (args.length == 3) {
               pagename = args[2];
            }

            if (pagename != null) {
               page = manager.getPage(pagename);
               if (page != null) {
                  page.openEditor(player);
                  return;
               }
            }

            if (isManaging(player, false)) {
               page = manager.getPage(manager.getEditorPage(player));
               if (page != null) {
                  page.openEditor(player);
               }
            }
         } else {
            String header;
            int index;
            if (command.equalsIgnoreCase("add")) {
               if (!player.hasPermission("shop.page.add")) {
                  player.sendMessage(Config.PERMISSION_ERROR.toString());
                  return;
               }

               if (args.length >= 3) {
                  if (isManaging(player, false)) {
                     page = manager.getPage(manager.getEditorPage(player));
                     index = page.getInventory().firstEmpty();
                     if (index == -1) {
                        player.sendMessage(ChatColor.RED + "The page is full");
                        return;
                     }

                     if (!this.hasItemInHand(player)) {
                        player.sendMessage(ChatColor.RED + "You need an item in your hand to use this command");
                        return;
                     }

                     header = args[2];
                     double amount = 0.0D;
                     double sell = 0.0D;

                     try {
                        amount = Double.parseDouble(header);
                     } catch (NumberFormatException var16) {
                     }

                     if (args.length >= 4) {
                        try {
                           sell = Double.parseDouble(args[3]);
                        } catch (NumberFormatException var15) {
                        }
                     }

                     if (page != null) {
                        page.setItem(index, player.getInventory().getItemInMainHand());
                        PageSlot ps = page.getPageSlot(index);
                        if (amount > 0.0D) {
                           ps.setFunction(Function.BUY);
                        }

                        if (amount == 0.0D && sell > 0.0D) {
                           ps.setFunction(Function.SELL);
                        }

                        if (amount == 0.0D && sell == 0.0D) {
                           ps.setFunction(Function.NONE);
                        }

                        if (amount > 0.0D) {
                           ps.setCost(amount);
                        }

                        if (sell > 0.0D) {
                           ps.setSell(sell);
                        }

                        player.sendMessage(ChatColor.GREEN + Editor.getItemName(player.getInventory().getItemInMainHand()) + " has been added to " + page.getID() + " with the cost as " + DoubleUtil.toString(amount) + ", sell as " + DoubleUtil.toString(sell) + ", and function as " + ps.getFunction().toString());
                     }

                     return;
                  }

                  return;
               }

               player.sendMessage(ChatColor.GRAY + "/shop page add <cost> <sell>");
            } else if (command.equalsIgnoreCase("title")) {
               if (!player.hasPermission("shop.page.title")) {
                  player.sendMessage(Config.PERMISSION_ERROR.toString());
                  return;
               }

               if (isManaging(player, false)) {
                  if (args.length >= 3) {
                     page = manager.getPage(manager.getEditorPage(player));
                     StringBuilder sb = new StringBuilder();

                     for(int i = 2; i < args.length; ++i) {
                        sb.append(args[i]).append(" ");
                     }

                     header = ChatColor.translateAlternateColorCodes('&', sb.toString().trim());
                     page.setTitle(header);
                     player.sendMessage(ChatColor.GREEN + "Title of " + page.getID() + " has been set to " + header);
                     return;
                  }

                  player.sendMessage(ChatColor.GRAY + "/shop page title <title>");
               }
            } else {
               if (command.equalsIgnoreCase("copy")) {
                  if (!player.hasPermission("shop.page.copy")) {
                     player.sendMessage(Config.PERMISSION_ERROR.toString());
                     return;
                  }

                  if (isManaging(player, false)) {
                     if (args.length == 3) {
                        page = manager.getPage(manager.getEditorPage(player));
                        header = args[2].toUpperCase();
                        if (manager.getPage(header) != null) {
                           page.copy(manager.getPage(header));
                           player.sendMessage(ChatColor.GREEN + header + " has been copied to " + page.getID());
                        } else {
                           player.sendMessage(ChatColor.RED + header + " does not exist");
                        }

                        return;
                     }

                     player.sendMessage(ChatColor.GRAY + "/shop page copy <page>");
                  }
               } else if (command.equalsIgnoreCase("recover")) {
                  if (!player.hasPermission("shop.page.recover")) {
                     player.sendMessage(Config.PERMISSION_ERROR.toString());
                     return;
                  }

                  if (manager.hasRecoveryPage(player)) {
                     page = manager.getRecoveryPage(player);
                     if (manager.getPage(page.getID()) == null) {
                        Manager.pages.add(page);
                        player.sendMessage(ChatColor.GREEN + page.getID() + " has been recovered");
                        return;
                     }

                     player.sendMessage(ChatColor.RED + "Failed to recover because the page already exists");
                     return;
                  }

                  player.sendMessage(ChatColor.RED + "No page found to recover");
               } else if (command.equalsIgnoreCase("size")) {
                  if (!player.hasPermission("shop.page.size")) {
                     player.sendMessage(Config.PERMISSION_ERROR.toString());
                     return;
                  }

                  if (args.length == 3) {
                     if (isManaging(player, false)) {
                        page = manager.getPage(manager.getEditorPage(player));

                        try {
                           index = Integer.parseInt(args[2]);
                        } catch (NumberFormatException var17) {
                           player.sendMessage(ChatColor.RED + "Invalid size");
                           return;
                        }

                        if (index <= 0) {
                           index = 1;
                        }

                        if (index > 6) {
                           index = 6;
                        }

                        page.setSize(index);
                        player.sendMessage(ChatColor.GREEN + "Size of " + page.getID() + " has been set to " + index);
                     }

                     return;
                  }

                  player.sendMessage(ChatColor.GRAY + "/shop page size <1-6>");
               } else if (command.equalsIgnoreCase("manage")) {
                  if (!player.hasPermission("shop.page.manage")) {
                     player.sendMessage(Config.PERMISSION_ERROR.toString());
                     return;
                  }

                  if (args.length == 3) {
                     pagename = args[2].toUpperCase();
                     if (manager.getPage(pagename) != null) {
                        Manager.edit.put(player.getName(), pagename);
                        player.sendMessage(ChatColor.GREEN + "You are now managing " + pagename + ChatColor.GREEN);
                     } else {
                        player.sendMessage(ChatColor.RED + pagename + " does not exist");
                     }

                     return;
                  }

                  player.sendMessage(ChatColor.GRAY + "/shop page manage <page>");
               } else if (command.equalsIgnoreCase("delete")) {
                  if (!player.hasPermission("shop.page.delete")) {
                     player.sendMessage(Config.PERMISSION_ERROR.toString());
                     return;
                  }

                  if (args.length == 3) {
                     pagename = args[2].toUpperCase();
                     if (manager.getPage(pagename) != null) {
                        page = manager.getPage(pagename);
                        Manager.pagerecovery.put(player.getUniqueId().toString(), new Page(page));
                        page.delete();
                        player.sendMessage(ChatColor.GREEN + pagename + " has been deleted");
                     } else {
                        player.sendMessage(ChatColor.RED + pagename + " does not exist");
                     }

                     return;
                  }

                  player.sendMessage(ChatColor.RED + "/shop page delete <page>");
               } else if (command.equalsIgnoreCase("create")) {
                  if (!player.hasPermission("shop.page.create")) {
                     player.sendMessage(Config.PERMISSION_ERROR.toString());
                     return;
                  }

                  if (args.length == 3) {
                     pagename = args[2].toUpperCase();
                     if (manager.getPage(pagename) == null) {
                        page = new Page(pagename);
                        page.create();
                        player.sendMessage(ChatColor.GREEN + pagename + " has been created");
                        Manager.edit.put(player.getName(), pagename);
                     } else {
                        player.sendMessage(ChatColor.RED + pagename + " already exists");
                     }

                     return;
                  }

                  player.sendMessage(ChatColor.RED + "/shop page create <entry>");
               } else if (command.equalsIgnoreCase("clear")) {
                  if (!player.hasPermission("shop.page.clear")) {
                     player.sendMessage(Config.PERMISSION_ERROR.toString());
                     return;
                  }

                  if (isManaging(player, false)) {
                     page = getPage(player);
                     if (page != null) {
                        page.clearItems();
                        player.sendMessage(ChatColor.GREEN + "All items have been cleared from " + page.getID());
                     }

                     return;
                  }
               } else if (command.equalsIgnoreCase("type")) {
                  if (!player.hasPermission("shop.page.type")) {
                     player.sendMessage(Config.PERMISSION_ERROR.toString());
                     return;
                  }

                  if (isManaging(player, false)) {
                     page = getPage(player);
                     if (page != null) {
                        if (args.length == 3) {
                           header = args[2];
                           if (header.equalsIgnoreCase("sell")) {
                              page.setType(1);
                           } else {
                              page.setType(0);
                           }

                           player.sendMessage(ChatColor.GREEN + "Type of " + page.getID() + " has been set to " + new String(page.getType() == 1 ? "sell" : "normal"));
                           return;
                        }

                        player.sendMessage(ChatColor.RED + "/shop page type <normal/sell>");
                     }

                     return;
                  }
               } else if (command.equalsIgnoreCase("properties")) {
                  if (!player.hasPermission("shop.page.properties")) {
                     player.sendMessage(Config.PERMISSION_ERROR.toString());
                     return;
                  }

                  if (isManaging(player, false)) {
                     page = getPage(player);
                     if (page != null) {
                        PageProperties.open(player, page, 1);
                     }

                     return;
                  }
               } else if (command.equalsIgnoreCase("list")) {
                  if (!player.hasPermission("shop.page.list")) {
                     player.sendMessage(Config.PERMISSION_ERROR.toString());
                     return;
                  }

                  index = 1;
                  if (args.length == 3) {
                     try {
                        index = Integer.parseInt(args[2]);
                     } catch (NumberFormatException var19) {
                     }
                  }

                  header = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "Shop Pages" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
                  List<String> help = new ArrayList();
                  Iterator var11 = (new Manager()).getPages().iterator();

                  while(var11.hasNext()) {
                     page = (Page)var11.next();
                     help.add(ChatColor.GRAY + "- " + ChatColor.YELLOW + page.getID() + ChatColor.GRAY + "  Title: " + ChatColor.RESET + page.getTitle() + ChatColor.GRAY + "  Type: " + ChatColor.RESET + new String(page.getType() == 1 ? "Sell" : "Normal"));
                  }

                  Control.list(player, help, index, header, 9);
               } else if (command.equalsIgnoreCase("open")) {
                  if (args.length == 3) {
                     pagename = args[2].toUpperCase();
                     if (!player.hasPermission("shop.page.open." + pagename) && !player.hasPermission("shop.page.open")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                     }

                     if (manager.getPage(pagename) != null) {
                        page = manager.getPage(pagename);
                        page.openPage(player);
                     } else {
                        player.sendMessage(ChatColor.RED + pagename + " does not exist");
                     }

                     return;
                  }

                  this.help(player);
               } else if (command.equalsIgnoreCase("help")) {
                  List<String> help = Manager.getAvailableCommands(player, "page");
                  if (help.isEmpty()) {
                     player.sendMessage(Config.PERMISSION_ERROR.toString());
                     return;
                  }

                  index = 1;
                  if (args.length == 3) {
                     try {
                        index = Integer.parseInt(args[2]);
                     } catch (NumberFormatException var18) {
                     }
                  }

                  header = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "Shop Page Help" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
                  Control.list(player, help, index, header, 7);
               } else {
                  this.help(player);
               }
            }
         }

      }
   }

   public void help(Player player) {
      if (!Manager.getAvailableCommands(player, "page").isEmpty()) {
         player.sendMessage(ChatColor.GRAY + "/shop page help");
      } else {
         player.sendMessage(Config.PERMISSION_ERROR.toString());
      }

   }

   public void cancelEdit(Player player) {
      if (Manager.edit.containsKey(player.getName())) {
         Manager.edit.remove(player.getName());
      }

   }

   public boolean isEditing(Player player, String id) {
      return Manager.edit.containsKey(player.getName()) && ((String)Manager.edit.get(player.getName())).equals(id);
   }

   public static boolean isManaging(Player player, boolean silent) {
      if (Manager.edit.containsKey(player.getName())) {
         Manager manager = new Manager();
         String id = (String)Manager.edit.get(player.getName());
         if (manager.getPage(id) != null) {
            return true;
         }

         player.sendMessage(ChatColor.RED + "You need to select a page to manage. /shop page manage <page>");
         Manager.edit.remove(player.getName());
      } else {
         player.sendMessage(ChatColor.RED + "You need to select a page to manage. /shop page manage <page>");
      }

      return false;
   }

   public static Page getPage(Player player) {
      if (Manager.edit.containsKey(player.getName())) {
         Manager manager = new Manager();
         String id = (String)Manager.edit.get(player.getName());
         return manager.getPage(id);
      } else {
         return null;
      }
   }

   public boolean hasItemInHand(Player player) {
      return player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR);
   }
}
