package conj.Shop.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class StringUtil {
   public static String toString(List<String> list) {
      if (list == null) {
         return null;
      } else {
         String string = "";

         String s;
         for(Iterator var3 = list.iterator(); var3.hasNext(); string = string + s + "/;") {
            s = (String)var3.next();
         }

         return string;
      }
   }

   public static List<String> fromString(String string) {
      if (string == null) {
         return null;
      } else {
         List<String> list = new ArrayList();
         List<String> split = Arrays.asList(string.split("/;"));
         Iterator var4 = split.iterator();

         while(var4.hasNext()) {
            String s = (String)var4.next();
            list.add(s);
            Debug.log("Added lore: " + s);
         }

         return list;
      }
   }
}
