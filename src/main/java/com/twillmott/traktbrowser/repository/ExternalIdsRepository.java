package com.twillmott.traktbrowser.repository;

import com.twillmott.traktbrowser.domain.ExternalIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Database repository for {@link ExternalIds}.
 * Created by tomw on 16/04/2017.
 */
@Repository
public interface ExternalIdsRepository  extends JpaRepository<ExternalIds, Long> {
}
