package conj.Shop.base;

import conj.Shop.tools.ItemCreator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemSerialize {
   public static final List<ItemStack> deserialize(List<HashMap<Map<String, Object>, Map<String, Object>>> items) {
      List<ItemStack> retrieveditems = new ArrayList();
      if (items == null) {
         return retrieveditems;
      } else {
         Iterator var3 = items.iterator();

         while(var3.hasNext()) {
            HashMap<Map<String, Object>, Map<String, Object>> serializemap = (HashMap)var3.next();
            Entry<Map<String, Object>, Map<String, Object>> serializeditems = (Entry)serializemap.entrySet().iterator().next();
            Map<String, Object> item = (Map)serializeditems.getKey();
            ItemStack i = ItemStack.deserialize(item);
            if (serializeditems.getValue() != null) {
               ItemMeta meta = (ItemMeta)ConfigurationSerialization.deserializeObject((Map)serializeditems.getValue(), ConfigurationSerialization.getClassByAlias("ItemMeta"));
               i.setItemMeta(meta);
            }

            if (i != null) {
               retrieveditems.add(i);
            }
         }

         return retrieveditems;
      }
   }

   public static final ItemStack deserializeSingle(List<HashMap<Map<String, Object>, Map<String, Object>>> itemserial) {
      Iterator var2 = itemserial.iterator();

      ItemStack i;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         HashMap<Map<String, Object>, Map<String, Object>> serializemap = (HashMap)var2.next();
         Entry<Map<String, Object>, Map<String, Object>> serializeditems = (Entry)serializemap.entrySet().iterator().next();
         Map<String, Object> item = (Map)serializeditems.getKey();
         i = ItemStack.deserialize(item);
         if (serializeditems.getValue() != null) {
            ItemMeta meta = (ItemMeta)ConfigurationSerialization.deserializeObject((Map)serializeditems.getValue(), ConfigurationSerialization.getClassByAlias("ItemMeta"));
            i.setItemMeta(meta);
         }
      } while(i == null);

      return i;
   }

   public static final List<HashMap<Map<String, Object>, Map<String, Object>>> serialize(List<ItemStack> items) {
      List<HashMap<Map<String, Object>, Map<String, Object>>> serialized = new ArrayList();
      Iterator var3 = items.iterator();

      while(var3.hasNext()) {
         ItemStack item = (ItemStack)var3.next();
         HashMap<Map<String, Object>, Map<String, Object>> serialization = new HashMap();
         if (item == null) {
            item = new ItemStack(Material.AIR);
         }

         Map<String, Object> itemmeta = item.hasItemMeta() ? item.getItemMeta().serialize() : null;
         item.setItemMeta((ItemMeta)null);
         Map<String, Object> itemstack = item.serialize();
         serialization.put(itemstack, itemmeta);
         serialized.add(serialization);
      }

      return serialized;
   }

   public static final List<HashMap<Map<String, Object>, Map<String, Object>>> serializeSingle(ItemStack item) {
      List<HashMap<Map<String, Object>, Map<String, Object>>> serialized = new ArrayList();
      HashMap<Map<String, Object>, Map<String, Object>> serialization = new HashMap();
      if (item == null) {
         item = new ItemStack(Material.AIR);
      }

      Map<String, Object> itemmeta = item.hasItemMeta() ? item.getItemMeta().serialize() : null;
      item.setItemMeta((ItemMeta)null);
      Map<String, Object> itemstack = item.serialize();
      serialization.put(itemstack, itemmeta);
      serialized.add(serialization);
      return serialized;
   }

   public static String serializeSoft(ItemStack item) {
      String serial = item.getType().toString() + ":" + item.getDurability();
      ItemCreator ic = new ItemCreator(item);
      if (ic.hasDisplayName()) {
         serial = serial + ":" + ic.getName();
      }

      if (ic.hasLore()) {
         serial = serial + ":" + ic.getLore();
      }

      if (ic.hasEnchantments()) {
         serial = serial + ic.getEnchants();
      }

      return serial;
   }

   public static String serializeSoftPerfect(ItemStack item) {
      String serial = item.getType().toString() + ":/:" + item.getDurability();
      ItemCreator ic = new ItemCreator(item);
      if (ic.hasDisplayName()) {
         serial = serial + ":/:" + ic.getName();
      }

      if (ic.hasLore()) {
         serial = serial + ":/:" + ic.getLore();
      }

      if (ic.hasEnchantments()) {
         serial = serial + ic.getEnchants();
      }

      return serial;
   }
}
