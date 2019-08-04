package com.planetgallium.kitpvp.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		
		if (command.getName().equalsIgnoreCase("cspawn")) {
			
			if (sender instanceof Player) {
					
				Player p = (Player) sender;
				
				if (p.hasPermission("kp.command.spawn")) {
				
					p.performCommand("kp spawn");
					return true;
					
				}
				
			}
			
		}
		
		return false;
		
	}
	
}
