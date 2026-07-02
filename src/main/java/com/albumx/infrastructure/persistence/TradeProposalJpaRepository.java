package com.albumx.infrastructure.persistence;

import com.albumx.domain.model.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TradeProposalJpaRepository extends JpaRepository<TradeProposalEntity, UUID> {

    @Query("SELECT t FROM TradeProposalEntity t WHERE " +
           "(:requesterUserId IS NULL OR t.requesterUserId = :requesterUserId) AND " +
           "(:targetUserId IS NULL OR t.targetUserId = :targetUserId)")
    List<TradeProposalEntity> findByFilters(@Param("requesterUserId") UUID requesterUserId,
                                            @Param("targetUserId") UUID targetUserId);

    @Query("SELECT COUNT(t) FROM TradeProposalEntity t WHERE t.status = :status " +
           "AND (t.requesterUserId = :userId OR t.targetUserId = :userId)")
    long countByStatusAndParticipant(@Param("userId") UUID userId, @Param("status") TradeStatus status);
}
