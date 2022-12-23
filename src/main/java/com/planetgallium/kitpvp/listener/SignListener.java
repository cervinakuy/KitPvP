package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignListener implements Listener {

	private final Arena arena;
	private final Resource messages;
	private final Resource signs;
	private final List<String> regularSignTypes;
	private final List<String> customSignTypes;

	public SignListener(Game plugin) {
		this.arena = plugin.getArena();
		this.messages = plugin.getResources().getMessages();
		this.signs = plugin.getResources().getSigns();
		this.regularSignTypes = new ArrayList<>(Arrays.asList("clear", "menu", "stats", "refill"));
		this.customSignTypes = new ArrayList<>(Arrays.asList("kit", "arena"));
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getLine(0) != null && e.getLine(0).equalsIgnoreCase("[KitPvP]")) {
			Player p = e.getPlayer();
			String signType = e.getLine(1);

			if (regularSignTypes.contains(signType)) {
				renameSign(e, "Signs." + Toolkit.capitalizeFirstChar(signType), null, null);
				p.sendMessage(messages.fetchString("Messages.Other.Sign"));
			} else if (customSignTypes.contains(signType)) {
				String placeholderKey = "%" + signType + "%";
				String placeholderValue = e.getLine(2);
				renameSign(e, "Signs." + Toolkit.capitalizeFirstChar(signType), placeholderKey, placeholderValue);
				p.sendMessage(messages.fetchString("Messages.Other.Sign"));
			}
		}
	}
	
	@EventHandler
	public void onSignUse(PlayerInteractEvent e) {
		// Logic behind this specific sign feature implementation: to avoid storing signs, most signs will
		// either match what is specified in the config 100%, or essentially 95%, where the other 5% may be one word
		// that is different (for instance a specified kit name, or arena name).
		// If there isn't a 100% match, a search for that one word delta will be started and subsequently used if found.

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof Sign) {
				ConfigurationSection signsSection = signs.getConfigurationSection("Signs");
				Sign sign = (Sign) e.getClickedBlock().getState();
				Player p = e.getPlayer();

				for (String signType : signsSection.getKeys(false)) {
					if (signType.equals("Locations")) break;

					List<String[]> mismatchedLines = new ArrayList<>();

					for (int i = 0; i < 3; i++) {
						String signLine = sign.getLine(i);
						String configSignLine = signs.fetchString("Signs." + signType + ".Line-" + (i + 1));

						if (!signLine.equals(configSignLine)) {
							mismatchedLines.add(new String[]{configSignLine, signLine});
						}
					}

					if (mismatchedLines.size() == 0) { // exact match
						executeSign(p, signType, null);
						break;
					} else if (mismatchedLines.size() == 1) { // one line (line with kit name or arena name) doesn't match
						executeSign(p, signType, getWordDelta(mismatchedLines));
						break;
					}
				}
			}
		}
	}
	
	private void renameSign(SignChangeEvent e, String path, String placeholder, String placeholderValue) {
		for (int i = 0; i < 3; i++) {
			String line = signs.fetchString(path + ".Line-" + (i + 1));

			if (placeholder != null && placeholderValue != null)
				line = line.replace(placeholder, placeholderValue);
			e.setLine(i, line);
		}
	}

	private String getWordDelta(List<String[]> mismatchedLines) {
		List<String> wordDelta = new ArrayList<>();

		String rawConfigSignLine = ChatColor.stripColor(mismatchedLines.get(0)[0]);
		String rawSignLine = ChatColor.stripColor(mismatchedLines.get(0)[1]);

		String[] rawSignLineWords = rawSignLine.split(" ");
		String[] rawConfigSignLineWords = rawConfigSignLine.split(" ");

		for (int i = 0; i < rawConfigSignLineWords.length; i++) {
			String rawConfigSignWord = rawConfigSignLineWords[i];
			String rawSignLineWord = rawSignLineWords[i];

			if (!rawConfigSignWord.equals(rawSignLineWord)) {
				wordDelta.add(rawSignLineWord);
			}
		}

		// if too many words differ, sign is not a match, return null
		return wordDelta.size() > 1 ? null : wordDelta.get(0);
	}

	private void executeSign(Player p, String type, String placeholder) {
		switch (type.toLowerCase()) {
			case "refill":
				arena.getMenus().getRefillMenu().open(p);
				break;
			case "clear": case "menu": case "stats":
				p.performCommand("kp " + type);
				break;
			case "kit":
				String kitName = placeholder;
				p.performCommand("kp kit " + kitName);
				break;
			case "arena":
				String arenaName = placeholder;
				p.performCommand("kp arena " + arenaName);
				break;
		}
	}
	
}
