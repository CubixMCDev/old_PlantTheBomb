package fr.karamouche.plantthebomb;

import fr.karamouche.plantthebomb.enums.ShopItem;
import fr.karamouche.plantthebomb.gui.GuiBuilder;
import fr.karamouche.plantthebomb.objects.PTBer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Shop implements GuiBuilder {
    Main myPlugin;
    public Shop(Main main) {
        myPlugin = main;
    }

    @Override
    public String name() {
        return "§eBoutique";
    }

    @Override
    public int getSize() {
        return 9*2;
    }

    @Override
    public void contents(Player player, Inventory inv) {
        inv.setItem(0, ShopItem.SWORD2.toShopItem());
        inv.setItem(1, ShopItem.SWORD3.toShopItem());
        inv.setItem(2, ShopItem.SWORD4.toShopItem());
        inv.setItem(8, ShopItem.ARMOR.toShopItem());

        inv.setItem(9, ShopItem.BOW2.toShopItem());
        inv.setItem(10, ShopItem.BOW3.toShopItem());
        inv.setItem(11, ShopItem.BOW4.toShopItem());
        inv.setItem(17, ShopItem.ARROW.toShopItem());
    }

    @Override
    public void onClick(Player player, Inventory inv, ItemStack current, int slot){
        PTBer ptber = myPlugin.getCurrentGame().getPtbers().get(player.getUniqueId());
        if(slot <= inv.getSize()){
            String itemName = current.getItemMeta().getDisplayName();
            for(ShopItem shopItem : ShopItem.values()){
                if(shopItem.getName().equals(itemName)){
                    if(ptber.getMoney() >= shopItem.getPrice()) {
                        if (current.getType().equals(Material.ARROW))
                            player.getInventory().addItem(shopItem.getStack(10));
                        else if (current.getType().equals(Material.IRON_CHESTPLATE))
                            player.getEquipment().setChestplate(shopItem.toItem());
                        else
                            player.getInventory().addItem(shopItem.toItem());
                        player.sendMessage(myPlugin.getCurrentGame().getTag() + "Vous avez acheté => " + itemName);
                        ptber.addMoney(-shopItem.getPrice());
                    }else{
                        player.sendMessage(myPlugin.getCurrentGame().getTag() + ChatColor.RED + "Vous n'avez pas assez d'argent !");
                        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
                    }
                }
            }
        }
    }
}
