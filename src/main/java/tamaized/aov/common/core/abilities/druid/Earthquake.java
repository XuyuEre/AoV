package tamaized.aov.common.core.abilities.druid;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tamaized.aov.AoV;
import tamaized.aov.common.capabilities.CapabilityList;
import tamaized.aov.common.capabilities.aov.IAoVCapability;
import tamaized.aov.common.core.abilities.Ability;
import tamaized.aov.common.core.abilities.AbilityBase;
import tamaized.aov.common.entity.EntityEarthquake;
import tamaized.aov.common.helper.UtilHelper;

public class Earthquake extends AbilityBase {

	private static final String UNLOC = "aov.spells.earthquake";
	private static final float DAMAGE = 1F;
	private static final int RANGE = 20;
	private static final int CHARGES = 3;

	private static final ResourceLocation ICON = new ResourceLocation(AoV.MODID, "textures/spells/earthquake.png");

	public Earthquake() {
		super(

				new TextComponentTranslation(UNLOC.concat(".name")),

				new TextComponentTranslation(""),

				new TextComponentTranslation("aov.spells.global.charges", CHARGES),

				new TextComponentTranslation("aov.spells.global.range", RANGE),

				new TextComponentTranslation("aov.spells.global.damage", DAMAGE),

				new TextComponentTranslation(""),

				new TextComponentTranslation(UNLOC.concat(".desc"))

		);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public String getName() {
		return I18n.format(UNLOC.concat(".name"));
	}

	@Override
	public int getMaxCharges() {
		return CHARGES;
	}

	@Override
	public int getChargeCost() {
		return 1;
	}

	@Override
	public double getMaxDistance() {
		return RANGE;
	}

	@Override
	public int getCoolDown() {
		return 15;
	}

	@Override
	public boolean usesInvoke() {
		return false;
	}

	@Override
	public boolean isCastOnTarget(EntityPlayer caster, IAoVCapability cap, EntityLivingBase target) {
		return IAoVCapability.selectiveTarget(caster, cap, target);
	}

	@Override
	public boolean cast(Ability ability, EntityPlayer caster, EntityLivingBase target) {
		IAoVCapability cap = CapabilityList.getCap(caster, CapabilityList.AOV);
		if (cap == null)
			return false;
		float damage = DAMAGE * (1F + (cap.getSpellPower() / 100F));
		EntityEarthquake quake = new EntityEarthquake(caster.world, caster, damage);
		Vec3d pos = UtilHelper.getSpellLocation(caster, RANGE, target);
		quake.setPosition(pos.x + 0.5F, pos.y + 1F, pos.z + 0.5F);
		caster.world.spawnEntity(quake);
		return true;
	}

	@Override
	public ResourceLocation getIcon() {
		return ICON;
	}
}
