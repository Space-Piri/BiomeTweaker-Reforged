package me.superckl.api.biometweaker.script.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import me.superckl.api.biometweaker.script.pack.BiomePackage;
import me.superckl.api.biometweaker.script.pack.IntersectBiomesPackage;
import me.superckl.api.superscript.script.ParameterWrapper;
import me.superckl.api.superscript.script.ScriptHandler;

public class IntersectPackParameterWrapper extends ParameterWrapper<IntersectBiomesPackage>{

	public IntersectPackParameterWrapper() {
		super(BTParameterTypes.INTERSECT_BIOMES_PACKAGE, 1, 1, false);
	}

	@Override
	public Pair<IntersectBiomesPackage[], String[]> parseArgs(final ScriptHandler handler, final String... args) throws Exception {
		final List<BiomePackage> parsed = new ArrayList<>();
		String[] toReturn = {};
		for(int i = 0; i < args.length; i++){
			final BiomePackage obj = BTParameterTypes.BASIC_BIOMES_PACKAGE.tryParse(args[i], handler);
			if(obj == null){
				toReturn = new String[args.length-i];
				System.arraycopy(args, i, toReturn, 0, toReturn.length);
				break;
			}
			parsed.add(obj);
		}
		return Pair.of(new IntersectBiomesPackage[] {new IntersectBiomesPackage(parsed.toArray(new BiomePackage[parsed.size()]))}, toReturn);
	}

}
