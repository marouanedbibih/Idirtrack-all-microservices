package org.idirtrack.backend.modules.sim;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SimRepository extends JpaRepository<Sim, Long>, JpaSpecificationExecutor<Sim> {

//     boolean existsByCcid(String ccid);

//     @Query("SELECT COUNT(s) > 0 FROM Sim s WHERE s.phone = :phone")
//     boolean existsByPhone(@Param("phone") String phone);

    Page<Sim> findAllByStatus(SimStatus pending, Pageable pageRequest);

//     @Query("SELECT s FROM Sim s WHERE s.status = :status AND (s.phone LIKE CONCAT('%',:query,'%') OR s.ccid LIKE CONCAT('%',:query,'%'))")
//     Page<Sim> findAllByStatusAndPhoneContainingOrCcidContaining(
//             @Param("status") SimStatus status,
//             @Param("query") String query,
//             Pageable pageable);

    long countByStatus(SimStatus pending);

//     boolean existsByPhoneAndIdNot(String phone, Long id);

//     boolean existsByCcidAndIdNot(String ccid, Long id);

    /**
     * Search function for the Sim entity by phone, ccid, status, puk,pin,and
     * operatorName
     * 
     * @param phone
     * @param ccid
     * @param status
     * @param puk
     * @param pin
     * @param operatorName
     */

//     @Query("SELECT s FROM Sim s WHERE " +
//             "(:term IS NULL OR " +
//             "s.phone LIKE CONCAT('%', :term, '%') OR " +
//             "s.ccid LIKE CONCAT('%', :term, '%') OR " +
//             "s.status = :term OR " +
//             "s.puk LIKE CONCAT('%', :term, '%') OR " +
//             "s.pin LIKE CONCAT('%', :term, '%') OR " +
//             "s.operator.name LIKE CONCAT('%', :term, '%'))")
//     Page<Sim> search(
//             @Param("term") String term,
//             Pageable pageable);

}
