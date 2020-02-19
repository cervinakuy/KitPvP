package com.planetgallium.kitpvp.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Title {

    private String version = null;

    public void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {

        if (version == null) {

            version = Bukkit.getVersion();

        }

        if (version.contains("1.8") || version.contains("1.9")) {

            Title1 title1 = new Title1(title, subtitle, fadeIn, stay, fadeOut);
            title1.setTimingsToTicks();
            title1.send(p);

        } else if (version.contains("1.10") || version.contains("1.11") || version.contains("1.12") || version.contains("1.13") || version.contains("1.14") || version.contains("1.15")) {

            Title2 title2 = new Title2(title, subtitle, fadeIn, stay, fadeOut);
            title2.setTimingsToTicks();
            title2.send(p);

        }

    }

}
