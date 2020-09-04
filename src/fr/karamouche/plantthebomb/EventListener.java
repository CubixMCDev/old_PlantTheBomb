package fr.karamouche.plantthebomb;

import fr.karamouche.plantthebomb.enums.PTBteam;
import fr.karamouche.plantthebomb.enums.Tools;
import fr.karamouche.plantthebomb.objects.Bomb;
import fr.karamouche.plantthebomb.objects.Game;
import fr.karamouche.plantthebomb.objects.PTBer;
import org.bukkit.*;
import org.bukkit.block.Block;
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
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
		Game game = myPlugin.getCurrentGame();
		if(game.getStatut().equals(Statut.LOBBY)) {
			player.teleport(Spawns.LOBBY.toLocation());
			game.addPlayer();
			event.setJoinMessage(game.getTag()+ ChatColor.YELLOW+event.getPlayer().getName()+ChatColor.AQUA +" a rejoint la partie "+"§8(§a"+myPlugin.getCurrentGame().getNbPlayers()+"§8/§a"+myPlugin.getCurrentGame().getMaxPlayer()+"§8)");
			player.setGameMode(GameMode.SURVIVAL);
			player.setFoodLevel(20);
			player.setHealth(20);
			player.setLevel(0);
			player.setExp(0);
			myPlugin.getCurrentGame().giveLobbyItems(player);
		}else if(game.getStatut().equals(Statut.INGAME)){
			if(game.getOfflineptbers().containsKey(player.getUniqueId())){
				PTBer ptber = game.getOfflineptbers().remove(player.getUniqueId());
				game.getPtbers().put(player.getUniqueId(), ptber);
				myPlugin.getCurrentGame().getTeam(ptber.getTeam()).addEntry(player.getName());
				event.setJoinMessage(game.getTag()+ptber.getTeam().getTag()+player.getName()+ChatColor.WHITE+" s'est reconnecté !");
			}else{
				player.getInventory().clear();
				player.setGameMode(GameMode.SPECTATOR);
				event.setJoinMessage("");
			}
		}
		else{
			event.setJoinMessage("");
			if(game.getStatut().equals(Statut.STARTING))
				player.kickPlayer(game.getTag()+"La partie est complète");
			else
				player.kickPlayer(game.getTag()+"La partie est finie");
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		myPlugin.getScoreboardManager().onLogout(event.getPlayer());
		Game game = myPlugin.getCurrentGame();
		Player player = event.getPlayer();
		if(myPlugin.getCurrentGame().getStatut().equals(Statut.LOBBY) || myPlugin.getCurrentGame().getStatut().equals(Statut.STARTING)) {
			myPlugin.getCurrentGame().removePlayer();
			event.setQuitMessage(game.getTag()+ChatColor.YELLOW+event.getPlayer().getName()+ChatColor.GRAY+" a quitté la partie "+"§8(§a"+(game.getNbPlayers())+"/§8/§a"+myPlugin.getCurrentGame().getMaxPlayer()+"§8)");

			if(game.getPtbers().containsKey(player.getUniqueId())){
				game.getPtbers().remove(player.getUniqueId()).destroy();
			}
		}else if(game.getStatut().equals(Statut.INGAME)){
			if(game.getPtbers().containsKey(player.getUniqueId())){
				PTBer ptber = game.getPtbers().remove(player.getUniqueId());
				ptber.kill(null);
				game.getOfflineptbers().put(player.getUniqueId(), ptber);
				game.getTeam(ptber.getTeam()).removeEntry(player.getName());
				if(game.getTeam(ptber.getTeam()).getEntries().size() == 0){
					if(ptber.getTeam().equals(PTBteam.ANTITERRORISTE)) {
						game.setScoreA(9);
						game.getActualRound().winner(PTBteam.ANTITERRORISTE);
					}
					else{
						game.setScoreT(9);
						game.getActualRound().winner(PTBteam.TERRORISTE);
					}
				}
				event.setQuitMessage(game.getTag()+ptber.getTeam().getTag()+player.getName()+ChatColor.WHITE+" s'est déconnecté !");
			}else{
				event.setQuitMessage("");
			}
		}else
			event.setQuitMessage("");
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
				double damage = event.getFinalDamage();
				EntityDamageEvent.DamageCause cause = event.getCause();
				if(victim.getHealth() - damage <= 0 && !cause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) && !cause.equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
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
				if (victim.getHealth() - event.getFinalDamage() <= 0) {
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
		else if (event.getItemDrop().getItemStack().isSimilar(Tools.BOMB.toItem())){
			BukkitRunnable run = new BukkitRunnable() {
				@Override
				public void run() {
					game.getActualRound().getBomb().setLoc(event.getItemDrop().getLocation());
					for(PTBer ptber : game.getPtbers().values()){
						if(ptber.getTeam().equals(PTBteam.TERRORISTE))
							game.getActualRound().getBomb().sendDirection(Bukkit.getPlayer(ptber.getPlayerID()));
					}
				}
			};
			run.runTaskLaterAsynchronously(myPlugin, 20);
		}
	}

	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent event){
		Player player = event.getPlayer();
		Game game = myPlugin.getCurrentGame();
		ItemStack item = event.getItem().getItemStack();
		if(game.getPtbers().containsKey(player.getUniqueId())){
			PTBer ptber = game.getPtbers().get(player.getUniqueId());
			if(item.isSimilar(Tools.BOMB.toItem())){
				if(ptber.getTeam().equals(PTBteam.TERRORISTE)){
					Bomb bomb = game.getActualRound().getBomb();
					bomb.setOwner(ptber);
					bomb.setLoc(null);
					event.setCancelled(true);
					player.getInventory().setItem(8, Tools.BOMB.toItem());
					player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
					event.getItem().remove();
				}else
					event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event){
		Game game = myPlugin.getCurrentGame();
		if(game.getStatut().equals(Statut.LOBBY) || game.getStatut().equals(Statut.STARTING)){
			if(!event.getPlayer().isOp() || !event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
				event.setCancelled(true);
		}else if(game.getStatut().equals(Statut.INGAME)){
			Material blockType = event.getBlock().getType();
			if(blockType.equals(Material.REDSTONE_TORCH_ON)) {
				if(!game.getActualRound().isFinish()) {
					Block downBlock = event.getBlockAgainst();
					Bomb bomb = game.getActualRound().getBomb();
					if(downBlock.getType().equals(Material.PRISMARINE)) {
						bomb.tryplant(event.getPlayer(), event.getBlockPlaced());
					}
					else {
						event.setCancelled(true);
						event.getPlayer().sendMessage(game.getTag()+ChatColor.RED+"La bombe ne peut être placée que sur un site !");
						event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.VILLAGER_NO, 1, 1);
					}
				}
				else
					event.setCancelled(true);
			}
		}else
			event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		Game game = myPlugin.getCurrentGame();
		if(game.getStatut().equals(Statut.LOBBY) || game.getStatut().equals(Statut.STARTING)){
			if(!event.getWhoClicked().isOp() || !event.getWhoClicked().getGameMode().equals(GameMode.CREATIVE))
				event.setCancelled(true);
		}else{
			ItemStack item = event.getCurrentItem();
			try {
				if(item.isSimilar(Tools.SHOP.toItem()) && event.getWhoClicked() instanceof Player){
					Player player = (Player) event.getWhoClicked();
					myPlugin.getGuiManager().open(player, Shop.class);
					event.setCancelled(true);
				}
				else if(event.getSlot() == 36 || event.getSlot() ==37 || event.getSlot() ==38 || event.getSlot() ==39)
					event.setCancelled(true);
			}catch (NullPointerException e){
				return;
			}
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
		}else if(game.getStatut().equals(Statut.INGAME) && game.getPtbers().containsKey(player.getUniqueId())){
			PTBer ptber = myPlugin.getCurrentGame().getPtbers().get(player.getUniqueId());
			if(ptber.getTeam().equals(PTBteam.TERRORISTE) && game.getActualRound().getBomb().isDrop()){
				game.getActualRound().getBomb().sendDirection(player);
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

	@EventHandler
	public void onWeather(WeatherChangeEvent event){
		event.setCancelled(true);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		String message = event.getMessage();
		Game game = myPlugin.getCurrentGame();
		if(game.getPtbers().containsKey(player.getUniqueId())) {
			PTBer ptber = game.getPtbers().get(player.getUniqueId());
			if(message.startsWith("!")){
				message = message.substring(1);
				if(ptber.getTeam().equals(PTBteam.ANTITERRORISTE))
					event.setFormat("§b[Antiterroriste] "+player.getName()+"§r: "+message);
				else
					event.setFormat("§c[Terroriste] "+player.getName()+"§r: "+message);
			}else{
				event.setCancelled(true);
				for(PTBer ptbers : game.getPtbers().values()){
					if(ptbers.getTeam().equals(ptber.getTeam())){
						Player players = Bukkit.getPlayer(ptbers.getPlayerID());
						players.sendMessage(player.getName()+" -> "+ptber.getTeam().toString()+ ChatColor.WHITE+": "+message);
					}
				}
			}
		}else{
			event.setCancelled(true);
			for(Player players : Bukkit.getOnlinePlayers()){
				if(!game.getPtbers().containsKey(players.getUniqueId()))
					players.sendMessage(ChatColor.GRAY+"[Spectateur] "+player.getName()+": "+message);
			}
		}
	}

	/*public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		Team playerTeam = FPTB.board.getPlayerTeam(player);
		if(playerTeam != null) {
			if(playerTeam.equals(FPTB.terroriste)) {
				event.setFormat("§c[Terroriste] "+player.getName()+"§r: "+message);
			}
			else if(playerTeam.equals(FPTB.antiterroriste)) {
				event.setFormat("§b[Antiterroriste] "+player.getName()+"§r: "+message);
			}
			else if(playerTeam.equals(FPTB.spectator)) {
				event.setFormat("§8[Spectateur] "+player.getName()+"§r: "+message);
			}
		}
	}*/
}
