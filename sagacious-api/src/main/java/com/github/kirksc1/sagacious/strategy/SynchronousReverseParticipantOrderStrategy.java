package com.github.kirksc1.sagacious.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kirksc1.sagacious.CompensatingActionExecutor;
import com.github.kirksc1.sagacious.Participant;
import com.github.kirksc1.sagacious.Saga;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SynchronousReverseParticipantOrderStrategy extends AbstractCompensatingActionStrategy {

    public SynchronousReverseParticipantOrderStrategy(CrudRepository<Saga, String> repository, CompensatingActionExecutor executor, ObjectMapper objectMapper) {
        super(repository, executor, objectMapper);
    }

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
