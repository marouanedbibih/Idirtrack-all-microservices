package org.idirtrack.backend.modules.sim;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimDTO {
    // Sim data
    private Long id;
    private String pin;
    private String puk;
    private String ccid;
    private String phone;
    private SimStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long operatorId;
    private String operatorName;

}
