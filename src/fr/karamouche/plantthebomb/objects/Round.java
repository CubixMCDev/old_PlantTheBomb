package fr.karamouche.plantthebomb.objects;

import fr.karamouche.plantthebomb.Main;
import fr.karamouche.plantthebomb.enums.PTBteam;
import fr.karamouche.plantthebomb.enums.Tools;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Round {
    private final PTBteam winner;
    private final Game game;
    private final Main myPlugin;
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
    }

    public String getTimer() {
        return timer;
    }

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
        runnable.runTaskTimerAsynchronously(myPlugin, 0, 20);
        this.runnable = runnable;
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
}
