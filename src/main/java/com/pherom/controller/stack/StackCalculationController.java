package com.pherom.controller.stack;

import com.pherom.operation.Operation;
import com.pherom.request.RequestManager;
import com.pherom.request.stack.StackArgumentAdditionRequest;
import com.pherom.result.ErrorMessage;
import com.pherom.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
public class StackCalculationController {

    public static final String ENDPOINT = "/stack";

    private static final Logger logger = LoggerFactory.getLogger("stack-logger");
    private final LinkedList<Integer> stack = new LinkedList<>();

    @GetMapping(path = ENDPOINT + "/size")
    public Result size(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        Result result = new Result(OptionalInt.of(stack.size()), Optional.empty());
        long end = System.currentTimeMillis();
        RequestManager.getInstance().incrementIncomingRequest(request, end - start);
        logger.info(String.format("Stack size is %d", stack.size()));
        logger.debug(String.format("Stack content (first == top): %s", Arrays.toString(stack.toArray())));
        return result;
    }

    @PutMapping(path = ENDPOINT + "/arguments")
    public Result addArguments(@RequestBody StackArgumentAdditionRequest argumentAdditionRequest, HttpServletRequest request) {
        long start = System.currentTimeMillis();
        int oldSize = stack.size();
        argumentAdditionRequest.arguments().forEach(stack::push);
        Result result = new Result(OptionalInt.of(stack.size()), Optional.empty());
        long end = System.currentTimeMillis();
        RequestManager.getInstance().incrementIncomingRequest(request, end - start);
        logger.info(String.format("Adding total of %d argument(s) to the stack | Stack size: %d",
                argumentAdditionRequest.arguments().size(), stack.size()));
        logger.debug(String.format("Adding arguments: %s | Stack size before %d | stack size after %d",
                argumentAdditionRequest.arguments().stream().map(String::valueOf).collect(Collectors.joining(", ")),
                oldSize, stack.size()));
        return result;
    }

    @GetMapping(path = ENDPOINT + "/operate")
    public ResponseEntity<Result> operate(@RequestParam String operation, HttpServletRequest request) {
        long start = System.currentTimeMillis();
        ResponseEntity<Result> response;
        Result result;
        Optional<Operation> oper = Operation.getOperation(operation);
        List<Integer> arguments = new ArrayList<>();
        if (oper.isPresent()) {
            int minArgs = oper.get().getMinArgs();
            String operName = oper.get().getCapitalizedName();
            if (stack.size() < minArgs) {
                result = new Result(OptionalInt.empty(), Optional.of(String.format(ErrorMessage.NOT_ENOUGH_ARGUMENTS_IN_STACK.getFormat(), operName, minArgs, stack.size())));
                response = new ResponseEntity<>(result, HttpStatus.CONFLICT);
            }
            else {
                IntStream.range(0, minArgs).forEach(i -> arguments.add(stack.pop()));
                if (oper.get() == Operation.DIVISION && arguments.get(1) == 0) {
                    result = new Result(OptionalInt.empty(), Optional.of(ErrorMessage.DIVISION_BY_ZERO.getFormat()));
                    response = new ResponseEntity<>(result, HttpStatus.CONFLICT);
                }
                else if (oper.get() == Operation.FACTORIAL && arguments.get(0) < 0) {
                    result = new Result(OptionalInt.empty(), Optional.of(ErrorMessage.FACTORIAL_OF_NEGATIVE_NUMBER.getFormat()));
                    response = new ResponseEntity<>(result, HttpStatus.CONFLICT);
                }
                else {
                    result = new Result(oper.get().calculate(arguments), Optional.empty());
                    response = new ResponseEntity<>(result, HttpStatus.OK);
                }
            }
        }
        else {
            result = new Result(OptionalInt.empty(), Optional.of(String.format(ErrorMessage.NO_SUCH_OPERATION.getFormat(), operation)));
            response = new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
        long end = System.currentTimeMillis();
        RequestManager.getInstance().incrementIncomingRequest(request, end - start);
        if (result.result().isPresent()) {
            logger.info(String.format("Performing operation %s. Result is %d | stack size: %d",
                    oper.get().getCapitalizedName(), result.result().getAsInt(), stack.size()));
            logger.debug(String.format("Performing operation: %s(%s) = %d",
                    oper.get().getCapitalizedName(),
                    arguments.stream().map(String::valueOf).collect(Collectors.joining(", ")),
                    result.result().getAsInt()));
        }
        else {
            logger.error(String.format("Server encountered an error ! message: %s",
                    result.errorMessage().get()));
        }
        return response;
    }

    @DeleteMapping(path = ENDPOINT + "/arguments")
    public ResponseEntity<Result> remove(@RequestParam int count, HttpServletRequest request) {
        long start = System.currentTimeMillis();
        ResponseEntity<Result> response;
        Result result;
        if (count <= stack.size()) {
            IntStream.range(0, count).forEach(i -> stack.pop());
            result = new Result(OptionalInt.of(stack.size()), Optional.empty());
            response = new ResponseEntity<>(result, HttpStatus.OK);
        }
        else {
            result = new Result(OptionalInt.empty(), Optional.of(String.format(ErrorMessage.EXCESSIVE_REMOVE_COUNT.getFormat(), count, stack.size())));
            response = new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
        long end = System.currentTimeMillis();
        RequestManager.getInstance().incrementIncomingRequest(request, end - start);
        if (result.result().isPresent()) {
            logger.info(String.format("Removing total %d argument(s) from the stack | Stack size: %d",
                    count, stack.size()));
        }
        else {
            logger.error(String.format("Server encountered an error ! message: %s",
                    result.errorMessage().get()));
        }
        return response;
    }
}
