package fr.karamouche.plantthebomb.scoreboard;

import fr.karamouche.plantthebomb.enums.Statut;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.karamouche.plantthebomb.Main;
import fr.karamouche.plantthebomb.objects.Game;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PersonalScoreboard {
    private final UUID uuid;
    private final ObjectiveSign objectiveSign;
    final Date date = new Date();
    private final String currentDate = (new SimpleDateFormat("dd-MM-yyyy")).format(date).replace("-", "/");
    private final Main myPlugin;

    PersonalScoreboard(Player player, Main main){
        uuid = player.getUniqueId();
        objectiveSign = new ObjectiveSign("sidebar","PlantTheBomb");
        reloadData();
        objectiveSign.addReceiver(player);
        this.myPlugin = main;
    }

    public void reloadData(){}

	public void setLines(String ip){
		Game game = myPlugin.getCurrentGame();
        objectiveSign.setDisplayName(ChatColor.YELLOW + "§ePlant§6The§cBomb");
        objectiveSign.setLine(0, ChatColor.GRAY + currentDate);
        objectiveSign.setLine(1, "§1");
		if (game.getStatut().equals(Statut.LOBBY) ||game.getStatut().equals(Statut.STARTING)){
            objectiveSign.setLine(2, "§e§lCoins");
            objectiveSign.setLine(3, "§7" + "0" /*myPlugin.getApi().getEcoManager().getBalanceCoins(player)*/);
            objectiveSign.setLine(4, "§2");
            objectiveSign.setLine(5, "§e§lVos Stats");
            objectiveSign.setLine(6, "§3Parties jouées: §b" + myPlugin.getStatManager().getGamePlayed(uuid));
            objectiveSign.setLine(7, "§3Victoires: §b" + myPlugin.getStatManager().getGamePlayed(uuid));
            objectiveSign.setLine(8, "§3Kills: §b" + + myPlugin.getStatManager().getGamePlayed(uuid));
            objectiveSign.setLine(9, "§3");
            objectiveSign.setLine(10, "§c§lDémarrage:");

            final int missingPlayers = game.getMaxPlayer()-game.getNbPlayers();
            if(missingPlayers == 1)
                objectiveSign.setLine(11, ChatColor.GRAY+"En attente de "+ChatColor.YELLOW+missingPlayers+ChatColor.GRAY+" joueur");
            else if(game.getStatut().equals(Statut.STARTING)) {
                //METTRE LE TIMER
                objectiveSign.setLine(11, ChatColor.GRAY+"En cours");
            }
            else
                objectiveSign.setLine(11, ChatColor.GRAY+"En attente de "+ChatColor.YELLOW+missingPlayers+ChatColor.GRAY+" joueurs");
            objectiveSign.setLine(12, "§4");
            objectiveSign.setLine(13, "§8» " + ip);
		}
        else{
            objectiveSign.setLine(2, "Score : §c" + game.getScoreT() + " §r- §b" + game.getScoreA());
            if(game.getStatut().equals(Statut.INGAME) && !game.getActualRound().getBomb().isPlanted())
                objectiveSign.setLine(3, "Round:" + ChatColor.GOLD + " " + game.getTimer());
            else if(game.getStatut().equals(Statut.INGAME) && game.getActualRound().getBomb().isPlanted() && !game.getActualRound().isFinish())
                objectiveSign.setLine(3, "Bombe:" + ChatColor.GOLD + " " + game.getActualRound().getBomb().getTimer());
            else{
                objectiveSign.setLine(3, "Victoire: " + game.getActualRound().getWinner().toString());
            }

            objectiveSign.setLine(4, "§2");
            objectiveSign.setLine(5, "§fArgent : §e" + game.getPtbers().get(uuid).getMoney());
            objectiveSign.setLine(6, "§fFrags : §d" + game.getPtbers().get(uuid).getKills());
            for(int i = 7 ; i< 13 ; i++) {
                objectiveSign.setLine(i, "§3");
                objectiveSign.setLine(13, ip);
            }

        }
        objectiveSign.updateLines();
    }

    public void onLogout(){
        objectiveSign.removeReceiver(Bukkit.getServer().getOfflinePlayer(uuid));
    }
}