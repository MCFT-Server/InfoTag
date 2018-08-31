package maru.infotag.tag;

import java.util.UUID;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.MoveEntityAbsolutePacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import cn.nukkit.scheduler.Task;
import maru.infotag.event.MessageTranslateEvent;

public class InfoTag {
	private Vector3 pos;
	private Player player;

	private UUID uuid;
	private long eid;
	private long peid;

	private boolean ishide = true;

	private String text = "Default";

	public InfoTag(Player player) {
		this.player = player;

		this.uuid = UUID.randomUUID();
		this.eid = Entity.entityCount++;
		this.peid = Entity.entityCount++;
	}

	private void initPos() {
		double x, y, z;

		x = (-Math.sin((this.player.yaw + 33) / 180 * Math.PI) * Math.cos((this.player.pitch + 5) / 180 * Math.PI))
				* 7.2;
		y = 3.2 + (-Math.sin(this.player.pitch / 180 * Math.PI)) * 7.2;
		z = (Math.cos((this.player.yaw + 33) / 180 * Math.PI) * Math.cos((this.player.pitch + 5) / 180 * Math.PI))
				* 7.2;

		pos = new Vector3(this.player.getX() + x, this.player.getY() + y, this.player.getZ() + z);
	}

	public String translateString(String string) {
		String str = string.replaceAll("%player%", this.player.getName())
				.replaceAll("%online%", String.valueOf(Server.getInstance().getOnlinePlayers().size()))
				.replaceAll("%max%", String.valueOf(Server.getInstance().getMaxPlayers()))
				.replaceAll("%x%", String.format("%.2f", this.player.getX()))
				.replaceAll("%y%", String.format("%.2f", this.player.getY()))
				.replaceAll("%z%", String.format("%.2f", this.player.getZ()));

		MessageTranslateEvent event = new MessageTranslateEvent(this, str);
		Server.getInstance().getPluginManager().callEvent(event);

		str = event.getMessage();

		return str;
	}

	public void show() {
		initPos();
		
		AddEntityPacket pk = new AddEntityPacket();
		pk.entityRuntimeId = this.eid;
		pk.entityUniqueId = this.eid;
		pk.type = EntitySnowball.NETWORK_ID;
		pk.x = (float) this.pos.x;
		pk.y = (float) this.pos.y;
		pk.z = (float) this.pos.z;
		pk.speedX = 0;
		pk.speedY = 0;
		pk.speedZ = 0;
		pk.yaw = 0;
		pk.pitch = 0;
		long flags = (1L << Entity.DATA_FLAG_IMMOBILE) | (1L << Entity.DATA_FLAG_CAN_SHOW_NAMETAG) | (1L << Entity.DATA_FLAG_ALWAYS_SHOW_NAMETAG);
		pk.metadata = new EntityMetadata()
				.putLong(Entity.DATA_FLAGS, flags)
				.putString(Entity.DATA_NAMETAG, this.text)
				.putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
				.putFloat(Entity.DATA_SCALE, 0.01f);
		
		SetEntityLinkPacket lpk = new SetEntityLinkPacket();
		lpk.rider = this.eid;
		lpk.riding = this.peid;
		lpk.type = SetEntityLinkPacket.TYPE_RIDE;
		
		this.player.dataPacket(pk);
		this.player.dataPacket(lpk);

		this.ishide = false;
	}

	public void hide() {
		RemoveEntityPacket pk = new RemoveEntityPacket();
		pk.eid = this.eid;
		this.player.dataPacket(pk);
		
		pk.eid = this.peid;
		this.player.dataPacket(pk);

		this.ishide = true;
	}

	public void move() {
		MoveEntityAbsolutePacket pk = new MoveEntityAbsolutePacket();
		pk.eid = this.eid;

		initPos();
		pk.x = this.pos.x;
		pk.y = this.pos.y;
		pk.z = this.pos.z;

		this.player.dataPacket(pk);
	}

	public boolean isHide() {
		return this.ishide;
	}

	public void setText(String text) {
		
		AddPlayerPacket pk = new AddPlayerPacket();
		pk.uuid = this.uuid;
		pk.username = text;
		pk.entityUniqueId = this.peid;
		pk.entityRuntimeId = this.peid;
		pk.x = (float) this.pos.x;
		pk.y = (float) this.pos.y;
		pk.z = (float) this.pos.z;
		pk.speedX = 0;
		pk.speedY = 0;
		pk.speedZ = 0;
		pk.yaw = 0;
		pk.pitch = 0;
		long flags = (1L << Entity.DATA_FLAG_IMMOBILE);
		pk.metadata = new EntityMetadata()
				.putLong(Entity.DATA_FLAGS, flags)
				.putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
				.putFloat(Entity.DATA_SCALE, 0.01f);
		pk.item = Item.get(Item.AIR);
		
		SetEntityLinkPacket lpk = new SetEntityLinkPacket();
		lpk.rider = this.eid;
		lpk.riding = this.peid;
		lpk.type = SetEntityLinkPacket.TYPE_RIDE;
		
		this.player.dataPacket(pk);
		
		Server.getInstance().getScheduler().scheduleRepeatingTask(new Task() {
			int count = 0;
			@Override
			public void onRun(int currentTick) {
				player.dataPacket(lpk);
				if (++count == 3) {
					this.getHandler().cancel();
				}
			}
		}, 1);
		
		this.text = text;
	}

	public String getText() {
		return this.text;
	}
	
	public Player getPlayer() {
		return this.player;
	}
}
