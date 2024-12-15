package sber.optim.serializer;

import sber.optim.dto.Person.PersonProto;

public class ProtobufSerializer {

    public static byte[] serialize(PersonProto sample) {
        return sample.toByteArray();
    }

    public static PersonProto deserialize(byte[] data) throws Exception {
        return PersonProto.parseFrom(data);
    }
}
