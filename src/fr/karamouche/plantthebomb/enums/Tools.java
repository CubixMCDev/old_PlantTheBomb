package fr.karamouche.plantthebomb.enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Tools {
    TERROJOIN(Material.REDSTONE_TORCH_ON, "§cTerroriste"),
    ANTITERROJOIN(Material.SHEARS, "§bAntiterroriste");

    private final Material mat;
    private final String title;

    Tools(Material mat, String title){
        this.mat = mat;
        this.title = title;
    }

    public ItemStack toItem(){
        ItemStack item = new ItemStack(this.mat);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(this.title);
        item.setItemMeta(itemMeta);
        return item;
    }
}
