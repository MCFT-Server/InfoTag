package maru.infotag;

import java.util.HashMap;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import maru.infotag.listener.EventListener;
import maru.infotag.tag.InfoTag;
import maru.infotag.task.SetInfoTagTask;

public class Main extends PluginBase {
	private EventListener listener;
	private Map<String, InfoTag> infoTags = new HashMap<>();
	
	private String message;
	
	@Override
	public void onEnable() {
		this.listener = new EventListener(this);
		this.getServer().getPluginManager().registerEvents(listener, this);
		
		this.saveResource("message.txt");
		StringBuffer stb = new StringBuffer();
		Config config = new Config(this.getDataFolder() + "/message.txt");
		for (String line : config.getAll().keySet()) {
			stb.append(line).append('\n');
		}
		this.message = stb.toString();
		
		this.getServer().getScheduler().scheduleRepeatingTask(new SetInfoTagTask(this), 20);
	}
	
	public void createInfoTag(Player player) {
		this.infoTags.put(player.getName(), new InfoTag(player));
	}
	
	public void removeInfoTag(Player player) {
		this.infoTags.remove(player.getName());
	}
	
	public InfoTag getInfoTag(Player player) {
		return this.infoTags.get(player.getName());
	}
	
	public String getMessage() {
		return this.message;
	}
}
