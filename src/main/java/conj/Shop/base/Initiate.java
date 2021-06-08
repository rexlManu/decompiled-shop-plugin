package conj.Shop.base;

import conj.Shop.auto.Autobackup;
import conj.Shop.auto.Autosave;
import conj.Shop.control.Control;
import conj.Shop.data.Page;
import conj.Shop.data.Sign;
import conj.Shop.data.Update;
import conj.Shop.enums.Config;
import conj.Shop.interaction.Editor;
import conj.Shop.interaction.PageProperties;
import conj.Shop.interaction.Shop;
import conj.Shop.interaction.TradeEditor;
import conj.Shop.tools.Debug;
import conj.Shop.tools.NPCAddon;
import conj.Shop.tools.PlaceholderAddon;
import conj.UA.api.files.ShopFile;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Initiate extends JavaPlugin {
   public static ShopFile sf;
   public static List<String> shop_purchase;
   public static List<String> shop_sell;
   public static boolean placeholderapi;
   public static boolean citizens;
   public static Economy econ;
   public static Permission perms;
   public static Chat chat;
   public String version = this.getDescription().getVersion();
   public String pluginname = this.getDescription().getName();

   public void onEnable() {
      shop_purchase = new ArrayList();
      String[] add = new String[]{"&aYou have purchased &b%quantity% &r%item% &afor &2%cost%", "&cA total of &4%failed% &cfailed to purchase.", "&eYour new balance is &2%balance%"};
      String[] var5 = add;
      int var4 = add.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         String s = var5[var3];
         shop_purchase.add(s);
      }

      shop_sell = new ArrayList();
      String[] adds = new String[]{"&aYou have sold &b%quantity% &r%item% &afor &2%cost%", "&eYour new balance is &2%balance%"};
      String[] var6 = adds;
      int var17 = adds.length;

      for(var4 = 0; var4 < var17; ++var4) {
         String s = var6[var4];
         shop_sell.add(s);
      }

      if (!this.setupEconomy()) {
         this.getLogger().log(Level.SEVERE, "Disabled due to no Vault dependency found. (Example: Essentials)");
         this.getServer().getPluginManager().disablePlugin(this);
      } else {
         this.setupPermissions();
         this.getCommand("shop").setExecutor(new Control());
         this.getServer().getPluginManager().registerEvents(new VersionChecker(), this);
         this.getServer().getPluginManager().registerEvents(new Sign(), this);
         this.getServer().getPluginManager().registerEvents(new Editor(), this);
         this.getServer().getPluginManager().registerEvents(new TradeEditor(), this);
         this.getServer().getPluginManager().registerEvents(new Shop(), this);
         this.getServer().getPluginManager().registerEvents(new PageProperties(), this);
         boolean mainfolder = this.getDataFolder().mkdir();
         if (Debug.debug) {
            this.getLogger().info("Created main folder: " + mainfolder);
         }

         File datafolder = new File(this.getDataFolder() + "/data");
         boolean data = datafolder.mkdir();
         if (Debug.debug) {
            this.getLogger().info("Created data folder: " + data);
         }

         File backupfolder = new File(this.getDataFolder() + "/backup");
         boolean back = backupfolder.mkdir();
         if (Debug.debug) {
            this.getLogger().info("Created backup folder: " + back);
         }

         Update.runUpdate(1);
         Config.load();
         Update.runUpdate(2);
         sf = new ShopFile(this.getDataFolder().getPath());
         sf.loadCitizensData();
         sf.loadWorthData();
         sf.loadMiscData();
         List<Page> pages = sf.loadPages();
         Iterator var10 = pages.iterator();

         while(var10.hasNext()) {
            Page p = (Page)var10.next();
            if (Debug.debug) {
               this.getLogger().info("Loaded page: " + p.getID());
            }
         }

         if (Config.UPDATE_CHECK.isActive()) {
            String pluginversion = VersionChecker.check();
            if (VersionChecker.check() != null && !this.version.equals(pluginversion)) {
               this.getLogger().info(this.version + " Shop is outdated");
               this.getLogger().info(pluginversion + " Shop is available for download");
               this.getLogger().info("Go to http://spigotmc.org/resources/shop.8185/ to update");
            }
         }

         if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderAddon.register(this);
            placeholderapi = true;
         } else {
            this.getLogger().info("PlaceholderAPI not found, alternative placeholders will be used.");
         }

         if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            try {
               CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(NPCAddon.class).withName("shop"));
               citizens = true;
               this.getLogger().info("Successfully hooked into Citizens.");
            } catch (NullPointerException var11) {
               this.getLogger().info("An error occured when trying to register a trait. Your Citizens version might not be supported.");
            } catch (NoClassDefFoundError var12) {
               this.getLogger().info("An error occured when trying to register a trait. Your Citizens version might not be supported.");
            }
         } else {
            this.getLogger().info("Citizens not found. NPCs will not be available.");
         }

         Autosave.start();
         Autobackup.start();
      }
   }

   public void onDisable() {
      Autosave.save();
   }

   private boolean setupEconomy() {
      if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
         return false;
      } else {
         RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
         if (rsp == null) {
            return false;
         } else {
            econ = (Economy)rsp.getProvider();
            return econ != null;
         }
      }
   }

   private boolean setupPermissions() {
      RegisteredServiceProvider<Permission> rsp = this.getServer().getServicesManager().getRegistration(Permission.class);
      perms = (Permission)rsp.getProvider();
      return perms != null;
   }
}
