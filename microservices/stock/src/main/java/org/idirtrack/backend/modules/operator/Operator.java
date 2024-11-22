package org.idirtrack.backend.modules.operator;

import java.util.List;

import org.idirtrack.backend.modules.sim.Sim;
import org.idirtrack.backend.utils.MyEntity;

import com.fasterxml.jackson.annotation.JsonBackReference;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "operators")
public class Operator extends MyEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "operator")
    @JsonBackReference
    private List<Sim> sims;
}
