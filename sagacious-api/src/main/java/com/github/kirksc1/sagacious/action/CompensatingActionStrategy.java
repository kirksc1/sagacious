package com.github.kirksc1.sagacious.action;

import com.github.kirksc1.sagacious.repository.Saga;

public interface CompensatingActionStrategy {

    void performCompensatingActions(Saga saga);

}
