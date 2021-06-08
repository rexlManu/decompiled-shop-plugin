package conj.UA.api.files;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SmartFile {
   private String name;
   private String directory;
   private String ext;

   public SmartFile(String directory, String name, String ext) {
      this.name = name;
      this.directory = directory;
      this.ext = ext;
   }

   public File getFile() {
      File f = new File(this.directory + "/" + this.name + this.ext);
      if (!f.exists()) {
         this.create();
      }

      return f;
   }

   public FileConfiguration getConfig() {
      File file = this.getFile();
      return YamlConfiguration.loadConfiguration(file);
   }

   public File create() {
      File f = new File(this.directory + "/" + this.name + this.ext);
      f.getParentFile().mkdirs();
      if (!f.exists()) {
         try {
            f.createNewFile();
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      }

      return f;
   }

   public boolean exists() {
      return (new File(this.directory + "/" + this.name + this.ext)).exists();
   }

   public File reset() {
      File f = this.getFile();
      f.delete();
      return this.create();
   }

   public boolean save(FileConfiguration fc) {
      try {
         fc.save(this.getFile());
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      return false;
   }
}
