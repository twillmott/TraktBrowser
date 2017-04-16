package com.twillmott.traktbrowser.repository;

import com.twillmott.traktbrowser.entity.Images;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Database repository for {@link Images}
 * Created by tomw on 16/04/2017.
 */
@Repository
public interface ImagesRepository extends JpaRepository<Images, Long>{
}
