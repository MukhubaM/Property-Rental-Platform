package propertyrental.platform.listing.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import propertyrental.platform.listing.api.dto.request.CreatePropertyTypeRequest;
import propertyrental.platform.listing.api.dto.response.PropertyTypeResponse;
import propertyrental.platform.listing.application.mapper.PropertyTypeMapper;
import propertyrental.platform.listing.application.service.PropertyTypeService;
import propertyrental.platform.listing.domain.PropertyTypeAlreadyExistsException;
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

    // ---- get all ----
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

    // ---- create ----
    @Test
    void createSavesAndReturnsNewPropertyType() {
        when(repository.save(any(PropertyTypeEntity.class))).thenAnswer(invocation -> {
            PropertyTypeEntity saved = invocation.getArgument(0);
            saved.setId(8L);

            return saved;
        });

        PropertyTypeResponse response = service.createPropertyType(new CreatePropertyTypeRequest("STUDIO", "Studio"));

        assertThat(response).isEqualTo(new PropertyTypeResponse(8L, "STUDIO", "Studio"));
    }

    @Test
    void createThrowsWhenCodeAlreadyExists() {
        when(repository.existsByCode("HOUSE")).thenReturn(true);

        assertThatThrownBy(() -> service.createPropertyType(new CreatePropertyTypeRequest("HOUSE", "Other")))
                .isInstanceOf(PropertyTypeAlreadyExistsException.class)
                .hasMessageContaining("code");
        verify(repository, never()).save(any());
    }

    @Test
    void createThrowsWhenNameAlreadyExists() {
        when(repository.existsByName("House")).thenReturn(true);

        assertThatThrownBy(() -> service.createPropertyType(new CreatePropertyTypeRequest("OTHER_CODE", "House")))
                .isInstanceOf(PropertyTypeAlreadyExistsException.class)
                .hasMessageContaining("name");
        verify(repository, never()).save(any());
    }
}