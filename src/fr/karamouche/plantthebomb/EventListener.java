package fr.karamouche.plantthebomb;

import fr.karamouche.plantthebomb.enums.PTBteam;
import fr.karamouche.plantthebomb.enums.Tools;
import fr.karamouche.plantthebomb.objects.Game;
import fr.karamouche.plantthebomb.objects.PTBer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import fr.karamouche.plantthebomb.enums.Spawns;
import fr.karamouche.plantthebomb.enums.Statut;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EventListener implements Listener {

	Main myPlugin;
	
	public EventListener(Main main) {
		myPlugin = main;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		myPlugin.getScoreboardManager().onLogin(event.getPlayer());
		Player player = event.getPlayer();
		if(myPlugin.getCurrentGame().getStatut().equals(Statut.LOBBY) || myPlugin.getCurrentGame().getStatut().equals(Statut.STARTING)) {
			player.teleport(Spawns.LOBBY.toLocation());
			myPlugin.getCurrentGame().addPlayer();
			event.setJoinMessage(myPlugin.getCurrentGame().getTag()+ ChatColor.YELLOW+event.getPlayer().getName()+ChatColor.AQUA +" a rejoint la partie "+"§8(§a"+myPlugin.getCurrentGame().getNbPlayers()+"§8/§a"+myPlugin.getCurrentGame().getMaxPlayer()+"§8)");
			player.setGameMode(GameMode.SURVIVAL);
			player.setFoodLevel(20);
			player.setHealth(20);
			player.setLevel(0);
			player.setExp(0);
			myPlugin.getCurrentGame().giveLobbyItems(player);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		myPlugin.getScoreboardManager().onLogout(event.getPlayer());
		Game game = myPlugin.getCurrentGame();
		Player player = event.getPlayer();
		if(myPlugin.getCurrentGame().getStatut().equals(Statut.LOBBY) || myPlugin.getCurrentGame().getStatut().equals(Statut.STARTING)) {
			myPlugin.getCurrentGame().removePlayer();
			event.setQuitMessage(myPlugin.getCurrentGame().getTag()+ChatColor.YELLOW+event.getPlayer().getName()+ChatColor.GRAY+" a quitté la partie "+ChatColor.YELLOW+"["+(myPlugin.getCurrentGame().getNbPlayers())+"/"+myPlugin.getCurrentGame().getMaxPlayer()+"]");
			if(game.getPtbers().containsKey(player.getUniqueId())){
				game.getPtbers().get(player.getUniqueId()).destroy();
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event){
		if(!myPlugin.getCurrentGame().getStatut().equals(Statut.LOBBY) || !event.getPlayer().isOp() || !event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
			event.setCancelled(true);
	}

	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event){
		Game game = myPlugin.getCurrentGame();
		if(game.getStatut().equals(Statut.LOBBY) || game.getStatut().equals(Statut.ENDGAME) || game.getStatut().equals(Statut.STARTING))
			event.setCancelled(true);
		else{
			if(event.getEntity() instanceof Player) {
				Player victim = (Player) event.getEntity();
				double damage = event.getDamage();
				if(victim.getHealth() - damage <= 0) {
					event.setCancelled(true);
					PTBer ptber = game.getPtbers().get(victim.getUniqueId());
					ptber.kill(null);
				}
			}
		}
	}


	@EventHandler
	public void onDamageBySomeone(EntityDamageByEntityEvent event){
		Game game = myPlugin.getCurrentGame();
		if(game.getStatut().equals(Statut.LOBBY) || game.getStatut().equals(Statut.STARTING)){
			event.setCancelled(true);
		}
		else {
			Entity damagerE = event.getDamager();
			Entity victimE = event.getEntity();
			if (victimE instanceof Player && damagerE.getType() == EntityType.ARROW) {
				if (getArrowMap().containsKey(damagerE)) {
					damagerE = getArrowMap().get(damagerE);
					getArrowMap().remove(damagerE);
				}
			}
			if (damagerE instanceof Player && victimE instanceof Player) {
				Player damager = (Player) damagerE;
				Player victim = (Player) victimE;
				if (victim.getHealth() - event.getDamage() <= 0) {
					event.setCancelled(true);
					game.getPtbers().get(victim.getUniqueId()).kill(damager);
				}
			}
		}
	}

	public Map<Entity, Player> ArrowMap = new HashMap<>();

	public Map<Entity, Player> getArrowMap(){
		return ArrowMap;
	}

	@EventHandler
	public void onFire (EntityShootBowEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof Player) {
			Entity arrow = event.getProjectile();
			getArrowMap().put(arrow, (Player) entity);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event){
		Game game = myPlugin.getCurrentGame();
		if(game.getStatut().equals(Statut.LOBBY) || game.getStatut().equals(Statut.STARTING)){
			event.setCancelled(true);
		}else if(event.getItemDrop().getItemStack().isSimilar(Tools.SHOP.toItem()))
			event.setCancelled(true);
	}
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event){
		Game game = myPlugin.getCurrentGame();
		if(game.getStatut().equals(Statut.LOBBY) || game.getStatut().equals(Statut.STARTING)){
			if(!event.getPlayer().isOp() || !event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		Game game = myPlugin.getCurrentGame();
		if(game.getStatut().equals(Statut.LOBBY) || game.getStatut().equals(Statut.STARTING)){
			if(!event.getWhoClicked().isOp() || !event.getWhoClicked().getGameMode().equals(GameMode.CREATIVE))
				event.setCancelled(true);
		}else{
			ItemStack item = event.getCurrentItem();
			if(item.isSimilar(Tools.SHOP.toItem()) && event.getWhoClicked() instanceof Player){
				Player player = (Player) event.getWhoClicked();
				myPlugin.getGuiManager().open(player, Shop.class);
				event.setCancelled(true);
			}
			else if(event.getSlot() == 36 || event.getSlot() ==37 || event.getSlot() ==38 || event.getSlot() ==39)
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Game game = myPlugin.getCurrentGame();
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack itemInHand = player.getItemInHand();
			if(itemInHand.isSimilar(Tools.TERROJOIN.toItem())) {
				PTBer ptber;
				event.setCancelled(true);
				if(game.getTerro().getEntries().size() < 5) {
					if (game.getPtbers().containsKey(player.getUniqueId())) {
						ptber = game.getPtbers().get(player.getUniqueId());
						ptber.setTeam(PTBteam.TERRORISTE);
					} else {
						ptber = new PTBer(player.getUniqueId(), PTBteam.TERRORISTE, myPlugin);
					}
					player.sendMessage(game.getTag() + "§cVous avez rejoint les terroristes");
					player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 1, 1);
				}
				else {
					player.sendMessage(game.getTag() + ChatColor.RED + "L'équipe est complète !");
					player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
				}
			}

			else if(itemInHand.isSimilar(Tools.ANTITERROJOIN.toItem())) {
				PTBer ptber;
				event.setCancelled(true);
				if (game.getAntiterro().getEntries().size() < 5) {
					if (game.getPtbers().containsKey(player.getUniqueId())) {
						ptber = game.getPtbers().get(player.getUniqueId());
						ptber.setTeam(PTBteam.ANTITERRORISTE);
					} else {
						ptber = new PTBer(player.getUniqueId(), PTBteam.ANTITERRORISTE, myPlugin);
					}
					player.sendMessage(game.getTag() + "§bVous avez rejoint les antiterroristes");
					player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 1, 1);
				}
				else {
					player.sendMessage(game.getTag() + ChatColor.RED + "L'équipe est complète !");
					player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
				}
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Game game = myPlugin.getCurrentGame();
		if (game.getStatut().equals(Statut.INGAME) && !game.getActualRound().isCanMoove() && !event.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
			Location place = player.getLocation();
			PTBer ptber = game.getPtbers().get(player.getUniqueId());
			if (ptber.getTeam().equals(PTBteam.TERRORISTE) && place.distance(Spawns.TERRO.toLocation()) > 5) {
				player.teleport(Spawns.TERRO.toLocation());
				player.sendMessage(game.getTag() + ChatColor.RED + "La partie n'a pas commencée");
				player.playSound(event.getPlayer().getLocation(), Sound.VILLAGER_NO, 1, 1);
			}
			if (ptber.getTeam().equals(PTBteam.ANTITERRORISTE) && place.distance(Spawns.ATERRO.toLocation()) > 5) {
				player.teleport(Spawns.ATERRO.toLocation());
				player.sendMessage(game.getTag() + ChatColor.RED + "La partie n'a pas commencée");
				player.playSound(event.getPlayer().getLocation(), Sound.VILLAGER_NO, 1, 1);
			}
		}
	}

	@EventHandler
	public void onArrowHit(ProjectileHitEvent event){
		if(event.getEntity() instanceof Arrow){
			Arrow arrow = (Arrow) event.getEntity();
			arrow.remove();
		}
	}
}
