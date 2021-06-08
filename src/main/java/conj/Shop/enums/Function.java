package conj.Shop.enums;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public enum Function {
   NONE("The item will do nothing when clicked"),
   BUY("The item can be purchased"),
   SELL("The item can only be sold"),
   TRADE("The item can be traded for"),
   COMMAND("The item will run commands when clicked"),
   CONFIRM("The item will confirm the sell");

   private String description;

   private Function(String description) {
      this.description = description;
   }

   public String getDescription() {
      return ChatColor.translateAlternateColorCodes('&', this.description);
   }

   public String toString() {
      return WordUtils.capitalizeFully(this.name().toLowerCase().replaceAll("_", " "));
   }

   public static Function fromString(String string) {
      Function function = NONE;
      string = string.replaceAll(" ", "_");
      string = string.toUpperCase();
      function = valueOf(string);
      return function;
   }
}
