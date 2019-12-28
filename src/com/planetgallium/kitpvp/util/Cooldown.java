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
	
	@Override
	public String toString() {
		
		String result = "";
		
		if (getDays() != 0) result = result + (getDays() + " days ");
		if (getHours() != 0) result = result + (getHours() + " hours ");
		if (getMinutes() != 0) result = result + (getMinutes() + " minutes ");
		if (getSeconds() != 0) result = result + (getSeconds() + " seconds");
		
		if (result.length() > 0 && result.charAt(result.length() - 1) == ' ') {
			
			result = result.substring(0, result.length() - 1);
			
		}
		
		return result;
		
	}
	
	public int getDays() { return days; }
	
	public int getHours() { return hours; }
	
	public int getMinutes() { return minutes; }
	
	public int getSeconds() { return seconds; }
	
}
