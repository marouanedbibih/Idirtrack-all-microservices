package org.idirtrack.backend.modules.operator;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "operators")
public interface OperatorRepository extends JpaRepository<Operator, Long> {
    boolean existsByName(String name);

    Optional<Operator> findByName(String name);

    Optional<Operator> findByNameAndCreatedAt(String name, Date createdAt);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT COUNT(s) FROM Sim s WHERE s.operator.id = :operatorId")
    long countSimsByOperatorId(@Param("operatorId") Long operatorId);

}
