package de.uhd.ifi.se.decision.management.jira.persistence;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Helper class to support serialization and deserialization with GSON in the
 * {@link ConfigPersistenceManager}.
 * 
 * @issue How can we serialize and deserialize an object with the type of an
 *        interface or abstract class (e.g. a list of
 *        ContextInformationProvider)?
 * @decision We write an adapter to serialize and deserialized an object with
 *           the type of an interface or abstract class!
 * @pro Seems to be the only way to do it.
 * @con Needs a lot of helper/util code.
 * @alternative We could change the link recommendation rules (context
 *              information providers) to work the same way as the change
 *              propagation rules.
 * @pro The code would be easier to understand if both features work the same.
 */
public class InterfaceAndAbstractClassAdapter implements JsonSerializer<Object>, JsonDeserializer<Object> {
	private static final String CLASSNAME = "CLASSNAME";
	private static final String DATA = "DATA";

	@Override
	public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
		String className = prim.getAsString();
		Class<?> klass = getObjectClass(className);
		return context.deserialize(jsonObject.get(DATA), klass);
	}

	@Override
	public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(CLASSNAME, src.getClass().getName());
		jsonObject.add(DATA, context.serialize(src));
		return jsonObject;
	}

	public Class<?> getObjectClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e.getMessage());
		}
	}
}