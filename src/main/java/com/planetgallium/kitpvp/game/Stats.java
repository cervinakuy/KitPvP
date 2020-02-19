package com.planetgallium.kitpvp.game;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;

import java.util.UUID;

public class Stats {

    private Game game;
    private Resources resources;

    public Stats(Game game, Resources resources) {
        this.game = game;
        this.resources = resources;
    }

    public void createPlayer(String username, UUID uuid) {

        if (!game.getDatabase().isEnabled()) {

            if (!resources.getStats().contains("Stats.Players." + uuid + ".Username")) {

                resources.getStats().set("Stats.Players." + uuid + ".Username", username);
                resources.getStats().set("Stats.Players." + uuid + ".Level", 0);
                resources.getStats().set("Stats.Players." + uuid + ".Experience", 0);
                resources.getStats().set("Stats.Players." + uuid + ".Kills", 0);
                resources.getStats().set("Stats.Players." + uuid + ".Deaths", 0);

                resources.save();

            }

        } else {

            if (!game.getDatabase().getCache().containsKey(uuid) || game.getDatabase().getCache().get(uuid) == null) {

                PlayerData playerData = new PlayerData(username, 0, 0, 0, 0);
                game.getDatabase().getCache().put(uuid, playerData);

            }

        }

    }

    public void addKill(UUID uuid) {

        if (!game.getDatabase().isEnabled()) {

            if (resources.getStats().contains("Stats.Players." + uuid + ".Kills")) {

                resources.getStats().set("Stats.Players." + uuid + ".Kills", getKills(uuid) + 1);
                resources.save();

            }

        } else {

            PlayerData playerData = game.getDatabase().getCache().get(uuid);
            playerData.addKills(1);

        }

    }

    public void addDeath(UUID uuid) {

        if (!game.getDatabase().isEnabled()) {

            if (resources.getStats().contains("Stats.Players." + uuid + ".Deaths")) {

                resources.getStats().set("Stats.Players." + uuid + ".Deaths", getDeaths(uuid) + 1);
                resources.save();

            }

        } else {

            PlayerData playerData = game.getDatabase().getCache().get(uuid);
            playerData.addDeaths(1);

        }

    }

    public void addExperience(UUID uuid, int experience) {

        if (!game.getDatabase().isEnabled()) {

            if (resources.getStats().contains("Stats.Players." + uuid + ".Experience")) {

                resources.getStats().set("Stats.Players." + uuid + ".Experience", getExperience(uuid) + experience);
                resources.save();

            }

        } else {

            PlayerData playerData = game.getDatabase().getCache().get(uuid);
            playerData.setExperience(getExperience(uuid) + experience);

        }

    }

    public void removeExperience(UUID uuid, int experience) {

        if (!game.getDatabase().isEnabled()) {

            if (resources.getStats().contains("Stats.Players." + uuid + ".Experience")) {

                if (resources.getStats().getInt("Stats.Players." + uuid + ".Experience") > experience) {

                    resources.getStats().set("Stats.Players." + uuid + ".Experience", getExperience(uuid) - experience);
                    resources.save();

                }

            }

        } else {

            PlayerData playerData = game.getDatabase().getCache().get(uuid);

            if (playerData.getExperience() > experience) {

                playerData.setExperience(getExperience(uuid) - experience);

            }

        }

    }

    public void setLevel(UUID uuid, int level) {

        if (!game.getDatabase().isEnabled()) {

            if (resources.getStats().contains("Stats.Players." + uuid + ".Level")) {

                resources.getStats().set("Stats.Players." + uuid + ".Level", level);
                resources.save();

            }

        } else {

            PlayerData playerData = game.getDatabase().getCache().get(uuid);
            playerData.setLevel(level);

        }

    }

    public void setExperience(UUID uuid, int experience) {

        if (!game.getDatabase().isEnabled()) {

            if (resources.getStats().contains("Stats.Players." + uuid + ".Experience")) {

                resources.getStats().set("Stats.Players." + uuid + ".Experience", experience);
                resources.save();

            }

        } else {

            PlayerData playerData = game.getDatabase().getCache().get(uuid);
            playerData.setExperience(experience);

        }

    }

    public int getKills(UUID uuid) {

        if (!game.getDatabase().isEnabled()) {

            if (resources.getStats().contains("Stats.Players." + uuid + ".Kills")) {

                return resources.getStats().getInt("Stats.Players." + uuid + ".Kills");

            }

        } else {

            PlayerData playerData = game.getDatabase().getCache().get(uuid);
            return playerData.getKills();

        }

        return 0;

    }

    public int getDeaths(UUID uuid) {

        if (!game.getDatabase().isEnabled()) {

            if (resources.getStats().contains("Stats.Players." + uuid + ".Deaths")) {

                return resources.getStats().getInt("Stats.Players." + uuid + ".Deaths");

            }

        } else {

            PlayerData playerData = game.getDatabase().getCache().get(uuid);
            return playerData.getDeaths();

        }

        return 0;

    }

    public double getKDRatio(UUID uuid) {

        if (getDeaths(uuid) != 0) {

            double divided = (double) getKills(uuid) / getDeaths(uuid);
            return Toolkit.round(divided, 2);

        }

        return 0.00;

    }

    public int getExperience(UUID uuid) {

        if (!game.getDatabase().isEnabled()) {

            if (resources.getStats().contains("Stats.Players." + uuid + ".Experience")) {

                return resources.getStats().getInt("Stats.Players." + uuid + ".Experience");

            }

        } else {

            PlayerData playerData = game.getDatabase().getCache().get(uuid);
            return playerData.getExperience();

        }

        return 0;

    }

    public int getLevel(UUID uuid) {

        if (!game.getDatabase().isEnabled()) {

            if (resources.getStats().contains("Stats.Players." + uuid + ".Level")) {

                return resources.getStats().getInt("Stats.Players." + uuid + ".Level");

            }

        } else {

            PlayerData playerData = game.getDatabase().getCache().get(uuid);
            return playerData.getLevel();

        }

        return 0;

    }

}
