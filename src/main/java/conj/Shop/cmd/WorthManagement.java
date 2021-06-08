package conj.Shop.cmd;

import conj.Shop.control.Control;
import conj.Shop.control.Manager;
import conj.Shop.enums.Config;
import conj.Shop.interaction.Editor;
import conj.Shop.tools.DoubleUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WorthManagement {
   public void run(Player player, Command cmd, String label, String[] args) {
      double amount;
      if (args.length == 1) {
         if (!player.hasPermission("shop.worth.item")) {
            player.sendMessage(Config.PERMISSION_ERROR.toString());
         } else if (player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR)) {
            double worth = Manager.get().getWorth(player.getItemInHand());
            amount = Manager.get().getFlatWorth(player.getItemInHand());
            player.sendMessage(ChatColor.BLUE + Editor.getItemName(player.getItemInHand()) + ChatColor.DARK_GRAY + ":" + ChatColor.AQUA + player.getItemInHand().getDurability() + ChatColor.GRAY + " : " + ChatColor.GREEN + worth + ChatColor.DARK_GRAY + " (" + ChatColor.GRAY + "x" + ChatColor.GOLD + player.getItemInHand().getAmount() + ChatColor.DARK_GRAY + ")" + ChatColor.GRAY + " : " + ChatColor.GREEN + amount + ChatColor.DARK_GRAY + " (" + ChatColor.GRAY + "x" + ChatColor.GOLD + "1" + ChatColor.DARK_GRAY + ")");
         } else {
            player.sendMessage(ChatColor.RED + "You need an item in your hand to use this command");
         }
      } else if (args.length < 2) {
         if (!Manager.getAvailableCommands(player, "worth").isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "/shop worth help");
         } else {
            player.sendMessage(Config.PERMISSION_ERROR.toString());
         }

      } else {
         String command = args[1];
         if (command.equalsIgnoreCase("set")) {
            if (!player.hasPermission("shop.worth.set")) {
               player.sendMessage(Config.PERMISSION_ERROR.toString());
               return;
            }

            if (args.length == 3) {
               if (player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR)) {
                  String str = args[2];
                  amount = 0.0D;

                  try {
                     amount = Double.parseDouble(str);
                  } catch (NumberFormatException var16) {
                     player.sendMessage(ChatColor.RED + "Invalid amount");
                     return;
                  }

                  if (amount < 0.0D) {
                     amount = 0.0D;
                  }

                  Manager.get().setWorth(player.getItemInHand(), amount, true);
                  player.sendMessage(ChatColor.GREEN + "Worth of " + Editor.getItemName(player.getItemInHand()) + ChatColor.DARK_GRAY + ":" + ChatColor.AQUA + player.getItemInHand().getDurability() + ChatColor.GREEN + " has been set to " + DoubleUtil.toString(amount));
                  return;
               }

               player.sendMessage(ChatColor.RED + "You need an item in your hand to use this command");
               return;
            }

            player.sendMessage(ChatColor.GRAY + "/shop worth set <amount>");
         } else if (command.equalsIgnoreCase("list")) {
            if (!player.hasPermission("shop.worth.list")) {
               player.sendMessage(Config.PERMISSION_ERROR.toString());
               return;
            }

            int index = 1;
            if (args.length == 3) {
               try {
                  index = Integer.parseInt(args[2]);
               } catch (NumberFormatException var18) {
               }
            }

            String header = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "Shop Worth" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
            List<String> help = new ArrayList();
            Material[] var12;
            int var11 = (var12 = Material.values()).length;

            for(int var10 = 0; var10 < var11; ++var10) {
               Material m = var12[var10];
               ItemStack item = new ItemStack(m);
               item.setAmount(1);
               double worth = Manager.get().getWorth(item);
               if (worth > 0.0D) {
                  help.add(ChatColor.BLUE + Editor.getItemName(item) + ChatColor.GRAY + " : " + ChatColor.GREEN + worth);
               }
            }

            Control.list(player, help, index, header, 9);
         } else if (command.equalsIgnoreCase("help")) {
            List<String> help = Manager.getAvailableCommands(player, "worth");
            if (help.isEmpty()) {
               player.sendMessage(Config.PERMISSION_ERROR.toString());
               return;
            }

            int index = 1;
            if (args.length == 3) {
               try {
                  index = Integer.parseInt(args[2]);
               } catch (NumberFormatException var17) {
               }
            }

            String header = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "Shop Worth Help" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
            Control.list(player, help, index, header, 7);
         } else if (!Manager.getAvailableCommands(player, "worth").isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "/shop worth help");
         } else {
            player.sendMessage(Config.PERMISSION_ERROR.toString());
         }

      }
   }
}
