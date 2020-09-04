package fr.karamouche.plantthebomb.enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public enum Tools {
    TERROJOIN(Material.REDSTONE_TORCH_ON, "§cTerroriste", ChatColor.GRAY+"Ton but est de planter la bombe !"),
    ANTITERROJOIN(Material.SHEARS, "§bAntiterroriste", ChatColor.GRAY+"Ton but est de désamorcer la bombe !"),
    SHOP(Material.BEACON, ChatColor.GREEN+"Boutique", ChatColor.GRAY+"Pour acheter du stuff"),
    BOMB(Material.REDSTONE_TORCH_ON, ChatColor.RED+"BOMBE", ChatColor.GRAY+"Bombe que vous pouvez planter sur les sites"),
    KIT(Material.SHEARS, ChatColor.AQUA+"KIT", ChatColor.GRAY+"Kit pour desamorcer la bomb plus rapidement");

    private final Material mat;
    private final String title;
    private final String lore;

    Tools(Material mat, String title, String lore){
        this.mat = mat;
        this.title = title;
        this.lore = lore;
    }

    public ItemStack toItem(){
        ItemStack item = new ItemStack(this.mat);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(this.title);
        itemMeta.setLore(Collections.singletonList(this.lore));
        item.setItemMeta(itemMeta);
        return item;
    }
}
