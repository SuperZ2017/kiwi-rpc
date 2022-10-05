package serializer;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum SerializerStrategy {
    JAVA_SERIALIZER(1,"JAVA"),
    JSON_SERIALIZER(2, "JSON"),
    PROTOSTUFF_SERIALIZER(3, "PROTOSTUFF");

    private int code;
    private String Algorithm;


    public static SerializerStrategy getStrategy(String algorithm) {
        for (SerializerStrategy strategy : SerializerStrategy.values()) {
            if (strategy.Algorithm.equals(algorithm)) {
                return strategy;
            }
        }

        return SerializerStrategy.PROTOSTUFF_SERIALIZER;
    }
}
