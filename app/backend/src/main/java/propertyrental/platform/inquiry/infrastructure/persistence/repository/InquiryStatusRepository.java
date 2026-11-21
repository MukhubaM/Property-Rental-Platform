package propertyrental.platform.inquiry.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propertyrental.platform.inquiry.infrastructure.persistence.entity.InquiryStatusEntity;

@Repository
public interface InquiryStatusRepository extends JpaRepository<InquiryStatusEntity, Long> {

    //This method is used CREATE to check is the code or name is already taken
    boolean existsByCode(String code);
    boolean existsByName(String name);

    // This method is used by update to check if the code or name is taken by a different inquiry status
    boolean existsByCodeAndIdNot(String code, Long id);
    boolean existsByNameAndIdNot(String name, Long id);
}