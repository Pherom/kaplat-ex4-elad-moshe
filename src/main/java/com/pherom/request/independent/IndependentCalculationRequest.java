package com.pherom.request.independent;

import java.util.List;

public record IndependentCalculationRequest(List<Integer> arguments, String operation) {
}
