package com.planetgallium.kitpvp.api.Managers;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.game.PlayerData;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;
import org.bukkit.Bukkit;

import java.util.UUID;

public class UserManager {

    private Game game;
    private Resources resources;
    private Arena arena;

    public UserManager() {
        game = Game.getInstance();
        resources = game.getResources();
        arena = game.getArena();
    }

    public void createPlayer(String username, UUID uuid, int level, int experience, int kills, int deaths) {
        if (!game.getDatabase().isEnabled()) {

            if (!resources.getStats().contains("Stats.Players." + uuid + ".Username")) {

                resources.getStats().set("Stats.Players." + uuid + ".Username", username);
                resources.getStats().set("Stats.Players." + uuid + ".Level", level);
                resources.getStats().set("Stats.Players." + uuid + ".Experience", experience);
                resources.getStats().set("Stats.Players." + uuid + ".Kills", kills);
                resources.getStats().set("Stats.Players." + uuid + ".Deaths", deaths);

                resources.save();

            } else {
                Bukkit.getConsoleSender().sendMessage(Config.tr("%prefix% &cUnable to create player &e" + username + " &cdue to player already existing"));
            }

        } else {

            if (!game.getDatabase().getCache().containsKey(uuid) || game.getDatabase().getCache().get(uuid) == null) {

                PlayerData playerData = new PlayerData(username, level, experience, kills, deaths);
                game.getDatabase().getCache().put(uuid, playerData);

            } else {
                Bukkit.getConsoleSender().sendMessage(Config.tr("%prefix% &cUnable to create player &e" + username + " &cdue to player already existing"));
            }

        }
    }

    public void addKills(UUID uuid, int amount) {
        int newAmt = amount;

        for (int i = amount; i <= 0; i--) {
            arena.getStats().addKill(uuid);
        }
    }

    public void removeKills(UUID uuid, int amount) {
        int newAmt = amount;

        for (int i = amount; i <= 0; i--) {
            if (arena.getStats().getKills(uuid) - 1 >= 0) {
                arena.getStats().removeKill(uuid);
            }
        }
    }

    public void setKills(UUID uuid, int amount) {
        if (amount > arena.getStats().getKills(uuid)) {
            int newAmt = amount - arena.getStats().getKills(uuid);
            for (int i = newAmt; i <= 0; i--) {
                arena.getStats().removeKill(uuid);
            }
        } else if (amount < arena.getStats().getKills(uuid)) {
            int newAmt = arena.getStats().getKills(uuid) - amount;
            for (int i = newAmt; i <= 0; i--) {
                arena.getStats().addKill(uuid);
            }
        }
    }

    public void addDeaths(UUID uuid, int amount) {
        int newAmt = amount;

        for (int i = amount; i <= 0; i--) {
            arena.getStats().addDeath(uuid);
        }
    }

    public void removeDeaths(UUID uuid, int amount) {
        int newAmt = amount;

        for (int i = amount; i <= 0; i--) {
            if (arena.getStats().getDeaths(uuid) - 1 >= 0) {
                arena.getStats().removeDeath(uuid);
            }
        }
    }

    public void setDeaths(UUID uuid, int amount) {
        if (amount > arena.getStats().getDeaths(uuid)) {
            int newAmt = amount - arena.getStats().getDeaths(uuid);
            for (int i = newAmt; i <= 0; i--) {
                arena.getStats().removeDeath(uuid);
            }
        } else if (amount < arena.getStats().getDeaths(uuid)) {
            int newAmt = arena.getStats().getDeaths(uuid) - amount;
            for (int i = newAmt; i <= 0; i--) {
                arena.getStats().addDeath(uuid);
            }
        }
    }

    public void addExperience(UUID uuid, int amount) { arena.getStats().addExperience(uuid, amount); }

    public void removeExperience(UUID uuid, int amount) { arena.getStats().removeExperience(uuid, amount); }

    public void setExperience(UUID uuid, int amount) { arena.getStats().setExperience(uuid, amount); }

    public void addLevels(UUID uuid, int amount) { arena.getStats().setLevel(uuid, arena.getStats().getLevel(uuid) + amount); }

    public void removeLevels(UUID uuid, int amount) {
        int realAmt;
        if (arena.getStats().getLevel(uuid) - amount < 0) {
            realAmt = 0;
        } else {
            realAmt = arena.getStats().getLevel(uuid) - amount;
        }
        arena.getStats().setLevel(uuid, realAmt);
    }

    public void setLevel(UUID uuid, int amount) {
        int realAmt;
        if (amount < 0) {
            realAmt = 0;
        } else {
            realAmt = amount;
        }
        arena.getStats().setLevel(uuid, realAmt);
    }

    public String getUsername(UUID uuid) { return arena.getStats().getUsername(uuid); }

    public int getKills(UUID uuid) { return arena.getStats().getKills(uuid); }

    public int getDeaths(UUID uuid) { return arena.getStats().getDeaths(uuid); }

    public int getExperience(UUID uuid) { return arena.getStats().getExperience(uuid); }

    public int getLevel(UUID uuid) { return arena.getStats().getLevel(uuid); }

}
