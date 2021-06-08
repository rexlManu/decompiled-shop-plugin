package conj.Shop.tools;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class VaultAddon {
   Economy economy;

   public VaultAddon(Economy economy) {
      this.economy = economy;
   }

   public boolean canAfford(Player player, double cost) {
      double balance = this.economy.getBalance(player);
      return balance >= cost;
   }

   public boolean canAfford(Player player, double cost, int quantity) {
      double balance = this.economy.getBalance(player);
      return balance >= cost * (double)quantity;
   }

   public int getAffordable(Player player, double cost, int quantity) {
      double balance = this.economy.getBalance(player);
      int affordable = 0;

      for(int x = 1; x <= quantity; ++x) {
         double c = cost * (double)x;
         if (balance >= c) {
            ++affordable;
         }
      }

      return affordable;
   }
}
