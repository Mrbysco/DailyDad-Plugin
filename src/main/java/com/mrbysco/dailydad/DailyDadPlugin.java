package com.mrbysco.dailydad;

import com.mrbysco.dailydad.handler.EventHandlers;
import com.mrbysco.dailydad.jokes.DadAbase;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class DailyDadPlugin extends JavaPlugin {
	public static final Logger LOGGER = LoggerFactory.getLogger("DailyDad");
	public static Plugin Plugin;
	public final FileConfiguration config = getConfig();
	public static boolean jokeUponRespawn = false;
	public static ChatColor jokeColor = ChatColor.WHITE;
	public static List<String> internal_dadabase = new ArrayList<>();
    public static String prefix = "§6<DailyDad> §r";

	@Override
	public void onEnable() {
		config.addDefault("internal_dadabase", Arrays.stream(DadAbase.internal_dadabase).toList());
		config.addDefault("jokeUponRespawn", false);
		config.addDefault("jokeColor", "f");
        config.addDefault("prefix", "§6<DailyDad> §r");
		config.options().copyDefaults(true);
		saveConfig();

		jokeUponRespawn = config.getBoolean("jokeUponRespawn");
		internal_dadabase = config.getStringList("internal_dadabase");
		jokeColor = ChatColor.getByChar(Objects.requireNonNull(config.getString("jokeColor")));
    prefix = config.getString("prefix");

		getServer().getPluginManager().registerEvents(new EventHandlers(), this);

		Plugin = this;
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}
}
