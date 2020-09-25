package fr.karamouche.plantthebomb.objects;

import fr.karamouche.plantthebomb.Main;
import fr.karamouche.plantthebomb.enums.Tools;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DefuzeTimer extends BukkitRunnable {
    Main myPlugin;
    PTBer defuzerPTB;
    int defuzeTime;
    Player defuzer;
    Location defuzerLocation;
    Block bombBlock;
    double playerLife;

    public DefuzeTimer(Main myPlugin, PTBer defuzerPTB, int defuzeTime){
        this.myPlugin = myPlugin;
        this.defuzerPTB = defuzerPTB;
        this.defuzeTime = defuzeTime;
        this.defuzer = Bukkit.getPlayer(defuzerPTB.getPlayerID());
        defuzerLocation = this.defuzer.getLocation();
        bombBlock = defuzer.getWorld().getBlockAt(myPlugin.getCurrentGame().getActualRound().getBomb().getLoc());
        playerLife = defuzer.getHealth();

    }
    int ticks = 0;
    @Override
    public void run() {
        ticks ++;
        Block defuzerPose = defuzerLocation.getBlock();
        if(myPlugin.getCurrentGame().getActualRound().getBomb().isDefuzing() && defuzerPose.equals(defuzer.getLocation().getBlock()) && defuzer.getHealth() == playerLife && (defuzer.getItemInHand().isSimilar(Tools.KIT.toItem()) || defuzer.getItemInHand().getType().equals(Material.AIR))) {
            if(ticks == defuzeTime) {
                myPlugin.getCurrentGame().getActualRound().getBomb().defuze(defuzerPTB);
                this.cancel();
            }
            else {
                int format = (defuzeTime-1) - ticks;
                int seconde = format/20;
                int centieme = format - seconde*20;
                centieme = centieme*5;
                //PACKET TO CHRONO // FAIRE UN DECOMPTE PLUTOT
                IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" +ChatColor.AQUA+"Defuzing : "+ seconde+":"+centieme + "\"}");
                PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
                ((CraftPlayer) defuzer).getHandle().playerConnection.sendPacket(ppoc);
            }
        }
        else {
            defuzer.sendMessage(myPlugin.getCurrentGame().getTag()+ ChatColor.YELLOW+"Le defuze de la bombe a été annulé");
            myPlugin.getCurrentGame().getActualRound().getBomb().setDefuzing(false);
            this.cancel();
        }
    }
}
