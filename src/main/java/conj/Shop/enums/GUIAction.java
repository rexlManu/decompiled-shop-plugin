package conj.Shop.enums;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public enum GUIAction {
   NONE("The item will do nothing when clicked"),
   PURCHASE("Buys item when clicked"),
   SELL("Sells item when clicked");

   private String description;

   private GUIAction(String description) {
      this.description = description;
   }

   public String getDescription() {
      return ChatColor.translateAlternateColorCodes('&', this.description);
   }

   public String toString() {
      return WordUtils.capitalizeFully(this.name().toLowerCase().replaceAll("_", " "));
   }

   public static GUIAction fromString(String string) {
      GUIAction action = NONE;
      string = string.replaceAll(" ", "_");
      string = string.toUpperCase();
      action = valueOf(string);
      return action;
   }
}
