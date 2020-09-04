package fr.karamouche.plantthebomb.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.karamouche.plantthebomb.Main;
import fr.karamouche.plantthebomb.enums.PTBteam;
import fr.karamouche.plantthebomb.enums.Statut;
import fr.karamouche.plantthebomb.enums.Tools;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Game {
	private Statut statut;
	private final Map<UUID, PTBer> ptbers;
	private final Map<UUID, PTBer> offlineptbers;
	private final String tag;
	private int nbPlayers = 0;
	private final int maxPlayer;
	private final Main myPlugin;
	private int scoreT;
	private int scoreA;
	private final Team terroriste;
	private final Team antiterroriste;
	private Round actualRound;
	private final ArrayList<Round> roundsList = new ArrayList();
	
	//CONSTRUCTEUR
	public Game(Main main) {
		for(Team team : Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeams())
			team.unregister();
		this.setStatut(Statut.LOBBY);
		this.ptbers = new HashMap<>();
		this.offlineptbers = new HashMap<>();
		this.tag = "§8[§eP§6T§cB§8] §r";
		this.myPlugin = main;
		this.scoreA = 0;
		this.scoreT = 0;
		maxPlayer = 10;
		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		this.terroriste = board.registerNewTeam("Terroriste");
		this.antiterroriste = board.registerNewTeam("Antiterroriste");
		this.actualRound = null;
	}

	public void giveLobbyItems(Player player) {
		ItemStack terroJoin = Tools.TERROJOIN.toItem();
		ItemStack antiJoin = Tools.ANTITERROJOIN.toItem();
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);

		player.getInventory().setItem(3, terroJoin);
		player.getInventory().setItem(5, antiJoin);

		System.out.println("ON GIVE LES OBJETS A "+player.getName());
	}

	public void initializeTeams(){
		this.terroriste.setPrefix("§c");
		this.terroriste.setAllowFriendlyFire(false);

		this.antiterroriste.setPrefix("§b");
		this.antiterroriste.setAllowFriendlyFire(false);
	}
	public String getTimer(){
		return this.getActualRound().getTimer();
	}

	public Team getTerro(){return terroriste;}
	public Team getAntiterro(){return antiterroriste;}

	public Team getTeam(PTBteam team){
		if(team.equals(PTBteam.ANTITERRORISTE))
			return getAntiterro();
		else
			return getTerro();
	}

	public Statut getStatut() {
		return statut;
	}

	public void setStatut(Statut statut) {
		this.statut = statut;
	}

	public Map<UUID, PTBer> getPtbers() {
		return ptbers;
	}

	public String getTag() {
		return tag;
	}

	public int getMaxPlayer() {
		return maxPlayer;
	}

	public Main getMyPlugin() {
		return myPlugin;
	}

	public int getScoreT() {
		return scoreT;
	}

	public void setScoreT(int scoreT) {
		this.scoreT = scoreT;
	}

	public int getScoreA() {
		return scoreA;
	}

	public void setScoreA(int scoreA) {
		this.scoreA = scoreA;
	}
	
	public void lunching() {
		Game thisGame = this;
		BukkitRunnable timer = new BukkitRunnable() {
			int timeB = 30;
			@Override
			public void run() {
				if(thisGame.getNbPlayers() != thisGame.getMaxPlayer()) {
					thisGame.setStatut(Statut.LOBBY);
					this.cancel();
				}
				else {
					if (timeB > 6) {
						if(timeB == 30)
							Bukkit.getServer().broadcastMessage(myPlugin.getCurrentGame().getTag()+ ChatColor.YELLOW+" La partie commence dans 30 secondes");
						if(timeB == 15)
							Bukkit.getServer().broadcastMessage(myPlugin.getCurrentGame().getTag()+ChatColor.YELLOW+" La partie commence dans 15 secondes");
						if(timeB == 10)
							Bukkit.getServer().broadcastMessage(myPlugin.getCurrentGame().getTag()+ChatColor.YELLOW+" La partie commence dans 10 secondes");
					}
					else if(timeB==6) {
						for(Player player : Bukkit.getOnlinePlayers()) {
							player.sendTitle("§6 Preparez vous... ", "§eLa partie va commencer");
							player.playSound(player.getLocation(), Sound.NOTE_PLING, 256, 1);}}
					else if(timeB < 6 && timeB != 0) {
						for(Player player : Bukkit.getOnlinePlayers()) {
							player.sendTitle("§a"+timeB, "§eLa partie va commencer");
							player.playSound(player.getLocation(), Sound.NOTE_PLING, 256, 1);}}

					else if (timeB == 0) {
						for (Player player : Bukkit.getOnlinePlayers()) {
							player.sendTitle("§bC'est parti !", "");
							player.playSound(player.getLocation(), Sound.CAT_MEOW, 256, 1000);
						}
						thisGame.start();
						this.cancel();
					}

					timeB--;
				}

			}
		};
		this.setStatut(Statut.STARTING);
		timer.runTaskTimer(myPlugin, 0, 20);
	}

	public void start() {
		Game game = myPlugin.getCurrentGame();
		game.setScoreA(0);
		game.setScoreT(0);
		for (Player player : Bukkit.getOnlinePlayers()) {
			PTBer ptber;
			player.setGameMode(GameMode.SPECTATOR);
			if (!game.getPtbers().containsKey(player.getUniqueId())) {
				if (game.getTeam(PTBteam.ANTITERRORISTE).getEntries().size() < game.getTeam(PTBteam.TERRORISTE).getEntries().size()) {
					ptber = new PTBer(player.getUniqueId(), PTBteam.ANTITERRORISTE, myPlugin);
				} else {
					ptber = new PTBer(player.getUniqueId(), PTBteam.TERRORISTE, myPlugin);
				}
			} else
				ptber = game.getPtbers().get(player.getUniqueId());
			player.getInventory().clear();
			ptber.setMoney(0);
			ptber.setKills(0);
			game.getTerro().setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
			game.getAntiterro().setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
		}
		game.setStatut(Statut.INGAME);
		this.actualRound = new Round(myPlugin, this);
	}

	public void endgame(PTBteam winnerTeam){
		this.setStatut(Statut.ENDGAME);
		if (winnerTeam.equals(PTBteam.TERRORISTE)) {
			Team winners = this.getTerro();
			Bukkit.getServer().broadcastMessage(this.getTag() + ChatColor.BOLD + "Victoire des " + winners.getPrefix() + "terroristes !");
		}else {
			Team winners = this.getAntiterro();
			Bukkit.getServer().broadcastMessage(this.getTag()+ChatColor.BOLD+"Victoire des "+winners.getPrefix()+"antiterroristes !");
		}
			for(PTBer ptber : this.getPtbers().values()) {
				Player player = Bukkit.getPlayer(ptber.getPlayerID());
				if(ptber.getTeam().equals(winnerTeam)) {
					player.setAllowFlight(true);
					if(!player.getGameMode().equals(GameMode.SPECTATOR)) {
						Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
						FireworkMeta data = firework.getFireworkMeta();
						data.addEffects(FireworkEffect.builder().withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE).withFlicker().build());
						data.setPower(1);
						firework.setFireworkMeta(data);
					}
				}
				else if(!player.getGameMode().equals(GameMode.SPECTATOR))
					ptber.kill(null);
			}
	}

	public int getNbPlayers() {
		return nbPlayers;
	}

	public void setNbPlayers(int nbPlayers) {
		this.nbPlayers = nbPlayers;
	}

	public void addPlayer() {
		this.setNbPlayers(this.getNbPlayers()+1);
	}

	public void removePlayer(){
		this.setNbPlayers(this.getNbPlayers()-1);
	}

	public void createPlayer(Player player, PTBteam team){
		new PTBer(player.getUniqueId(), team, myPlugin);
	}

	public ArrayList<Round> getRoundsList() {
		return roundsList;
	}

	public Round getActualRound() {
		return actualRound;
	}

	public void setActualRound(Round actualRound) {
		this.actualRound = actualRound;
	}

	public Map<UUID, PTBer> getOfflineptbers() {
		return offlineptbers;
	}
}
