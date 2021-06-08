package conj.Shop.cmd;

import conj.Shop.control.Control;
import conj.Shop.control.Manager;
import conj.Shop.enums.Config;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class ConfigManagement {
   public void run(Player player, Command cmd, String label, String[] args) {
      if (args.length >= 2) {
         String command = args[1];
         if (command.equalsIgnoreCase("edit")) {
            if (!player.hasPermission("shop.config.edit")) {
               player.sendMessage(Config.PERMISSION_ERROR.toString());
            }
         } else if (command.equalsIgnoreCase("help")) {
            List<String> help = Manager.getAvailableCommands(player, "config");
            if (help.isEmpty()) {
               player.sendMessage(Config.PERMISSION_ERROR.toString());
            } else {
               int index = 1;
               if (args.length == 3) {
                  try {
                     index = Integer.parseInt(args[2]);
                  } catch (NumberFormatException var9) {
                  }
               }

               String header = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "Shop Config Help" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
               Control.list(player, help, index, header, 7);
            }
         }
      } else {
         if (!Manager.getAvailableCommands(player, "config").isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "/shop config help");
         } else {
            player.sendMessage(Config.PERMISSION_ERROR.toString());
         }

      }
   }
}
