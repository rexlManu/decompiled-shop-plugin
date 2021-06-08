package conj.Shop.cmd;

import conj.Shop.control.Control;
import conj.Shop.control.Manager;
import conj.Shop.enums.Config;
import java.util.Iterator;
import java.util.List;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CitizensManagement {
   public void run(Player player, Command cmd, String label, String[] args) {
      Manager manager = new Manager();
      if (args.length >= 2) {
         String command = args[1];
         NPC npc;
         String sub;
         if (command.equalsIgnoreCase("page")) {
            if (!player.hasPermission("shop.citizen.page")) {
               player.sendMessage(Config.PERMISSION_ERROR.toString());
            } else {
               npc = CitizensAPI.getDefaultNPCSelector().getSelected(player);
               if (npc != null) {
                  if (args.length == 3) {
                     sub = args[2];
                     if (manager.getPage(sub) != null) {
                        manager.setCitizenPage(npc.getId(), sub);
                        player.sendMessage(ChatColor.GREEN + "Page of " + npc.getName() + ChatColor.GREEN + " has been set to " + manager.getPage(sub).getID());
                     } else {
                        player.sendMessage(ChatColor.RED + sub + " does not exist");
                     }
                  } else {
                     player.sendMessage(ChatColor.GRAY + "/shop citizen page <page>");
                  }
               } else {
                  player.sendMessage(ChatColor.RED + "You need to select an NPC");
               }
            }
         } else {
            String p;
            if (command.equalsIgnoreCase("permission")) {
               if (!player.hasPermission("shop.citizen.permission.add") && !player.hasPermission("shop.citizen.permission.remove") && !player.hasPermission("shop.citizen.permission.clear")) {
                  player.sendMessage(Config.PERMISSION_ERROR.toString());
                  return;
               }

               npc = CitizensAPI.getDefaultNPCSelector().getSelected(player);
               if (npc != null) {
                  if (args.length > 3) {
                     sub = args[2];
                     StringBuilder sb;
                     int i;
                     boolean removed;
                     String text;
                     if (sub.equalsIgnoreCase("add")) {
                        if (!player.hasPermission("shop.citizen.permission.add")) {
                           player.sendMessage(Config.PERMISSION_ERROR.toString());
                           return;
                        }

                        sb = new StringBuilder();

                        for(i = 3; i < args.length; ++i) {
                           sb.append(args[i]).append(" ");
                        }

                        text = sb.toString().trim();
                        removed = manager.addCitizenPermission(npc.getId(), text);
                        player.sendMessage(new String(removed ? ChatColor.GREEN + "Added permission " + text + " to " + npc.getFullName() : ChatColor.RED + "Failed to add permission " + text + " to " + npc.getFullName()));
                        return;
                     }

                     if (sub.equalsIgnoreCase("remove")) {
                        if (!player.hasPermission("shop.citizen.permission.remove")) {
                           player.sendMessage(Config.PERMISSION_ERROR.toString());
                           return;
                        }

                        sb = new StringBuilder();

                        for(i = 3; i < args.length; ++i) {
                           sb.append(args[i]).append(" ");
                        }

                        text = sb.toString().trim();
                        removed = manager.removeCitizenPermission(npc.getId(), text);
                        player.sendMessage(new String(removed ? ChatColor.GREEN + "Removed permission " + text + " from " + npc.getFullName() : ChatColor.RED + "Failed to remove permission " + text + " from " + npc.getFullName()));
                        return;
                     }
                  } else if (args.length == 3) {
                     sub = args[2];
                     if (sub.equalsIgnoreCase("clear")) {
                        if (!player.hasPermission("shop.citizen.permission.clear")) {
                           player.sendMessage(Config.PERMISSION_ERROR.toString());
                           return;
                        }

                        manager.clearCitizenPermissions(npc.getId());
                        player.sendMessage(ChatColor.GREEN + "Permissions of " + npc.getFullName() + " have been cleared");
                        return;
                     }
                  }

                  List<String> permissions = manager.getCitizenPermissions(npc.getId());
                  player.sendMessage(ChatColor.DARK_GREEN + npc.getFullName() + "'s permissions");
                  Iterator var18 = permissions.iterator();

                  while(var18.hasNext()) {
                     p = (String)var18.next();
                     player.sendMessage(ChatColor.GRAY + "- " + ChatColor.GREEN + p);
                  }

                  return;
               }

               player.sendMessage(ChatColor.RED + "You need to select an NPC");
            } else if (command.equalsIgnoreCase("help")) {
               List<String> help = Manager.getAvailableCommands(player, "citizen");
               if (help.isEmpty()) {
                  player.sendMessage(Config.PERMISSION_ERROR.toString());
                  return;
               }

               int index = 1;
               if (args.length == 3) {
                  try {
                     index = Integer.parseInt(args[2]);
                  } catch (NumberFormatException var12) {
                  }
               }

               p = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "Shop Citizen Help" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
               Control.list(player, help, index, p, 7);
               return;
            }

            if (!Manager.getAvailableCommands(player, "citizen").isEmpty()) {
               player.sendMessage(ChatColor.GRAY + "/shop citizen help");
            } else {
               player.sendMessage(Config.PERMISSION_ERROR.toString());
            }

         }
      } else {
         if (!Manager.getAvailableCommands(player, "citizen").isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "/shop citizen help");
         } else {
            player.sendMessage(Config.PERMISSION_ERROR.toString());
         }

      }
   }
}
