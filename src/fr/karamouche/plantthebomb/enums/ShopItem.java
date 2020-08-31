package fr.karamouche.plantthebomb.enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum ShopItem {
    SWORD1(Material.WOOD_SWORD, ChatColor.GREEN+"Epée lvl.1", null, 0, "sword", 1, 0),
    SWORD2(Material.STONE_SWORD, ChatColor.GREEN+"Epée lvl.2", null, 0, "sword", 2, 200),
    SWORD3(Material.STONE_SWORD,ChatColor.GREEN+"Epée lvl.3" , Enchantment.DAMAGE_ALL, 1, "sword", 3, 350),
    SWORD4(Material.STONE_SWORD,ChatColor.GREEN+"Epée lvl.4", Enchantment.DAMAGE_ALL, 2, "sword", 4, 500),

    ARMOR(Material.IRON_CHESTPLATE, ChatColor.GOLD+"Armure", Enchantment.PROTECTION_ENVIRONMENTAL, 3, "armor", 1, 200),

    BOW1(Material.BOW,ChatColor.GREEN+"Arc lvl.1", null, 0, "bow", 1, 0),
    BOW2(Material.BOW,ChatColor.GREEN+"Arc lvl.2", Enchantment.ARROW_DAMAGE, 1, "bow", 2, 350),
    BOW3(Material.BOW,ChatColor.GREEN+"Arc lvl.3", Enchantment.ARROW_DAMAGE, 2,  "bow", 3, 700),
    BOW4(Material.BOW,ChatColor.GREEN+"Arc lvl.4", Enchantment.ARROW_DAMAGE, 1, "bow", 4, 850),

    ARROW(Material.ARROW, ChatColor.AQUA+"Flèches", null, 0, "arrow", 1, 100);


    private final Material mat;
    private final String name;
    private final Enchantment enchantment;
    private final int enchantLvl;
    private final String categorie;
    private final int level;
    private final int prix;

    ShopItem(Material mat, String name, Enchantment enchantment, int enchantLvl, String categorie, int level, int prix) {
        this.mat = mat;
        this.name = name;
        this.enchantment = enchantment;
        this.enchantLvl = enchantLvl;
        this.categorie = categorie;
        this.level = level;
        this.prix = prix;
    }

    public Material getMat() {
        return mat;
    }

    public String getName(){
        return name;
    }

    public String getCategorie() {
        return categorie;
    }
    public int getLevel() {
        return level;
    }
    public int getPrice() {
        return prix;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public int getEnchantLvl() {
        return enchantLvl;
    }

    public ItemStack toItem(){
        ItemStack item = new ItemStack(this.getMat());
        ItemMeta itemMeta = item.getItemMeta();
        if(this.getCategorie().equals("bow") || this.getCategorie().equals("sword"))
            itemMeta.spigot().setUnbreakable(true);
        itemMeta.setDisplayName(this.getName());
        item.setItemMeta(itemMeta);
        if(this.getName().equals("Arc lvl.4")){
            item.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
            item.addEnchantment(Enchantment.ARROW_FIRE, 1);
            item.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
        }else if(this.getEnchantment() != null){
            item.addEnchantment(this.getEnchantment(), this.getEnchantLvl());
        }
        return item;
    }

    public ItemStack getStack(int number){
        if(this.getMat().equals(Material.ARROW)) {
            ItemStack item = new ItemStack(this.getMat(), number);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(this.getName());
            item.setItemMeta(itemMeta);
            return item;
        }else
            return null;
    }
}
