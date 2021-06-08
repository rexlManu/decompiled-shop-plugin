package conj.Shop.tools;

import conj.Shop.base.Initiate;

public class Debug {
   public static boolean debug;

   public static void log(String message) {
      if (debug) {
         ((Initiate)Initiate.getPlugin(Initiate.class)).getLogger().info(message);
      }

   }

   public static void log(Object o) {
      if (debug) {
         ((Initiate)Initiate.getPlugin(Initiate.class)).getLogger().info(o.toString());
      }

   }
}
