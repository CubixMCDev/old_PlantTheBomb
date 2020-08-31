package fr.karamouche.plantthebomb.enums;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public enum Spawns {
	LOBBY(1072, 106, 1015, "CSGO"),
	TERRO(1040, 66, 988, "CSGO"),
	ATERRO(1058, 60, 922, "CSGO");
	
	final int X;
	final int Y;
	final int Z;
	final String world;
	
	Spawns(int X, int Y, int Z, String world) {
		this.X = X;
		this.Y = Y;
		this.Z = Z;
		this.world = world;
	}
	
	public Location toLocation(){
		return new Location(Bukkit.getWorld(this.world), this.X, this.Y, this.Z);
	}
}
