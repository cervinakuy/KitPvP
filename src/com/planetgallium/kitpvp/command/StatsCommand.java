package com.planetgallium.kitpvp.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		
		if (command.getName().equalsIgnoreCase("cstats")) {
			
			if (sender instanceof Player) {
					
				Player p = (Player) sender;
					
				p.performCommand("kp stats");
				return true;
				
			}
			
		}
		
		return false;
		
	}
	
}
