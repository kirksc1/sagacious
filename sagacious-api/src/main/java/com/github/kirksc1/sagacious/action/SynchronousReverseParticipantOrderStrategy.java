package com.github.kirksc1.sagacious.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.repository.Participant;
import com.github.kirksc1.sagacious.repository.Saga;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SynchronousReverseParticipantOrderStrategy is a concrete implementation of the {@link CompensatingActionStrategy}
 * interface that executes the compensating actions for a saga in the reverse order in which the participants
 * were added to the saga.
 */
public class SynchronousReverseParticipantOrderStrategy extends AbstractCompensatingActionStrategy {

    /**
     * Construct a new instance.
     * @param repository A Repository for reading Saga entities.
     * @param manager The CompensatingActionManager that identifies the appropriate {@link CompensatingActionExecutor}
     * instance and executes the compensating action.
     * @param objectMapper An ObjectMapper to serialize/deserialize {@link CompensatingActionDefinition} instances
     * to/from Strings.
     */
    public SynchronousReverseParticipantOrderStrategy(CrudRepository<Saga, String> repository, CompensatingActionManager manager, ObjectMapper objectMapper) {
        super(repository, manager, objectMapper);
    }

    /**
     * Execute the compensating actions for the provided saga in the reverse order from which they were added
     * to the saga.
     * @param saga The saga for which the compensating actions are to be executed.
     */
    @Override
    public void performCompensatingActions(Saga saga) {
        List<String> participantIdentifiers = Optional.ofNullable(saga)
                .map(Saga::getParticipants)
                .map(participants -> participants.stream()
                            .map(Participant::getIdentifier)
                            .collect(Collectors.toList())
                ).orElse(new ArrayList<>());

        participantIdentifiers.stream()
                .collect(Collectors.toCollection(LinkedList::new))
                .descendingIterator()
                .forEachRemaining(participantIdentifier -> failParticipantIfNotAlreadyFailed(saga.getIdentifier(), participantIdentifier));
    }

}
