package com.github.kirksc1.sagacious.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SagaRepository is a JPA repository for storing saga entities.
 */
@Repository
public interface SagaRepository extends JpaRepository<Saga, String> {

}
