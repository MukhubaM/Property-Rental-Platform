package propertyrental.platform.listing.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import propertyrental.platform.listing.api.controller.PropertyTypeController;
import propertyrental.platform.listing.api.dto.response.PropertyTypeResponse;
import propertyrental.platform.listing.application.service.PropertyTypeService;

@WebMvcTest(PropertyTypeController.class)
class PropertyTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PropertyTypeService propertyTypeService;

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
}