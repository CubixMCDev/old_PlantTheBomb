package fr.karamouche.plantthebomb.objects;

import java.util.UUID;
import org.bukkit.scoreboard.Team;

import fr.karamouche.plantthebomb.Main;

public class PTBer {
	private final UUID playerID;
	private int kills;
	private Team team;
	private int money;
	private final Main myPlugin;
	
	public PTBer(UUID playerID, Team team, Main main) {
		this.playerID = playerID;
		this.setKills(0);
		this.setMoney(0);
		this.myPlugin = main;
		this.setTeam(team);
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

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
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
