package serializer;


import serializer.Impl.JavaSerializer;
import serializer.Impl.JsonSerializer;
import serializer.Impl.ProtostuffSerializer;

public class SerializerFactory implements Serializer {

    private Serializer serializer;

    public SerializerFactory(SerializerStrategy strategy) {
        setSerializer(strategy);
    }

    public void setSerializer(SerializerStrategy strategy) {
        switch (strategy) {
            case JAVA_SERIALIZER:
                serializer = new JavaSerializer();
                break;

            case JSON_SERIALIZER:
                serializer = new JsonSerializer();
                break;

            case PROTOSTUFF_SERIALIZER:
                serializer = new ProtostuffSerializer();
                break;

            default:
                throw new IllegalArgumentException("There's no such strategy yet.");
        }
    }


    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return serializer.deserialize(clazz, bytes);
    }

    @Override
    public <T> byte[] serialize(T object) {
        return serializer.serialize(object);
    }
}
