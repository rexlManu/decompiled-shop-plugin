package conj.UA.api.files;

import conj.Shop.control.Manager;
import conj.Shop.data.Page;
import conj.Shop.data.PageSlot;
import conj.Shop.enums.Function;
import conj.Shop.enums.GUIFunction;
import conj.Shop.enums.Hidemode;
import conj.Shop.tools.Debug;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class ShopFile {
   private String directory;

   public ShopFile(String directory) {
      this.directory = directory + "/data";
   }

   public SmartFile getPageFile(String page) {
      return new SmartFile(this.directory + "/pages", page, ".yml");
   }

   public SmartFile getCitizensFile() {
      return new SmartFile(this.directory, "citizens_storage", ".yml");
   }

   public SmartFile getMiscFile() {
      return new SmartFile(this.directory, "misc_storage", ".yml");
   }

   public SmartFile getWorthFile() {
      return new SmartFile(this.directory, "worth_storage", ".yml");
   }

   public void loadCitizensData() {
      SmartFile sf = this.getCitizensFile();
      FileConfiguration data = sf.getConfig();
      if (data.getConfigurationSection("") != null) {
         Iterator var4 = data.getConfigurationSection("").getKeys(false).iterator();

         String citizen;
         int cid;
         while(var4.hasNext()) {
            citizen = (String)var4.next();

            try {
               cid = Integer.parseInt(citizen);
               String page = data.getString(citizen + ".page");
               if (page != null) {
                  Manager.cnpcs.put(cid, page);
               }
            } catch (NumberFormatException var8) {
            }
         }

         var4 = data.getConfigurationSection("").getKeys(false).iterator();

         while(var4.hasNext()) {
            citizen = (String)var4.next();

            try {
               cid = Integer.parseInt(citizen);
               List<String> permissions = data.getStringList(citizen + ".permission");
               if (permissions != null) {
                  Manager.cnpcpermissions.put(cid, permissions);
               }
            } catch (NumberFormatException var7) {
            }
         }
      }

   }

   public void loadMiscData() {
      SmartFile sf = this.getMiscFile();
      FileConfiguration data = sf.getConfig();
      List<String> blacklist = data.getStringList("blacklist");
      if (blacklist != null) {
         Iterator var5 = blacklist.iterator();

         while(var5.hasNext()) {
            String s = (String)var5.next();
            if (Bukkit.getWorld(s) != null) {
               Manager.blacklist.add(s);
            }
         }
      }

   }

   public void loadWorthData() {
      SmartFile sf = this.getWorthFile();
      FileConfiguration data = sf.getConfig();
      if (data.getConfigurationSection("Worth") != null) {
         Iterator var4 = data.getConfigurationSection("Worth").getKeys(false).iterator();

         while(var4.hasNext()) {
            String get = (String)var4.next();
            Manager.worth.put(get, data.getDouble("Worth." + get));
         }
      }

   }

   public List<Page> loadPages() {
      List<Page> pages = new ArrayList();
      File folder = new File(this.directory + "/pages/");
      if (!folder.exists()) {
         folder.mkdirs();
      }

      File[] files = folder.listFiles();
      File[] var7 = files;
      int var6 = files.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         File f = var7[var5];
         if (f.isFile()) {
            String pagename = f.getName().replace(".yml", "");
            Page p = new Page(pagename);
            SmartFile sf = this.getPageFile(pagename);
            if (sf != null) {
               FileConfiguration fc = sf.getConfig();
               if (fc.getConfigurationSection("data.properties") != null) {
                  p.type = fc.getInt("data.properties.type");
                  p.title = fc.getString("data.properties.title");
                  p.size = fc.getInt("data.properties.size");
                  p.gui = fc.getBoolean("data.properties.gui");
                  HashMap<String, Object> pagedata = new HashMap();
                  if (fc.getConfigurationSection("data.properties.pagedata") != null) {
                     Iterator var14 = fc.getConfigurationSection("data.properties.pagedata").getKeys(false).iterator();

                     while(var14.hasNext()) {
                        String key = (String)var14.next();
                        if (fc.get("data.properties.pagedata." + key) != null) {
                           pagedata.put(key, fc.get("data.properties.pagedata." + key));
                        }
                     }
                  }

                  p.pagedata = pagedata;
                  p.slots = (List<Integer>) fc.getList("data.inventory.slots");
                  p.items = (List)fc.get("data.inventory.items");
                  HashMap<Integer, PageSlot> pageslots = new HashMap();
                  if (fc.getConfigurationSection("data.inventory.slotdata") != null) {
                     Iterator var15 = fc.getConfigurationSection("data.inventory.slotdata").getKeys(false).iterator();

                     label88:
                     while(true) {
                        String slotstring;
                        int slot;
                        while(true) {
                           if (!var15.hasNext()) {
                              break label88;
                           }

                           slotstring = (String)var15.next();
                           slot = 0;

                           try {
                              slot = Integer.parseInt(slotstring);
                              break;
                           } catch (NumberFormatException var35) {
                              Debug.log(pagename + " slot " + slot + " failed to load.");
                           }
                        }

                        String page = fc.getString("data.inventory.slotdata." + slotstring + ".page");
                        double cost = fc.getDouble("data.inventory.slotdata." + slotstring + ".cost");
                        double sell = fc.getDouble("data.inventory.slotdata." + slotstring + ".sell");
                        int cooldown = fc.getInt("data.inventory.slotdata." + slotstring + ".cooldown");
                        String visiblity = fc.getString("data.inventory.slotdata." + slotstring + ".visibility");
                        String function = fc.getString("data.inventory.slotdata." + slotstring + ".function");
                        String guifunction = fc.getString("data.inventory.slotdata." + slotstring + ".guifunction");
                        List<String> commands = (List<String>) fc.getList("data.inventory.slotdata." + slotstring + ".command");
                        List<String> permissions = (List<String>) fc.getList("data.inventory.slotdata." + slotstring + ".permission");
                        List<String> hidepermissions = (List)fc.get("data.inventory.slotdata." + slotstring + ".hidepermission");
                        List<String> pagelore = (List)fc.get("data.inventory.slotdata." + slotstring + ".pagelore");
                        List<HashMap<Map<String, Object>, Map<String, Object>>> pageitems = (ArrayList)fc.get("data.inventory.slotdata." + slotstring + ".items");
                        HashMap<String, Long> cds = new HashMap();
                        if (fc.getConfigurationSection("data.inventory.slotdata." + slotstring + ".cd") != null) {
                           Iterator var33 = fc.getConfigurationSection("data.inventory.slotdata." + slotstring + ".cd").getKeys(false).iterator();

                           while(var33.hasNext()) {
                              String player = (String)var33.next();
                              if (fc.get("data.inventory.slotdata." + slotstring + ".cd." + player) != null) {
                                 cds.put(player, fc.getLong("data.inventory.slotdata." + slotstring + ".cd." + player));
                              }
                           }
                        }

                        HashMap<String, Object> slotdata = new HashMap();
                        if (fc.getConfigurationSection("data.inventory.slotdata." + slotstring + ".slotdata") != null) {
                           Iterator var34 = fc.getConfigurationSection("data.inventory.slotdata." + slotstring + ".slotdata").getKeys(false).iterator();

                           while(var34.hasNext()) {
                              String k = (String)var34.next();
                              if (fc.get("data.inventory.slotdata." + slotstring + ".slotdata." + k) != null) {
                                 slotdata.put(k, fc.get("data.inventory.slotdata." + slotstring + ".slotdata." + k));
                              }
                           }
                        }

                        PageSlot ps = new PageSlot(page, slot, cost, sell, cooldown, cds, Hidemode.fromString(visiblity), Function.fromString(function), GUIFunction.fromString(guifunction), commands, permissions, hidepermissions, pagelore, pageitems, slotdata);
                        pageslots.put(slot, ps);
                     }
                  }

                  p.pageslots = pageslots;
                  pages.add(p);
               }
            }
         }
      }

      Iterator var37 = pages.iterator();

      while(var37.hasNext()) {
         Page p = (Page)var37.next();
         p.create();
      }

      return pages;
   }

   public void savePageData(String page) {
      Page p = Manager.get().getPage(page);
      if (p != null) {
         SmartFile sf = this.getPageFile(page);
         sf.reset();
         long start = System.currentTimeMillis();
         FileConfiguration data = sf.getConfig();
         data.set("data.properties.type", p.type);
         data.set("data.properties.title", p.title);
         data.set("data.properties.size", p.size);
         data.set("data.properties.gui", p.gui);
         Iterator var8 = p.pagedata.entrySet().iterator();

         while(var8.hasNext()) {
            Entry<String, Object> v = (Entry)var8.next();
            if (v.getValue() == null || v.getValue() instanceof List && ((List)v.getValue()).isEmpty()) {
               break;
            }

            data.set("data.properties.pagedata." + (String)v.getKey(), v.getValue());
         }

         data.set("data.inventory.slots", p.slots);
         data.set("data.inventory.items", p.items);
         var8 = p.pageslots.values().iterator();

         while(var8.hasNext()) {
            PageSlot ps = (PageSlot)var8.next();
            data.set("data.inventory.slotdata." + ps.getSlot() + ".page", ps.getPage());
            data.set("data.inventory.slotdata." + ps.getSlot() + ".cost", ps.getCost());
            data.set("data.inventory.slotdata." + ps.getSlot() + ".sell", ps.getSell());
            data.set("data.inventory.slotdata." + ps.getSlot() + ".cooldown", ps.getCooldown());
            data.set("data.inventory.slotdata." + ps.getSlot() + ".visibility", ps.getHidemode().toString());
            data.set("data.inventory.slotdata." + ps.getSlot() + ".function", ps.getFunction().toString());
            data.set("data.inventory.slotdata." + ps.getSlot() + ".guifunction", ps.getGUIFunction().toString());
            if (!ps.getCommands().isEmpty()) {
               data.set("data.inventory.slotdata." + ps.getSlot() + ".command", ps.getCommands());
            }

            if (!ps.getPermissions().isEmpty()) {
               data.set("data.inventory.slotdata." + ps.getSlot() + ".permission", ps.getPermissions());
            }

            if (!ps.getHidePermissions().isEmpty()) {
               data.set("data.inventory.slotdata." + ps.getSlot() + ".hidepermission", ps.getHidePermissions());
            }

            if (!ps.getPageLore().isEmpty()) {
               data.set("data.inventory.slotdata." + ps.getSlot() + ".pagelore", ps.getPageLore());
            }

            Iterator var10 = ps.getCooldowns().entrySet().iterator();

            Entry d;
            while(var10.hasNext()) {
               d = (Entry)var10.next();
               if (ps.inCooldown((Long)d.getValue())) {
                  data.set("data.inventory.slotdata." + ps.getSlot() + ".cd." + (String)d.getKey(), d.getValue());
               }
            }

            var10 = ps.getSlotData().entrySet().iterator();

            while(var10.hasNext()) {
               d = (Entry)var10.next();
               if (d.getValue() == null || d.getValue() instanceof List && ((List)d.getValue()).isEmpty()) {
                  break;
               }

               data.set("data.inventory.slotdata." + ps.getSlot() + ".slotdata." + (String)d.getKey(), d.getValue());
            }

            if (!ps.items.isEmpty()) {
               data.set("data.inventory.slotdata." + ps.getSlot() + ".items", ps.items);
            }
         }

         sf.save(data);
         Debug.log("Page " + page + " write took: " + Manager.getDuration(start));
      }

   }

   public void saveCitizensData() {
      long start = System.currentTimeMillis();
      SmartFile sf = this.getCitizensFile();
      sf.reset();
      FileConfiguration data = sf.getConfig();
      Iterator var6 = Manager.cnpcs.entrySet().iterator();

      Entry v;
      while(var6.hasNext()) {
         v = (Entry)var6.next();
         data.set(String.valueOf(v.getKey()) + ".page", v.getValue());
      }

      var6 = Manager.cnpcpermissions.entrySet().iterator();

      while(var6.hasNext()) {
         v = (Entry)var6.next();
         data.set(String.valueOf(v.getKey()) + ".permission", v.getValue());
      }

      sf.save(data);
      Debug.log("Citizens write took: " + Manager.getDuration(start));
   }

   public void saveWorthData() {
      SmartFile sf = this.getWorthFile();
      FileConfiguration data = sf.getConfig();
      data.set("Worth", Manager.worth);
      sf.save(data);
   }

   public void saveMiscData() {
      SmartFile sf = this.getMiscFile();
      FileConfiguration data = sf.getConfig();
      data.set("blacklist", Manager.blacklist);
      sf.save(data);
   }
}
