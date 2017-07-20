package csokicraft.forge17.orechicken;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import csokicraft.forge17.orechicken.entity.EntityOreChicken;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.Vec3;

public class PacketOreChickenType implements IMessage{
	public Vec3 pos;
	public int type;

	public PacketOreChickenType(){}
	
	public PacketOreChickenType(EntityOreChicken ent){
		pos=ent.getPosition(1);
		type=ent.getOreType();
	}
	
	@Override
	public void fromBytes(ByteBuf buf){
		type=buf.readInt();
		pos=Vec3.createVectorHelper(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}

	@Override
	public void toBytes(ByteBuf buf){
		buf.writeInt(type);
		buf.writeDouble(pos.xCoord);
		buf.writeDouble(pos.yCoord);
		buf.writeDouble(pos.zCoord);
	}

}
