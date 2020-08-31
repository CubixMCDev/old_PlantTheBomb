package fr.karamouche.plantthebomb.objects;

import java.util.UUID;

import fr.karamouche.plantthebomb.enums.PTBteam;
import fr.karamouche.plantthebomb.enums.ShopItem;
import fr.karamouche.plantthebomb.enums.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Team;

import fr.karamouche.plantthebomb.Main;

public class PTBer {
	private final UUID playerID;
	private int kills;
	private PTBteam team;
	private int money;
	private final Main myPlugin;
	
	public PTBer(UUID playerID, PTBteam team, Main main) {
		this.playerID = playerID;
		this.setKills(0);
		this.setMoney(0);
		this.myPlugin = main;
		this.setTeam(team);
		myPlugin.getCurrentGame().getPtbers().put(playerID, this);
	}

	public UUID getPlayerID() {
		return playerID;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public PTBteam getTeam() {
		return team;
	}

	public void setTeam(PTBteam team) {
		this.team = team;
		Team teamR = myPlugin.getCurrentGame().getTeam(team);
		Player player = Bukkit.getServer().getPlayer(this.getPlayerID());
		for(Team teamIter : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()){
			if(teamIter.hasEntry(player.getName()))
				teamIter.removeEntry(player.getName());
		}
		teamR.addEntry(player.getName());
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public Main getMyPlugin() {
		return myPlugin;
	}

	public void destroy() {
		Team team = myPlugin.getCurrentGame().getTeam(this.getTeam());
		Player player = Bukkit.getPlayer(this.getPlayerID());
		team.removeEntry(player.getName());
		myPlugin.getCurrentGame().getPtbers().remove(this.getPlayerID());
	}

	public void giveBasicStuff() {
		Player player = Bukkit.getServer().getPlayer(this.getPlayerID());
		Inventory inventaire = player.getInventory();
		inventaire.setItem(13, Tools.SHOP.toItem());
		inventaire.setItem(0, ShopItem.SWORD1.toItem());
		inventaire.setItem(1, ShopItem.BOW1.toItem());
		inventaire.setItem(28,ShopItem.ARROW.getStack(10));
		player.updateInventory();
		EntityEquipment stuff = player.getEquipment();
		PTBteam team = myPlugin.getCurrentGame().getPtbers().get(player.getUniqueId()).getTeam();
		Color color;
		if(team.equals(PTBteam.TERRORISTE)) {
			color = Color.RED;
		}
		else{
			color = Color.AQUA;
		}

		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);

		LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
		bootsMeta.setColor(color);
		bootsMeta.spigot().setUnbreakable(true);


		LeatherArmorMeta pantsMeta = (LeatherArmorMeta) pants.getItemMeta();
		pantsMeta.setColor(color);
		pantsMeta.spigot().setUnbreakable(true);


		LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
		chestMeta.setColor(color);
		chestMeta.spigot().setUnbreakable(true);


		boots.setItemMeta(bootsMeta);
		boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		pants.setItemMeta(pantsMeta);
		pants.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		chest.setItemMeta(chestMeta);
		chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		stuff.setBoots(boots);
		stuff.setLeggings(pants);
		stuff.setChestplate(chest);

	}
}
