package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.game.Arena;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.planetgallium.kitpvp.menu.KitMenu;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Menu;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.XMaterial;

public class SignListener implements Listener {

	private Arena arena;
	private Resources resources;
	
	public SignListener(Arena arena, Resources resources) {
		this.arena = arena;
		this.resources = resources;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {

		if (e.getLine(0).equalsIgnoreCase("[KitPvP]")) {
			
			Player p = e.getPlayer();
			
			if (e.getLine(1).equalsIgnoreCase("kit")) {
				
				String kitName = e.getLine(2);
				
				renameSign(e, "Signs.Kit", "%kit%", kitName);
				saveSign("Kit", e.getBlock().getLocation(), "Kit", kitName);
				
				p.sendMessage(resources.getMessages().getString("Messages.Other.Sign"));
				
			} else if (e.getLine(1).equalsIgnoreCase("clear")) {
				
				renameSign(e, "Signs.Clear", null, null);
				saveSign("Clear", e.getBlock().getLocation(), null, null);
				
				p.sendMessage(resources.getMessages().getString("Messages.Other.Sign"));
				
			} else if (e.getLine(1).equalsIgnoreCase("menu")) {
				
				renameSign(e, "Signs.Menu", null, null);
				saveSign("Menu", e.getBlock().getLocation(), null, null);
				
				p.sendMessage(resources.getMessages().getString("Messages.Other.Sign"));
				
			} else if (e.getLine(1).equalsIgnoreCase("stats")) {
				
				renameSign(e, "Signs.Stats", null, null);
				saveSign("Stats", e.getBlock().getLocation(), null, null);
				
				p.sendMessage(resources.getMessages().getString("Messages.Other.Sign"));
				
			} else if (e.getLine(1).equalsIgnoreCase("refill")) {
				
				renameSign(e, "Signs.Refill", null, null);
				saveSign("Refill", e.getBlock().getLocation(), null, null);
				
				p.sendMessage(resources.getMessages().getString("Messages.Other.Sign"));
				
			} else if (e.getLine(1).equalsIgnoreCase("arena")) {

				String arenaName = e.getLine(2);

				renameSign(e, "Signs.Arena", "%arena%", arenaName);
				saveSign("Arena", e.getBlock().getLocation(), "Arena", arenaName);

				p.sendMessage(resources.getMessages().getString("Messages.Other.Sign"));

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

				if (sign.getLine(0).equals(resources.getSigns().getString("Signs.Kit.Line-1")) ||
						sign.getLine(0).equals(resources.getSigns().getString("Signs.Clear.Line-1")) ||
						sign.getLine(0).equals(resources.getSigns().getString("Signs.Menu.Line-1")) ||
						sign.getLine(0).equals(resources.getSigns().getString("Signs.Stats.Line-1")) ||
						sign.getLine(0).equals(resources.getSigns().getString("Signs.Refill.Line-1")) ||
						sign.getLine(0).equals(resources.getSigns().getString("Signs.Arena.Line-1"))) {

					if (signsMatch(sign.getLines(), "Signs.Menu", null, null)) {

						arena.getMenus().getKitMenu().open(p);
						
					} else if (signsMatch(sign.getLines(), "Signs.Clear", null, null)) {
						
						p.performCommand("kp clear");
						
					} else if (signsMatch(sign.getLines(), "Signs.Stats", null, null)) {
						
						p.performCommand("kp stats");
						
					} else if (signsMatch(sign.getLines(), "Signs.Refill", null, null)) {
						
						arena.getMenus().getRefillMenu().open(p);
						
					} else if (signsMatch(sign.getLines(), "Signs.Kit", "%kit%",
							resources.getSigns().getString("Signs.Locations." + signIndex + ".Kit"))) {
						
						p.performCommand("kp kit " + resources.getSigns().getString("Signs.Locations." + signIndex + ".Kit"));
						
					} else if (signsMatch(sign.getLines(), "Signs.Arena", "%arena%",
							resources.getSigns().getString("Signs.Locations." + signIndex + ".Arena"))) {

						p.performCommand("kp arena " + resources.getSigns().getString("Signs.Locations." + signIndex + ".Arena"));

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
		
		resources.getSigns().set("Signs.Locations." + start + ".Type", type);
		resources.getSigns().set("Signs.Locations." + start + "." + option, optionValue != null ? optionValue : null);
		resources.getSigns().set("Signs.Locations." + start + ".World", location.getWorld().getName());
		resources.getSigns().set("Signs.Locations." + start + ".X", location.getBlock().getX());
		resources.getSigns().set("Signs.Locations." + start + ".Y", location.getBlock().getY());
		resources.getSigns().set("Signs.Locations." + start + ".Z", location.getBlock().getZ());
		
		resources.getSigns().save();
		
	}
	
	private void renameSign(SignChangeEvent e, String path, String placeholder, String placeholderValue) {
		
		for (int i = 0; i < 3; i++) {

			String line = resources.getSigns().getString(path + ".Line-" + (i + 1));

			if (placeholder != null && placeholderValue != null)
				line = line.replace(placeholder, placeholderValue);

			e.setLine(i, line);
			
		}
		
	}

	private boolean signsMatch(String[] sign, String path, String placeholder, String placeholderValue) {

		for (int i = 0; i < 3; i++) {

			if (sign[i] != null && sign[i].length() > 0) {

				String line = resources.getSigns().getString(path + ".Line-" + (i + 1));

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
			
			if (location.getWorld().getName().equals(resources.getSigns().getString("Signs.Locations." + i + ".World"))) {
				
				if (location.getX() == resources.getSigns().getInt("Signs.Locations." + i + ".X")) {
					
					if (location.getY() == resources.getSigns().getInt("Signs.Locations." + i + ".Y")) {
						
						if (location.getZ() == resources.getSigns().getInt("Signs.Locations." + i + ".Z")) {
							
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
			
			if (!resources.getSigns().contains("Signs.Locations." + i)) {
				
				return i;
				
			}
			
		}
		
		return 0;
		
	}

	private void deleteSign(Location location) {

		resources.getSigns().set("Signs.Locations." + findSign(location), null);
		resources.getSigns().save();

	}
	
}
