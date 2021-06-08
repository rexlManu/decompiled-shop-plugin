package conj.Shop.tools;

import conj.Shop.interaction.Editor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryCreator {
   String title;
   int size;
   Inventory inv;

   public InventoryCreator(List<ItemStack> items, int size) {
      this.inv = Bukkit.createInventory((InventoryHolder)null, size * 9);
      Iterator var4 = items.iterator();

      while(var4.hasNext()) {
         ItemStack i = (ItemStack)var4.next();
         if (i != null) {
            this.inv.addItem(new ItemStack[]{i});
         }
      }

   }

   public InventoryCreator(List<ItemStack> items) {
      int size = 1;
      if (items.size() > 9) {
         size = 2;
      }

      if (items.size() > 18) {
         size = 3;
      }

      if (items.size() > 27) {
         size = 4;
      }

      if (items.size() > 36) {
         size = 5;
      }

      if (items.size() > 45) {
         size = 6;
      }

      this.inv = Bukkit.createInventory((InventoryHolder)null, size * 9);
      Iterator var4 = items.iterator();

      while(var4.hasNext()) {
         ItemStack i = (ItemStack)var4.next();
         if (i != null) {
            this.inv.addItem(new ItemStack[]{i});
         }
      }

   }

   public InventoryCreator(ItemStack[] items) {
      int size = 1;
      if (items.length > 9) {
         size = 2;
      }

      if (items.length > 18) {
         size = 3;
      }

      if (items.length > 27) {
         size = 4;
      }

      if (items.length > 36) {
         size = 5;
      }

      if (items.length > 45) {
         size = 6;
      }

      this.inv = Bukkit.createInventory((InventoryHolder)null, size * 9);
      ItemStack[] var6 = items;
      int var5 = items.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         ItemStack i = var6[var4];
         if (i != null) {
            this.inv.addItem(new ItemStack[]{i});
         }
      }

   }

   public InventoryCreator(Inventory inventory) {
      this.inv = inventory;
   }

   public InventoryCreator(Inventory inventory, boolean copy) {
      if (copy) {
         Inventory i = Bukkit.createInventory((InventoryHolder)null, 54);
         i.setContents(inventory.getContents());
         this.inv = i;
      } else {
         this.inv = inventory;
      }

   }

   public InventoryCreator(String title, int size) {
      this.title = title;
      this.size = size;
      this.inv = Bukkit.createInventory((InventoryHolder)null, size * 9, title);
   }

   public Inventory getInventory() {
      return this.inv;
   }

   public void setItem(int slot, Material type, int damage, String name) {
      ItemStack i = new ItemStack(type, 1, (short)damage);
      ItemMeta im = i.getItemMeta();
      im.setDisplayName(name);
      i.setItemMeta(im);
      this.inv.setItem(slot, i);
   }

   public void setItem(int[] slot, Material type, int damage, String name) {
      ItemStack i = new ItemStack(type, 1, (short)damage);
      ItemMeta im = i.getItemMeta();
      im.setDisplayName(name);
      i.setItemMeta(im);
      int[] var10 = slot;
      int var9 = slot.length;

      for(int var8 = 0; var8 < var9; ++var8) {
         int s = var10[var8];
         this.inv.setItem(s, i);
      }

   }

   public void setItem(int slot, Material type, String name) {
      ItemStack i = new ItemStack(type, 1);
      ItemMeta im = i.getItemMeta();
      im.setDisplayName(name);
      i.setItemMeta(im);
      this.inv.setItem(slot, i);
   }

   public void setItem(int[] slot, Material type, String name) {
      ItemStack i = new ItemStack(type, 1);
      ItemMeta im = i.getItemMeta();
      im.setDisplayName(name);
      i.setItemMeta(im);
      int[] var9 = slot;
      int var8 = slot.length;

      for(int var7 = 0; var7 < var8; ++var7) {
         int s = var9[var7];
         this.inv.setItem(s, i);
      }

   }

   public void setBlank(int[] slot, Material type) {
      ItemStack i = new ItemStack(type);
      ItemMeta im = i.getItemMeta();
      im.setDisplayName(" ");
      i.setItemMeta(im);
      int[] var8 = slot;
      int var7 = slot.length;

      for(int var6 = 0; var6 < var7; ++var6) {
         int s = var8[var6];
         this.inv.setItem(s, i);
      }

   }

   public void setBlank(int[] slot, Material type, int damage) {
      ItemStack i = new ItemStack(type, 1, (short)damage);
      ItemMeta im = i.getItemMeta();
      im.setDisplayName(" ");
      i.setItemMeta(im);
      int[] var9 = slot;
      int var8 = slot.length;

      for(int var7 = 0; var7 < var8; ++var7) {
         int s = var9[var7];
         this.inv.setItem(s, i);
      }

   }

   public void setFill(ItemStack i) {
      for(int x = 0; x < this.inv.getSize(); ++x) {
         ItemStack item = this.inv.getItem(x);
         if (item == null) {
            this.inv.setItem(x, i);
         }
      }

   }

   public void setBlank(Material type, int damage) {
      ItemStack i = new ItemStack(type, 1, (short)damage);
      ItemMeta im = i.getItemMeta();
      im.setDisplayName(" ");
      i.setItemMeta(im);

      for(int x = 0; x < this.inv.getSize(); ++x) {
         ItemStack item = this.inv.getItem(x);
         if (item == null) {
            this.inv.setItem(x, i);
         }
      }

   }

   public void setDisplay(int slot, String name) {
      if (this.inv.getItem(slot) != null) {
         ItemStack item = this.inv.getItem(slot);
         ItemMeta im = item.getItemMeta();
         im.setDisplayName(name);
         item.setItemMeta(im);
      }

   }

   public void addLore(int slot, String lore) {
      if (this.inv.getItem(slot) != null) {
         ItemStack item = this.inv.getItem(slot);
         ItemMeta im = item.getItemMeta();
         List<String> l = new ArrayList();
         if (im.hasLore()) {
            l = im.getLore();
         }

         ((List)l).add(lore);
         im.setLore((List)l);
         item.setItemMeta(im);
      }

   }

   public void addLore(int[] slots, String lore) {
      int[] var6 = slots;
      int var5 = slots.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         int slot = var6[var4];
         if (this.inv.getItem(slot) != null) {
            ItemStack item = this.inv.getItem(slot);
            ItemMeta im = item.getItemMeta();
            List<String> l = new ArrayList();
            if (im.hasLore()) {
               l = im.getLore();
            }

            ((List)l).add(lore);
            im.setLore((List)l);
            item.setItemMeta(im);
         }
      }

   }

   public void setItem(int i, ItemStack item) {
      this.inv.setItem(i, item);
   }

   public int getAmount(ItemStack item) {
      int amount = 0;

      for(int x = 0; this.inv.getSize() > x; ++x) {
         ItemStack i = this.inv.getItem(x);
         if (i != null && i.isSimilar(item)) {
            amount += i.getAmount();
         }
      }

      return amount;
   }

   public static boolean hasPlaceholder(Inventory inventory) {
      for(int x = 0; inventory.getSize() > x; ++x) {
         ItemStack i = inventory.getItem(x);
         if (i != null) {
            ItemCreator ic = new ItemCreator(i);
            Iterator var5 = ic.getLore().iterator();

            while(var5.hasNext()) {
               String s = (String)var5.next();
               if (s.contains("%")) {
                  return true;
               }
            }

            if (ic.getName().contains("%")) {
               return true;
            }
         }
      }

      return false;
   }

   public void replace(String replace, String replacement) {
      for(int x = 0; this.inv.getSize() > x; ++x) {
         ItemStack item = this.inv.getItem(x);
         if (item != null) {
            ItemMeta itemmeta = item.getItemMeta();
            new ArrayList();
            String name = Editor.getItemName(item);
            if (itemmeta.hasLore()) {
               List<String> lore = itemmeta.getLore();
               List<String> placehold = new ArrayList();
               Iterator var10 = lore.iterator();

               while(var10.hasNext()) {
                  String s = (String)var10.next();
                  if (s.contains(replace)) {
                     placehold.add(s.replaceAll(replace, replacement));
                  } else {
                     placehold.add(s);
                  }
               }

               itemmeta.setLore(placehold);
            }

            if (name.contains(replace)) {
               name = name.replaceAll(replace, replacement);
               itemmeta.setDisplayName(name);
            }

            item.setItemMeta(itemmeta);
         }
      }

   }

   public boolean canAdd(ItemStack item) {
      if (this.getInventory().firstEmpty() != -1) {
         return true;
      } else {
         Iterator var3 = this.getInventory().iterator();

         while(var3.hasNext()) {
            ItemStack i = (ItemStack)var3.next();
            if (i != null && !i.getType().equals(Material.AIR) && i.isSimilar(item)) {
               int c = i.getMaxStackSize() - i.getAmount();
               if (item.getAmount() <= c) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}
