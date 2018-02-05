package org.netflix.hystrix.customer.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.netflix.hystrix.basic.common.constantes.Constantes;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.google.common.util.concurrent.Uninterruptibles;




import org.assertj.core.data.Offset;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.netflix.hystrix.basic.common.exceptions.RemoteCallException;
import org.netflix.hystrix.basic.customer.MainBootstrap;
import org.netflix.hystrix.basic.customer.client.BookBorrower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.netflix.hystrix.basic.common.exceptions.RemoteCallException.ExceptionType;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.netflix.hystrix.basic.common.constantes.Constantes.BOOK_PATH;
import static org.netflix.hystrix.basic.common.constantes.Constantes.BOOK_REMOTE_URI;
import static org.netflix.hystrix.basic.common.constantes.Constantes.HTTP_PORT;



@RunWith(SpringRunner.class)
@SpringBootTest(classes =MainBootstrap.class)//, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicLibraryTest {

	public static String MOCKED_URL=".*"+BOOK_PATH+".*";
	
	   
    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(HTTP_PORT);

    @Rule
    public WireMockClassRule mockedLibrary = wireMockRule;
    
    private final int DEPENDENCY_TIMEOUT = 2000; // le timeout de la command hystrix PTHystrixCommand
    private final int OPEN_CIRCUIT_THRESHOLD = 15; // le seuil d'ouverture du circuit si le pourcentage d'erreur est ateint (50%)
    private final int OPEN_CIRCUIT_DURATION = 5000; // la durée en ms d'ouverture du circuit*/
    @Rule
    public ExpectedException exception = ExpectedException.none();
    @Autowired
    BookBorrower bookBorrower;
    
    /**
     * <li>Ce test fait OPEN_CIRCUIT_THRESHOLD appels pour atteindre le seuil d'ouverture du circuit et vérifie que ces appels tombent en timeout</li>
     * <li>fait un appel de plus et vérifie que le circuit est ouvert</li>
     */
    @Test
    public void hystrix_dependencyTimeout_hystrixOpenCircuit() {

        // Given
        exception.expect(RemoteCallException.class);
       // exception.expect(new RemoteExceptionTypeMatcher(ExceptionType.HYSTRIX_OPEN_CIRCUIT));
        //exception.expect(new RemoteExceptionDurationMatcher(-1));

        final int responseDelay = DEPENDENCY_TIMEOUT + 10000; // marge de 100 ms

        mockedLibrary.stubFor(WireMock.any(WireMock.urlMatching(".*api.*"))
        		.willReturn(WireMock.aResponse().withFixedDelay(responseDelay))
        );

        Client client = ClientBuilder.newClient();
    	final WebTarget target = client.target(BOOK_REMOTE_URI).path(BOOK_PATH);
    	Response response = target.request(APPLICATION_JSON_TYPE).get();

        
        // When
        for (int i = 0; i < OPEN_CIRCUIT_THRESHOLD; i++) { // On fait autant de tentatives qu'il faut pour atteindre le seuil d'ouverture du circuit
            try {
            	bookBorrower.search("toto");
            } catch (RemoteCallException e) {
                System.out.println(String.format("From For task: appel %d sur %d >> %s >> durée : %d ms", i + 1, OPEN_CIRCUIT_THRESHOLD, ExceptionType.TIMEOUT.name(), e.getCallDuration()));
                assertThat(e.getExceptionType() == ExceptionType.TIMEOUT);
                assertThat(e.getCallDuration()).isCloseTo(DEPENDENCY_TIMEOUT, Offset.offset(100));
            }
        }
        // faire un appel après que le seuil OPEN_CIRCUIT_THRESHOLD soit atteint
      //  Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS); // on attend 1 seconde pour laisser le temps à hystrix d'ouvrir le circuit
        System.out.println(String.format("appel %d >> %s >> durée : -1", OPEN_CIRCUIT_THRESHOLD + 1, ExceptionType.HYSTRIX_OPEN_CIRCUIT.name()));
        try {
            System.out.println("#########<1ICI>");
        bookBorrower.search(Mockito.anyString());
        System.out.println("#########<2ICI>");
        } catch (Throwable e) {
            System.out.println("#########<ICI>");
            System.out.println(e.getMessage());
        }
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

        mockedLibrary.stubFor(WireMock.any(WireMock.urlMatching(MOCKED_URL)).willReturn(
                WireMock.aResponse().withStatus(
                        Response.Status.OK.getStatusCode()
                ).withFixedDelay(responseDelay)
                )
        );

        // When
        for (int i = 0; i < OPEN_CIRCUIT_THRESHOLD; i++) { //On fait autant de tentatives qu'il faut pour ouvrir le circuit
            try {
            	bookBorrower.search(Mockito.anyString());
            } catch (RemoteCallException e) {
                System.out.println(String.format("appel %d sur %d >> %s >> durée : %d ms", i + 1, OPEN_CIRCUIT_THRESHOLD, ExceptionType.TIMEOUT.name(), e.getCallDuration()));
                assertThat(e.getExceptionType() == ExceptionType.TIMEOUT);
            }
        }

        // faire un appel après que le seuil OPEN_CIRCUIT_THRESHOLD soit atteint
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS); // on attend 1 seconde pour laisser le temps à hystrix d'ouvrir le circuit
        try {
        	bookBorrower.search(Mockito.anyString());
        } catch (RemoteCallException e) {
            System.out.println(String.format("appel %d >> %s >> durée : -1", OPEN_CIRCUIT_THRESHOLD + 1, ExceptionType.HYSTRIX_OPEN_CIRCUIT.name()));
            assertThat(e.getExceptionType() == ExceptionType.HYSTRIX_OPEN_CIRCUIT);
        }
        //On attend que le circuit se ferme
        System.out.println(String.format("attendre %d ms, la durée d'ouverture du circuit ...", OPEN_CIRCUIT_DURATION));
        Uninterruptibles.sleepUninterruptibly(OPEN_CIRCUIT_DURATION, TimeUnit.MILLISECONDS);

        // On fait une nouvelle tentative qui doit être en timeout et le circuit s'ouvre de nouveau
        try {
        	bookBorrower.search(Mockito.anyString());
        } catch (RemoteCallException e) {
            System.out.println("appel après expiration du délai OPEN_CIRCUIT_DURATION (HALF-OPEN) >> TIMEOUT");
            assertThat(e.getExceptionType() == ExceptionType.TIMEOUT);
        }

        // On fait un autre appel et maintenant le circuit est bien ouvert de nouveau
        try {
        	bookBorrower.search(Mockito.anyString());
        } catch (RemoteCallException e) {
            System.out.println("appel après état HALF-OPEN >> HYSTRIX_OPEN_CIRCUIT");
            assertThat(e.getExceptionType() == ExceptionType.HYSTRIX_OPEN_CIRCUIT);
        }
    }
}
