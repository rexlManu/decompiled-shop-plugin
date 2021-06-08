package conj.Shop.tools;

import conj.Shop.control.Manager;
import conj.Shop.data.Page;
import java.util.Iterator;
import java.util.List;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class NPCAddon extends Trait {
   public NPCAddon() {
      super("shop");
   }

   @EventHandler
   public void click(NPCRightClickEvent event) {
      if (event.getNPC().hasTrait(NPCAddon.class)) {
         Player player = event.getClicker();
         Manager manager = new Manager();
         Page page = manager.getPage(event.getNPC());
         List<String> perms = manager.getCitizenPermissions(event.getNPC().getId());
         if (!perms.isEmpty()) {
            Iterator var7 = perms.iterator();

            while(var7.hasNext()) {
               String s = (String)var7.next();
               if (!player.hasPermission(s)) {
                  return;
               }
            }
         }

         if (page != null) {
            page.openPage(player);
         }
      }

   }

   @EventHandler
   public void click(NPCRemoveEvent event) {
      Manager.get().setCitizenPage(event.getNPC().getId(), (String)null);
   }

   public static void setCitizenPage(int id, String page) {
      if (page == null) {
         if (Manager.cnpcs.containsKey(id)) {
            Manager.cnpcs.remove(id);
         }

      } else {
         Manager.cnpcs.put(id, page);
      }
   }

   public void onAttach() {
   }

   public void onDespawn() {
   }

   public void onSpawn() {
   }

   public void onRemove() {
   }
}
