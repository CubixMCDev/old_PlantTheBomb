package fr.karamouche.plantthebomb.objects;

import java.util.UUID;

import fr.karamouche.plantthebomb.enums.PTBteam;
import fr.karamouche.plantthebomb.enums.ShopItem;
import fr.karamouche.plantthebomb.enums.Tools;
import org.bukkit.*;
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
		Bukkit.getServer().getPlayer(this.getPlayerID()).setLevel(money);
	}

	public void addMoney(int value){
		this.setMoney(this.getMoney() + value);
		//RAJOUTER UNE ALERTE
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
	public void oneKill(){
		this.setKills(this.getKills()+1);
		Bukkit.getServer().getPlayer(this.getPlayerID()).playSound(Bukkit.getServer().getPlayer(this.getPlayerID()).getLocation(), Sound.GHAST_MOAN, 350, 500);
		this.addMoney(50);
	}

	public void kill(Player killer) {
		Game game = myPlugin.getCurrentGame();
		Player player = Bukkit.getPlayer(this.getPlayerID());
		if(!game.getActualRound().isFinish()) {
			player.setGameMode(GameMode.SPECTATOR);
			if(killer != null){
				PTBer killerPTB = game.getPtbers().get(killer.getUniqueId());
				Bukkit.broadcastMessage(game.getTeam(killerPTB.getTeam()).getPrefix()+killer.getName() + "§r a tué "+game.getTeam(killerPTB.getTeam()).getPrefix()+player.getName());
				killerPTB.oneKill();
			}
			else
				Bukkit.broadcastMessage(game.getTag() + game.getTeam(this.getTeam()).getPrefix()+player.getName() + ChatColor.BOLD+" est mort.");
			Inventory loot = player.getInventory();
			//RAJOUTER LA SUPPRESSION DE LA BOMBE
			if(loot.contains(ShopItem.SWORD1.toItem()))
				loot.remove(ShopItem.SWORD1.toItem());
			if(loot.contains(ShopItem.BOW1.toItem()))
				loot.remove(ShopItem.BOW1.toItem());
			if(loot.contains(Tools.SHOP.toItem()))
				loot.remove(Tools.SHOP.toItem());
			if(loot.contains(ShopItem.ARMOR.toItem()))
				loot.remove(ShopItem.ARMOR.toItem());
			/*RAJOUTER LE CLEAR DE LA BOMB
			if(loot.contains(bomb))
				FPTB.bombDrop(player.getLocation());*/
			ItemStack[] content =  loot.getContents();
			for(ItemStack items : content) {
				if(items != null)
					player.getWorld().dropItemNaturally(player.getLocation(), items);
			}
			player.getInventory().clear();
			EntityEquipment stuff = player.getEquipment();
			stuff.setHelmet(null);
			stuff.setChestplate(null);
			stuff.setLeggings(null);
			stuff.setBoots(null);
			player.setHealth(20);
			/*if (isTeamDead(getTeam(player)) && !isBombPlaced && !hasVictory) {
				if(!FPTB.isBombPlaced) {
					FPTB.victoryOp(FPTB.getTeam(player));
				}
				else if (FPTB.getTeam(player).equals(FPTB.antiterroriste)){
					if(BombExplosionTimer.seconds < (35)) {
						BombExplosionTimer.seconds = 35;
						Bukkit.getServer().broadcastMessage(FPTB.tag+ChatColor.YELLOW+"La bombe explose dans 10 secondes");
					}
					else
						System.out.println("La bombe est à "+BombExplosionTimer.seconds);

				}
			}*/
		}
	}
}
