package propertyrental.platform.location.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propertyrental.platform.location.infrastructure.persistence.entity.SuburbEntity;

@Repository
public interface SuburbRepository extends JpaRepository<SuburbEntity, Long> {

    // This is used by the list endpoint when a city filter is given
    List<SuburbEntity> findByCityId(Long cityId);

    // This one is used by create to check if the suburb name already taken within the city
    boolean existsByCityIdAndNameIgnoreCase(Long cityId, String name);

    // This is used by update (vhecks if the name is taken by a different suburb in the city)
    boolean existsByCityIdAndNameIgnoreCaseAndIdNot(Long cityId, String name, Long id);
}