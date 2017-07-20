package csokicraft.forge17.orechicken.entity;

import csokicraft.forge17.orechicken.OreChickenMod;
import csokicraft.forge17.orechicken.PacketOreChickenType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class EntityOreChicken extends EntityChicken{
	protected int oretype=-1;

	public EntityOreChicken(World w){
		super(w);
	}
	
	public int getOreType(){
		return oretype;
	}
	
	public void setOreType(int type){
		oretype = type;
	}
	
	public boolean hasOreType(){
		return oretype >= 0;
	}
	
	@Override
	public boolean interact(EntityPlayer p){
		if(p.getHeldItem()!=null&&p.getHeldItem().getItem().equals(OreChickenMod.debug)){
			debugInfo(p, "Oretype", oretype);
			debugInfo(p, "Time", timeUntilNextEgg);
			if(!worldObj.isRemote && p.isSneaking()) timeUntilNextEgg=5;
			return true;
		}
		return super.interact(p);
	}
	
	private void debugInfo(EntityPlayer p, String s, int i){
		String side=(p.getEntityWorld().isRemote?"Client":"Server");
		p.addChatComponentMessage(new ChatComponentText(side+">"+s+":"+i));
	}

	@Override
	public EntityItem dropItem(Item it, int qty){
		if(it.equals(Items.egg)&&hasOreType())
			return super.entityDropItem(new ItemStack(OreChickenMod.egg, qty, oretype), 0.0f);
		
		return super.dropItem(it, qty);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound tag){
		super.writeEntityToNBT(tag);
		tag.setInteger("type", oretype);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound tag){
		super.readEntityFromNBT(tag);
		setOreType(tag.getInteger("type"));
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		OreChickenMod.nethndl.sendToDimension(new PacketOreChickenType(this), worldObj.provider.dimensionId);
	}
}
