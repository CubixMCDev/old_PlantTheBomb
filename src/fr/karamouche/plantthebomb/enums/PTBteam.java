package fr.karamouche.plantthebomb.enums;

import org.bukkit.scoreboard.Team;

public enum PTBteam {
    TERRORISTE,
    ANTITERRORISTE;

    public String toString(){
        if(this.equals(TERRORISTE))
            return "§cTerroriste";
        else
            return "§bAntiterroriste";
    }

    public String getTag(){
        if(this.equals(TERRORISTE))
            return "§c";
        else
            return "§b";
    }
}
