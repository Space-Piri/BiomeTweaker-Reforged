package me.superckl.biometweaker;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import it.unimi.dsi.fastutil.doubles.DoubleDoublePair;
import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.superckl.biometweaker.util.IntRange;
import me.superckl.biometweaker.util.RegistryNameHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeGenerationSettingsBuilder;
import net.neoforged.neoforge.common.world.BiomeSpecialEffectsBuilder;
import net.neoforged.neoforge.common.world.ClimateSettingsBuilder;
import net.neoforged.neoforge.common.world.MobSpawnSettingsBuilder;
import net.neoforged.registries.ForgeRegistries;

public class BiomeModificationManager {

	public static boolean hasMobEffects = false;
	private static final Map<ResourceLocation, BiomeModificationManager> modifiers = new Object2ObjectOpenHashMap<>();

	public static BiomeModificationManager forBiome(final ResourceLocation rLoc) {
		return BiomeModificationManager.modifiers.computeIfAbsent(rLoc, loc -> new BiomeModificationManager());
	}

	public static Optional<BiomeModificationManager> forBiomeOpt(final ResourceLocation rLoc) {
		return Optional.ofNullable(BiomeModificationManager.modifiers.get(rLoc));
	}

	public static Optional<BiomeModificationManager> forBiomeOpt(final ResourceKey<Biome> key) {
		return BiomeModificationManager.forBiomeOpt(key.location());
	}

	private ClimateModification climate;
	private EffectsModification effects;
	private FogModification fog;
	private GenerationModification generation;
	private MobSpawnModification spawn;
	@Getter @Setter
	private boolean disableSleep;
	@Getter @Setter
	private boolean disableBonemeal;
	@Getter @Setter
	private boolean disableCropGrowth;
	@Getter @Setter
	private boolean disableSaplingGrowth;

	private final Multimap<EntityType<?>, MobEffectModification> potionEffects = MultimapBuilder.hashKeys().arrayListValues().build();

	private BiomeModificationManager() {}

	public boolean hasClimate() {
		return this.climate != null;
	}

	public ClimateModification getClimate() {
		if(this.climate == null)
			this.climate = new ClimateModification();
		return this.climate;
	}

	public boolean hasEffects() {
		return this.effects != null;
	}

	public EffectsModification getEffects() {
		if(this.effects == null)
			this.effects = new EffectsModification();
		return this.effects;
	}

	public boolean hasGeneration() {
		return this.generation != null;
	}

	public GenerationModification getGeneration() {
		if(this.generation == null)
			this.generation = new GenerationModification();
		return this.generation;
	}

	public boolean hasSpawn() {
		return this.spawn != null;
	}

	public MobSpawnModification getSpawn() {
		if(this.spawn == null)
			this.spawn = new MobSpawnModification();
		return this.spawn;
	}

	public void addMobEffect(final EntityType<?> type, final MobEffectModification effect) {
		BiomeModificationManager.hasMobEffects = true;
		this.potionEffects.put(type, effect);
	}

	public Collection<MobEffectModification> getMobEffects(final EntityType<?> type){
		return this.potionEffects.get(type);
	}

	public boolean hasFog() {
		return this.fog != null;
	}

	public FogModification getFog() {
		if(this.fog == null)
			this.fog = new FogModification();
		return this.fog;
	}

	public static void checkBiomes() {
		BiomeModificationManager.modifiers.keySet().forEach(loc -> {
			if(!ForgeRegistries.BIOMES.containsKey(loc))
				BiomeTweaker.LOG.error("No biome found for resource location %s. Ensure it is correct and has there is a corresponding biome in the output folder.", loc);
		});
	}

	@Data
	public static class ClimateModification{

		private Biome.ClimateSettings climateSettings;
		private boolean precipitation = climateSettings.hasPrecipitation();
		private Optional<Float> temperature = Optional.empty();
		private Biome.TemperatureModifier temperatureModifier;
		private float downfall = climateSettings.downfall();

		public void modify(final ClimateSettingsBuilder val) {
			val.setHasPrecipitation(this.precipitation);
			if(this.temperatureModifier != null)
				val.setTemperatureModifier(this.temperatureModifier);
			this.temperature.ifPresent(val::setTemperature);
			val.setDownfall(this.downfall);
		}

	}

	public static class FogModification{

		@Getter @Setter
		private FogShape shape;
		private final Object2FloatMap<IntRange> nearModifiers = new Object2FloatLinkedOpenHashMap<>();
		private final Object2FloatMap<IntRange> farModifiers = new Object2FloatLinkedOpenHashMap<>();

		public void addNearModifier(final int minY, final int maxY, final float modifier) {
			this.nearModifiers.put(new IntRange(minY, maxY), modifier);
		}

		public void addFarModifier(final int minY, final int maxY, final float modifier) {
			this.farModifiers.put(new IntRange(minY, maxY), modifier);
		}

		public float getNearModifier(final int y) {
			return (float) this.nearModifiers.object2FloatEntrySet().stream().filter(entry -> entry.getKey().contains(y))
					.mapToDouble(Entry::getFloatValue).reduce(1D, (a, b) -> a*b);
		}

		public float getFarModifier(final int y) {
			return (float) this.farModifiers.object2FloatEntrySet().stream().filter(entry -> entry.getKey().contains(y))
					.mapToDouble(Entry::getFloatValue).reduce(1D, (a, b) -> a*b);
		}

		public boolean hasShape() {
			return this.shape != null;
		}

	}

	@Data
	public static class EffectsModification{

		private OptionalInt fogColor = OptionalInt.empty();
		private OptionalInt waterColor = OptionalInt.empty();
		private OptionalInt waterFogColor = OptionalInt.empty();
		private OptionalInt skyColor = OptionalInt.empty();
		private OptionalInt foliageColorOverride = OptionalInt.empty();
		private OptionalInt grassColorOverride = OptionalInt.empty();
		private BiomeSpecialEffects.GrassColorModifier grassColorModifier;
		private Optional<SoundEvent> ambientSoundLoop = Optional.empty();
		private Optional<AmbientParticleSettings> particle = Optional.empty();
		private Optional<AmbientAdditionsSettings> additions = Optional.empty();
		private Optional<Music> backgroundMusic = Optional.empty();


		public void modify(final BiomeSpecialEffectsBuilder builder) {
			this.fogColor.ifPresent(builder::fogColor);
			this.waterColor.ifPresent(builder::waterColor);
			this.waterFogColor.ifPresent(builder::waterFogColor);
			this.skyColor.ifPresent(builder::skyColor);
			this.foliageColorOverride.ifPresent(builder::foliageColorOverride);
			this.grassColorOverride.ifPresent(builder::grassColorOverride);
			if(this.grassColorModifier != null)
				builder.grassColorModifier(this.grassColorModifier);
			this.ambientSoundLoop.ifPresent(builder::ambientLoopSound);
			this.particle.ifPresent(builder::ambientParticle);
			this.additions.ifPresent(builder::ambientAdditionsSound);
			this.backgroundMusic.ifPresent(builder::backgroundMusic);
		}

	}

	public static class MobSpawnModification{

		private final Multimap<MobCategory, ResourceLocation> removedSpawns = MultimapBuilder.enumKeys(MobCategory.class).hashSetValues().build();
		private final Set<MobCategory> allSpawns = EnumSet.noneOf(MobCategory.class);
		private final Multimap<MobCategory, SpawnerData> addedSpawns = MultimapBuilder.enumKeys(MobCategory.class).hashSetValues().build();
		private final Map<ResourceLocation, DoubleDoublePair> costs = new HashMap<>();
		@Setter
		@Getter
		private Optional<Float> probability = Optional.empty();

		public void modify(final MobSpawnSettingsBuilder val) {
			this.probability.ifPresent(val::creatureGenerationProbability);
			this.costs.forEach((loc, pair) -> val.addMobCharge(ForgeRegistries.ENTITY_TYPES.getValue(loc), pair.leftDouble(), pair.rightDouble()));
		}

		public void doRemove(final MobSpawnSettingsBuilder val) {
			this.allSpawns.forEach(type -> val.getSpawner(type).clear());
			this.removedSpawns.forEach((type, loc) -> val.getSpawner(type).removeIf(spawner -> RegistryNameHelper.getRegistryNameE(spawner.type).equals(loc)));
		}

		public void doAdd(final MobSpawnSettingsBuilder val) {
			this.addedSpawns.forEach((type, spawn) -> val.getSpawner(type).add(spawn));
		}

		public void removeSpawn(final ResourceLocation feature, final MobCategory... types) {
			for (final MobCategory type : types)
				this.removedSpawns.put(type, feature);
		}

		public void removeAllSpawns(final MobCategory type) {
			this.allSpawns.add(type);
		}

		public void addSpawn(final SpawnerData spawn, final MobCategory... types) {
			for (final MobCategory type : types)
				this.addedSpawns.put(type, spawn);
		}

		public void setCost(final ResourceLocation entity, final double costPer, final double maxCost) {
			this.costs.put(entity, DoubleDoublePair.of(costPer, maxCost));
		}

	}

	public static class GenerationModification{

		private final Multimap<Decoration, ResourceLocation> removedFeatures = MultimapBuilder.enumKeys(Decoration.class).hashSetValues().build();
		private final Set<Decoration> allFeatures = EnumSet.noneOf(Decoration.class);
		private final Multimap<Carving, ResourceLocation> removedCarvers = MultimapBuilder.enumKeys(Carving.class).hashSetValues().build();
		private final Set<Carving> allCarvers = EnumSet.noneOf(Carving.class);

		private final Multimap<Decoration, Holder<PlacedFeature>> addedFeatures = MultimapBuilder.enumKeys(Decoration.class).arrayListValues().build();
		private final Multimap<Carving, Holder<ConfiguredWorldCarver<?>>> addedCarvers = MultimapBuilder.enumKeys(Carving.class).arrayListValues().build();

		public void removeFeature(final ResourceLocation feature, final Decoration... stages) {
			for (final Decoration stage : stages)
				this.removedFeatures.put(stage, feature);
		}

		public void removeAllFeatures(final Decoration stage) {
			this.allFeatures.add(stage);
		}

		public void addFeature(final Holder<PlacedFeature> feature, final Decoration... stages) {
			for (final Decoration stage : stages)
				this.addedFeatures.put(stage, feature);
		}

		public void removeCarver(final ResourceLocation feature, final Carving... stages) {
			for (final Carving stage : stages)
				this.removedCarvers.put(stage, feature);
		}

		public void removeAllCarvers(final Carving stage) {
			this.allCarvers.add(stage);
		}

		public void addCarver(final Holder<ConfiguredWorldCarver<?>> feature, final Carving... stages) {
			for (final Carving stage : stages)
				this.addedCarvers.put(stage, feature);
		}

		public void doAdd(final BiomeGenerationSettingsBuilder val) {
			this.addedFeatures.forEach((stage, holder) -> val.getFeatures(stage).add(holder));
			this.addedCarvers.forEach((stage, holder) -> val.getCarvers(stage).add(holder));
		}

		public void doRemove(final BiomeGenerationSettingsBuilder val) {
			this.allFeatures.forEach(stage -> val.getFeatures(stage).clear());
			this.removedFeatures.forEach((stage, loc) -> val.getFeatures(stage).removeIf(feature -> feature.is(loc)));
			this.allCarvers.forEach(stage -> val.getCarvers(stage).clear());
			this.removedCarvers.forEach((stage, loc) -> val.getCarvers(stage).removeIf(carver -> carver.is(loc)));
		}

	}


	public static record MobEffectModification(MobEffect effect, int amplifier, int duration, int interval, float chance, boolean visible, boolean showIcon) {

		public MobEffectInstance createInstance() {
			return new MobEffectInstance(this.effect, this.duration, this.amplifier, this.showIcon, this.visible, this.showIcon);
		}

		public static Builder builder(final ResourceLocation effect) {
			return new Builder(effect);
		}

		@Data
		@Accessors(fluent = true)
		public static class Builder{

			private final ResourceLocation effect;
			private int amplifier = 0;
			private int duration = 200;
			private int interval = 198;
			private float chance = 1;
			private boolean visible = false;
			private boolean showIcon = true;

			public MobEffectModification build() throws IllegalArgumentException {
				if(!ForgeRegistries.MOB_EFFECTS.containsKey(this.effect))
					throw new IllegalArgumentException(String.format("No mob effect %s found!", this.effect));
				return new MobEffectModification(ForgeRegistries.MOB_EFFECTS.getValue(this.effect), this.amplifier, this.duration, this.interval, this.chance, this.visible, this.showIcon);
			}

		}

	}

	public enum FogShape {
		SPHERE,
		CYLINDER;
	}

}
