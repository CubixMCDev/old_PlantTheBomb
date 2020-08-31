package fr.karamouche.plantthebomb.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.karamouche.plantthebomb.Main;
import fr.karamouche.plantthebomb.enums.Statut;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Game {
	private Statut statut;
	private final Map<UUID, PTBer> ptbers;
	private String timer;
	private final String tag;
	private final int maxPlayer = 10;
	private int nbPlayers = 0;
	private final Main myPlugin;
	private int scoreT;
	private int scoreA;
	
	//CONSTRUCTEUR
	public Game(Main main) {
		this.setStatut(Statut.LOBBY);
		this.ptbers = new HashMap<UUID, PTBer>();
		this.tag = "§8[§eP§6T§cB§8] §r";
		this.myPlugin = main;
		this.scoreA = 0;
		this.scoreT = 0;
		this.timer = "00:00";
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
					else if(timeB<6 && timeB != 0) {
						for(Player player : Bukkit.getOnlinePlayers()) {
							player.sendTitle("§a"+timeB, "§eLa partie va commencer");
							player.playSound(player.getLocation(), Sound.NOTE_PLING, 256, 1);}}

					else if (timeB==0) {
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

	public void start(){

	}

	public String getTimer() {
		return timer;
	}

	public void setTimer(String timer) {
		this.timer = timer;
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
}
