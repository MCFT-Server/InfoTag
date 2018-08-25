package maru.infotag.event;

import cn.nukkit.event.HandlerList;
import maru.infotag.tag.InfoTag;

public class MessageTranslateEvent extends InfoTagEvent {

	private static final HandlerList handlers = new HandlerList();
	
	private String message;
	
	public static HandlerList getHandlers() {
		return handlers;
	}
	
	public MessageTranslateEvent(InfoTag tag, String msg) {
		super(tag);
		this.message = msg;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
