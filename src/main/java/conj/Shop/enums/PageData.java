package conj.Shop.enums;

import org.bukkit.ChatColor;

public enum PageData {
   NONE("Nothing"),
   SHOP("Normal view page"),
   PURCHASE_ITEM("Purchase item"),
   TRADE_ITEM("Trade item"),
   SELL_ITEM("Sell item"),
   EDIT_ITEM("Change item's display"),
   EDIT_ITEM_HIDEMODE("Change item's hidemode"),
   EDIT_ITEM_VIEW("Page editor select item"),
   EDIT_ITEM_MOVE("Change item's position"),
   EDIT_ITEM_MANAGE("Manage clicked item"),
   EDIT_ITEM_FUNCTION("Change item's function"),
   EDIT_ITEM_MESSAGES("Change item's messages"),
   EDIT_ITEM_INVENTORY("Change item's inventory"),
   MOVE_ITEM("Move items"),
   PAGE_PROPERTIES("Page properties"),
   PAGE_PROPERTIES_FILL_SLOTS("Page properties - fill slots"),
   GUI("View and interact with GUI");

   private String info;

   private PageData(String info) {
      this.info = info;
   }

   public String getInfo() {
      return ChatColor.translateAlternateColorCodes('&', this.info);
   }
}
