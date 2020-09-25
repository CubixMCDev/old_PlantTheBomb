package fr.karamouche.plantthebomb.objects;

import fr.karamouche.plantthebomb.enums.Tools;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class BombPlantingTimer extends BukkitRunnable {
    int ticks = 0;
    //ON ENVOIE CES TROIS VARIABLES DP FPTB
    private final Game game;
    private final Player planter;
    private final Location planterLocation;
    private final Bomb bomb;
    private final Block bombBlock;
    private final double playerLife;

    public BombPlantingTimer(Game game, Player planter, double health, Block bomb, Location location) {
        this.game = game;
        this.planter = planter;
        this.playerLife = health;
        this.bombBlock = bomb;
        this.planterLocation = location;
        this.bomb = game.getActualRound().getBomb();
    }

    @Override
    public void run() {
        ticks ++;
        Block planterPose = planterLocation.getBlock();
        ItemStack itemHand = planter.getItemInHand();
        if(planterPose.equals(planter.getLocation().getBlock()) && itemHand.getType().equals(Material.AIR) && planter.getHealth() == playerLife && !game.getActualRound().isFinish()) {
            if(ticks == 64) {
                bomb.bombPlaceEvent(planter, bombBlock);
                this.cancel();
            }
            else {
                int format = 63 - ticks;
                int seconde = format/20;
                int centieme = format - seconde*20;
                centieme = centieme*5;
                //PACKET TO CHRONO // FAIRE UN DECOMPTE PLUTOT
                IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" +ChatColor.RED+"Planting : "+ seconde+":"+centieme + "\"}");
                PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
                ((CraftPlayer) planter).getHandle().playerConnection.sendPacket(ppoc);
            }
        }
        else {
            bombBlock.setType(Material.AIR);
            planter.getInventory().setItem(8, Tools.BOMB.toItem());
            if(game.getActualRound().isFinish())
                planter.sendMessage(game.getTag()+ChatColor.YELLOW+"Fin du round");
            else
                planter.sendMessage(game.getTag()+ChatColor.YELLOW+"La pose de la bombe a été annulé");
            bomb.setLoc(null);
            this.cancel();
        }
    }

}
