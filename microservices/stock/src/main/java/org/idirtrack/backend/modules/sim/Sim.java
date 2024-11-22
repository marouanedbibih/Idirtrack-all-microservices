package org.idirtrack.backend.modules.sim;

import org.idirtrack.backend.modules.operator.Operator;
import org.idirtrack.backend.utils.MyEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sims")
public class Sim extends MyEntity {

    @Column(name = "pin")
    private String pin;

    @Column(name = "puk")
    private String puk;

    @Column(name = "ccid")
    private String CCID;

    @ManyToOne
    @JoinColumn(name = "operator_id")
    private Operator operator;

    @Column(name = "phone", nullable = false,unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SimStatus status;
}
