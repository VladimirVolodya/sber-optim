package sber.optim.benchmark;

import org.openjdk.jmh.annotations.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import sber.optim.dto.Person.PersonProto;
import sber.optim.serializer.JsonSerializer;
import sber.optim.dto.PersonDTO;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@State(Scope.Thread)
public class SerializationBenchmark {

    private PersonDTO testDto;
    private byte[] jsonBytes;
    private PersonProto testDtoProto;
    private byte[] protoBytes;

    @Setup
    public void setup() throws IOException {
        testDto = new PersonDTO();
        testDto.setId(1);
        testDto.setFirstName("FirstName");
        testDto.setSecondName("SecondName");
        testDto.setAge(20);
        testDto.setActiveProfile(true);

        // Serialize JSON
        jsonBytes = JsonSerializer.serialize(testDto);

        // Serialize Protobuf
        testDtoProto = PersonProto.newBuilder()
                .setId(1)
                .setName("FirstName")
                .setSurname("SecondName")
                .setAge(20)
                .setActiveProfile(true)
                .build();
        protoBytes = testDtoProto.toByteArray();
    }

    @Benchmark
    public byte[] jsonSerialization() throws IOException {
        return JsonSerializer.serialize(testDto);
    }

    @Benchmark
    public PersonDTO jsonDeserialization() throws IOException {
        return JsonSerializer.deserialize(jsonBytes, PersonDTO.class);
    }

    @Benchmark
    public byte[] protobufSerialization() {
        return testDtoProto.toByteArray();
    }

    @Benchmark
    public PersonProto protobufDeserialization() throws IOException {
        return PersonProto.parseFrom(protoBytes);
    }
}
