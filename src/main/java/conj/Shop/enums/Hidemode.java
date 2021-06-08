package conj.Shop.enums;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public enum Hidemode {
   VISIBLE("Visible to all"),
   HIDDEN("Visible to nobody"),
   PERMISSION("Visible to players with permission"),
   OP("Visible to players with OP");

   private String description;

   private Hidemode(String description) {
      this.description = description;
   }

   public String getDescription() {
      return ChatColor.translateAlternateColorCodes('&', this.description);
   }

   public String toString() {
      return WordUtils.capitalizeFully(this.name().toLowerCase().replaceAll("_", " "));
   }

   public static Hidemode fromString(String string) {
      Hidemode hidemode = VISIBLE;
      string = string.replaceAll(" ", "_");
      string = string.toUpperCase();
      hidemode = valueOf(string);
      return hidemode;
   }
}
