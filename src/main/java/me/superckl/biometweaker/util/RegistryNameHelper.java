package me.superckl.biometweaker.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.minecraft.core.registries.Registries;

public class RegistryNameHelper {

	public static ResourceLocation getRegistryName(final Biome biome, final CommonLevelAccessor level) {
		final Registry<Biome> reg = level.registryAccess().registryOrThrow(BuiltInRegistries.BIOME_SOURCE);
		return reg.getKey(biome);
	}

	public static ResourceLocation getRegistryNameE(final EntityType<?> type) {
		return BuiltInRegistries.ENTITY_TYPE.getKey(type);
	}

	public static ResourceLocation getRegistryNameS(final SoundEvent sound) {
		return BuiltInRegistries.SOUND_EVENT.getKey(sound);
	}

}
