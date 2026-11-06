package propertyrental.platform.listing.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import propertyrental.platform.listing.api.controller.PropertyTypeController;
import propertyrental.platform.listing.api.dto.request.CreatePropertyTypeRequest;
import propertyrental.platform.listing.api.dto.response.PropertyTypeResponse;
import propertyrental.platform.listing.application.service.PropertyTypeService;
import propertyrental.platform.listing.domain.PropertyTypeAlreadyExistsException;

@WebMvcTest(PropertyTypeController.class)
class PropertyTypeControllerTest {

    private static final String VALID_BODY = "{\"code\":\"STUDIO\",\"name\":\"Studio\"}";
    private static final String BLANK_CODE_BODY = "{\"code\":\"\",\"name\":\"Studio\"}";
    private static final String LONG_CODE_BODY = "{\"code\":\"" + "A".repeat(51) + "\",\"name\":\"Studio\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PropertyTypeService propertyTypeService;

    // ---- get all ----
    @Test
    void getAllReturns200WithAllPropertyTypes() throws Exception {
        when(propertyTypeService.getAllPropertyTypes()).thenReturn(List.of(
                new PropertyTypeResponse(1L, "HOUSE", "House"),
                new PropertyTypeResponse(2L, "FLAT", "Flat")));

        mockMvc.perform(get("/api/v1/property-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("HOUSE"))
                .andExpect(jsonPath("$[0].name").value("House"))
                .andExpect(jsonPath("$[1].code").value("FLAT"));
    }

    @Test
    void getAllReturns200WithEmptyListWhenThereAreNoPropertyTypes() throws Exception {
        when(propertyTypeService.getAllPropertyTypes()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/property-types"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ---- create ----
    @Test
    void createReturns201WithCreatedPropertyType() throws Exception {
        when(propertyTypeService.createPropertyType(any(CreatePropertyTypeRequest.class)))
                .thenReturn(new PropertyTypeResponse(8L, "STUDIO", "Studio"));

        mockMvc.perform(post("/api/v1/property-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.code").value("STUDIO"))
                .andExpect(jsonPath("$.name").value("Studio"));
    }

    @Test
    void createReturns400WhenCodeIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/property-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(propertyTypeService);
    }

    @Test
    void createReturns400WhenCodeIsTooLong() throws Exception {
        mockMvc.perform(post("/api/v1/property-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LONG_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(propertyTypeService);
    }

    @Test
    void createReturns409WhenCodeOrNameAlreadyExists() throws Exception {
        when(propertyTypeService.createPropertyType(any(CreatePropertyTypeRequest.class)))
                .thenThrow(new PropertyTypeAlreadyExistsException("code", "STUDIO"));

        mockMvc.perform(post("/api/v1/property-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Property type already exists"));
    }
}