package com.pherom.controller.stack;

import com.pherom.operation.Operation;
import com.pherom.request.independent.IndependentCalculationRequest;
import com.pherom.request.stack.StackArgumentAdditionRequest;
import com.pherom.result.ErrorMessage;
import com.pherom.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.stream.events.EndDocument;
import java.util.*;
import java.util.stream.IntStream;

@RestController
public class StackCalculationController {

    public static final String ENDPOINT = "/stack";
    private LinkedList<Integer> stack = new LinkedList<>();

    @GetMapping(path = ENDPOINT + "/size")
    public Result size() {
        return new Result(OptionalInt.of(stack.size()), Optional.empty());
    }

    @PutMapping(path = ENDPOINT + "/arguments")
    public Result addArguments(@RequestBody StackArgumentAdditionRequest argumentAdditionRequest) {
        argumentAdditionRequest.arguments().forEach(arg -> stack.push(arg));
        return new Result(OptionalInt.of(stack.size()), Optional.empty());
    }

    @GetMapping(path = ENDPOINT + "/operate")
    public ResponseEntity<Result> operate(@RequestParam String operation) {
        ResponseEntity<Result> response;
        Result result;
        Optional<Operation> oper = Operation.getOperation(operation);
        if (oper.isPresent()) {
            int minArgs = oper.get().getMinArgs();
            String operName = oper.get().getCapitalizedName();
            if (stack.size() < minArgs) {
                result = new Result(OptionalInt.empty(), Optional.of(String.format(ErrorMessage.NOT_ENOUGH_ARGUMENTS_IN_STACK.getFormat(), operName, minArgs, stack.size())));
                response = new ResponseEntity<>(result, HttpStatus.CONFLICT);
            }
            else {
                List<Integer> arguments = new ArrayList<>();
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

        return response;
    }

    @DeleteMapping(path = ENDPOINT + "/arguments")
    public ResponseEntity<Result> remove(@RequestParam int count) {
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

        return response;
    }
}
