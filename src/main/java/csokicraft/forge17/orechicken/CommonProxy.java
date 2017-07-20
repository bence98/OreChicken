package csokicraft.forge17.orechicken;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.*;
import csokicraft.forge17.orechicken.entity.EntityOreChicken;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommonProxy implements IMessageHandler<PacketOreChickenType, IMessage>{
	public void initRenderer(FMLInitializationEvent evt){}

	@Override
	public IMessage onMessage(PacketOreChickenType message, MessageContext ctx){
		return null;
	}
}
