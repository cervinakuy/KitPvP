package com.planetgallium.kitpvp.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Title {

	public void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {

		if (Toolkit.versionToNumber() <= 19) {

			Title1 title1 = new Title1(title, subtitle, fadeIn, stay, fadeOut);
			title1.setTimingsToTicks();
			title1.send(p);

		} else {
			
			Title2 title2 = new Title2(title, subtitle, fadeIn, stay, fadeOut);
			title2.setTimingsToTicks();
			title2.send(p);
			
		}
		
	}
	
}
