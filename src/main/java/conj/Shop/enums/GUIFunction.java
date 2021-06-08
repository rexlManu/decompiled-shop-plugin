package conj.Shop.enums;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public enum GUIFunction {
   NONE("The item will do nothing when clicked"),
   QUANTITY("The item will allow you to increase/decrease the quantity"),
   CONFIRM("The item will act as a confirm button"),
   BACK("The item will take you back");

   private String description;

   private GUIFunction(String description) {
      this.description = description;
   }

   public String getDescription() {
      return ChatColor.translateAlternateColorCodes('&', this.description);
   }

   public String toString() {
      return WordUtils.capitalizeFully(this.name().toLowerCase().replaceAll("_", " "));
   }

   public static GUIFunction fromString(String string) {
      GUIFunction function = NONE;
      string = string.replaceAll(" ", "_");
      string = string.toUpperCase();
      function = valueOf(string);
      return function;
   }
}
