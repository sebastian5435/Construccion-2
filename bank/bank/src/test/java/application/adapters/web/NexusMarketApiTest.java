package application.adapters.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class NexusMarketApiTest {

    private static final String ADMIN = "admin";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void completePurchaseFlowThroughTheApi() throws Exception {
        registerParticipants();
        String warehouseId = JsonPath.read(
                perform(get("/api/sellers/seller-1/warehouses"), null, null)
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(),
                "$[0].id");

        String productId = idOf(perform(post("/api/products"), "seller-1", """
                {"name": "Keyboard", "description": "Mechanical", "price": 150.0,
                 "productType": "PHYSICAL", "variants": ["Black", "White"]}
                """).andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUSPENDED")), "$.id");

        perform(patch("/api/products/" + productId + "/publish"), "seller-1", null)
                .andExpect(status().isUnprocessableContent());

        perform(post("/api/inventory"), "operator-1", """
                {"productId": "%s", "warehouseId": "%s", "initialQuantity": 10}
                """.formatted(productId, warehouseId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.availableQuantity").value(10));

        perform(patch("/api/products/" + productId + "/publish"), "seller-1", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        perform(post("/api/cart/items"), "buyer-1", """
                {"productId": "%s", "quantity": 2}
                """.formatted(productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(300.0));

        String orderId = idOf(perform(post("/api/orders"), "buyer-1", null)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"))
                .andExpect(jsonPath("$.items[0].sourceWarehouseId").value(warehouseId)), "$.id");

        perform(get("/api/inventory/" + productId + "/" + warehouseId + "/availability"), null, null)
                .andExpect(jsonPath("$.availableQuantity").value(8));

        perform(patch("/api/orders/" + orderId + "/pay"), "seller-1", null)
                .andExpect(status().isForbidden());
        perform(patch("/api/orders/" + orderId + "/pay"), "buyer-1", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
        perform(get("/api/orders/" + orderId + "/invoice"), null, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(300.0));

        String trackingId = JsonPath.read(perform(post("/api/shipments"), "operator-1", """
                {"orderId": "%s"}
                """.formatted(orderId)).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(), "$.trackingId");

        perform(patch("/api/shipments/" + trackingId + "/dispatch"), "operator-1", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("DISPATCHED"));
        perform(patch("/api/shipments/" + trackingId + "/deliver"), "operator-1", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("DELIVERED"));
        perform(patch("/api/orders/" + orderId + "/cancel"), "buyer-1", null)
                .andExpect(status().isConflict());

        String returnId = idOf(perform(post("/api/returns"), "buyer-1", """
                {"orderId": "%s", "reason": "Two keys do not work"}
                """.formatted(orderId)).andExpect(status().isCreated()), "$.id");
        perform(post("/api/returns/" + returnId + "/refund"), ADMIN, null)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(300.0));

        perform(get("/api/inventory/" + productId + "/" + warehouseId + "/movements"), null, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[3].type").value("RETURN"));
        perform(get("/api/inventory").param("productId", productId), null, null)
                .andExpect(jsonPath("$[0].quantity").value(10));

        perform(get("/api/reports/operational"), "supervisor-1", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ordersByStatus.DELIVERED").value(1))
                .andExpect(jsonPath("$.totalRefunded").value(300.0));
        perform(get("/api/reports/operational"), "buyer-1", null)
                .andExpect(status().isForbidden());
    }

    @Test
    void requestErrorsAreTranslatedToHttpStatuses() throws Exception {
        perform(post("/api/buyers"), null, """
                {"identifier": "", "fullName": "No Email", "email": "not-an-email", "mainAddress": "Street 1"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.length()").value(2));

        perform(post("/api/buyers"), null, """
                {"identifier": "b-1", "fullName": "Buyer", "email": "admin@nexusmarket.com", "mainAddress": "Street 1"}
                """)
                .andExpect(status().isConflict());

        perform(get("/api/users"), null, null)
                .andExpect(status().isUnauthorized());

        perform(get("/api/products/unknown"), null, null)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with identifier: unknown"));
    }

    private void registerParticipants() throws Exception {
        perform(post("/api/sellers"), ADMIN, """
                {"identifier": "seller-1", "fullName": "Seller One", "email": "seller1@mail.com",
                 "firstWarehouse": {"name": "Seller One Warehouse", "address": "Calle 10 # 20-30"}}
                """).andExpect(status().isCreated());
        perform(post("/api/logistics-operators"), ADMIN, """
                {"identifier": "operator-1", "fullName": "Operator One", "email": "operator1@mail.com"}
                """).andExpect(status().isCreated());
        perform(post("/api/supervisors"), ADMIN, """
                {"identifier": "supervisor-1", "fullName": "Supervisor One", "email": "supervisor1@mail.com"}
                """).andExpect(status().isCreated());
        perform(post("/api/buyers"), null, """
                {"identifier": "buyer-1", "fullName": "Buyer One", "email": "buyer1@mail.com",
                 "mainAddress": "Carrera 50 # 10-20"}
                """).andExpect(status().isCreated())
                .andExpect(jsonPath("$.commercialStatus").value("ENABLED"));
    }

    private ResultActions perform(MockHttpServletRequestBuilder request, String userId, String body) throws Exception {
        if (userId != null) {
            request.header(RequestHeaders.USER_ID, userId);
        }
        if (body != null) {
            request.contentType(MediaType.APPLICATION_JSON).content(body);
        }
        return mockMvc.perform(request);
    }

    private String idOf(ResultActions result, String path) throws Exception {
        return JsonPath.read(result.andReturn().getResponse().getContentAsString(), path);
    }
}
