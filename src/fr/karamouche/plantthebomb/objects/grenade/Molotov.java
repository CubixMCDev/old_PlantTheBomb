package fr.karamouche.plantthebomb.objects.grenade;

import fr.karamouche.plantthebomb.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class Molotov extends Grenade{
    public Molotov(Main myPlugin, Location loc) {
        super(myPlugin, loc);
        Molotov molo = this;
        this.run = new BukkitRunnable() {
            int time = 0;
            Block block;
            @Override
            public void run() {
                if(time == 15*10) {
                    molo.removeEffect();
                }
                if(time == 0) {
                    for(int i = -2; i <= 2; i++){
                        for(int j = -2; j <= 2; j++){

                            loc.add(i, 0, j);
                            block = Bukkit.getWorld("CSGO").getBlockAt(loc);
                            loc.subtract(i, 0, j);

                            if(block.getType() == null || block.getType().equals(Material.AIR))
                                block.setType(Material.FIRE);
                        }
                    }
                }
                time++;
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                for(int i = -2; i <= 2; i++){
                    for(int j = -2; j <= 2; j++){

                        loc.add(i, 0, j);
                        block = Bukkit.getWorld("CSGO").getBlockAt(loc);
                        loc.subtract(i, 0, j);

                        if(block.getType().equals(Material.FIRE))
                            block.setType(Material.AIR);
                    }
                }
                super.cancel();
            }
        };
    }

    @Override
    public void setEffect() {
        this.setLunch(true);
        this.run.runTaskTimer(myPlugin, 0, 2);
    }

    @Override
    public void removeEffect() {
        this.run.cancel();
        this.setLunch(false);
    }


}
