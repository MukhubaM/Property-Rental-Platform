package propertyrental.platform.listing.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import propertyrental.platform.listing.api.dto.response.PropertyTypeResponse;
import propertyrental.platform.listing.application.mapper.PropertyTypeMapper;
import propertyrental.platform.listing.application.service.PropertyTypeService;
import propertyrental.platform.listing.infrastructure.persistence.entity.PropertyTypeEntity;
import propertyrental.platform.listing.infrastructure.persistence.repository.PropertyTypeRepository;

class PropertyTypeServiceTest {

    private final PropertyTypeRepository repository = mock(PropertyTypeRepository.class);
    private final PropertyTypeService service = new PropertyTypeService(repository, new PropertyTypeMapper());

    private PropertyTypeEntity propertyType(Long id, String code, String name) {
        PropertyTypeEntity entity = new PropertyTypeEntity();
        entity.setId(id);
        entity.setCode(code);
        entity.setName(name);

        return entity;
    }

    @Test
    void getAllReturnsMappedPropertyTypes() {
        when(repository.findAll()).thenReturn(List.of(
                propertyType(1L, "HOUSE", "House"),
                propertyType(2L, "FLAT", "Flat")));

        assertThat(service.getAllPropertyTypes()).containsExactly(
                new PropertyTypeResponse(1L, "HOUSE", "House"),
                new PropertyTypeResponse(2L, "FLAT", "Flat"));
    }

    @Test
    void getAllReturnsEmptyListWhenThereAreNoPropertyTypes() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.getAllPropertyTypes()).isEmpty();
    }
}