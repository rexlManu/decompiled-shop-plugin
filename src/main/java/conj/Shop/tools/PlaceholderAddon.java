package conj.Shop.tools;

import conj.Shop.base.Initiate;
import conj.Shop.control.Manager;
import conj.Shop.enums.Config;
import java.util.List;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("UnstableApiUsage")
public class PlaceholderAddon {
   public static List<String> placehold(Player player, List<String> messages) {
      messages = PlaceholderAPI.setPlaceholders(player, messages);
      return messages;
   }

   public static String placehold(Player player, String message) {
      message = PlaceholderAPI.setPlaceholders(player, message);
      return message;
   }

   public static void register(Plugin plugin) {
      boolean hooked = PlaceholderAPI.registerPlaceholderHook(plugin, new PlaceholderHook() {
         public String onPlaceholderRequest(Player player, String identify) {
            if (identify.equals("previous")) {
               return String.valueOf((new Manager()).getPreviousPage(player));
            } else if (identify.equals("current")) {
               return String.valueOf((new Manager()).getOpenPage(player));
            } else {
               return identify.equals("main") ? String.valueOf(Config.MAIN_PAGE.toString()) : null;
            }
         }
      });
      if (hooked) {
         plugin.getLogger().info("Successfully hooked into PlaceholderAPI.");
         Initiate.placeholderapi = true;
      }

   }
}
