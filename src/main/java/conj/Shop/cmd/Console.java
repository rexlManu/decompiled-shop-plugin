package conj.Shop.cmd;

import conj.Shop.base.Initiate;
import conj.Shop.control.Manager;
import conj.Shop.data.Page;
import conj.Shop.enums.Config;
import conj.Shop.tools.Placeholder;
import java.util.Iterator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Console {
   public boolean run(CommandSender sender, Command cmd, String label, String[] args) {
      Manager manager = new Manager();
      int var10000 = args.length;
      if (args.length >= 2) {
         String command = args[0];
         String arg2;
         String arg3;
         String xs;
         Player player;
         Page page;
         if (command.equalsIgnoreCase("open")) {
            if (!sender.hasPermission("shop.console.page")) {
               sender.sendMessage(Config.PERMISSION_ERROR.toString());
               return true;
            }

            if (args.length == 4) {
               arg2 = args[1];
               if (arg2.equalsIgnoreCase("page")) {
                  arg3 = args[3];
                  xs = args[2];
                  player = Bukkit.getPlayer(arg3);
                  page = manager.getPage(xs);
                  if (page != null && player != null) {
                     page.openPage(player);
                  }

                  return true;
               }
            }
         } else {
            int to;
            if (command.equalsIgnoreCase("page")) {
               if (!sender.hasPermission("shop.console.page")) {
                  sender.sendMessage(Config.PERMISSION_ERROR.toString());
                  return true;
               }

               if (args.length == 4) {
                  arg2 = args[1];
                  if (arg2.equalsIgnoreCase("open")) {
                     arg3 = args[3];
                     xs = args[2];
                     player = Bukkit.getPlayer(arg3);
                     page = manager.getPage(xs);
                     if (page != null && player != null) {
                        page.openPage(player);
                     }

                     return true;
                  }
               } else if (args.length == 6) {
                  arg2 = args[1];
                  if (arg2.equalsIgnoreCase("move")) {
                     arg3 = args[2];
                     boolean var28 = false;

                     int from;
                     try {
                        from = Integer.parseInt(args[3]);
                        to = Integer.parseInt(args[4]);
                     } catch (NumberFormatException var23) {
                        return true;
                     }

                     page = manager.getPage(arg3);
                     if (page != null) {
                        if (args[5].equalsIgnoreCase("soft")) {
                           page.moveItemSoft(from, to);
                        } else if (args[5].equalsIgnoreCase("hard")) {
                           page.moveItem(from, to);
                        }

                        page.updateViewers(true);
                     }
                  }
               }
            } else if (command.equalsIgnoreCase("help")) {
               if (!(sender instanceof Player)) {
                  return true;
               }
            } else {
               String text;
               if (command.equalsIgnoreCase("teleport")) {
                  if (args.length >= 6) {
                     arg2 = args[1];
                     arg3 = args[2];
                     xs = args[3];
                     text = args[4];
                     text = args[5];
                     if (Bukkit.getPlayer(arg2) == null) {
                        return true;
                     }

                     player = Bukkit.getPlayer(arg2);
                     if (Bukkit.getWorld(arg3) == null) {
                        return true;
                     }

                     World world = Bukkit.getWorld(arg3);

                     try {
                        double x = Double.parseDouble(xs);
                        double y = Double.parseDouble(text);
                        double z = Double.parseDouble(text);
                        Float yaw = player.getLocation().getYaw();
                        Float pitch = player.getLocation().getPitch();
                        if (args.length >= 7) {
                           yaw = Float.parseFloat(args[6]);
                        }

                        if (args.length >= 8) {
                           pitch = Float.parseFloat(args[7]);
                        }

                        Location location = new Location(world, x, y, z, yaw - 180.0F, pitch);
                        player.teleport(location);
                        return true;
                     } catch (NumberFormatException var25) {
                     }
                  }
               } else {
                  if (command.equalsIgnoreCase("send")) {
                     if (args.length >= 3) {
                        arg2 = args[1];
                        arg3 = args[2];
                        if (arg2.equalsIgnoreCase("message")) {
                           if (args.length >= 4) {
                              player = Bukkit.getPlayer(arg3);
                              StringBuilder sb = new StringBuilder();

                              for(int x = 3; x < args.length; ++x) {
                                 sb.append(args[x]).append(" ");
                              }

                              text = sb.toString().trim();
                              if (player != null) {
                                 player.sendMessage(Placeholder.placehold(player, text));
                              }

                              return true;
                           }
                        } else if (arg2.equalsIgnoreCase("broadcast")) {
                           StringBuilder sb = new StringBuilder();

                           for(to = 2; to < args.length; ++to) {
                              sb.append(args[to]).append(" ");
                           }

                           text = sb.toString().trim();
                           Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', text));
                           return true;
                        }
                     }
                  } else if (command.equalsIgnoreCase("take")) {
                     if (args.length >= 4) {
                        arg2 = args[1];
                        arg3 = args[2];
                        if (arg2.equalsIgnoreCase("money")) {
                           xs = args[3];
                           player = Bukkit.getPlayer(xs);
                           if (player != null) {
                              try {
                                 OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
                                 double amount = Double.parseDouble(arg3);
                                 Initiate.econ.withdrawPlayer(op, amount);
                                 return true;
                              } catch (NumberFormatException var24) {
                              }
                           }
                        }
                     }
                  } else if (command.equalsIgnoreCase("cooldown")) {
                     if (args.length == 3) {
                        arg2 = args[1];
                        arg3 = args[2];
                        if (arg2.equalsIgnoreCase("clear")) {
                           player = Bukkit.getPlayer(arg3);
                           if (player != null) {
                              Iterator var39 = Manager.get().getPages().iterator();

                              while(var39.hasNext()) {
                                 Page p = (Page)var39.next();
                                 p.uncooldown(player);
                              }

                              return true;
                           }
                        }
                     }
                  } else if (command.equalsIgnoreCase("close")) {
                     if (args.length >= 3) {
                        arg2 = args[1];
                        arg3 = args[2];
                        if (arg2.equalsIgnoreCase("inventory")) {
                           player = Bukkit.getPlayer(arg3);
                           if (player != null) {
                              player.closeInventory();
                              return true;
                           }
                        }
                     }
                  } else if (command.equalsIgnoreCase("reload") && !(sender instanceof Player)) {
                     Config.load();
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }
}
