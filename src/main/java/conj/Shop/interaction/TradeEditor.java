package conj.Shop.interaction;

import conj.Shop.base.Initiate;
import conj.Shop.data.Page;
import conj.Shop.data.PageSlot;
import conj.Shop.enums.Function;
import conj.Shop.enums.PageData;
import conj.Shop.events.PageClickEvent;
import conj.Shop.tools.GUI;
import conj.Shop.tools.InventoryCreator;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class TradeEditor implements Listener {
   @EventHandler
   public void PageviewClick(PageClickEvent event) {
      Player player = event.getPlayer();
      int slot = event.getSlot();
      Page page = event.getPage();
      ItemStack item = event.getItem();
      int itemslot;
      PageSlot ps;
      if (event.getPageData().equals(PageData.EDIT_ITEM_MANAGE)) {
         itemslot = (Integer)event.getGUI().getPass().get("slot");
         ps = page.getPageSlot(itemslot);
         if (event.isTopInventory() && slot == 19) {
            if (!ps.getFunction().equals(Function.TRADE)) {
               return;
            }

            if (event.getClick().equals(ClickType.LEFT)) {
               this.openTradeEditor(player, page, itemslot);
            }
         }
      } else if (event.getPageData().equals(PageData.EDIT_ITEM_INVENTORY)) {
         itemslot = (Integer)event.getGUI().getPass().get("slot");
         ps = page.getPageSlot(itemslot);
         if (event.isTopInventory()) {
            if (slot == 22) {
               Editor.editItem(player, page, itemslot);
               return;
            }

            if (item != null && !item.getType().equals(Material.AIR) && ps.getItems().contains(item)) {
               ps.removeItem(item);
               this.openTradeEditor(player, page, itemslot);
            }
         } else if (item != null && !item.getType().equals(Material.AIR) && ps.getItems().size() < 7) {
            ps.addItem(item);
            this.openTradeEditor(player, page, itemslot);
         }
      }

   }

   public void openTradeEditor(Player player, Page page, int slot) {
      InventoryCreator inv = new InventoryCreator("" + ChatColor.BLUE + slot + ChatColor.DARK_GRAY + "▐ Inventory", 3);
      GUI gui = new GUI(Initiate.getPlugin(Initiate.class), PageData.EDIT_ITEM_INVENTORY, inv.getInventory(), page);
      if (page.getInventory().getItem(slot) == null) {
         page.openEditor(player);
      } else {
         PageSlot ps = page.getPageSlot(slot);
         int[] blanks = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 23, 24, 25, 26};
         inv.setBlank(blanks, Material.LEGACY_STAINED_GLASS_PANE, 15);
         inv.setItem(22, Material.COMPASS, ChatColor.RED + "Back");
         inv.addLore(22, ChatColor.GRAY + "Return to item properties");
         Iterator var9 = ps.getItems().iterator();

         while(var9.hasNext()) {
            ItemStack i = (ItemStack)var9.next();
            if (inv.getInventory().firstEmpty() != -1) {
               inv.setItem(inv.getInventory().firstEmpty(), i);
            }
         }

         gui.addPass("slot", slot);
         gui.open(player);
      }
   }
}
