package com.rvinproject.camerarentalbe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class CameraRentalBeApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void contextLoads() {
    }

    @Test
    void authenticatedWriteEndpointsDoNotFailWithLazyAdminProxy() throws Exception {
        String superAdminToken = login("admin@camera-rental.test");
        String adminToken = login("rental@camera-rental.test");
        String suffix = String.valueOf(System.nanoTime());

        Long adminId = idFrom(create(superAdminToken, "/api/admins", """
                {
                  "name": "Smoke Admin %s",
                  "email": "smoke-admin-%s@camera-rental.test",
                  "password": "admin123",
                  "role": "admin"
                }
                """.formatted(suffix, suffix)));
        update(superAdminToken, "/api/admins/" + adminId, """
                {
                  "name": "Smoke Admin Updated %s",
                  "email": "smoke-admin-%s@camera-rental.test",
                  "password": "",
                  "role": "admin"
                }
                """.formatted(suffix, suffix));

        Long customerId = idFrom(createMultipart(adminToken, "/api/customers", image("identity_image", "customer.jpg"),
                "name", "Smoke Customer " + suffix,
                "phone", "0812" + suffix.substring(Math.max(0, suffix.length() - 8)),
                "address", "Smoke address",
                "identity_type", "id_card",
                "identity_number", "SMOKE-ID-" + suffix));
        updateMultipart(superAdminToken, "/api/customers/" + customerId, null,
                "name", "Smoke Customer Updated " + suffix,
                "phone", "0813" + suffix.substring(Math.max(0, suffix.length() - 8)),
                "address", "Smoke address updated",
                "identity_type", "id_card",
                "identity_number", "SMOKE-ID-" + suffix);

        Long categoryId = idFrom(create(superAdminToken, "/api/categories", """
                {
                  "name": "Smoke Category %s",
                  "description": "Smoke category"
                }
                """.formatted(suffix)));
        update(superAdminToken, "/api/categories/" + categoryId, """
                {
                  "name": "Smoke Category Updated %s",
                  "description": "Smoke category updated"
                }
                """.formatted(suffix));

        Long categoryDetailId = idFrom(create(superAdminToken, "/api/category-details", """
                {
                  "category_id": %d,
                  "name": "Smoke Detail %s",
                  "description": "Smoke category detail"
                }
                """.formatted(categoryId, suffix)));
        update(superAdminToken, "/api/category-details/" + categoryDetailId, """
                {
                  "category_id": %d,
                  "name": "Smoke Detail Updated %s",
                  "description": "Smoke category detail updated"
                }
                """.formatted(categoryId, suffix));

        Long itemId = idFrom(createMultipart(superAdminToken, "/api/items", image("image_upload", "item.jpg"),
                "category_id", categoryId.toString(),
                "category_detail_id", categoryDetailId.toString(),
                "name", "Smoke Item " + suffix,
                "brand", "Smoke",
                "model", "Model",
                "serial_number", "SMOKE-ITEM-" + suffix,
                "description", "Smoke item",
                "daily_price", "100000",
                "stock", "5",
                "status", "available"));
        updateMultipart(superAdminToken, "/api/items/" + itemId, null,
                "category_id", categoryId.toString(),
                "category_detail_id", categoryDetailId.toString(),
                "name", "Smoke Item Updated " + suffix,
                "brand", "Smoke",
                "model", "Model Updated",
                "serial_number", "SMOKE-ITEM-" + suffix,
                "description", "Smoke item updated",
                "daily_price", "120000",
                "stock", "5",
                "status", "available");

        Long paymentMethodId = idFrom(createMultipart(superAdminToken, "/api/payment-methods", image("image_upload", "qris.png"),
                "name", "Smoke Payment " + suffix,
                "type", "bank_transfer",
                "content_type", "image",
                "active", "true"));
        updateMultipart(superAdminToken, "/api/payment-methods/" + paymentMethodId, null,
                "name", "Smoke Payment Updated " + suffix,
                "type", "bank_transfer",
                "content_type", "image",
                "active", "true");

        Long rentalId = idFrom(create(adminToken, "/api/rentals", """
                {
                  "customer_id": %d,
                  "rental_date": "2026-05-23",
                  "planned_return_date": "2026-05-25",
                  "status": "pending",
                  "note": "Smoke rental",
                  "details": [
                    {
                      "item_id": %d,
                      "quantity": 1
                    }
                  ]
                }
                """.formatted(customerId, itemId)));
        update(adminToken, "/api/rentals/" + rentalId, """
                {
                  "customer_id": %d,
                  "rental_date": "2026-05-23",
                  "planned_return_date": "2026-05-25",
                  "status": "ongoing",
                  "note": "Smoke rental updated",
                  "details": [
                    {
                      "item_id": %d,
                      "quantity": 1
                    }
                  ]
                }
                """.formatted(customerId, itemId));

        Long rentalPaymentId = idFrom(createMultipart(adminToken, "/api/rental-payments", image("proof_image", "proof.jpg"),
                "rental_id", rentalId.toString(),
                "payment_method_id", paymentMethodId.toString(),
                "amount", "100000",
                "payment_date", "2026-05-23T10:00:00",
                "status", "paid"));
        updateMultipart(adminToken, "/api/rental-payments/" + rentalPaymentId, null,
                "rental_id", rentalId.toString(),
                "payment_method_id", paymentMethodId.toString(),
                "amount", "120000",
                "payment_date", "2026-05-23T11:00:00",
                "status", "paid");

        Long returnId = idFrom(create(adminToken, "/api/returns", """
                {
                  "rental_id": %d,
                  "return_date": "2026-05-25",
                  "condition_note": "Smoke return"
                }
                """.formatted(rentalId)));
        update(adminToken, "/api/returns/" + returnId, """
                {
                  "rental_id": %d,
                  "return_date": "2026-05-25",
                  "condition_note": "Smoke return updated"
                }
                """.formatted(rentalId));

        Long penaltyId = idFrom(create(adminToken, "/api/penalties", """
                {
                  "return_id": %d,
                  "penalty_type": "other",
                  "description": "Smoke penalty",
                  "amount": 50000,
                  "status": "unpaid",
                  "paid_at": null
                }
                """.formatted(returnId)));
        update(adminToken, "/api/penalties/" + penaltyId, """
                {
                  "return_id": %d,
                  "penalty_type": "other",
                  "description": "Smoke penalty updated",
                  "amount": 50000,
                  "status": "paid",
                  "paid_at": "2026-05-25T12:00:00"
                }
                """.formatted(returnId));

        Long maintenanceId = idFrom(create(superAdminToken, "/api/maintenance", """
                {
                  "item_id": %d,
                  "title": "Smoke maintenance %s",
                  "description": "Smoke maintenance",
                  "maintenance_date": "2026-05-26",
                  "cost": 10000,
                  "status": "in_progress"
                }
                """.formatted(itemId, suffix)));
        update(superAdminToken, "/api/maintenance/" + maintenanceId, """
                {
                  "item_id": %d,
                  "title": "Smoke maintenance updated %s",
                  "description": "Smoke maintenance updated",
                  "maintenance_date": "2026-05-26",
                  "cost": 15000,
                  "status": "completed"
                }
                """.formatted(itemId, suffix));

        remove(adminToken, "/api/penalties/" + penaltyId);
        remove(adminToken, "/api/rental-payments/" + rentalPaymentId);
        remove(adminToken, "/api/returns/" + returnId);
        remove(superAdminToken, "/api/maintenance/" + maintenanceId);
        remove(adminToken, "/api/rentals/" + rentalId);
        remove(superAdminToken, "/api/items/" + itemId);
        remove(superAdminToken, "/api/payment-methods/" + paymentMethodId);
        remove(superAdminToken, "/api/category-details/" + categoryDetailId);
        remove(superAdminToken, "/api/categories/" + categoryId);
        remove(superAdminToken, "/api/customers/" + customerId);
        remove(superAdminToken, "/api/admins/" + adminId);
    }

    private String login(String email) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "admin123"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("data").get("token").asText();
    }

    private String create(String token, String path, String body) throws Exception {
        return json(post(path), token, body).andReturn().getResponse().getContentAsString();
    }

    private void update(String token, String path, String body) throws Exception {
        json(put(path), token, body);
    }

    private String createMultipart(String token, String path, MockMultipartFile file, String... params) throws Exception {
        var builder = multipart(path);
        if (file != null) {
            builder.file(file);
        }
        for (int i = 0; i < params.length; i += 2) {
            builder.param(params[i], params[i + 1]);
        }
        return mockMvc.perform(builder.header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private void updateMultipart(String token, String path, MockMultipartFile file, String... params) throws Exception {
        var builder = multipart(path).with(request -> {
            request.setMethod("PUT");
            return request;
        });
        if (file != null) {
            builder.file(file);
        }
        for (int i = 0; i < params.length; i += 2) {
            builder.param(params[i], params[i + 1]);
        }
        mockMvc.perform(builder.header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private MockMultipartFile image(String field, String filename) {
        return new MockMultipartFile(field, filename, MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3});
    }

    private void remove(String token, String path) throws Exception {
        mockMvc.perform(delete(path).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private ResultActions json(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder, String token, String body) throws Exception {
        return mockMvc.perform(builder
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    private Long idFrom(String response) throws Exception {
        JsonNode data = objectMapper.readTree(response).get("data");
        return data.get("id").asLong();
    }
}
