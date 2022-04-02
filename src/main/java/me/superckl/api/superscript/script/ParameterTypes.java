package me.superckl.api.superscript.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.primitives.Floats;
import com.google.gson.JsonPrimitive;

import me.superckl.api.superscript.util.CollectionHelper;
import me.superckl.api.superscript.util.WarningHelper;

public final class ParameterTypes {

	private static final Map<Class<?>, ParameterType<?>> defaultTypes = new HashMap<>();
	private static final Map<String, ParameterWrapper<?>> exceptionTypes = new HashMap<>();

	public static final ParameterType<JsonPrimitive> JSON_PRIMITIVE = new ParameterType<>(JsonPrimitive.class) {

		@Override
		public JsonPrimitive tryParse(final String parameter, final ScriptHandler handler) {
			return new JsonPrimitive(parameter);
		}
	};
	public static final ParameterType<Byte> NON_NEG_BYTE = new ParameterType<>(Byte.class) {

		@Override
		public Byte tryParse(final String parameter, final ScriptHandler handler) throws Exception {
			final Integer i = ParameterTypes.NON_NEG_INTEGER.tryParse(parameter, handler);
			return i == null || i > Byte.MAX_VALUE || i < Byte.MIN_VALUE ? null:i.byteValue();
		}
	};
	public static final ParameterType<Byte> BYTE = new ParameterType<>(Byte.class) {

		@Override
		public Byte tryParse(final String parameter, final ScriptHandler handler) throws Exception {
			final Integer i = ParameterTypes.INTEGER.tryParse(parameter, handler);
			return i == null || i > Byte.MAX_VALUE || i < Byte.MIN_VALUE ? null:i.byteValue();
		}
	};
	public static final ParameterType<Float> FLOAT = new ParameterType<>(Float.class) {

		@Override
		public Float tryParse(final String parameter, final ScriptHandler handler) {
			return Floats.tryParse(parameter);
		}
	};
	public static final ParameterType<int[]> NON_NEG_INTEGERS = new ParameterType<>(int[].class) {

		@Override
		public int[] tryParse(final String parameter, final ScriptHandler handler) throws Exception {
			final int[] ints = WarningHelper.uncheckedCast(ParameterTypes.INTEGERS.tryParse(parameter, handler));
			final List<Integer> newInts = new ArrayList<>();
			Arrays.stream(ints).forEach(i -> newInts.add(i));
			final Iterator<Integer> it = newInts.iterator();
			while(it.hasNext())
				if(it.next() < 0)
					it.remove();
			return newInts.stream().mapToInt(i -> i).toArray();
		}
	};
	public static final ParameterType<Integer> NON_NEG_INTEGER = new ParameterType<>(Integer.class) {

		@Override
		public Integer tryParse(final String parameter, final ScriptHandler handler) throws Exception {
			final Integer i = ParameterTypes.INTEGER.tryParse(parameter, handler);
			if(i != null && i.intValue() >= 0)
				return i;
			return null;
		}
	};
	public static final ParameterType<int[]> INTEGERS = new ParameterType<>(int[].class) {

		@Override
		public int[] tryParse(final String parameter, final ScriptHandler handler) throws Exception {
			final List<Integer> ints = new ArrayList<>();
			final Object i = ParameterTypes.INTEGER.tryParse(parameter, handler);
			if(i == null){
				if(parameter.contains("-")){
					final String[] split = parameter.split("-");
					if(split.length == 2 && ScriptParser.isPositiveInteger(split[0]) && ScriptParser.isPositiveInteger(split[1])){
						final int start = Integer.parseInt(split[0]);
						final int end = Integer.parseInt(split[1]);
						final int[] values = CollectionHelper.range(start, end);
						CollectionHelper.addAll(ints, values);
					}
				}
			}else
				ints.add((Integer) i);
			return ints.stream().mapToInt(j -> j).toArray();
		}
	};
	public static final ParameterType<Integer> INTEGER = new ParameterType<>(Integer.class) {

		@Override
		public Integer tryParse(final String parameter, final ScriptHandler handler) {
			Integer i = null;
			try { i = Integer.decode(parameter);} catch (final Exception e) {}
			return i;
		}
	};
	public static final ParameterType<String> STRING = new ParameterType<>(String.class){

		@Override
		public String tryParse(final String parameter, final ScriptHandler handler) {
			if(parameter.startsWith("\"") && parameter.endsWith("\""))
				return parameter.substring(1, parameter.length()-1);
			return null;
		}

	};

	public static final ParameterType<String[]> STRING_ARRAY = new ParameterType<>(String[].class, new ParameterWrapperStringArray()) {

		@Override
		public String[] tryParse(final String parameter, final ScriptHandler handler) throws Exception {
			final String parsed = ParameterTypes.STRING.tryParse(parameter, handler);
			if(parsed == null)
				return null;
			return new String[] {parsed};
		}
	};

	public static final ParameterType<Boolean> BOOLEAN = new ParameterType<>(Boolean.class) {

		@Override
		public Boolean tryParse(final String parameter, final ScriptHandler handler) throws Exception {
			if("true".equals(parameter))
				return true;
			if("false".equals(parameter))
				return false;
			return null;
		}
	};

	public static final ParameterType<Object> BLANK = new ParameterType<>(Object.class) {

		@Override
		public Object tryParse(final String parameter, final ScriptHandler handler) throws Exception {
			return null;
		}
	};

	public static <T, K extends T> void registerDefaultType(final Class<T> clazz, final ParameterType<K> type){
		ParameterTypes.defaultTypes.put(clazz, type);
	}

	public static void registerExceptionWrapper(final String key, final ParameterWrapper<?> type){
		ParameterTypes.exceptionTypes.put(key, type);
	}

	public static <T> ParameterType<? extends T> getDefaultType(final Class<T> clazz){
		return WarningHelper.uncheckedCast(ParameterTypes.defaultTypes.get(clazz));
	}

	public static ParameterWrapper<?> getExceptionWrapper(final String key){
		return ParameterTypes.exceptionTypes.get(key);
	}

	static {
		ParameterTypes.registerDefaultType(Integer.class, ParameterTypes.INTEGER);
		ParameterTypes.registerDefaultType(Integer.TYPE, ParameterTypes.INTEGER);
		ParameterTypes.registerDefaultType(Float.class, ParameterTypes.FLOAT);
		ParameterTypes.registerDefaultType(Float.TYPE, ParameterTypes.FLOAT);
		ParameterTypes.registerDefaultType(Byte.class, ParameterTypes.BYTE);
		ParameterTypes.registerDefaultType(Byte.TYPE, ParameterTypes.BYTE);
		ParameterTypes.registerDefaultType(Boolean.class, ParameterTypes.BOOLEAN);
		ParameterTypes.registerDefaultType(Boolean.TYPE, ParameterTypes.BOOLEAN);
		ParameterTypes.registerDefaultType(int[].class, ParameterTypes.INTEGERS);
		ParameterTypes.registerDefaultType(String.class, ParameterTypes.STRING);
		ParameterTypes.registerDefaultType(String[].class, ParameterTypes.STRING_ARRAY);
		ParameterTypes.registerDefaultType(JsonPrimitive.class, ParameterTypes.JSON_PRIMITIVE);

		ParameterTypes.registerExceptionWrapper("nonNegInt", ParameterTypes.NON_NEG_INTEGER.getSimpleWrapper());
		ParameterTypes.registerExceptionWrapper("nonNegByte", ParameterTypes.NON_NEG_BYTE.getSimpleWrapper());
		ParameterTypes.registerExceptionWrapper("nonNegInts", ParameterTypes.NON_NEG_INTEGERS.getSimpleWrapper());
	}

}
