package com.pherom.result;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pherom.json.result.ResultJsonDeserializer;
import com.pherom.json.result.ResultJsonSerializer;

import java.util.Optional;
import java.util.OptionalInt;

@JsonSerialize(using = ResultJsonSerializer.class)
@JsonDeserialize(using = ResultJsonDeserializer.class)
public record Result(OptionalInt result, Optional<String> errorMessage) {
}
