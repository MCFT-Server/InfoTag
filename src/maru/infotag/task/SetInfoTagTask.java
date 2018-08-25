package maru.infotag.task;

import cn.nukkit.Server;
import cn.nukkit.scheduler.PluginTask;
import maru.infotag.Main;
import maru.infotag.tag.InfoTag;

public class SetInfoTagTask extends PluginTask<Main> {

	public SetInfoTagTask(Main owner) {
		super(owner);
	}

	@Override
	public void onRun(int currentTick) {
		if (Server.getInstance().getOnlinePlayers().size() > 0) {
			Server.getInstance().getOnlinePlayers().values().forEach(player -> {
				InfoTag tag = this.getOwner().getInfoTag(player);
				if (tag == null) return;
				String text = tag.translateString(this.getOwner().getMessage());
				
				tag.setText(text);
			});
		}
	}
	
}
