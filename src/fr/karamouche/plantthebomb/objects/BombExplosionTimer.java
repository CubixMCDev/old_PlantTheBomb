package fr.karamouche.plantthebomb.objects;


import fr.karamouche.plantthebomb.Main;
import fr.karamouche.plantthebomb.enums.PTBteam;
import fr.karamouche.plantthebomb.enums.Statut;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class BombExplosionTimer extends BukkitRunnable {
    Main myPlugin;
    int seconds = 0;
    Bomb bomb;
    Round round;
    Block bombBlock;
    public BombExplosionTimer(Main myPlugin, Bomb bomb) {
        this.myPlugin = myPlugin;
        this.bomb = bomb;
        this.round = myPlugin.getCurrentGame().getActualRound();
        this.bombBlock = Bukkit.getWorld("CSGO").getBlockAt(bomb.getLoc());
    }

    @Override
    public void run() {
        Location bombLoc = bomb.getLoc();
        if (seconds == 45) {
            if(!round.isFinish())
                round.winner(PTBteam.TERRORISTE);
            this.cancel();
            bombBlock.getWorld().playSound(bombLoc, Sound.EXPLODE, 600, 1);
            bombBlock.getWorld().createExplosion(bombLoc.getX(), bombLoc.getY()+1, bombLoc.getZ(), 4F, false, false);
            Bukkit.getServer().broadcastMessage(myPlugin.getCurrentGame().getTag()+ChatColor.YELLOW+"La bombe a explosée");
            if(bomb.isPlanted()) {
                bombBlock.setType(Material.AIR);
            }
            for(PTBer ptber : myPlugin.getCurrentGame().getPtbers().values()) {
                Player joueur = Bukkit.getPlayer(ptber.getPlayerID());
                double distance = bombLoc.distance(joueur.getLocation());
                if(ptber.getTeam().equals(PTBteam.TERRORISTE))
                    ptber.addMoney(50);
                if(distance < 26 && !joueur.getGameMode().equals(GameMode.SPECTATOR))
                    ptber.kill(null);
            }
        }
        else {
            if(!bomb.isPlanted() || bomb.isDefuze() || myPlugin.getCurrentGame().getStatut().equals(Statut.ENDGAME))
                this.cancel();
            bombBlock.getWorld().playSound(bombLoc, Sound.NOTE_PLING, 2, 300);
            if(bombBlock.getType().equals(Material.REDSTONE_TORCH_ON))
                bombBlock.setType(Material.REDSTONE_TORCH_OFF);
            else if(bombBlock.getType().equals(Material.REDSTONE_TORCH_OFF))
                bombBlock.setType(Material.REDSTONE_TORCH_ON);
        }
        if(seconds <= 35)
            bomb.setTimer("00:"+(45-seconds));
        else
            bomb.setTimer("00:0"+(45-seconds));
        seconds ++;
    }
}
