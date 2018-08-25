package maru.infotag.event;

import cn.nukkit.event.Event;
import maru.infotag.tag.InfoTag;

public class InfoTagEvent extends Event {
	private InfoTag tag;
	
	public InfoTagEvent(InfoTag tag) {
		this.tag = tag;
	}
	
	public InfoTag getInfoTag() {
		return this.tag;
	}
}
