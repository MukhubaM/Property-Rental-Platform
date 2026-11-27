package propertyrental.platform.media.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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
import java.util.UUID;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import propertyrental.platform.media.api.controller.PropertyImageController;
import propertyrental.platform.media.api.dto.request.CreatePropertyImageRequest;
import propertyrental.platform.media.api.dto.request.UpdatePropertyImageRequest;
import propertyrental.platform.media.api.dto.response.PropertyImageResponse;
import propertyrental.platform.media.application.service.PropertyImageService;
import propertyrental.platform.media.domain.PropertyImageNotFoundException;
import propertyrental.platform.media.domain.PropertyImageOrderTakenException;
import propertyrental.platform.media.domain.PropertyImagePropertyNotFoundException;

@WebMvcTest(PropertyImageController.class)
class PropertyImageControllerTest {

    private static final UUID PROPERTY_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID IMAGE_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final String BASE = "/api/v1/properties/" + PROPERTY_ID + "/images";

    private static final String VALID_BODY =
            "{\"imageUrl\":\"https://example.com/a.jpg\",\"altText\":\"Front\",\"displayOrder\":1}";
    private static final String BLANK_URL_BODY =
            "{\"imageUrl\":\"\",\"altText\":\"Front\",\"displayOrder\":1}";
    private static final String NEGATIVE_ORDER_BODY =
            "{\"imageUrl\":\"https://example.com/a.jpg\",\"altText\":\"Front\",\"displayOrder\":-1}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PropertyImageService propertyImageService;

    private PropertyImageResponse response() {

        return new PropertyImageResponse(IMAGE_ID, PROPERTY_ID, "https://example.com/a.jpg", "Front", 1);
    }

    // ---- list ----
    @Test
    void listReturns200() throws Exception {
        when(propertyImageService.getImages(PROPERTY_ID)).thenReturn(List.of(response()));

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(IMAGE_ID.toString()))
                .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/a.jpg"))
                .andExpect(jsonPath("$[0].displayOrder").value(1));
    }

    @Test
    void listReturns404WhenPropertyDoesNotExist() throws Exception {
        when(propertyImageService.getImages(PROPERTY_ID))
                .thenThrow(new PropertyImagePropertyNotFoundException(PROPERTY_ID));

        mockMvc.perform(get(BASE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Property not found"));
    }

    @Test
    void listReturns400WhenPropertyIdIsNotAUuid() throws Exception {
        mockMvc.perform(get("/api/v1/properties/abc/images"))
                .andExpect(status().isBadRequest());
    }

    // ---- add ----
    @Test
    void addReturns201WithCreatedImage() throws Exception {
        when(propertyImageService.addImage(eq(PROPERTY_ID), any(CreatePropertyImageRequest.class)))
                .thenReturn(response());

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(IMAGE_ID.toString()))
                .andExpect(jsonPath("$.propertyId").value(PROPERTY_ID.toString()));
    }

    @Test
    void addReturns400WhenImageUrlIsBlank() throws Exception {
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_URL_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.imageUrl").exists());

        verifyNoInteractions(propertyImageService);
    }

    @Test
    void addReturns400WhenDisplayOrderIsNegative() throws Exception {
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NEGATIVE_ORDER_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.displayOrder").exists());

        verifyNoInteractions(propertyImageService);
    }

    @Test
    void addReturns404WhenPropertyDoesNotExist() throws Exception {
        when(propertyImageService.addImage(eq(PROPERTY_ID), any(CreatePropertyImageRequest.class)))
                .thenThrow(new PropertyImagePropertyNotFoundException(PROPERTY_ID));

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void addReturns409WhenDisplayOrderIsTaken() throws Exception {
        when(propertyImageService.addImage(eq(PROPERTY_ID), any(CreatePropertyImageRequest.class)))
                .thenThrow(new PropertyImageOrderTakenException(1));

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Display order already used"));
    }

    // ---- update ----
    @Test
    void updateReturns200WithUpdatedImage() throws Exception {
        when(propertyImageService.updateImage(eq(PROPERTY_ID), eq(IMAGE_ID), any(UpdatePropertyImageRequest.class)))
                .thenReturn(response());

        mockMvc.perform(put(BASE + "/" + IMAGE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.altText").value("Front"));
    }

    @Test
    void updateReturns400WhenBodyIsInvalid() throws Exception {
        mockMvc.perform(put(BASE + "/" + IMAGE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_URL_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.imageUrl").exists());

        verifyNoInteractions(propertyImageService);
    }

    @Test
    void updateReturns404WhenImageIsNotFound() throws Exception {
        when(propertyImageService.updateImage(eq(PROPERTY_ID), eq(IMAGE_ID), any(UpdatePropertyImageRequest.class)))
                .thenThrow(new PropertyImageNotFoundException(IMAGE_ID));

        mockMvc.perform(put(BASE + "/" + IMAGE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Property image not found"));
    }

    @Test
    void updateReturns409WhenDisplayOrderIsTaken() throws Exception {
        when(propertyImageService.updateImage(eq(PROPERTY_ID), eq(IMAGE_ID), any(UpdatePropertyImageRequest.class)))
                .thenThrow(new PropertyImageOrderTakenException(1));

        mockMvc.perform(put(BASE + "/" + IMAGE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    // ---- delete ----
    @Test
    void deleteReturns204WithNoBody() throws Exception {
        mockMvc.perform(delete(BASE + "/" + IMAGE_ID))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void deleteReturns404WhenImageIsNotFound() throws Exception {
        doThrow(new PropertyImageNotFoundException(IMAGE_ID))
                .when(propertyImageService).deleteImage(PROPERTY_ID, IMAGE_ID);

        mockMvc.perform(delete(BASE + "/" + IMAGE_ID))
                .andExpect(status().isNotFound());
    }
}