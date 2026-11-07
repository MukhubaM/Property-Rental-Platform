package propertyrental.platform.listing.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import propertyrental.platform.listing.api.dto.request.CreatePropertyTypeRequest;
import propertyrental.platform.listing.api.dto.response.PropertyTypeResponse;
import propertyrental.platform.listing.application.mapper.PropertyTypeMapper;
import propertyrental.platform.listing.application.service.PropertyTypeService;
import propertyrental.platform.listing.domain.PropertyTypeAlreadyExistsException;
import propertyrental.platform.listing.domain.PropertyTypeNotFoundException;
import propertyrental.platform.listing.infrastructure.persistence.entity.PropertyTypeEntity;
import propertyrental.platform.listing.infrastructure.persistence.repository.PropertyTypeRepository;
import propertyrental.platform.listing.domain.PropertyTypeInUseException;
import propertyrental.platform.listing.domain.PropertyTypeNotFoundException;

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
    // ---- get by id ----
    @Test
    void getByIdReturnsPropertyTypeWhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(propertyType(1L, "HOUSE", "House")));

        assertThat(service.getPropertyTypeById(1L))
                .isEqualTo(new PropertyTypeResponse(1L, "HOUSE", "House"));
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getPropertyTypeById(99L))
                .isInstanceOf(PropertyTypeNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ---- delete ----
    @Test
    void deleteRemovesExistingPropertyType() {
        PropertyTypeEntity entity = propertyType(8L, "TEMP", "Temp Type");
        when(repository.findById(8L)).thenReturn(Optional.of(entity));

        service.deletePropertyType(8L);

        verify(repository).delete(entity);
    }

    @Test
    void deleteThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deletePropertyType(99L))
                .isInstanceOf(PropertyTypeNotFoundException.class)
                .hasMessageContaining("99");
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteThrowsWhenPropertyTypeIsStillUsedByProperties() {
        when(repository.findById(1L)).thenReturn(Optional.of(propertyType(1L, "HOUSE", "House")));
        doThrow(new DataIntegrityViolationException("fk_properties_type"))
                .when(repository).flush();

        assertThatThrownBy(() -> service.deletePropertyType(1L))
                .isInstanceOf(PropertyTypeInUseException.class)
                .hasMessageContaining("1");
    }
}