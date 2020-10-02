package fr.karamouche.plantthebomb;

import eu.cubixmc.com.sql.MysqlManager;
import fr.karamouche.plantthebomb.objects.Game;
import fr.karamouche.plantthebomb.objects.PTBer;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.UUID;

public class StatManager {

    private final Main myPlugin;
    private final MysqlManager sqlManager;
    public StatManager(Main myPlugin){
        this.myPlugin = myPlugin;
        sqlManager = myPlugin.getApi().getDatabaseManager();
    }

    //UPDATE TOUTE LES TABLES A LA FIN DE LA GAME
    public void gameHasEnd() {
        Game game = myPlugin.getCurrentGame();
        for(PTBer ptber : game.getPtbers().values()){
            UUID id = ptber.getPlayerID();
            updateKill(id, ptber.getKills());
            updateGamePlayed(id, 1);
            if(ptber.getTeam().equals(game.getGameWinner())){
                updateVictory(id, 1);
            }
        }
        for(PTBer ptber : game.getOfflineptbers().values()){
            UUID id = ptber.getPlayerID();
            updateKill(ptber.getPlayerID(), ptber.getKills());
            updateGamePlayed(id, 1);
        }
    }

    //UDATE LES PARTIES JOUEES

    public int getGamePlayed(UUID uuid) {
        CachedRowSet set = sqlManager.performQuery("SELECT playedgame FROM ptb WHERE uuid = ?", uuid);
        try {
            return set.getInt("playedgame");
        } catch (SQLException e) {
            System.out.println("Error performing SQL query: " + e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
            e.printStackTrace();
            return 0;
        }
    }

    public void updateGamePlayed(UUID uuid, int toAdd) {
        int oldCount = getGamePlayed(uuid);
        sqlManager.performUpdate("UPDATE ptb SET gameplayed = ? WHERE uuid = ?", oldCount + toAdd, uuid);
    }

    //UDATE LES VICTOIRES

    public int getVictory(UUID uuid) {
        CachedRowSet set = sqlManager.performQuery("SELECT victory FROM ptb WHERE uuid = ?", uuid);
        try {
            return set.getInt("victory");
        } catch (SQLException e) {
            System.out.println("Error performing SQL query: " + e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
            e.printStackTrace();
            return 0;
        }
    }

    public void updateVictory(UUID uuid, int toAdd) {
        int oldCount = getVictory(uuid);
        sqlManager.performUpdate("UPDATE ptb SET victory = ? WHERE uuid = ?", oldCount + toAdd, uuid);
    }

    //UDATE LES KILLS

    public int getKills(UUID uuid) {
        CachedRowSet set = sqlManager.performQuery("SELECT kills FROM ptb WHERE uuid = ?", uuid);
        try {
            return set.getInt("kills");
        } catch (SQLException e) {
            System.out.println("Error performing SQL query: " + e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
            e.printStackTrace();
            return 0;
        }
    }

    public void updateKill(UUID uuid, int toAdd) {
        int oldKills = getKills(uuid);
        sqlManager.performUpdate("UPDATE ptb SET kills = ? WHERE uuid = ?", oldKills + toAdd, uuid);
    }


}
