package me.superckl.biometweaker;

import net.neoforged.neoforge.common.ModConfigSpec;

import lombok.Getter;
import net.neoforged.fml.event.config.ModConfigEvent;

@Getter
public class Config {
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

	@Getter
	private static Config instance;

	public static boolean separateFiles() {
		return SEPARATE_FILES.get();
	}
	private static final ModConfigSpec.BooleanValue SEPARATE_FILES = BUILDER
			.comment("If true, BiomeTweaker will output separate files for each object (e.g., biomes)")
			.define("Output Separate Files", true);

	public static boolean outputBiomes() {
		return OUTPUT_BIOMES.get();
	}
	private static final ModConfigSpec.BooleanValue OUTPUT_BIOMES = BUILDER
			.comment("Whether or not BiomeTweaker should output biome info. You can turn this off if you don't need it to shave output time.")
			.define("Output Biomes", true);

	public static boolean outputEntities() {
		return OUTPUT_ENTITIES.get();
	}
	private static final ModConfigSpec.BooleanValue OUTPUT_ENTITIES = BUILDER
			.comment("Whether or not BiomeTweaker should output entity info. You can turn this off if you don't need it to shave output time.")
			.define("Output Entities", true);

	public static boolean outputDims() {
		return OUTPUT_DIMS.get();
	}
	private static final ModConfigSpec.BooleanValue OUTPUT_DIMS = BUILDER
			.comment("Whether or not BiomeTweaker should output dimension info. You can turn this off if you don't need it to shave output time.")
			.define("Output Dimensions", true);

	public static boolean outputFeatures() {
		return OUTPUT_FEATURES.get();
	}
	private static final ModConfigSpec.BooleanValue OUTPUT_FEATURES = BUILDER
			.comment("Whether or not BiomeTweaker should output feature info. You can turn this off if you don't need it to shave output time.")
			.define("Output Features", true);

	public static boolean outputCarvers() {
		return OUTPUT_CARVERS.get();
	}
	private static final ModConfigSpec.BooleanValue OUTPUT_CARVERS = BUILDER
			.comment("Whether or not BiomeTweaker should output carver info. You can turn this off if you don't need it to shave output time.")
			.define("Output Carvers", true);

	public static boolean consumeBonemeal() {
		return CONSUME_BONEMEAL.get();
	}
	private static final ModConfigSpec.BooleanValue CONSUME_BONEMEAL = BUILDER
			.comment("Whether or not bonemeal should be consumed when used in a biome with bonemeal use disabled.")
			.define("Consume Bonemeal", false);

	static final ModConfigSpec SPEC = BUILDER.build();

	private void onConfigLoad(final ModConfigEvent e) {

	}

}
