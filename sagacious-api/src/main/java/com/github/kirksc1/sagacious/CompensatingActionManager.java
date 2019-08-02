package com.github.kirksc1.sagacious;

import java.util.*;

public class CompensatingActionManager {

    private final CompensatingActionDefinitionMatcher matcher;
    private final Map<List<Executable>,CompensatingActionExecutor> executorMap = new LinkedHashMap<>();

    public CompensatingActionManager(CompensatingActionDefinitionMatcher matcher, List<CompensatingActionExecutor> executors) {
        this.matcher = matcher;
        executors.stream()
            .forEach(executor -> {
                List<Executable> specs = readExecutables(executor);
                executorMap.put(specs, executor);
            });
    }

    public boolean execute(CompensatingActionDefinition definition) {
        boolean retVal = false;

        for (Map.Entry<List<Executable>,CompensatingActionExecutor> entry : executorMap.entrySet()) {
            if (entry.getKey().stream().anyMatch(specification -> matcher.matches(specification, definition))) {
                retVal = entry.getValue().execute(definition);
                break;
            }
        }
        return retVal;
    }

    private List<Executable> readExecutables(CompensatingActionExecutor executor) {
        List<Executable> retVal = new ArrayList<>();

        Optional.ofNullable(executor.getClass().getAnnotation(Executable.class))
                .ifPresent(executionSpecification -> retVal.add(executionSpecification));

        if (retVal.isEmpty()) {
            Optional.ofNullable(executor.getClass().getAnnotation(Executables.class))
                    .ifPresent(executables ->
                            Arrays.stream(executables.value())
                                    .forEach(executionSpecification -> retVal.add(executionSpecification)));
        }

        return retVal;
    }

}
