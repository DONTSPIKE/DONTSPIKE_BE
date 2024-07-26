package org.boot.dontspike.FoodWiki;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodWikiRepository extends JpaRepository<Foodwiki, Long> {
    List<Foodwiki> findByFooddataId(Long fooddataId);
}
