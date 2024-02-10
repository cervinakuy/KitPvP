package com.planetgallium.kitpvp.util;

public class Cooldown {

	private int days;
	private int hours;
	private int minutes;
	private int seconds;
	
	public Cooldown(int days, int hours, int minutes, int seconds) {
		this.days = days;
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
	}
	
	public Cooldown(int seconds) {
		if (seconds / 86400 > 0) {
			this.days = seconds / 86400;
			seconds -= (days * 86400);
		}

		if (seconds / 3600 > 0) {
			this.hours = seconds / 3600;
			seconds -= (hours * 3600);
		}

		if (seconds / 60 > 0) {
			this.minutes = seconds / 60;
			seconds -= (minutes * 60);
		}

		if (seconds > 0) {
			this.seconds = seconds;
		}
	}

	public Cooldown(String formattedCooldown) {
		String[] units = formattedCooldown.split(":");

		for (int i = 0; i < units.length; i++) {
			if (units[i].toUpperCase().endsWith("D")) {
				days = Integer.parseInt(units[i].split("D")[0]);
			} else if (units[i].toUpperCase().endsWith("H")) {
				hours = Integer.parseInt(units[i].split("H")[0]);
			} else if (units[i].toUpperCase().endsWith("M")) {
				minutes = Integer.parseInt(units[i].split("M")[0]);
			} else if (units[i].toUpperCase().endsWith("S")) {
				seconds = Integer.parseInt(units[i].split("S")[0]);
			}
		}
	}

	public String formatted(boolean condensed) {
		if (condensed) {
			String condensedCooldown = "";

			if (getDays() != 0) condensedCooldown += getDays() + "D:";
			if (getHours() != 0) condensedCooldown += getHours() + "H:";
			if (getMinutes() != 0) condensedCooldown += getMinutes() + "M:";
			if (getSeconds() != 0) condensedCooldown += getSeconds() + "S:";

			if (condensedCooldown.endsWith(":")) {
				condensedCooldown = condensedCooldown.substring(0, condensedCooldown.length() - 1);
			} else if (condensedCooldown.equals("")) {
				condensedCooldown = null;
			}

			return condensedCooldown;
		} else {
			String longCooldown = "";

			if (getDays() != 0) longCooldown += (getDays() + " days ");
			if (getHours() != 0) longCooldown += (getHours() + " hours ");
			if (getMinutes() != 0) longCooldown += (getMinutes() + " minutes ");
			if (getSeconds() != 0) longCooldown += (getSeconds() + " seconds");

			if (longCooldown.length() > 0 && longCooldown.charAt(longCooldown.length() - 1) == ' ') {
				longCooldown = longCooldown.substring(0, longCooldown.length() - 1);
			}

			return longCooldown;
		}
	}

	public int toSeconds() {
		int daysToSeconds = days * 86400;
		int hoursToSeconds = hours * 3600;
		int minutesToSeconds = minutes * 60;
		return daysToSeconds + hoursToSeconds + minutesToSeconds + seconds;
	}
	
	public int getDays() { return days; }
	
	public int getHours() { return hours; }
	
	public int getMinutes() { return minutes; }
	
	public int getSeconds() { return seconds; }
	
}
