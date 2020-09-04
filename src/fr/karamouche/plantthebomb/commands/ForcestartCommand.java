package fr.karamouche.plantthebomb.commands;

import fr.karamouche.plantthebomb.Main;
import fr.karamouche.plantthebomb.objects.Game;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForcestartCommand implements CommandExecutor {
    Main myPlugin;
    public ForcestartCommand(Main main){
        this.myPlugin = main;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdS, String[] arg) {
        Game game = myPlugin.getCurrentGame();
        if(game.getNbPlayers()>1){
            myPlugin.getCurrentGame().start();
        }
        else
            sender.sendMessage(ChatColor.RED+"La game ne peut être lancée avec un joueur !");
        return false;
    }
}
