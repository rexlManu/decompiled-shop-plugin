package conj.Shop.enums;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;

public enum MessageType {
   COOLDOWN("&cYou're currently on cooldown."),
   PERMISSION("&cYou don't have enough permission to access this item.");

   private String def;

   private MessageType(String def) {
      this.def = def;
   }

   public List<String> getDefault() {
      List<String> list = new ArrayList();
      list.add(ChatColor.translateAlternateColorCodes('&', this.def));
      return list;
   }

   public static MessageType fromString(String string) {
      MessageType type = COOLDOWN;
      string = string.replaceAll(" ", "_");
      string = string.toUpperCase();
      type = valueOf(string);
      return type;
   }
}
