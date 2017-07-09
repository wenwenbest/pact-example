package example.vehicles;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactHttpsProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class VehiclesRegistryTest {

    @Rule
    public PactHttpsProviderRuleMk2 hamburgDataProvider =
            new PactHttpsProviderRuleMk2("hamburg-data-provider", "localhost",8080, this);

    @Pact(consumer="info-app-service")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        return builder
                .given("data from 2017 exists")
                .uponReceiving("a request for all vehicle registrations in hamburg")
                .path("/vehicles/registrations/all")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body("{\"city\": \"Hamburg\"," +
                        "\"gasoline\": " +
                                    "{\"2017\": 100}," +
                        "\"diesel\": " +
                                     "{\"2017\": 10}" +
                        "}")
                .toPact();
    }

    @Test
    @PactVerification("hamburg-data-provider")
    public void shouldRetriveTheVehiclesInformationFromHamburgService() throws Exception {
        VehiclesRegistry vehiclesRegistry = new VehiclesRegistry();

        Vehicles vehicles = vehiclesRegistry.informationFor("Hamburg");

        assertThat(vehicles.getCity(), is("Hamburg"));
        assertThat(vehicles.getDiesel().get(2017), is(10L));
        assertThat(vehicles.getGasoline().get(2017), is(100L));
    }
}