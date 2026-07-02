package com.albumx.infrastructure.persistence;

import com.albumx.domain.model.TradeProposal;
import com.albumx.domain.model.TradeStatus;
import com.albumx.domain.repository.TradeProposalRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TradeProposalRepositoryAdapter implements TradeProposalRepository {

    private final TradeProposalJpaRepository jpaRepository;

    public TradeProposalRepositoryAdapter(TradeProposalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TradeProposal save(TradeProposal proposal) {
        TradeProposalEntity entity = new TradeProposalEntity(
                proposal.getId(),
                proposal.getRequesterUserId(),
                proposal.getTargetUserId(),
                proposal.getOfferedStickerNumber(),
                proposal.getRequestedStickerNumber(),
                proposal.getStatus(),
                proposal.getCreatedAt()
        );
        jpaRepository.save(entity);
        return proposal;
    }

    @Override
    public List<TradeProposal> findAll(UUID requesterUserId, UUID targetUserId) {
        return jpaRepository.findByFilters(requesterUserId, targetUserId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<TradeProposal> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public int countAcceptedByUserId(UUID userId) {
        return (int) jpaRepository.countByStatusAndParticipant(userId, TradeStatus.ACCEPTED);
    }

    private TradeProposal toDomain(TradeProposalEntity entity) {
        return new TradeProposal(
                entity.getId(),
                entity.getRequesterUserId(),
                entity.getTargetUserId(),
                entity.getOfferedStickerNumber(),
                entity.getRequestedStickerNumber(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
