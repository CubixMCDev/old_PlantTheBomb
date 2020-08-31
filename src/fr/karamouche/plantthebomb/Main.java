package fr.karamouche.plantthebomb;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import fr.karamouche.plantthebomb.commands.ForcestartCommand;
import fr.karamouche.plantthebomb.gui.GuiBuilder;
import fr.karamouche.plantthebomb.gui.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.karamouche.plantthebomb.objects.Game;
import fr.karamouche.plantthebomb.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Main extends JavaPlugin {
	
	
	//VARIABLES
	
    private ScoreboardManager sc;
    private ScheduledExecutorService executorMonoThread;
    private ScheduledExecutorService scheduledExecutorService;
	private GuiManager guiManager;
	private Map<Class<? extends GuiBuilder>, GuiBuilder> registeredMenus;
    private Game game;
	
	@Override
	public void onEnable() {
		System.out.println("PTB ON");
		//GUI
		loadGui();
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
	public Map<Class<? extends GuiBuilder>, GuiBuilder> getRegisteredMenus() {
		return registeredMenus;
	}

	private void loadGui(){
		guiManager = new GuiManager(this);
		Bukkit.getPluginManager().registerEvents(guiManager, this);
		registeredMenus = new HashMap<>();
		guiManager.addMenu(new Shop(this));
	}

	public GuiManager getGuiManager() {
		return guiManager;
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

	public Map<Entity, Player> ArrowMap = new HashMap<Entity, Player>();

	public Map<Entity, Player> getArrowMap(){
		return ArrowMap;
	}
	
}
