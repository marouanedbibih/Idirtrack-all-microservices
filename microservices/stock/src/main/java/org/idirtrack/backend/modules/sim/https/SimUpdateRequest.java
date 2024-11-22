package org.idirtrack.backend.modules.sim.https;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimUpdateRequest {

    @NotBlank(message = "The PIN is required")
    @Pattern(regexp = "\\d{4}", message = "The PIN must be exactly 4 digits")
    private String pin;

    @NotBlank(message = "The PUK is required")
    @Pattern(regexp = "\\d{8}", message = "The PUK must be exactly 8 digits")
    private String puk;

    @NotBlank(message = "The CCID is required")
    @Pattern(regexp = "\\d{1,18}", message = "The CCID must be up to 18 digits")
    private String ccid;

    @NotBlank(message = "The phone number is required")
    @Pattern(regexp = "\\d{10,15}", message = "The Phone Number must be between 10 and 15 digits")
    private String phone;


    @NotNull(message = "The Operator is required")
    private Long operatorId;


}
