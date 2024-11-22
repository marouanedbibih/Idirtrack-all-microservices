package org.idirtrack.backend.modules.sim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimBoitierDTO {
    private Long simMicroserviceId;
    private String ccid;
    private String phone;
    private String operatorName;

}
