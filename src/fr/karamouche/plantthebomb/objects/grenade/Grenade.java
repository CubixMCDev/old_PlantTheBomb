package fr.karamouche.plantthebomb.objects.grenade;

import fr.karamouche.plantthebomb.Main;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class Grenade {
    final Main myPlugin;
    final Location loc;
    boolean isLunch;
    BukkitRunnable run;

    public Grenade(Main myPlugin, Location loc){
        this.myPlugin = myPlugin;
        this.loc = loc;
        this.isLunch = false;
        this.run = null;
    }

    public void setEffect(){

    }

    public void removeEffect(){

    }

    public void setLunch(boolean bool){
        this.isLunch = bool;
    }

    public boolean hasLunch(){
        return this.isLunch;
    }

}
