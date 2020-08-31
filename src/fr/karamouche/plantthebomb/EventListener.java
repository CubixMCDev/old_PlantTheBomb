package fr.karamouche.plantthebomb;

import fr.karamouche.plantthebomb.enums.PTBteam;
import fr.karamouche.plantthebomb.enums.Tools;
import fr.karamouche.plantthebomb.objects.Game;
import fr.karamouche.plantthebomb.objects.PTBer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.karamouche.plantthebomb.enums.Spawns;
import fr.karamouche.plantthebomb.enums.Statut;
import org.bukkit.inventory.ItemStack;

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
	public void onDamage(EntityDamageEvent e){
		EntityDamageEvent.DamageCause cause = e.getCause();
		if(cause.equals(EntityDamageEvent.DamageCause.FALL))
			e.setCancelled(true);
	}

	@EventHandler
	public void onDamageBySomeone(EntityDamageByEntityEvent e){
		Game game = myPlugin.getCurrentGame();
		if(game.getStatut().equals(Statut.LOBBY) || game.getStatut().equals(Statut.STARTING)){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event){
		Game game = myPlugin.getCurrentGame();
		if(game.getStatut().equals(Statut.LOBBY) || game.getStatut().equals(Statut.STARTING)){
			event.setCancelled(true);
		}
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
				if(game.getPtbers().containsKey(player.getUniqueId())){
					ptber = game.getPtbers().get(player.getUniqueId());
					ptber.setTeam(PTBteam.TERRORISTE);
				}else{
					ptber = new PTBer(player.getUniqueId(), PTBteam.TERRORISTE, myPlugin);
				}
				player.sendMessage(game.getTag()+"§cVous avez rejoint les terroristes");
				player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 1, 1);
			}

			else if(itemInHand.isSimilar(Tools.ANTITERROJOIN.toItem())) {
				PTBer ptber;
				event.setCancelled(true);
				if(game.getPtbers().containsKey(player.getUniqueId())){
					ptber = game.getPtbers().get(player.getUniqueId());
					ptber.setTeam(PTBteam.ANTITERRORISTE);
				}else{
					ptber = new PTBer(player.getUniqueId(), PTBteam.ANTITERRORISTE, myPlugin);
				}
				player.sendMessage(game.getTag()+"§bVous avez rejoint les antiterroristes");
				player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 1, 1);
			}
		}
	}
}
