package com.pherom.json.result;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.pherom.result.Result;

import java.io.IOException;

public class ResultJsonSerializer extends StdSerializer<Result> {

    public ResultJsonSerializer() {
        this(null);
    }

    public ResultJsonSerializer(Class<Result> t) {
        super(t);
    }

    @Override
    public void serialize(Result result, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        if(result.result().isPresent()) {
            jsonGenerator.writeNumberField("result", result.result().getAsInt());
        }
        else {
            jsonGenerator.writeNullField("result");
        }
        if(result.errorMessage().isPresent()) {
            jsonGenerator.writeStringField("error-message", result.errorMessage().get());
        }
        else {
            jsonGenerator.writeNullField("error-message");
        }
        jsonGenerator.writeEndObject();
    }

}
