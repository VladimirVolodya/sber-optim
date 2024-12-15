package sber.optim.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import sber.optim.dto.PersonDTO;

import java.io.IOException;

public class JsonSerializer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static byte[] serialize(PersonDTO dto) throws IOException {
        return objectMapper.writeValueAsBytes(dto);
    }

    public static <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        return objectMapper.readValue(data, clazz);
    }
}
