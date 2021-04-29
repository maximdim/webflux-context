package webflux;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = HomeController.class)
class HomeControllerTest {
    @Autowired
    WebTestClient webTestClient;

    @Test
    public void testExistingPath() {
        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri(HomeController.HOME_PATH)
                .exchange();
        response.expectStatus().isOk();
        response.expectHeader().exists(CorrelationIdFilter.CORRELATION_ID_HEADER_NAME);
    }

    @Test
    public void testNonExistingPath() {
        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri(HomeController.HOME_PATH + "foo") // there is no mapping for this path
                .exchange();
        response.expectStatus().isNotFound();
        response.expectHeader().exists(CorrelationIdFilter.CORRELATION_ID_HEADER_NAME);
    }
}
