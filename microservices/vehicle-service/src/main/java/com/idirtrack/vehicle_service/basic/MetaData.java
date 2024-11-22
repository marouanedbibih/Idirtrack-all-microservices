package com.idirtrack.vehicle_service.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetaData {
    private Integer currentPage;
    private Integer totalPages;
    private Integer size;
}
