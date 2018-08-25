package maru.infotag.listener;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.scheduler.AsyncTask;
import maru.infotag.Main;

public class EventListener implements Listener {
	private Main plugin;
	
	public EventListener(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.plugin.createInfoTag(event.getPlayer());
		this.plugin.getInfoTag(event.getPlayer()).show();
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		this.plugin.removeInfoTag(event.getPlayer());
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Server.getInstance().getScheduler().scheduleAsyncTask(this.plugin, new AsyncTask() {
			
			@Override
			public void onRun() {
				plugin.getInfoTag(event.getPlayer()).move();
			}
		});
	}
}
