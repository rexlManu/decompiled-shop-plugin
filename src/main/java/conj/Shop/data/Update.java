package conj.Shop.data;

import conj.Shop.base.Initiate;
import conj.Shop.enums.Config;
import conj.Shop.enums.Function;
import conj.Shop.enums.GUIFunction;
import conj.Shop.enums.Hidemode;
import conj.Shop.enums.MessageType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.FileUtil;

public class Update {
   public static void runUpdate(int id) {
      String page;
      Iterator var4;
      if (id == 1) {
         File file = new File(((Initiate)Initiate.getPlugin(Initiate.class)).getDataFolder() + "/data/page_storage.yml");
         if (file.exists()) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            var4 = data.getConfigurationSection("").getKeys(false).iterator();

            while(var4.hasNext()) {
               page = (String)var4.next();
               Bukkit.getLogger().info("Converting page file " + page);
               Page p = new Page(page);
               p.title = data.getString(p.getID() + ".title");
               p.size = data.getInt(p.getID() + ".size");
               p.type = data.getInt(p.getID() + ".type");
               p.gui = data.getBoolean(p.getID() + ".gui");
               p.slots = (List)data.get(p.getID() + ".slots");
               p.items = (List)data.get(p.getID() + ".items");
               HashMap<Integer, Double> cost = loadDouble(p, data, "cost");
               HashMap<Integer, Double> sell = loadDouble(p, data, "sell");
               HashMap<Integer, List<String>> command = loadStringMap(p, data, "command");
               HashMap<Integer, Integer> cooldown = loadInt(p, data, "cooldown");
               HashMap<Integer, List<String>> hidepermission = loadStringMap(p, data, "hidepermission");
               HashMap<Integer, List<String>> permission = loadStringMap(p, data, "permission");
               HashMap<Integer, HashMap<String, Long>> cd = new HashMap();
               HashMap<Integer, Function> functions = new HashMap();
               HashMap<Integer, GUIFunction> guifunctions = new HashMap();
               HashMap<Integer, HashMap<String, List<String>>> messages = new HashMap();
               HashMap<Integer, Hidemode> visibility = new HashMap();
               String numeral;
               Iterator var18;
               String s;
               Iterator var20;
               HashMap hash;
               if (data.getConfigurationSection(p.getID() + ".cd") != null) {
                  var18 = data.getConfigurationSection(p.getID() + ".cd").getKeys(false).iterator();

                  label129:
                  while(true) {
                     do {
                        if (!var18.hasNext()) {
                           break label129;
                        }

                        numeral = (String)var18.next();
                     } while(data.getConfigurationSection(p.getID() + ".cd." + numeral) == null);

                     var20 = data.getConfigurationSection(p.getID() + ".cd." + numeral).getKeys(false).iterator();

                     while(var20.hasNext()) {
                        s = (String)var20.next();
                        if (data.get(p.getID() + ".cd." + numeral + "." + s) != null) {
                           hash = new HashMap();
                           hash.put(s, data.getLong(p.getID() + ".cd." + numeral + "." + s));
                           cd.put(Integer.parseInt(numeral), hash);
                        }
                     }
                  }
               }

               if (data.getConfigurationSection(p.getID() + ".function") != null) {
                  var18 = data.getConfigurationSection(p.getID() + ".function").getKeys(false).iterator();

                  while(var18.hasNext()) {
                     numeral = (String)var18.next();
                     if (data.get(p.getID() + ".function." + numeral) != null) {
                        functions.put(Integer.parseInt(numeral), Function.fromString(data.getString(p.getID() + ".function." + numeral)));
                     }
                  }
               }

               if (data.getConfigurationSection(p.getID() + ".guifunction") != null) {
                  var18 = data.getConfigurationSection(p.getID() + ".guifunction").getKeys(false).iterator();

                  while(var18.hasNext()) {
                     numeral = (String)var18.next();
                     if (data.get(p.getID() + ".guifunction." + numeral) != null) {
                        guifunctions.put(Integer.parseInt(numeral), GUIFunction.fromString(data.getString(p.getID() + ".guifunction." + numeral)));
                     }
                  }
               }

               if (data.getConfigurationSection(p.getID() + ".messages") != null) {
                  var18 = data.getConfigurationSection(p.getID() + ".messages").getKeys(false).iterator();

                  label162:
                  while(true) {
                     do {
                        if (!var18.hasNext()) {
                           break label162;
                        }

                        numeral = (String)var18.next();
                     } while(data.getConfigurationSection(p.getID() + ".messages." + numeral) == null);

                     var20 = data.getConfigurationSection(p.getID() + ".messages." + numeral).getKeys(false).iterator();

                     while(var20.hasNext()) {
                        s = (String)var20.next();
                        if (data.getStringList(p.getID() + ".messages." + numeral + "." + s) != null) {
                           hash = new HashMap();
                           hash.put(MessageType.fromString(s).toString(), data.getStringList(p.getID() + ".messages." + numeral + "." + s));
                           messages.put(Integer.parseInt(numeral), hash);
                        }
                     }
                  }
               }

               if (data.getConfigurationSection(p.getID() + ".visibility") != null) {
                  var18 = data.getConfigurationSection(p.getID() + ".visibility").getKeys(false).iterator();

                  while(var18.hasNext()) {
                     numeral = (String)var18.next();
                     if (data.get(p.getID() + ".visibility." + numeral) != null) {
                        visibility.put(Integer.parseInt(numeral), Hidemode.fromString(data.getString(p.getID() + ".visibility." + numeral)));
                     }
                  }
               }

               for(int i = 0; i < 54; ++i) {
                  PageSlot ps = new PageSlot(page, i);
                  if (cost.get(i) != null) {
                     ps.setCost((Double)cost.get(i));
                  }

                  if (sell.get(i) != null) {
                     ps.setSell((Double)sell.get(i));
                  }

                  if (command.get(i) != null) {
                     ps.setCommands((List)command.get(i));
                  }

                  if (cooldown.get(i) != null) {
                     ps.setCooldown((Integer)cooldown.get(i));
                  }

                  if (hidepermission.get(i) != null) {
                     var20 = ((List)hidepermission.get(i)).iterator();

                     while(var20.hasNext()) {
                        s = (String)var20.next();
                        ps.addHidePermission(s);
                     }
                  }

                  if (permission.get(i) != null) {
                     var20 = ((List)permission.get(i)).iterator();

                     while(var20.hasNext()) {
                        s = (String)var20.next();
                        ps.addPermission(s);
                     }
                  }

                  Entry v;
                  if (cd.get(i) != null) {
                     var20 = ((HashMap)cd.get(i)).entrySet().iterator();

                     while(var20.hasNext()) {
                        v = (Entry)var20.next();
                        ps.getCooldowns().put((String)v.getKey(), (Long)v.getValue());
                     }
                  }

                  if (functions.get(i) != null) {
                     ps.setFunction((Function)functions.get(i));
                  }

                  if (guifunctions.get(i) != null) {
                     ps.setGUIFunction((GUIFunction)guifunctions.get(i));
                  }

                  if (visibility.get(i) != null) {
                     ps.setHidemode((Hidemode)visibility.get(i));
                  }

                  if (messages.get(i) != null) {
                     var20 = ((HashMap)messages.get(i)).entrySet().iterator();

                     while(var20.hasNext()) {
                        v = (Entry)var20.next();
                        ps.setMessage((String)v.getKey(), (List)v.getValue());
                     }
                  }

                  p.addPageSlot(ps);
               }

               p.create();
               p.saveData();
               Bukkit.getLogger().info("Page " + page + " conversion complete");
            }

            File backup = new File(((Initiate)Initiate.getPlugin(Initiate.class)).getDataFolder() + "/backup/page_storage-2.0.8.yml");
            if (!backup.exists()) {
               try {
                  backup.createNewFile();
               } catch (IOException var22) {
               }
            }

            FileUtil.copy(file, backup);
            file.delete();
         }
      } else if (id == 2) {
         Plugin plugin = Initiate.getPlugin(Initiate.class);
         boolean changes = false;
         var4 = plugin.getConfig().getConfigurationSection("").getKeys(false).iterator();

         while(var4.hasNext()) {
            page = (String)var4.next();
            boolean b = false;
            Config[] var30;
            int var29 = (var30 = Config.values()).length;

            for(int var28 = 0; var28 < var29; ++var28) {
               Config c = var30[var28];
               if (page.equalsIgnoreCase(c.getBase())) {
                  b = true;
                  break;
               }
            }

            if (!b) {
               changes = true;
               plugin.getConfig().set(page, (Object)null);
            }
         }

         plugin.saveConfig();
         if (changes) {
            Config.load();
         }
      }

   }

   public static HashMap<Integer, List<String>> loadStringMap(Page page, FileConfiguration data, String base) {
      HashMap<Integer, List<String>> value = new HashMap();
      if (data.getConfigurationSection(page.getID() + "." + base) != null) {
         Iterator var6 = data.getConfigurationSection(page.getID() + "." + base).getKeys(false).iterator();

         while(var6.hasNext()) {
            String get = (String)var6.next();
            int id = Integer.parseInt(get);
            if (data.getList(page.getID() + "." + base + "." + get) != null) {
               List<String> list = (List<String>) data.getList(page.getID() + "." + base + "." + get);
               value.put(id, list);
            }
         }
      }

      return value;
   }

   public static HashMap<Integer, Double> loadDouble(Page page, FileConfiguration data, String base) {
      HashMap<Integer, Double> value = new HashMap();
      if (data.get(page.getID() + "." + base) != null) {
         Iterator var5 = data.getConfigurationSection(page.getID() + "." + base).getKeys(false).iterator();

         while(var5.hasNext()) {
            String get = (String)var5.next();
            if (data.get(page.getID() + "." + base + "." + get) != null) {
               value.put(Integer.parseInt(get), data.getDouble(page.getID() + "." + base + "." + get));
            }
         }
      }

      return value;
   }

   public static HashMap<Integer, Integer> loadInt(Page page, FileConfiguration data, String base) {
      HashMap<Integer, Integer> value = new HashMap();
      if (data.get(page.getID() + "." + base) != null) {
         Iterator var5 = data.getConfigurationSection(page.getID() + "." + base).getKeys(false).iterator();

         while(var5.hasNext()) {
            String get = (String)var5.next();
            if (data.get(page.getID() + "." + base + "." + get) != null) {
               value.put(Integer.parseInt(get), data.getInt(page.getID() + "." + base + "." + get));
            }
         }
      }

      return value;
   }
}
