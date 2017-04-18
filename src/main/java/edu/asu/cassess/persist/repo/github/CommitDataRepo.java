package edu.asu.cassess.persist.repo.github;

import edu.asu.cassess.persist.entity.github.CommitData;
import edu.asu.cassess.persist.entity.github.CommitDataPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CommitDataRepo extends JpaRepository<CommitData, CommitDataPK> {
}
