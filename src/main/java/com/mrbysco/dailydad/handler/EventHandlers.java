package com.mrbysco.dailydad.handler;

import com.mrbysco.dailydad.DailyDadPlugin;
import com.mrbysco.dailydad.jokes.DadAbase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EventHandlers implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		DadAbase.getJokeAsync((joke, chatFormatted) -> {
			player.sendMessage(chatFormatted);
		});
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (DailyDadPlugin.jokeUponRespawn) {
			DadAbase.getJokeAsync((joke, chatFormatted) -> {
				player.sendMessage(chatFormatted);
			});
		}
	}
}
