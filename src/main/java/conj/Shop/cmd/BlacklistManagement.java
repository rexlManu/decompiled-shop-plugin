package conj.Shop.cmd;

import conj.Shop.control.Control;
import conj.Shop.control.Manager;
import conj.Shop.enums.Config;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class BlacklistManagement {
   public void run(Player player, Command cmd, String label, String[] args) {
      Manager manager = new Manager();
      if (args.length < 2) {
         if (!Manager.getAvailableCommands(player, "blacklist").isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "/shop blacklist help");
         } else {
            player.sendMessage(Config.PERMISSION_ERROR.toString());
         }

      } else {
         String command = args[1];
         World world;
         if (command.equalsIgnoreCase("add")) {
            if (!player.hasPermission("shop.blacklist.add")) {
               player.sendMessage(Config.PERMISSION_ERROR.toString());
            } else {
               world = player.getLocation().getWorld();
               if (manager.blacklistContains(world.getName())) {
                  player.sendMessage(ChatColor.RED + world.getName() + " is already on the blacklist");
               } else {
                  manager.blacklistAdd(world.getName());
                  player.sendMessage(ChatColor.GREEN + world.getName() + " has been added to the blacklist");
               }
            }
         } else if (command.equalsIgnoreCase("remove")) {
            if (!player.hasPermission("shop.blacklist.remove")) {
               player.sendMessage(Config.PERMISSION_ERROR.toString());
            } else {
               world = player.getLocation().getWorld();
               if (!manager.blacklistContains(world.getName())) {
                  player.sendMessage(ChatColor.RED + world.getName() + " is not on the blacklist");
               } else {
                  manager.blacklistRemove(world.getName());
                  player.sendMessage(ChatColor.GREEN + world.getName() + " has been removed from the blacklist");
               }
            }
         } else {
            if (command.equalsIgnoreCase("list")) {
               if (!player.hasPermission("shop.blacklist.list")) {
                  player.sendMessage(Config.PERMISSION_ERROR.toString());
                  return;
               }

               int index = 1;
               if (args.length == 3) {
                  try {
                     index = Integer.parseInt(args[2]);
                  } catch (NumberFormatException var13) {
                  }
               }

               String header = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "Shop Blacklist" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
               List<String> help = new ArrayList();
               Iterator var11 = Manager.get().getBlacklist().iterator();

               while(var11.hasNext()) {
                  help.add(ChatColor.GRAY + "- " + ChatColor.GOLD + var11.next());
               }

               Control.list(player, help, index, header, 9);
            } else if (command.equalsIgnoreCase("help")) {
               List<String> help = Manager.getAvailableCommands(player, "blacklist");
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

               String header = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "Shop Blacklist Help" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
               Control.list(player, help, index, header, 7);
               return;
            }

         }
      }
   }
}
