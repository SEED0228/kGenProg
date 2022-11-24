package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.project.LineNumberRange;

public class SuspiciousnessSerializer implements JsonSerializer<Suspiciousness> {

    @Override
    public JsonElement serialize(final Suspiciousness suspiciousness, final Type type,
                                 final JsonSerializationContext context) {
        final JsonObject serializedSuspiciousness = new JsonObject();
        serializedSuspiciousness.addProperty("value", suspiciousness.getValue());
        serializedSuspiciousness.addProperty("path", suspiciousness.getLocation().getSourcePath().toString());
        serializedSuspiciousness.add("lineNumberRange", context.serialize(suspiciousness.getLocation().inferLineNumbers(),
                LineNumberRange.class));
        return serializedSuspiciousness;
    }
}