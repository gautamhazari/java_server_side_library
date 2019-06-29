package com.gsma.mobileconnect.r2.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListJsonAdapter implements JsonSerializer<List<String>>,
        JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        List<String> result;

        if (jsonElement.isJsonArray()) {
            result = jsonDeserializationContext.deserialize(jsonElement, type);
        }else {
            result  =  new ArrayList<>();
            result.add(jsonDeserializationContext.deserialize(jsonElement, String.class));
        }
        return result;
    }

    @Override
    public JsonElement serialize(List<String> strings, Type type, JsonSerializationContext jsonSerializationContext) {
        return strings.size() == 1 ? jsonSerializationContext.serialize(strings.get(0)) : jsonSerializationContext.serialize(strings);
    }
}
