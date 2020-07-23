package com.planetgallium.kitpvp.listener;

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

	private Resources resources;
	
	public SignListener(Resources resources) {
		this.resources = resources;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {

		if (e.getLine(0).equalsIgnoreCase("[KitPvP]")) {
			
			Player p = e.getPlayer();
			
			if (e.getLine(1).equalsIgnoreCase("kit")) {
				
				String kit = e.getLine(2);
				
				renameSign(e, "Signs.Kit", kit);
				saveSign("Kit", e.getBlock().getLocation(), kit);
				
				p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Other.Sign").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
				
			} else if (e.getLine(1).equalsIgnoreCase("clear")) {
				
				renameSign(e, "Signs.Clear", null);
				saveSign("Clear", e.getBlock().getLocation(), null);
				
				p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Other.Sign").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
				
			} else if (e.getLine(1).equalsIgnoreCase("menu")) {
				
				renameSign(e, "Signs.Menu", null);
				saveSign("Menu", e.getBlock().getLocation(), null);
				
				p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Other.Sign").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
				
			} else if (e.getLine(1).equalsIgnoreCase("stats")) {
				
				renameSign(e, "Signs.Stats", null);
				saveSign("Stats", e.getBlock().getLocation(), null);
				
				p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Other.Sign").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
				
			} else if (e.getLine(1).equalsIgnoreCase("refill")) {
				
				renameSign(e, "Signs.Refill", null);
				saveSign("Refill", e.getBlock().getLocation(), null);
				
				p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Other.Sign").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
				
			}
			
		}
		
	}
	
	@EventHandler
	public void onSignUse(PlayerInteractEvent e) {
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			if (e.getClickedBlock().getState() instanceof Sign) {
				
				Sign sign = (Sign) e.getClickedBlock().getState();
				Player p = e.getPlayer();

				if (sign.getLine(0).equals(resources.getSigns().getString("Signs.Kit.Line-1")) ||
					sign.getLine(0).equals(resources.getSigns().getString("Signs.Clear.Line-1")) ||
					sign.getLine(0).equals(resources.getSigns().getString("Signs.Menu.Line-1")) ||
					sign.getLine(0).equals(resources.getSigns().getString("Signs.Stats.Line-1")) ||
					sign.getLine(0).equals(resources.getSigns().getString("Signs.Refill.Line-1"))) {

					if (signsMatch(sign.getLines(), "Signs.Menu", null)) {
						
						KitMenu menu = new KitMenu(resources);
						menu.create(p);
						
					} else if (signsMatch(sign.getLines(), "Signs.Clear", null)) {
						
						p.performCommand("kp clear");
						
					} else if (signsMatch(sign.getLines(), "Signs.Stats", null)) {
						
						p.performCommand("kp stats");
						
					} else if (signsMatch(sign.getLines(), "Signs.Refill", null)) {
						
						Menu menu = new Menu("Refill", null, 54);
						
						for (int i = 0; i < menu.getSize(); i++) {
							
							menu.addItem(Config.getS("Soups.Name"), XMaterial.MUSHROOM_STEW.parseMaterial().get(), Config.getC().getStringList("Soups.Lore"), i);
							
						}
						
						menu.openMenu(p);
						
					} else if (signsMatch(sign.getLines(), "Signs.Kit", resources.getSigns().getString("Signs.Locations." + findSign(sign.getLocation()) + ".Kit"))) {
						
						p.performCommand("kp kit " + resources.getSigns().getString("Signs.Locations." + findSign(sign.getLocation()) + ".Kit"));
						
					}
					
				}
				
			}
			
		}
		
	}

	@EventHandler
	public void onSignBreak(BlockBreakEvent e) {

		deleteSign(e.getBlock().getLocation());

	}
	
	private void saveSign(String type, Location location, String kit) {
		
		int start = findStart();
		
		resources.getSigns().set("Signs.Locations." + start + ".Type", type);
		resources.getSigns().set("Signs.Locations." + start + ".Kit", kit != null ? kit : null);
		resources.getSigns().set("Signs.Locations." + start + ".World", location.getWorld().getName());
		resources.getSigns().set("Signs.Locations." + start + ".X", location.getBlock().getX());
		resources.getSigns().set("Signs.Locations." + start + ".Y", location.getBlock().getY());
		resources.getSigns().set("Signs.Locations." + start + ".Z", location.getBlock().getZ());
		
		resources.getSigns().save();
		
	}
	
	private void renameSign(SignChangeEvent e, String path, String kit) {
		
		for (int i = 0; i < 3; i++) {
			
			e.setLine(i, Config.tr(resources.getSigns().getString(path + ".Line-" + (i + 1)).replace("%kit%", kit != null ? kit : "")));
			
		}
		
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
	
	private boolean signsMatch(String[] sign, String path, String kit) {
		
		for (int i = 0; i < 3; i++) {
			
			if (sign[i] != null && sign[i].length() > 0) {
				
				if (!sign[i].equals(resources.getSigns().getString(path + ".Line-" + (i + 1)).replace("%kit%", kit != null ? kit : ""))) {
					
					return false;
					
				}
				
			}
			
		}
		
		return true;
		
	}

	private void deleteSign(Location location) {

		resources.getSigns().set("Signs.Locations." + findSign(location), null);
		resources.getSigns().save();

	}
	
}
