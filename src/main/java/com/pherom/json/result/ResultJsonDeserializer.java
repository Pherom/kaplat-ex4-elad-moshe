package com.pherom.json.result;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.pherom.result.Result;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalInt;

public class ResultJsonDeserializer extends StdDeserializer<Result> {

    public ResultJsonDeserializer() {
        this(null);
    }

    public ResultJsonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Result deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        OptionalInt result = OptionalInt.empty();
        Optional<String> errorMessage = Optional.empty();

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if(node.has("result")) {
            if (node.hasNonNull("result"))
                result = OptionalInt.of(node.get("result").asInt());
            else result = OptionalInt.empty();
        }

        if(node.has("error-message")) {
            if (node.hasNonNull("error-message"))
                errorMessage = Optional.of(node.get("error-message").asText());
            else errorMessage = Optional.empty();
        }

        return new Result(result, errorMessage);
    }
}
