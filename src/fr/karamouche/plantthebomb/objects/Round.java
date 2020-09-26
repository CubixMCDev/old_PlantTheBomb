package fr.karamouche.plantthebomb.objects;

import fr.karamouche.plantthebomb.Main;
import fr.karamouche.plantthebomb.enums.PTBteam;
import fr.karamouche.plantthebomb.enums.Spawns;
import fr.karamouche.plantthebomb.enums.Tools;
import fr.karamouche.plantthebomb.objects.grenade.Grenade;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class Round {
    private PTBteam winner;
    private final Game game;
    private final Main myPlugin;
    private final Bomb bomb;
    private final ArrayList<Grenade> grenades = new ArrayList();
    private String timer;
    private BukkitRunnable runnable;
    private boolean canMoove;
    private boolean isFinish;

    public Round(Main myPlugin, Game game) {
        this.game = game;
        this.myPlugin = myPlugin;
        winner = null;
        timer = "00:00";
        startTimer();
        canMoove = false;
        isFinish = false;
        for(Entity element : Bukkit.getWorld("CSGO").getEntities()) {
            if (element.getType().equals(org.bukkit.entity.EntityType.DROPPED_ITEM))
                element.remove();
        }
        for (PTBer ptber : game.getPtbers().values()) {
            PTBteam team = ptber.getTeam();
            Player player = Bukkit.getPlayer(ptber.getPlayerID());
            if (team.equals(PTBteam.ANTITERRORISTE)) {
                player.teleport(Spawns.ATERRO.toLocation());
            } else {
                player.teleport(Spawns.TERRO.toLocation());
            }
            if(player.getGameMode().equals(GameMode.SPECTATOR)){
                player.getInventory().clear();
                ptber.giveBasicStuff();
            }else
                ptber.clearStuff();
            player.setGameMode(GameMode.SURVIVAL);
            ptber.addMoney(100);
        }
        this.bomb = new Bomb(myPlugin);
    }

    public String getTimer() {
        return timer;
    }
    //RAJOUTER L'ETAT DE JEU A LA PLACE DU TIMER
    public void startTimer(){
        Round round = this;
        BukkitRunnable runnable = new BukkitRunnable() {
            int s = 0;
            int m = 0;
            @Override
            public void run() {
                if(s >= 60){
                    s = 0;
                    m++;
                }
                if(m==0 && s==15){
                    round.setCanMoove(true);
                    for(Player player : Bukkit.getOnlinePlayers()){
                        if(player.getInventory().contains(Tools.SHOP.toItem()))
                            player.getInventory().remove(Tools.SHOP.toItem());
                        player.closeInventory();
                    }
                    Bukkit.getServer().broadcastMessage(myPlugin.getCurrentGame().getTag()+"Vous pouvez maintenant bouger !");
                }
                if(m==3 && s==0 && !game.getActualRound().getBomb().isPlanted()){
                    game.getActualRound().winner(PTBteam.ANTITERRORISTE);
                }
                //FORMAT
                String sFormat = "";
                String mFormat = "";
                if(s<10)
                    sFormat = "0";
                if(m<10)
                    mFormat = "0";
                String time = mFormat+m+":"+sFormat+s;
                round.setTimer(time);
                s++;
            }
        };
        runnable.runTaskTimer(myPlugin, 0, 20);
        this.runnable = runnable;
    }

    public void winner(PTBteam team){
        this.stop();
        this.setWinner(team);
        int winnerPoint = 10;

        Game game = myPlugin.getCurrentGame();
        if(team.equals(PTBteam.TERRORISTE)){
            game.setScoreT(game.getScoreT()+1);
            Bukkit.getServer().broadcastMessage(game.getTag()+ChatColor.YELLOW+"Les "+game.getTerro().getPrefix()+"terroristes"+ChatColor.YELLOW+" remportent le round !");
            if(game.getScoreT() == winnerPoint){
                game.endgame(PTBteam.TERRORISTE);
                return;
            }
        }else if(team.equals(PTBteam.ANTITERRORISTE)){
            game.setScoreA(game.getScoreA()+1);
            Bukkit.getServer().broadcastMessage(game.getTag()+ ChatColor.YELLOW+"Les "+game.getAntiterro().getPrefix()+"antiterroristes"+ChatColor.YELLOW+" remportent le round !");
            if(game.getScoreA() == winnerPoint) {
                game.endgame(PTBteam.ANTITERRORISTE);
                return;
            }
        }
        for(PTBer ptber : game.getPtbers().values()){
            Player player = Bukkit.getPlayer(ptber.getPlayerID());
            if(ptber.getTeam().equals(team)){
                ptber.addMoney(250);
                player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
            }else{
                ptber.addMoney(175);
                player.playSound(player.getLocation(), Sound.BAT_DEATH, 1, 1);
            }
        }
        BukkitRunnable runnable = new BukkitRunnable() {
            int i = 0;
            final Round round = game.getActualRound();
            @Override
            public void run() {
                round.setTimer("00:0"+(5-i));
                if(i == 5) {
                    Bomb bomb = game.getActualRound().getBomb();
                    bomb.remove();
                    for(Grenade grenade : game.getActualRound().getGrenades()){
                        grenade.removeEffect();
                    }
                    game.getRoundsList().add(game.getActualRound());
                    game.setActualRound(new Round(myPlugin, game));
                    this.cancel();
                }
                i++;
            }
        };
        runnable.runTaskTimer(myPlugin,0, 20);
    }

    private void setWinner(PTBteam team) {
        this.winner = team;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public void stop(){
        this.runnable.cancel();
        this.setFinish(true);
    }

    public boolean isCanMoove() {
        return canMoove;
    }

    public void setCanMoove(boolean canMoove) {
        this.canMoove = canMoove;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean stop) {
        isFinish = stop;
    }

    public PTBteam getWinner() {
        return winner;
    }

    public Bomb getBomb() {
        return bomb;
    }

    public ArrayList<Grenade> getGrenades() {
        return grenades;
    }
}
