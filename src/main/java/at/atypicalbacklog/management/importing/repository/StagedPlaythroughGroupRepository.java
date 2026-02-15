package at.atypicalbacklog.management.importing.repository;

import at.atypicalbacklog.management.importing.ResolutionStatus;
import at.atypicalbacklog.management.importing.domain.StagedPlaythroughGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StagedPlaythroughGroupRepository
        extends JpaRepository<StagedPlaythroughGroup, String> {

    List<StagedPlaythroughGroup> findByImportBatchUid(String batchUid);

    List<StagedPlaythroughGroup> findByStagedGameUid(String stagedGameUid);

    /** Groups that have no staged game assigned. */
    List<StagedPlaythroughGroup> findByStagedGameUidIsNull();

    /** Groups that have a staged game assigned. */
    List<StagedPlaythroughGroup> findByStagedGameUidIsNotNullAndResolutionStatusNot(ResolutionStatus status);
}