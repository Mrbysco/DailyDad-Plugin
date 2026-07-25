package com.mrbysco.dailydad.jokes;

import com.mrbysco.dailydad.DailyDadPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;

public class DadAbase {
	public static final String[] internal_dadabase = new String[]
			{
					"I invented a new word! Plagiarism!",
					"Whoever invented the knock-knock joke should get a no bell prize.",
					"My pet mouse 'Elvis' died last night. He was caught in a trap..",
					"Why should you never trust a pig with a secret? Because it's bound to squeal.",
					"How do you organize a space party? You planet.",
					"Somebody stole my Microsoft Office and they're going to pay - you have my Word.",
					"What do you call a nervous javelin thrower? Shakespeare.",
					"I went to the zoo the other day, there was only one dog in it. It was a shitzu.",
					"\"Hold on, I have something in my shoe\" \"I'm pretty sure it's a foot\"",
					"What's black and white and read all over? The newspaper.",
					"Do you know where you can get chicken broth in bulk? The stock market.",
					"What kind of dinosaur loves to sleep? A stega-snore-us.",
					"I used to be a banker, but I lost interest.",
					"What kind of music do planets listen to? Nep-tunes.",
					"My friend said to me: \"What rhymes with orange\" I said: \"no it doesn't\"",
					"I asked the surgeon if I could administer my own anesthetic, they said: go ahead, knock yourself out.",
					"Why did the worker get fired from the orange juice factory? Lack of concentration.",
					"Why did the cookie cry? Because his mother was a wafer so long",
					"Where did Captain Hook get his hook? From a second hand store.",
					"I wish I could clean mirrors for a living. It's just something I can see myself doing.",
					"Yesterday a clown held a door open for me. I thought it was a nice jester.",
					"How many bones are in the human hand? A handful of them.",
					"A Sandwich walks into a bar, the bartender says \"Sorry, we don't serve food here\"",
					"The invention of the wheel was what got things rolling",
					"Geology rocks, but Geography is where it's at!",
					"Did you know you should always take an extra pair of pants golfing? Just in case you get a hole in one.",
					"What do you get if you put a duck in a cement mixer? Quacks in the pavement.",
					"Why did the opera singer go sailing? They wanted to hit the high Cs.",
					"How does a French skeleton say hello? Bone-jour.",
					"What did the dog say to the two trees? Bark bark."
			};

	private static final Random random = new Random();
	private static final String DAD_JOKE_URL = "https://icanhazdadjoke.com/";

	public static HttpClient CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(5 * 1000)).version(HttpClient.Version.HTTP_2).build();

	public static void getJokeAsync(JokeResolved resolved) {
		new Thread(() -> {
			String joke = DadAbase.getDadJoke();
			resolved.onResolve(joke, DadAbase.generateChatFormattedJoke(joke));
		}).start();
	}

	public static String getDadJoke() {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(DAD_JOKE_URL))
				.header("Accept-Encoding", "gzip")
				.header("Accept", "text/plain")
				.header("User-Agent", "Daily Dad Minecraft Mod (https://github.com/Mrbysco/DailyDad-Plugin)")
				.timeout(Duration.ofMinutes(1)).GET().build();

		String dadJoke = getResponseAsString(request);

		if (dadJoke.isEmpty()) {
			DailyDadPlugin.LOGGER.info("Getting internal dad joke instead");
			dadJoke = getInternalDadJoke();
		}
		return dadJoke;
	}

	public static TextComponent generateChatFormattedJoke(String joke) {
		TextComponent component = Component.text(String.format("<%s> ", DailyDadPlugin.name)).color(NamedTextColor.GOLD);


		String[] lines = joke.split("\\R");
		for (int i = 0; i < lines.length; i++) {
			component = component.append(Component.text((lines[i] + (i != lines.length - 1 ? "\n" : ""))).color(DailyDadPlugin.jokeColor));
		}

		return component;
	}

	public static String getResponseAsString(HttpRequest request) {
		try {
			HttpResponse<InputStream> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
			String encoding = response.headers().firstValue("Content-Encoding").orElse("");
			return switch (encoding) {
				case "" -> convertToString(response.body());
				case "gzip" -> convertToString(new GZIPInputStream(response.body()));
				default -> throw new UnsupportedOperationException("Unexpected Content-Encoding: " + encoding);
			};
		} catch (Exception e) {
			DailyDadPlugin.LOGGER.error("Fetching dad joke failed!", new IOException(e.getMessage()));
		}
		return "";
	}

	public static String convertToString(InputStream inputStream) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		for (int length; (length = inputStream.read(buffer)) != -1; )
			result.write(buffer, 0, length);
		return result.toString(StandardCharsets.UTF_8);
	}

	public static String getInternalDadJoke() {
		List<? extends String> internalDadabase = DailyDadPlugin.internal_dadabase;
		return internalDadabase.isEmpty() ? "" : internalDadabase.get(random.nextInt(internalDadabase.size()));
	}

	public interface JokeResolved {
		void onResolve(String joke, TextComponent chatFormatted);
	}
}
