/* (C)2025 */
package co.humand.communications.marty.performance.configuration;

import static io.gatling.javaapi.core.CoreDsl.csv;

import co.humand.communications.marty.performance.configuration.TargetEnvResolver.EnvInfoMarty;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.Session;
import io.github.jaspeen.ulid.ULID;
import java.util.function.Function;

public class Configuration {
    public static final int VIRTUAL_USERS = Integer.getInteger("virtualUsers", 10);
    public static final int MESSAGES_PER_USER = Integer.getInteger("messagePerUser", 100);
    public static final EnvInfoMarty ENV_MARTY =
            TargetEnvResolver.resolveMartyEnvInfo(System.getProperty("env", "DEV"));
    public static final Function<Session, String> ULID_HEADER_GENERATOR =
            s -> ULID.random().toString();

    public static final FeederBuilder.Batchable<String> MESSAGES_FEEDER =
            csv("data/messages.csv").circular();

    private Configuration() {}
}
