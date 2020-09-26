package fr.karamouche.plantthebomb.objects.grenade;

import fr.karamouche.plantthebomb.Main;
import javafx.scene.shape.Sphere;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/*
(x - a)² + (y - b)² + (z - c)² = r²  where (a, b, c) represents the center of the sphere .
r = 3

 */


public class Smoke extends Grenade{

    public Smoke(Main myPlugin, Location loc) {
        super(myPlugin, loc);
        Smoke smoke = this;
        this.run = new BukkitRunnable() {
            int time = 0;
            @Override
            public void run() {
                if(time == 15*10) {
                    smoke.removeEffect();
                }
                loc.add(0, 1, 0);
                for (double i = 0; i <= Math.PI; i += Math.PI / 30) {
                    double radius = 2.5*Math.sin(i);
                    double y = 2.5*Math.cos(i);
                    for (double a = 0; a < Math.PI * 2; a+= Math.PI / 30) {
                        double x = Math.cos(a) * radius;
                        double z = Math.sin(a) * radius;
                        loc.add(x, y, z);
                        ParticleEffect packet1 = new ParticleEffect(EnumParticle.CLOUD, loc,
                                0, 0, 0, 0, 1);
                        for(Player p : Bukkit.getOnlinePlayers())
                            packet1.sendToPlayer(p);
                        loc.subtract(x, y, z);

                        // display particle at 'location'.
                    }
                }

                loc.subtract(0, 1, 0);
                time++;
            }
        };
    }

    @Override
    public void setEffect() {
        super.setEffect();
        this.setLunch(true);
        this.run.runTaskTimer(myPlugin, 0, 2);


    }

    @Override
    public void removeEffect() {
        this.run.cancel();
        this.setLunch(false);
    }

}
