package com.pherom.operation;

import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

public enum Operation {
    ADDITION("Plus", 2, 2, (args) -> OptionalInt.of(args.get(0) + args.get(1))),
    SUBTRACTION("Minus", 2, 2, (args) -> OptionalInt.of(args.get(0) - args.get(1))),
    MULTIPLICATION("Times", 2, 2, (args) -> OptionalInt.of(args.get(0) * args.get(1))),
    DIVISION("Divide", 2, 2, (args) -> args.get(1) != 0 ? OptionalInt.of(args.get(0) / args.get(1)) : OptionalInt.empty()),
    EXPONENT("Pow", 2, 2, (args) -> OptionalInt.of((int)Math.pow(args.get(0), args.get(1)))),
    ABSOLUTE_VALUE("Abs", 1, 1, (args) -> OptionalInt.of(Math.abs(args.get(0)))),
    FACTORIAL("Fact", 1, 1, (args) -> OptionalInt.of((int) CombinatoricsUtils.factorial(args.get(0))));

    private final String capitalizedName;
    private final int minArgs;
    private final int maxArgs;
    private final Function<List<Integer>, OptionalInt> function;

    Operation(String uppercaseName, int minArgs, int maxArgs, Function<List<Integer>, OptionalInt> method) {
        this.capitalizedName = uppercaseName;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.function = method;
    }

    public String getCapitalizedName() {
        return capitalizedName;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public int getMaxArgs() {
        return maxArgs;
    }

    public OptionalInt calculate(List<Integer> arguments) {
        return function.apply(arguments);
    }

    public static Optional<Operation> getOperation(String name) {
        return Arrays.stream(Operation.values()).filter((op) -> op.getCapitalizedName().equalsIgnoreCase(name)).findFirst();
    }
}
