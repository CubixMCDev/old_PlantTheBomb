package fr.karamouche.plantthebomb.objects;

import fr.karamouche.plantthebomb.Main;
import fr.karamouche.plantthebomb.enums.PTBteam;
import fr.karamouche.plantthebomb.enums.Tools;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class Bomb {
    private final Main myPlugin;
    private Location loc;
    private PTBer owner;
    private boolean isPlanted;
    private String timer;

    public Bomb(Main myPlugin){
        this.myPlugin = myPlugin;
        ArrayList<PTBer> terros = new ArrayList();
        for(PTBer ptber : myPlugin.getCurrentGame().getPtbers().values()){
            if(ptber.getTeam().equals(PTBteam.TERRORISTE))
                terros.add(ptber);
        }
        int size = terros.size();
        Random rand = new Random();
        this.owner = terros.get(rand.nextInt(size));
        Player player = Bukkit.getPlayer(this.owner.getPlayerID());
        player.getInventory().setItem(8, Tools.BOMB.toItem());
        this.loc = null;
        this.isPlanted = false;
        this.timer = "00:00";
    }

    public static char getDirectionArrow(Location playerLoc, Location targetLoc) {
        Vector inBetween = targetLoc.clone().subtract(playerLoc).toVector();
        Vector lookVec = playerLoc.getDirection();

        //Thanks to creator3 on spigotmc.org
        double angleDir = (Math.atan2(inBetween.getZ(),inBetween.getX()) / 2 / Math.PI * 360 + 360) % 360;
        double angleLook = (Math.atan2(lookVec.getZ(),lookVec.getX()) / 2 / Math.PI * 360 + 360) % 360;

        double angle = (angleDir - angleLook + 360) % 360;
        //NORTH
        if(angle > 337.5 || angle < 22.5)
            return '↑';
            //NORTH EAST
        else if(angle > 22.5 && angle < 67.5)
            return '↗';
            //EASR
        else if(angle > 67.5 && angle < 112.5)
            return '→';
            //SOUTH EAST
        else if( angle > 112.5 && angle < 157.5)
            return '↘';
            //SOUTH
        else if(angle > 157.5 && angle < 202.5)
            return '↓';
            //SOUTH WEST
        else if(angle > 202.5 && angle < 247.5)
            return '↙';
            //WEST
        else if(angle > 247.5 && angle < 292.5)
            return '←';
            //NORTH WEST
        else if(angle > 292.5 && angle < 337.5)
            return '↖';
            //NONE
        else
            return '*';
    }
    //←↑→↓↖↗↘↙
    //🡰 🡲 🡱 🡳 🡴 🡵 🡶 🡷



    public void sendDirection(Player p) {
        if(this.getLoc() != null) {
            Location playerPlace = p.getLocation();
            Location bombDropLoc = this.getLoc();
            char arrow = getDirectionArrow(p.getLocation(), bombDropLoc);
            int distance = (int) playerPlace.distance(bombDropLoc);
            IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.GOLD + "Bombe : "+"§l"+ChatColor.BOLD+arrow+" §r§6"+distance+ "\"}");
            PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
        }
    }
    public boolean isDrop(){
        if(this.getLoc() != null && !this.isPlanted())
            return true;
        else
            return false;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public PTBer getOwner() {
        return owner;
    }

    public void setOwner(PTBer owner) {
        this.owner = owner;
    }

    public boolean isPlanted() {
        return isPlanted;
    }

    public void setPlanted(boolean planted) {
        isPlanted = planted;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public Main getMyPlugin() {
        return myPlugin;
    }

    public void tryplant(Player planter, Block bomb) {
        Game game = myPlugin.getCurrentGame();
        if(!game.getActualRound().isFinish()) {
            if(planter.getLocation().distance(bomb.getLocation()) >= 1.2) {
                planter.sendMessage(ChatColor.YELLOW+"Vous êtes trop loin pour planter la bombe");
                bomb.setType(Material.AIR);
                planter.getInventory().setItem(8, Tools.BOMB.toItem());
            }
            else {
                BombPlantingTimer timer = new BombPlantingTimer(game, planter, planter.getHealth(), bomb, planter.getLocation());
                planter.sendMessage(ChatColor.YELLOW+"Vous plantez la bombe");
                planter.getWorld().playSound(planter.getLocation(), Sound.PISTON_EXTEND, 1, 1);
                timer.runTaskTimer(myPlugin, 0, 1);
            }
        }
        else {
            planter.sendMessage(game.getTag()+"Vous ne pouvez pas planter la bombe alors que la manche est finie");
            bomb.setType(Material.AIR);
            planter.getInventory().setItem(8, Tools.BOMB.toItem());
        }
    }

    public void bombPlaceEvent(Player planter, Block bombBlock) {
        Game game = myPlugin.getCurrentGame();
        this.setLoc(bombBlock.getLocation());
        PTBer ptber = game.getPtbers().get(planter.getUniqueId());
        BombExplosionTimer timer = new BombExplosionTimer(myPlugin, this);
        Bukkit.broadcastMessage(game.getTag() + ChatColor.YELLOW+"Bomb has been planted");
        for(Player joueurs : Bukkit.getServer().getOnlinePlayers())
            joueurs.playSound(joueurs.getLocation(), Sound.ANVIL_LAND, 2, 1);
        ptber.addMoney(50);
        this.setPlanted(true);
        timer.runTaskTimer(myPlugin, 0, 20);
    }

    public void remove() {
        this.setPlanted(false);
        if(this.getLoc() != null){
            this.getLoc().getBlock().setType(Material.AIR);
        }
    }
}
