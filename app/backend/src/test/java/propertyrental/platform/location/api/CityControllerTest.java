package propertyrental.platform.location.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import propertyrental.platform.location.api.controller.CityController;
import propertyrental.platform.location.api.dto.request.CreateCityRequest;
import propertyrental.platform.location.api.dto.request.UpdateCityRequest;
import propertyrental.platform.location.api.dto.response.CityResponse;
import propertyrental.platform.location.application.service.CityService;
import propertyrental.platform.location.domain.CityAlreadyExistsException;
import propertyrental.platform.location.domain.CityInUseException;
import propertyrental.platform.location.domain.CityNotFoundException;
import propertyrental.platform.location.domain.CityProvinceNotFoundException;

@WebMvcTest(CityController.class)
class CityControllerTest {

    private static final String VALID_BODY = "{\"provinceId\":3,\"name\":\"Temp City\"}";
    private static final String BLANK_NAME_BODY = "{\"provinceId\":3,\"name\":\"\"}";
    private static final String MISSING_PROVINCE_BODY = "{\"name\":\"Temp City\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CityService cityService;

    // ---- list / get by id ----
    @Test
    void getAllReturns200() throws Exception {
        when(cityService.getAllCities(null))
                .thenReturn(List.of(new CityResponse(1L, 3L, "Johannesburg")));

        mockMvc.perform(get("/api/v1/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].provinceId").value(3))
                .andExpect(jsonPath("$[0].name").value("Johannesburg"));
    }

    @Test
    void getAllPassesTheProvinceFilter() throws Exception {
        when(cityService.getAllCities(3L))
                .thenReturn(List.of(new CityResponse(1L, 3L, "Johannesburg")));

        mockMvc.perform(get("/api/v1/cities").param("provinceId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(cityService).getAllCities(3L);
    }

    @Test
    void getByIdReturns200WhenFound() throws Exception {
        when(cityService.getCityById(1L)).thenReturn(new CityResponse(1L, 3L, "Johannesburg"));

        mockMvc.perform(get("/api/v1/cities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Johannesburg"));
    }

    @Test
    void getByIdReturns404WhenNotFound() throws Exception {
        when(cityService.getCityById(99L)).thenThrow(new CityNotFoundException(99L));

        mockMvc.perform(get("/api/v1/cities/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("City not found"));
    }

    // ---- create ----
    @Test
    void createReturns201WithCreatedCity() throws Exception {
        when(cityService.createCity(any(CreateCityRequest.class)))
                .thenReturn(new CityResponse(10L, 3L, "Temp City"));

        mockMvc.perform(post("/api/v1/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.provinceId").value(3));
    }

    @Test
    void createReturns400WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_NAME_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());

        verifyNoInteractions(cityService);
    }

    @Test
    void createReturns400WhenProvinceIdIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MISSING_PROVINCE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.provinceId").exists());

        verifyNoInteractions(cityService);
    }

    @Test
    void createReturns400WhenProvinceDoesNotExist() throws Exception {
        when(cityService.createCity(any(CreateCityRequest.class)))
                .thenThrow(new CityProvinceNotFoundException(3L));

        mockMvc.perform(post("/api/v1/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Province does not exist"));
    }

    @Test
    void createReturns409WhenNameAlreadyExists() throws Exception {
        when(cityService.createCity(any(CreateCityRequest.class)))
                .thenThrow(new CityAlreadyExistsException("Temp City", 3L));

        mockMvc.perform(post("/api/v1/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("City already exists"));
    }

    // ---- update ----
    @Test
    void updateReturns200WithUpdatedCity() throws Exception {
        when(cityService.updateCity(eq(10L), any(UpdateCityRequest.class)))
                .thenReturn(new CityResponse(10L, 3L, "Temp City 2"));

        mockMvc.perform(put("/api/v1/cities/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Temp City 2"));
    }

    @Test
    void updateReturns400WhenBodyIsInvalid() throws Exception {
        mockMvc.perform(put("/api/v1/cities/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_NAME_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());

        verifyNoInteractions(cityService);
    }

    @Test
    void updateReturns404WhenNotFound() throws Exception {
        when(cityService.updateCity(eq(99L), any(UpdateCityRequest.class)))
                .thenThrow(new CityNotFoundException(99L));

        mockMvc.perform(put("/api/v1/cities/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturns409WhenNameBelongsToAnotherCity() throws Exception {
        when(cityService.updateCity(eq(10L), any(UpdateCityRequest.class)))
                .thenThrow(new CityAlreadyExistsException("Temp City", 3L));

        mockMvc.perform(put("/api/v1/cities/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    // ---- delete ----
    @Test
    void deleteReturns204WithNoBody() throws Exception {
        mockMvc.perform(delete("/api/v1/cities/10"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void deleteReturns404WhenNotFound() throws Exception {
        doThrow(new CityNotFoundException(99L)).when(cityService).deleteCity(99L);

        mockMvc.perform(delete("/api/v1/cities/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns409WhenCityIsInUse() throws Exception {
        doThrow(new CityInUseException(1L)).when(cityService).deleteCity(1L);

        mockMvc.perform(delete("/api/v1/cities/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("City in use"));
    }
}