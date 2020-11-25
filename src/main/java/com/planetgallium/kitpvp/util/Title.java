package com.planetgallium.kitpvp.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Title {

	private Title1 title1;
	private Title2 title2;
	private boolean is19OrBelow;

	public Title() {
		this.title1 = new Title1("", "", 0, 0, 0);
		this.title2 = new Title2("", "", 0, 0, 0);
		this.is19OrBelow = Toolkit.versionToNumber() <= 19;

		title1.setTimingsToTicks();
		title2.setTimingsToTicks();
	}

	public void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {

		if (is19OrBelow) {

			title1.setTitle(title);
			title1.setSubtitle(subtitle);
			title1.setFadeInTime(fadeIn);
			title1.setStayTime(stay);
			title1.setFadeOutTime(fadeOut);
			title1.send(p);

		} else {

			title2.setTitle(title);
			title2.setSubtitle(subtitle);
			title2.setFadeInTime(fadeIn);
			title2.setStayTime(stay);
			title2.setFadeOutTime(fadeOut);
			title2.send(p);
			
		}
		
	}
	
}
