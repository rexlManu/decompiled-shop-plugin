package conj.UA.api.files;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class SmartFileConfig {
   private SmartFile sf;

   public SmartFileConfig(SmartFile sf) {
      this.sf = sf;
   }

   public void overrideData() {
      this.sf.reset();

      try {
         this.sf.getConfig().save(this.sf.getFile());
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   public void saveData() {
      try {
         this.sf.getConfig().save(this.sf.getFile());
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   public Set<String> getCategories() {
      return this.sf.getConfig().getConfigurationSection("").getKeys(false);
   }

   public Object getValue(String path) {
      Iterator var3 = this.getCategories().iterator();
      if (var3.hasNext()) {
         String s = (String)var3.next();
         return this.sf.getConfig().get(s + "." + path);
      } else {
         return null;
      }
   }
}
