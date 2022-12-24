package com.pherom.result;

public enum ErrorMessage {

    NO_SUCH_OPERATION("Error: unknown operation: %s"),
    NOT_ENOUGH_ARGUMENTS("Error: Not enough arguments to perform the operation %s"),
    TOO_MANY_ARGUMENTS("Error: Too many arguments to perform the operation %s"),
    DIVISION_BY_ZERO("Error while performing operation Divide: division by 0"),
    FACTORIAL_OF_NEGATIVE_NUMBER("Error while performing operation Factorial: not supported for the negative number"),
    NOT_ENOUGH_ARGUMENTS_IN_STACK("Error: cannot implement operation %s. It requires %d arguments and the stack has only %d arguments"),
    EXCESSIVE_REMOVE_COUNT("Error: cannot remove %d from the stack. It has only %d arguments");
    private final String format;

    ErrorMessage(String format) {
        this.format = format;
    };

    public String getFormat() {
        return format;
    }
}
