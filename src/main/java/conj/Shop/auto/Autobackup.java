package conj.Shop.auto;

import conj.Shop.base.Initiate;
import conj.Shop.control.Manager;
import conj.Shop.enums.Config;
import conj.Shop.tools.Debug;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public class Autobackup {
   private static int id;

   public static void start() {
      if (Config.AUTOBACKUP.isActive()) {
         cancel();
         BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
         id = scheduler.scheduleSyncRepeatingTask(Initiate.getPlugin(Initiate.class), new Runnable() {
            public void run() {
               if (!Config.AUTOBACKUP.isActive()) {
                  Autobackup.cancel();
                  Debug.log("Autobackup has been disabled.");
               } else {
                  Autobackup.create();
               }
            }
         }, 0L, 72000L);
      }

   }

   public static void create() {
      long start = System.currentTimeMillis();
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Calendar cal = Calendar.getInstance();
      String date = dateFormat.format(cal.getTime());
      File file = new File(((Initiate)Initiate.getPlugin(Initiate.class)).getDataFolder().getPath() + "/data");
      File backupfile = new File(((Initiate)Initiate.getPlugin(Initiate.class)).getDataFolder().getPath() + "/backup/" + date);
      if (!backupfile.exists()) {
         backupfile.mkdir();
      }

      if (file.exists()) {
         try {
            FileUtils.copyDirectory(file, backupfile);
         } catch (IOException var8) {
            var8.printStackTrace();
         }
      }

      Debug.log("Backup took: " + Manager.getDuration(start));
   }

   public static void delete() {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm");
      Calendar cal = Calendar.getInstance();
      String date = dateFormat.format(cal.getTime());
      File backupfile = new File(((Initiate)Initiate.getPlugin(Initiate.class)).getDataFolder().getPath() + "/backup/" + date);
      if (backupfile.exists()) {
         backupfile.delete();
      }

   }

   public static void cancel() {
      BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
      scheduler.cancelTask(id);
   }
}
