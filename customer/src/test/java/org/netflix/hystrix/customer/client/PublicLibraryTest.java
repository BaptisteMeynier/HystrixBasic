package org.netflix.hystrix.customer.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.netflix.hystrix.basic.common.constantes.Constantes;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.google.common.util.concurrent.Uninterruptibles;

import junit.framework.Assert;

import org.assertj.core.data.Offset;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.netflix.hystrix.basic.book.finder.service.BookService;
import org.netflix.hystrix.basic.common.exceptions.RemoteCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.netflix.hystrix.basic.common.exceptions.RemoteCallException;

import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.netflix.hystrix.basic.common.exceptions.RemoteCallException.ExceptionType;

import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PublicLibraryTest {

    @ClassRule
    public static WireMockClassRule wireMockServer = new WireMockClassRule(wireMockConfig().port(8091));
    private final int DEPENDENCY_TIMEOUT = 2000; // le timeout de la command hystrix PTHystrixCommand
    private final int OPEN_CIRCUIT_THRESHOLD = 5; // le seuil d'ouverture du circuit si le pourcentage d'erreur est ateint (50%)
    private final int OPEN_CIRCUIT_DURATION = 5000; // la durée en ms d'ouverture du circuit
    @Rule
    public ExpectedException exception = ExpectedException.none();
    @Autowired
    BookService bookService;

    /**
     * <li>Ce test fait OPEN_CIRCUIT_THRESHOLD appels pour atteindre le seuil d'ouverture du circuit et vérifie que ces appels tombent en timeout</li>
     * <li>fait un appel de plus et vérifie que le circuit est ouvert</li>
     */
    @Test
    public void hystrix_dependencyTimeout_hystrixOpenCircuit() {

        // Given
        exception.expect(RemoteCallException.class);
        exception.expect(new RemoteExceptionTypeMatcher(RemoteCallException.ExceptionType.HYSTRIX_OPEN_CIRCUIT));
        exception.expect(new RemoteExceptionDurationMatcher(-1));

        final int responseDelay = DEPENDENCY_TIMEOUT + 100; // marge de 100 ms

        WireMock.stubFor(WireMock.any(WireMock.urlMatching(".*books.*")).willReturn(
                WireMock.aResponse().withFixedDelay(responseDelay))
        );

        // When
        for (int i = 0; i < OPEN_CIRCUIT_THRESHOLD; i++) { // On fait autant de tentatives qu'il faut pour atteindre le seuil d'ouverture du circuit
            try {
            	bookService.findByName(Mockito.anyString());
            } catch (RemoteCallException e) {
                System.out.println(String.format("appel %d sur %d >> %s >> durée : %d ms", i + 1, OPEN_CIRCUIT_THRESHOLD, ExceptionType.TIMEOUT.name(), e.getCallDuration()));
                Assert.assertTrue(e.getExceptionType() == ExceptionType.TIMEOUT);
              //  assertThat(e.getCallDuration()).isCloseTo(DEPENDENCY_TIMEOUT, Offset.offset(100));
            }
        }
        // faire un appel après que le seuil OPEN_CIRCUIT_THRESHOLD soit atteint
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS); // on attend 1 seconde pour laisser le temps à hystrix d'ouvrir le circuit
        System.out.println(String.format("appel %d >> %s >> durée : -1", OPEN_CIRCUIT_THRESHOLD + 1, ExceptionType.HYSTRIX_OPEN_CIRCUIT.name()));
        bookService.findByName(Mockito.anyString());
        // Then --> exception levée
    }

    /**
     * <li>Ce test fait OPEN_CIRCUIT_THRESHOLD appels pour atteindre le seuil d'ouverture du circuit et vérifie que ces appels tombent en timeout</li>
     * <li>fait un appel de plus et vérifie que le circuit est ouvert</li>
     * <li>attend OPEN_CIRCUIT_DURATION millisecondes puis retente un appel et vérifie que cet appel tombe en timeout</li>
     */
    @Test
    public void hystrix_circuitOpen_CircuitIsClosedWhenDurationIsReached() {
        // Given
        final int responseDelay = DEPENDENCY_TIMEOUT + 100;

        WireMock.stubFor(WireMock.any(WireMock.urlMatching(".*books/*"))
        		.willReturn(WireMock.aResponse().withStatus(Response.Status.OK.getStatusCode())
        				.withFixedDelay(responseDelay)
                )
        );

        // When
        for (int i = 0; i < OPEN_CIRCUIT_THRESHOLD; i++) { //On fait autant de tentatives qu'il faut pour ouvrir le circuit
            try {
            	bookService.findByName(Mockito.anyString());
            } catch (RemoteCallException e) {
                System.out.println(String.format("appel %d sur %d >> %s >> durée : %d ms", i + 1, OPEN_CIRCUIT_THRESHOLD, ExceptionType.TIMEOUT.name(), e.getCallDuration()));
                Assert.assertEquals(e.getExceptionType(),ExceptionType.TIMEOUT);
                
            }
        }

        // faire un appel après que le seuil OPEN_CIRCUIT_THRESHOLD soit atteint
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS); // on attend 1 seconde pour laisser le temps à hystrix d'ouvrir le circuit
        try {
        	bookService.findByName(Mockito.anyString());
        } catch (RemoteCallException e) {
            System.out.println(String.format("appel %d >> %s >> durée : -1", OPEN_CIRCUIT_THRESHOLD + 1, ExceptionType.HYSTRIX_OPEN_CIRCUIT.name()));
            Assert.assertTrue(e.getExceptionType() == ExceptionType.HYSTRIX_OPEN_CIRCUIT);
        }
        //On attend que le circuit se ferme
        System.out.println(String.format("attendre %d ms, la durée d'ouverture du circuit ...", OPEN_CIRCUIT_DURATION));
        Uninterruptibles.sleepUninterruptibly(OPEN_CIRCUIT_DURATION, TimeUnit.MILLISECONDS);

        // On fait une nouvelle tentative qui doit être en timeout et le circuit s'ouvre de nouveau
        try {
        	bookService.findByName(Mockito.anyString());
        } catch (RemoteCallException e) {
            System.out.println("appel après expiration du délai OPEN_CIRCUIT_DURATION (HALF-OPEN) >> TIMEOUT");
            Assert.assertTrue(e.getExceptionType() == ExceptionType.TIMEOUT);
        }

        // On fait un autre appel et maintenant le circuit est bien ouvert de nouveau
        try {
        	bookService.findByName(Mockito.anyString());
        } catch (RemoteCallException e) {
            System.out.println("appel après état HALF-OPEN >> HYSTRIX_OPEN_CIRCUIT");
            Assert.assertTrue(e.getExceptionType() == ExceptionType.HYSTRIX_OPEN_CIRCUIT);
        }
    }
}
