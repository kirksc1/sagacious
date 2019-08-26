package com.github.kirksc1.sagacious.annotation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * RuleBasedExceptionManager provides a concrete implementation of {@link ExceptionManager} that
 * relies on matching the provided Throwable to a collection of {@link ExceptionRule} instances.
 */
@Slf4j
public class RuleBasedExceptionManager extends DefaultExceptionManager {

    private final List<ExceptionRule> rules = new ArrayList<>();

    /**
     * Construct a new instance with the provided collection of {@link ExceptionRule}s.
     * @param rules The collection of ExceptionRule instances.
     */
    public RuleBasedExceptionManager(Collection<ExceptionRule> rules) {
        Assert.notNull(rules, "The ExceptionRule collection provided is null");

        this.rules.addAll(rules);
    }

    /**
     * Determine if the provided Throwable should result in a Saga failure.
     * <p>Failure is based on ExceptionRules matching to the provided Throwable.</p>
     */
    @Override
    public boolean failOn(Throwable e) {
        log.trace("Applying rules to determine whether saga should fail on exception: {}", e);

        ExceptionRule selected = null;
        int deepest = Integer.MAX_VALUE;

        for (ExceptionRule rule : rules) {
            int depth = rule.getMatchDepth(e);
            if (depth >= 0 && depth < deepest) {
                deepest = depth;
                selected = rule;
            }
            if (depth == 0){
                break;
            }
        }

        log.trace("Selected rule is: {}", selected);

        if (selected == null) {
            log.trace("No rule found: applying default behavior");
            return super.failOn(e);
        } else {
            log.trace("Selected rule is: " + selected);
        }

        return selected.failOn();
    }
}
