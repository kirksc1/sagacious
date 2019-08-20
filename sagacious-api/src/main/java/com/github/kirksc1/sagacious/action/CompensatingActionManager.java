package com.github.kirksc1.sagacious.action;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.annotation.Executable;
import com.github.kirksc1.sagacious.annotation.Executables;
import org.springframework.util.Assert;

import java.util.*;

/**
 * CompensatingActionManager manages a collection of {@link CompensatingActionExecutor}s that it searches
 * when asked to execute a {@link CompensatingActionDefinition}.  It locates the appropriate CompensatingActionExecutor
 * and executes the CompensatingActionDefinition.
 */
public class CompensatingActionManager {

    private final CompensatingActionDefinitionMatcher matcher;
    private final Map<List<Executable>,CompensatingActionExecutor> executorMap = new LinkedHashMap<>();

    /**
     * Construct a new CompensatingActionManager with the matcher and executors provided.
     * @param matcher The CompensatingActionDefinitionMatcher used to locate the appropriate CompensatingActionExecutor.
     * @param executors A collection of CompensatingActionExecutor that the manager can choose from to execute
     * a CompensatingActionDefinition.
     */
    public CompensatingActionManager(CompensatingActionDefinitionMatcher matcher, List<CompensatingActionExecutor> executors) {
        Assert.notNull(matcher, "The CompensatingActionDefinitionMatcher provided is null");
        Assert.notNull(executors, "The executor list provided is null");

        this.matcher = matcher;
        executors.stream()
            .forEach(executor -> {
                List<Executable> specs = readExecutables(executor);
                executorMap.put(specs, executor);
            });
    }

    /**
     * Locate the appropriate CompensatingActionExecutor for the provided CompensatingActionDefinition and execute it.
     * @param definition The definition to execute.
     * @return True if the execution was successful, otherwise false.
     */
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

    /**
     * Read the Executables from the provided CompensatingActionExecutor.
     * @param executor The CompensatingActionExecutor to read Executables from.
     * @return A list of Executables.
     */
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
