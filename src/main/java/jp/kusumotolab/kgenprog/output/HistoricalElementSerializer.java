package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;

public class HistoricalElementSerializer implements JsonSerializer<HistoricalElement> {

  @Override
  public JsonElement serialize(final HistoricalElement historicalElement, final Type type,
      final JsonSerializationContext context) {

    final List<String> parentIds = historicalElement.getParents()
        .stream()
        .map(e -> String.valueOf(e.getId()))
        .collect(Collectors.toList());

    final JsonObject serializedHistoricalElement = new JsonObject();
    serializedHistoricalElement.add("parentIds", context.serialize(parentIds));
    serializedHistoricalElement.addProperty("name", historicalElement.getOperationName());

    return serializedHistoricalElement;
  }
}
