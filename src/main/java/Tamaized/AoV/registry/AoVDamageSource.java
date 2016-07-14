package Tamaized.AoV.registry;

import java.util.ArrayList;

import net.minecraft.util.DamageSource;
import Tamaized.AoV.AoV;
import Tamaized.TamModized.registry.ITamModel;
import Tamaized.TamModized.registry.ITamRegistry;

public class AoVDamageSource implements ITamRegistry {

	private static ArrayList<ITamModel> modelList;
	
	public static DamageSource damageSource_caster_NimbusRay;

	@Override
	public void preInit() {
		modelList = new ArrayList<ITamModel>();
		
		damageSource_caster_NimbusRay = new DamageSource("nimbusRay").setDamageBypassesArmor().setMagicDamage();
	}

	@Override
	public void init() {

	}

	@Override
	public void postInit() {

	}

	@Override
	public ArrayList<ITamModel> getModelList() {
		return modelList;
	}

	@Override
	public String getModID() {
		return AoV.modid;
	}

}