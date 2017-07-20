package csokicraft.forge17.orechicken;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.*;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import appeng.api.AEApi;
import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import cofh.thermalexpansion.util.crafting.PulverizerManager;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.Side;
import csokicraft.forge17.orechicken.entity.*;
import csokicraft.forge17.orechicken.item.*;
import csokicraft.util.StringOutputStream;
import factorization.oreprocessing.TileEntityGrinder;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.Recipes;
import mods.railcraft.api.crafting.IRockCrusherRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.util.crafting.RockCrusherCraftingManager;

@Mod(modid = OreChickenMod.MODID, version = OreChickenMod.VERSION)
public class OreChickenMod
{
    public static final String MODID = "OreChicken";
    public static final String VERSION = "1.1";
    
    @SidedProxy(clientSide="csokicraft.forge17.orechicken.ClientProxy", serverSide="csokicraft.forge17.orechicken.CommonProxy")
    public static CommonProxy proxy;
    @Instance
    public static OreChickenMod inst;
    public static SimpleNetworkWrapper nethndl=new SimpleNetworkWrapper(MODID);
    public static Item chickenFood=new ItemOreChickenFood(),
    				   egg=new ItemOrePart("egg"),
    				   nugget=new ItemOrePart("nugget"),
    				   debug=new Item().setUnlocalizedName(MODID+":debug");
    public static CreativeTabs tab=new CreativeTabs(MODID){
		
		@Override
		public Item getTabIconItem(){
			return chickenFood;
		}
	};
    
    @EventHandler
    public void init(FMLInitializationEvent event) throws Exception{
    	nethndl.registerMessage(proxy, PacketOreChickenType.class, 0, Side.CLIENT);
    	
    	chickenFood.setCreativeTab(tab);
    	egg.setCreativeTab(tab);
    	nugget.setCreativeTab(tab);
    	debug.setCreativeTab(tab);
    	
        registerEntity(EntityOreChicken.class, "oreChicken", 777);
        
        GameRegistry.registerItem(chickenFood, "chickenfood");
        GameRegistry.registerItem(egg, "egg");
        GameRegistry.registerItem(nugget, "nugget");
        GameRegistry.registerItem(debug, "debug");
        
        if(Loader.isModLoaded("Factorization")){
        	System.out.println("[OreChicken] Factorization support is not yet implemented :(");
        }
        
        Document doc = null;
        Element group = null;
        if(Loader.isModLoaded("EnderIO")){
        	DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        	doc = docBuilder.newDocument();
        	
        	Element root = doc.createElement("SAGMillRecipes");
        	doc.appendChild(root);
        	
        	group = doc.createElement("recipeGroup");
        	group.setAttribute("name", MODID);
        	root.appendChild(group);
        }
        for(int tmp=0;tmp<OreChickenTypes.oredicts.length;tmp++){
        	Element fill = null;
        	if(doc != null){
        		fill=doc.createElement("recipe");
        		fill.setAttribute("energyCost", "3200");
        	}
        	registerRecipe(tmp, fill);
        	if(fill!=null)
        		group.appendChild(fill);
        }
        if(Loader.isModLoaded("EnderIO")){
        	OutputStream os = new StringOutputStream();
        	Transformer tfm = TransformerFactory.newInstance().newTransformer();
        	tfm.transform(new DOMSource(doc), new StreamResult(os));
        	FMLInterModComms.sendMessage("EnderIO", "recipe:sagmill", os.toString());
        }
        
        proxy.initRenderer(event);
    }
    
    private void registerRecipe(int type, Element fill){
		String  oredict=OreChickenTypes.oredicts[type],
				prefixes[]=new String[]{"ingot", "gem", "material", "item", "dust"};
		ItemStack material = null;
		
		if(type==OreChickenTypes.NETHER_QUARTZ)
			material=new ItemStack(Items.quartz);
		else if(type==OreChickenTypes.COAL)
			material=new ItemStack(Items.coal);
		else if(type==OreChickenTypes.OBSIDIAN)
			material=new ItemStack(Blocks.obsidian);
		else for(String s:prefixes){
			List<ItemStack> materials=OreDictionary.getOres(s+oredict);
			if(materials!=null&&!materials.isEmpty()){
				material=materials.get(0);
				break;
			}
		}
		
		if(material==null){
			System.out.println("[OreChickenMod] No OreRegistry found for "+oredict+", skipping!");
			return;
		}
		
		ItemStack stackEgg=new ItemStack(egg, 1, type),
				  stackNugget=new ItemStack(nugget, 1, type),
				  stackDoubleNugget=new ItemStack(nugget, 2, type);
		
		OreDictionary.registerOre("itemOreEgg"+oredict, stackEgg);
		OreDictionary.registerOre("nugget"+oredict, stackNugget);
		
		ShapedOreRecipe foodRecipe=new ShapedOreRecipe(new ItemStack(chickenFood, 1, type), 
										" d ", "dsd", " d ",
										'd', "dust"+oredict, 's', Items.wheat_seeds);
		
		List<ItemStack> l=new ArrayList<ItemStack>();
		l.add(stackEgg);
		ShapelessRecipes eggRecipe=new ShapelessRecipes(new ItemStack(nugget, 1, type), l);
		
		List<ItemStack> l2=new ArrayList<ItemStack>();
		for(int tmp=0;tmp<9;tmp++) l2.add(stackNugget);
		ShapelessRecipes nuggetRecipe=new ShapelessRecipes(material, l2);
		
		GameRegistry.addRecipe(foodRecipe);
		GameRegistry.addRecipe(eggRecipe);
		GameRegistry.addRecipe(nuggetRecipe);
		
		if(Loader.isModLoaded("IC2")){
			Recipes.macerator.addRecipe(new RecipeInputItemStack(stackEgg), null, stackDoubleNugget);
		}
		if(Loader.isModLoaded("ThermalExpansion")){
			PulverizerManager.addOreToDustRecipe(3200, stackEgg, stackNugget, null, 0);
		}
		if(Loader.isModLoaded("ImmersiveEngineering")){
			CrusherRecipe.addRecipe(stackDoubleNugget, stackEgg, 3200);
		}
		if(Loader.isModLoaded("Railcraft")){
			IRockCrusherRecipe rec = RockCrusherCraftingManager.getInstance().createNewRecipe(stackEgg, true, false);
			rec.addOutput(stackDoubleNugget, 1);
		}
		if(Loader.isModLoaded("Mekanism")){
			NBTTagCompound imcTag=new NBTTagCompound();
			imcTag.setTag("input", stackEgg.writeToNBT(new NBTTagCompound()));
			imcTag.setTag("output", stackDoubleNugget.writeToNBT(new NBTTagCompound()));
			FMLInterModComms.sendMessage("Mekanism", "EnrichmentChamberRecipe", imcTag);
		}
		if(Loader.isModLoaded("appliedenergistics2")){
			AEApi.instance().registries().grinder().addRecipe(stackEgg, stackNugget, stackNugget, 0.9f, 8);
		}
		if(Loader.isModLoaded("NuclearCraft")){//nc.crafting.machine.CrusherRecipes.instance().addRecipe(stackEgg, stackDoubleNugget);
			try {
				Object ncRecipeInstance = Class.forName("nc.crafting.machine.CrusherRecipes").getMethod("instance").invoke(null);
				Class.forName("nc.crafting.NCRecipeHelper").getDeclaredMethod("addRecipe", Object[].class).invoke(ncRecipeInstance, new Object[]{new Object[]{stackEgg, stackDoubleNugget}});
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		if(Loader.isModLoaded("EnderIO")){
			Document doc = fill.getOwnerDocument();
			fill.setAttribute("name", "oreegg"+oredict);
				Element recipeInput=doc.createElement("input");
					Element inStack=doc.createElement("itemStack");
					inStack.setAttribute("oreDictionary", "itemOreEgg"+oredict);
				recipeInput.appendChild(inStack);
			fill.appendChild(recipeInput);
				Element recipeOutput=doc.createElement("output");
					Element outStack=doc.createElement("itemStack");
					outStack.setAttribute("oreDictionary", "nugget"+oredict);
					outStack.setAttribute("number", "2");
				recipeOutput.appendChild(outStack);
			fill.appendChild(recipeOutput);
		}
		/*if(Loader.isModLoaded("Factorization")){
			TileEntityGrinder.addRecipe(stackEgg, stackNugget, 2.5f);
		}*/
		System.out.println("[OreChickenMod] Added recipes for "+oredict);
	}
    
    private void registerEntity(Class<? extends Entity> cls, String name, int id){
    	EntityRegistry.registerModEntity(cls, name, id, this, 64, 1, true);
	}
}
