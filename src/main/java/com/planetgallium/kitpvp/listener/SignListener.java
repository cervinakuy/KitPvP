package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Resource;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.planetgallium.kitpvp.util.Resources;

public class SignListener implements Listener {

	private Arena arena;
	private Resource messages;
	private Resource signs;

	public SignListener(Game plugin) {
		this.arena = plugin.getArena();
		this.messages = plugin.getResources().getMessages();
		this.signs = plugin.getResources().getSigns();
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {

		if (e.getLine(0).equalsIgnoreCase("[KitPvP]")) {
			
			Player p = e.getPlayer();
			
			if (e.getLine(1).equalsIgnoreCase("kit")) {
				
				String kitName = e.getLine(2);
				
				renameSign(e, "Signs.Kit", "%kit%", kitName);
				saveSign("Kit", e.getBlock().getLocation(), "Kit", kitName);
				
				p.sendMessage(messages.getString("Messages.Other.Sign"));
				
			} else if (e.getLine(1).equalsIgnoreCase("clear")) {
				
				renameSign(e, "Signs.Clear", null, null);
				saveSign("Clear", e.getBlock().getLocation(), null, null);
				
				p.sendMessage(messages.getString("Messages.Other.Sign"));
				
			} else if (e.getLine(1).equalsIgnoreCase("menu")) {
				
				renameSign(e, "Signs.Menu", null, null);
				saveSign("Menu", e.getBlock().getLocation(), null, null);
				
				p.sendMessage(messages.getString("Messages.Other.Sign"));
				
			} else if (e.getLine(1).equalsIgnoreCase("stats")) {
				
				renameSign(e, "Signs.Stats", null, null);
				saveSign("Stats", e.getBlock().getLocation(), null, null);
				
				p.sendMessage(messages.getString("Messages.Other.Sign"));
				
			} else if (e.getLine(1).equalsIgnoreCase("refill")) {
				
				renameSign(e, "Signs.Refill", null, null);
				saveSign("Refill", e.getBlock().getLocation(), null, null);
				
				p.sendMessage(messages.getString("Messages.Other.Sign"));
				
			} else if (e.getLine(1).equalsIgnoreCase("arena")) {

				String arenaName = e.getLine(2);

				renameSign(e, "Signs.Arena", "%arena%", arenaName);
				saveSign("Arena", e.getBlock().getLocation(), "Arena", arenaName);

				p.sendMessage(messages.getString("Messages.Other.Sign"));

			}
			
		}
		
	}
	
	@EventHandler
	public void onSignUse(PlayerInteractEvent e) {
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			if (e.getClickedBlock().getState() instanceof Sign) {
				
				Sign sign = (Sign) e.getClickedBlock().getState();
				int signIndex = findSign(sign.getLocation());
				Player p = e.getPlayer();

				if (sign.getLine(0).equals(signs.getString("Signs.Kit.Line-1")) ||
						sign.getLine(0).equals(signs.getString("Signs.Clear.Line-1")) ||
						sign.getLine(0).equals(signs.getString("Signs.Menu.Line-1")) ||
						sign.getLine(0).equals(signs.getString("Signs.Stats.Line-1")) ||
						sign.getLine(0).equals(signs.getString("Signs.Refill.Line-1")) ||
						sign.getLine(0).equals(signs.getString("Signs.Arena.Line-1"))) {

					if (signsMatch(sign.getLines(), "Signs.Menu", null, null)) {

						arena.getMenus().getKitMenu().open(p);
						
					} else if (signsMatch(sign.getLines(), "Signs.Clear", null, null)) {
						
						p.performCommand("kp clear");
						
					} else if (signsMatch(sign.getLines(), "Signs.Stats", null, null)) {
						
						p.performCommand("kp stats");
						
					} else if (signsMatch(sign.getLines(), "Signs.Refill", null, null)) {
						
						arena.getMenus().getRefillMenu().open(p);
						
					} else if (signsMatch(sign.getLines(), "Signs.Kit", "%kit%",
							signs.getString("Signs.Locations." + signIndex + ".Kit"))) {
						
						p.performCommand("kp kit " + signs.getString("Signs.Locations." + signIndex + ".Kit"));
						
					} else if (signsMatch(sign.getLines(), "Signs.Arena", "%arena%",
							signs.getString("Signs.Locations." + signIndex + ".Arena"))) {

						p.performCommand("kp arena " + signs.getString("Signs.Locations." + signIndex + ".Arena"));

					}

					
				}
				
			}
			
		}
		
	}

	@EventHandler
	public void onSignBreak(BlockBreakEvent e) {

		deleteSign(e.getBlock().getLocation());

	}
	
	private void saveSign(String type, Location location, String option, String optionValue) {
		
		int start = findStart();
		
		signs.set("Signs.Locations." + start + ".Type", type);
		signs.set("Signs.Locations." + start + "." + option, optionValue != null ? optionValue : null);
		signs.set("Signs.Locations." + start + ".World", location.getWorld().getName());
		signs.set("Signs.Locations." + start + ".X", location.getBlock().getX());
		signs.set("Signs.Locations." + start + ".Y", location.getBlock().getY());
		signs.set("Signs.Locations." + start + ".Z", location.getBlock().getZ());
		
		signs.save();
		
	}
	
	private void renameSign(SignChangeEvent e, String path, String placeholder, String placeholderValue) {
		
		for (int i = 0; i < 3; i++) {

			String line = signs.getString(path + ".Line-" + (i + 1));

			if (placeholder != null && placeholderValue != null)
				line = line.replace(placeholder, placeholderValue);

			e.setLine(i, line);
			
		}
		
	}

	private boolean signsMatch(String[] sign, String path, String placeholder, String placeholderValue) {

		for (int i = 0; i < 3; i++) {

			if (sign[i] != null && sign[i].length() > 0) {

				String line = signs.getString(path + ".Line-" + (i + 1));

				if (placeholder != null && placeholderValue != null)
					line = line.replace(placeholder, placeholderValue);

				if (!sign[i].equals(line)) {

					return false;

				}

			}

		}

		return true;

	}
	
	private int findSign(Location location) {
		
		for (int i = 1; i <= 100; i++) {
			
			if (location.getWorld().getName().equals(signs.getString("Signs.Locations." + i + ".World"))) {
				
				if (location.getX() == signs.getInt("Signs.Locations." + i + ".X")) {
					
					if (location.getY() == signs.getInt("Signs.Locations." + i + ".Y")) {
						
						if (location.getZ() == signs.getInt("Signs.Locations." + i + ".Z")) {
							
							return i;
							
						}
						
					}
					
				}
				
			}
			
		}
		
		return 0;
		
	}
	
	private int findStart() {
		
		for (int i = 1; i <= 100; i++) {
			
			if (!signs.contains("Signs.Locations." + i)) {
				
				return i;
				
			}
			
		}
		
		return 0;
		
	}

	private void deleteSign(Location location) {

		signs.set("Signs.Locations." + findSign(location), null);
		signs.save();

	}
	
}
