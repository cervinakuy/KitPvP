package com.planetgallium.kitpvp.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;

public class KitCommand implements CommandExecutor {

	private Resources resources;
	
	public KitCommand(Resources resources) {
		this.resources = resources;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		
		if (command.getName().equalsIgnoreCase("ckit")) {
			
			if (sender instanceof Player) {
					
				Player p = (Player) sender;
					
				if (args.length == 0) {
					
					p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Arguments")));
					return true;
					
				} else if (args.length == 1) {
					
					p.performCommand("kp kit " + args[0]);
					return true;
					
				} else if (args.length == 2) {

					p.performCommand("kp kit " + args[0] + " " + args[1]);
					return true;

				}
				
			}
			
		}
		
		return false;
		
	}
	
}
