package fr.karamouche.plantthebomb.objects;

import java.util.UUID;

import fr.karamouche.plantthebomb.enums.PTBteam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
	
}
