package fr.karamouche.plantthebomb.objects.grenade;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ParticleEffect {

    public PacketPlayOutWorldParticles packet;

    public ParticleEffect(EnumParticle particle, Location loc, float xOffset, float yOffset, float zOffset, float speed,
                          int count) {
        float x = (float) loc.getX();
        float y = (float) loc.getY();
        float z = (float) loc.getZ();
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, x, y, z, xOffset, yOffset,
                zOffset, speed, count,  null);
        this.packet = packet;
    }

    public void sendToPlayer(Player p) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

}
