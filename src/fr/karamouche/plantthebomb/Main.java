package fr.karamouche.plantthebomb;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import fr.karamouche.plantthebomb.commands.ForcestartCommand;
import fr.karamouche.plantthebomb.enums.PTBteam;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.karamouche.plantthebomb.objects.Game;
import fr.karamouche.plantthebomb.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Main extends JavaPlugin {
	
	
	//VARIABLES
	
    private ScoreboardManager sc;
    private ScheduledExecutorService executorMonoThread;
    private ScheduledExecutorService scheduledExecutorService;
    private Game game;
	
	@Override
	public void onEnable() {
		System.out.println("PTB ON");
		//SCOREBOARD
		 scheduledExecutorService = Executors.newScheduledThreadPool(16);
	     executorMonoThread = Executors.newScheduledThreadPool(1);
	     sc = new ScoreboardManager(this);
	     getServer().getPluginManager().registerEvents(new EventListener(this), this);
	     getCommand("forcestart").setExecutor(new ForcestartCommand(this));
	     game = new Game(this);
	     game.initializeTeams();
	}
	
	@Override
	public void onDisable() {
		System.out.println("PTB OFF");
		this.getScoreboardManager().onDisable();
		for(Team team :Bukkit.getScoreboardManager().getMainScoreboard().getTeams())
			team.unregister();
	}

	
	public Game getCurrentGame() {
		return this.game;
	}
	
	
    public ScoreboardManager getScoreboardManager() {
        return sc;
    }

    public ScheduledExecutorService getExecutorMonoThread() {
        return executorMonoThread;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
	
}
