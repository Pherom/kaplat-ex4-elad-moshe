package com.pherom.controller.independent;

import com.pherom.operation.Operation;
import com.pherom.request.RequestManager;
import com.pherom.request.independent.IndependentCalculationRequest;
import com.pherom.result.ErrorMessage;
import com.pherom.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

@RestController
public class IndependentCalculationController {

    public static final String ENDPOINT = "/independent";

    private static final Logger logger = LoggerFactory.getLogger("independent-logger");

    @PostMapping(path = ENDPOINT + "/" + "calculate")
    public ResponseEntity<Result> calculate(@RequestBody IndependentCalculationRequest calculationRequest, HttpServletRequest request) {
        long start = System.currentTimeMillis();
        ResponseEntity<Result> response;
        Result result;
        Optional<Operation> operation = Operation.getOperation(calculationRequest.operation());

        if (operation.isPresent()) {
            if (calculationRequest.arguments().size() < operation.get().getMinArgs()) {
                result = onInsufficientArguments(operation.get());
                response = new ResponseEntity<>(result, HttpStatus.CONFLICT);
            }
            else if (calculationRequest.arguments().size() > operation.get().getMaxArgs()) {
                result = onExcessiveArguments(operation.get());
                response = new ResponseEntity<>(result, HttpStatus.CONFLICT);
            }
            else {
                result = onArgumentsInBound(calculationRequest.arguments(), operation.get());
                response = new ResponseEntity<>(result, result.errorMessage().isEmpty() ? HttpStatus.OK : HttpStatus.CONFLICT);
            }
        }
        else {
            result = onUnsupportedOperation(calculationRequest.operation());
            response = new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }

        long end = System.currentTimeMillis();
        RequestManager.getInstance().incrementIncomingRequest(request, end - start);
        if (result.result().isPresent()) {
            logger.info(String.format("Performing operation %s. Result is %d",
                    operation.get().getCapitalizedName(),
                    result.result().getAsInt()));
            logger.debug(String.format("Performing operation: %s(%s) = %d",
                    operation.get().getCapitalizedName(),
                    calculationRequest.arguments().stream().map(String::valueOf).collect(Collectors.joining(", ")),
                    result.result().getAsInt()));
        }
        else {
            logger.error(String.format("Server encountered an error ! message: %s",
                    result.errorMessage().get()));
        }
        return response;
    }

    public Result onUnsupportedOperation(String attemptedOperation) {
        return new Result(OptionalInt.empty(), Optional.of(String.format(ErrorMessage.NO_SUCH_OPERATION.getFormat(), attemptedOperation)));
    }

    public Result onInsufficientArguments(Operation operation) {
        return new Result(OptionalInt.empty(), Optional.of(String.format(ErrorMessage.NOT_ENOUGH_ARGUMENTS.getFormat(), operation.getCapitalizedName())));
    }

    public Result onExcessiveArguments(Operation operation) {
        return new Result(OptionalInt.empty(), Optional.of(String.format(ErrorMessage.TOO_MANY_ARGUMENTS.getFormat(), operation.getCapitalizedName())));
    }

    public Result onDivisionByZero() {
        return new Result(OptionalInt.empty(), Optional.of(ErrorMessage.DIVISION_BY_ZERO.getFormat()));
    }

    public Result onNegativeNumberFactorial() {
        return new Result(OptionalInt.empty(), Optional.of(ErrorMessage.FACTORIAL_OF_NEGATIVE_NUMBER.getFormat()));
    }

    public Result onValid(List<Integer> arguments, Operation operation) {
        return new Result(operation.calculate(arguments), Optional.empty());
    }

    public Result onArgumentsInBound(List<Integer> arguments, Operation operation) {
        Result result;
        if (operation == Operation.DIVISION && arguments.get(1) == 0) {
            result = onDivisionByZero();
        }
        else if (operation == Operation.FACTORIAL && arguments.get(0) < 0) {
            result = onNegativeNumberFactorial();
        }
        else {
            result = onValid(arguments, operation);
        }

        return result;
    }

}
