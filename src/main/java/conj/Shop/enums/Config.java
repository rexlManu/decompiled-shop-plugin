package conj.Shop.enums;

import conj.Shop.auto.Autobackup;
import conj.Shop.auto.Autosave;
import conj.Shop.base.Initiate;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public enum Config {
   MAIN_PAGE("Misc", ""),
   UPDATE_CHECK("Misc", true),
   COMMAND_PLACEHOLD("Misc", false),
   PURCHASE_GUI("Purchase", ""),
   COST_PREFIX("Purchase", "&aCost&7: &2"),
   SHOP_PURCHASE("Purchase", Initiate.shop_purchase),
   SELL_GUI("Sell", ""),
   SELL_PREFIX("Sell", "&bSell&7: &3"),
   SELL_COMPLETE("Sell", "&aYou earned &2%worth%"),
   SHOP_SELL("Sell", Initiate.shop_sell),
   TRADE_GUI("Trade", ""),
   BLACKLIST_ERROR("Messages", "&cYou can't use the shop in this world"),
   PERMISSION_ERROR("Messages", "&cYou don't have permission to use this command"),
   COST_CANNOT_AFFORD("Messages", "&cYou can't afford this item"),
   TRADE_NEED_SPACE("Messages", "&cYou need space in your inventory to complete this trade"),
   AUTOSAVE("Auto", true),
   AUTOBACKUP("Auto", true),
   AUTOSAVE_DELAY("Auto", 20),
   SIGN_TAG("Sign", "[Shop]"),
   SIGN_ENABLED("Sign", true);

   private String base;
   private String message;
   private List<String> messages;
   private int numeral;
   private boolean active;

   private Config(String base, String message) {
      this.base = base;
      this.message = message;
   }

   private Config(String base, List<String> messages) {
      this.base = base;
      this.messages = messages;
   }

   private Config(String base, boolean active) {
      this.base = base;
      this.active = active;
   }

   private Config(String base, int numeral) {
      this.base = base;
      this.numeral = numeral;
   }

   public String getBase() {
      return this.base;
   }

   public List<String> getList() {
      return this.messages;
   }

   public String getMessage() {
      return this.message;
   }

   public Object getValue() {
      if (this.message != null) {
         return this.message;
      } else if (this.messages != null) {
         return this.messages;
      } else {
         return this.numeral > 0 ? this.numeral : this.active;
      }
   }

   public int getNumeral() {
      return this.numeral;
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

   public static void save() {
      Plugin plugin = Initiate.getPlugin(Initiate.class);
      Config[] var4;
      int var3 = (var4 = values()).length;

      for(int var2 = 0; var2 < var3; ++var2) {
         Config c = var4[var2];
         plugin.getConfig().set(c.name(), c.getValue());
      }

      plugin.saveConfig();
   }

   public static void load() {
      Plugin plugin = Initiate.getPlugin(Initiate.class);
      plugin.reloadConfig();
      Config[] var4;
      int var3 = (var4 = values()).length;

      for(int var2 = 0; var2 < var3; ++var2) {
         Config c = var4[var2];
         Object o;
         if (plugin.getConfig().get(c.base + "." + c.name()) != null) {
            o = plugin.getConfig().get(c.base + "." + c.name());

            try {
               if (o instanceof Boolean) {
                  boolean ob = (Boolean)o;
                  c.active = ob;
               }

               if (o instanceof Integer) {
                  int ib = (Integer)o;
                  c.numeral = ib;
               }

               if (o instanceof String) {
                  String sb = (String)o;
                  c.message = sb;
               }

               if (o instanceof List) {
                  List<String> lb = (List)o;
                  c.messages = lb;
               }
            } catch (ClassCastException var7) {
               plugin.getLogger().info("Failed to load config value: " + c.base + "." + c.name());
            }
         } else {
            o = plugin.getConfig().get(c.name());
            plugin.getConfig().set(c.base + "." + c.name(), o == null ? c.getValue() : o);
         }
      }

      plugin.saveConfig();
      Autosave.start();
      Autobackup.start();
   }

   public String toString() {
      if (this.message != null) {
         return ChatColor.translateAlternateColorCodes('&', this.message);
      } else if (this.messages == null) {
         if (this.numeral > 0) {
            return String.valueOf(this.numeral);
         } else {
            return this.active ? "True" : "False";
         }
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
