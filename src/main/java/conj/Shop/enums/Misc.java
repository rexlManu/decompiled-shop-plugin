package conj.Shop.enums;

import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;

public enum Misc {
   UPDATE_2_X(false);

   private String message;
   private List<String> messages;
   private boolean active;

   private Misc(String message) {
      this.message = message;
   }

   private Misc(List<String> messages) {
      this.messages = messages;
   }

   private Misc(boolean active) {
      this.active = active;
   }

   public List<String> getList() {
      return this.messages;
   }

   public Object getValue() {
      if (this.message != null) {
         return this.message;
      } else {
         return this.messages != null ? this.messages : this.active;
      }
   }

   public boolean isActive() {
      return this.active;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public void setBoolean(Boolean active) {
      this.active = active;
   }

   public String toString() {
      if (this.message != null) {
         return ChatColor.translateAlternateColorCodes('&', this.message);
      } else if (this.messages == null) {
         return this.active ? "True" : "False";
      } else {
         String build = "";

         String s;
         for(Iterator var3 = this.messages.iterator(); var3.hasNext(); build = build + s) {
            s = (String)var3.next();
         }

         return ChatColor.translateAlternateColorCodes('&', this.message);
      }
   }
}
