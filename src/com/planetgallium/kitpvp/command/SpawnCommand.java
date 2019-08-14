package com.planetgallium.kitpvp.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;

public class SpawnCommand implements CommandExecutor {
	
	private Resources resources;
	
	public SpawnCommand(Resources resources) {
		this.resources = resources;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		
		if (command.getName().equalsIgnoreCase("cspawn")) {
			
			if (sender instanceof Player) {
					
				Player p = (Player) sender;
				
				if (p.hasPermission("kp.command.spawn")) {
				
					p.performCommand("kp spawn");
					return true;
					
				} else {
					
					p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
					
				}
				
			}
			
		}
		
		return false;
		
	}
	
}
