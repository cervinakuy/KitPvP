package com.planetgallium.kitpvp.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitsCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
			
		if (sender instanceof Player) {

			Player p = (Player) sender;

			p.performCommand("kp kits");
			return true;

		}
		
		return false;
		
	}
	
}
