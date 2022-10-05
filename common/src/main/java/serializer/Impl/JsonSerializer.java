package serializer.Impl;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import serializer.Serializer;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JsonSerializer implements Serializer {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return gson.fromJson(new String(bytes, StandardCharsets.UTF_8), clazz);
    }

    @Override
    public <T> byte[] serialize(T object) {
        String strJson = gson.toJson(object);
        return strJson.getBytes(StandardCharsets.UTF_8);
    }


    static class ClassCodec implements com.google.gson.JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            try {
                String str = jsonElement.getAsString();
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                log.error("error : ", e);
                throw new RuntimeException(e);
            }
        }

        @Override
        public JsonElement serialize(Class<?> aClass, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(aClass.getName());
        }
    }


}
