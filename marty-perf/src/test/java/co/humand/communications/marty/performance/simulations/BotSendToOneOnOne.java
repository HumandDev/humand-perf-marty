/* (C)2025 */
package co.humand.communications.marty.performance.simulations;

import static co.humand.communications.marty.performance.configuration.Configuration.ENV_MARTY;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.http.HttpDsl.http;

import co.humand.communications.marty.performance.configuration.Configuration;
import co.humand.communications.marty.performance.legacy.Chat1SendOneToOne;
import co.humand.communications.marty.performance.marty.MartySendOneToOne;
import io.gatling.javaapi.core.Assertion;
import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class BotSendToOneOnOne extends Simulation {
    private static final HttpProtocolBuilder httpProtocol =
            http.baseUrl(ENV_MARTY.baseUrl()).acceptHeader("application/json").contentTypeHeader("application/json");

    private static final Assertion assertion = global().failedRequests().count().lt(1L);

    {
        OpenInjectionStep onceUsers = atOnceUsers(Configuration.VIRTUAL_USERS);
        setUp(MartySendOneToOne.scenario.injectOpen(onceUsers), Chat1SendOneToOne.scenario.injectOpen(onceUsers))
                .assertions(assertion)
                .protocols(httpProtocol);
    }
}
