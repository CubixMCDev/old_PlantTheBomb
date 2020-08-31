package fr.karamouche.plantthebomb;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.karamouche.plantthebomb.enums.Spawns;
import fr.karamouche.plantthebomb.enums.Statut;

public class EventListener implements Listener {

	Main myPlugin;
	
	public EventListener(Main main) {
		myPlugin = main;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		myPlugin.getScoreboardManager().onLogin(event.getPlayer());
		if(myPlugin.getCurrentGame().getStatut().equals(Statut.LOBBY) || myPlugin.getCurrentGame().getStatut().equals(Statut.STARTING)) {
			event.getPlayer().teleport(Spawns.lobby.toLocation());
			myPlugin.getCurrentGame().addPlayer();
			event.setJoinMessage(myPlugin.getCurrentGame().getTag()+ ChatColor.YELLOW+event.getPlayer().getName()+ChatColor.GRAY+" a rejoint la partie "+"§8(§a"+myPlugin.getCurrentGame().getNbPlayers()+"§8/§a"+myPlugin.getCurrentGame().getMaxPlayer()+"§8)");
			event.getPlayer().setGameMode(GameMode.SURVIVAL);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		myPlugin.getScoreboardManager().onLogout(event.getPlayer());
		if(myPlugin.getCurrentGame().getStatut().equals(Statut.LOBBY) || myPlugin.getCurrentGame().getStatut().equals(Statut.STARTING)) {
			myPlugin.getCurrentGame().removePlayer();
			event.setQuitMessage(myPlugin.getCurrentGame().getTag()+ChatColor.YELLOW+event.getPlayer().getName()+ChatColor.GRAY+" a quitté la partie "+ChatColor.YELLOW+"["+(myPlugin.getCurrentGame().getNbPlayers()-1)+"/"+myPlugin.getCurrentGame().getMaxPlayer()+"]");
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event){
		if(!myPlugin.getCurrentGame().getStatut().equals(Statut.LOBBY) || !event.getPlayer().isOp())
			event.setCancelled(true);
	}
}
