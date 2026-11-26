package propertyrental.platform.location.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propertyrental.platform.location.infrastructure.persistence.entity.CityEntity;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {

    // This is used by the list endpoint when a province filter is given
    List<CityEntity> findByProvinceId(Long provinceId);

    // This one is used by create to check if the city name is already taken within the province
    boolean existsByProvinceIdAndNameIgnoreCase(Long provinceId, String name);

    // This is used by update to check if the name is taken by a different city in the province
    boolean existsByProvinceIdAndNameIgnoreCaseAndIdNot(Long provinceId, String name, Long id);
}