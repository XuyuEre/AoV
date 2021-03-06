package tamaized.aov.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tamaized.aov.AoV;
import tamaized.aov.proxy.CommonProxy;
import tamaized.aov.registry.AoVEntities;

import javax.annotation.Nonnull;
import java.util.Objects;

public class EntitySpellAoVParticles extends Entity {

	private static final CommonProxy.ParticleType[] particles = CommonProxy.ParticleType.values();
	private static final DataParameter<Integer> PARTICLE = EntityDataManager.createKey(EntitySpellAoVParticles.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntitySpellAoVParticles.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> RATE = EntityDataManager.createKey(EntitySpellAoVParticles.class, DataSerializers.VARINT);
	private Entity target;
	private int tick = 0;

	public EntitySpellAoVParticles(World worldIn) {
		super(Objects.requireNonNull(AoVEntities.entityspellaovparticles), worldIn);
	}

	public EntitySpellAoVParticles(World world, Entity entity, CommonProxy.ParticleType particle, int color, int rate) {
		this(world);
		target = entity;
		setPositionAndUpdate(target.posX, target.posY, target.posZ);
		tick = rand.nextInt(10) + 20;
		setParticle(particle);
		setColor(color);
		dataManager.set(RATE, rate);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getBrightnessForRender() {
		return 0xF000F0;
	}

	@Override
	protected void registerData() {
		dataManager.register(PARTICLE, CommonProxy.ParticleType.Fluff.ordinal());
		dataManager.register(COLOR, 0xFFFFFFFF);
		dataManager.register(RATE, 10);
	}

	public int getColor() {
		return dataManager.get(COLOR);
	}

	public void setColor(int color) {
		dataManager.set(COLOR, color);
	}

	public CommonProxy.ParticleType getParticle() {
		int index = dataManager.get(PARTICLE);
		return index >= 0 && index < particles.length ? particles[index] : CommonProxy.ParticleType.Fluff;
	}

	public void setParticle(CommonProxy.ParticleType particle) {
		dataManager.set(PARTICLE, particle.ordinal());
	}

	@Override
	protected void readAdditional(@Nonnull NBTTagCompound compound) {
		dataManager.set(PARTICLE, compound.getInt("particle"));
	}

	@Override
	protected void writeAdditional(@Nonnull NBTTagCompound compound) {
		compound.setInt("particle", dataManager.get(PARTICLE));
	}

	@Override
	public void tick() {
		if (world.isRemote) {
			for (int index = 0; index < dataManager.get(RATE); index++) {
				Vec3d vec = getLook(1.0F).rotatePitch(rand.nextInt(360)).rotateYaw(rand.nextInt(360));
				Vec3d pos = getPositionVector().add(0, 0.65F, 0).add(vec);
				AoV.proxy.spawnParticle(getParticle(), world, pos, new Vec3d(0, 0.0625F, 0), 16, 0, 1, getColor());
			}
			return;
		}
		if (tick-- <= 0 || target == null || !target.isAlive()) {
			remove();
			return;
		}
		setPositionAndUpdate(target.posX, target.posY, target.posZ);
	}

}