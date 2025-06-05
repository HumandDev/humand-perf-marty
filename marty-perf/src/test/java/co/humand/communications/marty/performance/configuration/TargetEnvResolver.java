/* (C)2025 */
package co.humand.communications.marty.performance.configuration;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static java.util.Objects.requireNonNull;

import io.gatling.javaapi.core.FeederBuilder.Batchable;
import java.util.Locale;
import javax.annotation.Nullable;

public class TargetEnvResolver {
    // Record to store environment-specific information
    public record EnvInfoMarty(String env, String baseUrl, String apiKey, int instanceId, int botUserId) {

        public static EnvInfoMarty build(
                String env,
                @Nullable String baseUrl,
                @Nullable String apiKey,
                @Nullable Integer instanceId,
                @Nullable Integer botUserId) {

            return new EnvInfoMarty(
                    env,
                    requireNonNull(System.getProperty("baseUrl", baseUrl), "Missing baseUrl"),
                    requireNonNull(System.getProperty("apiKey", apiKey), "Missing apiKey"),
                    requireNonNull(Integer.getInteger("instanceId", instanceId), "Missing instanceId"),
                    requireNonNull(Integer.getInteger("botUserId", botUserId), "Missing botUserId"));
        }

        public static EnvInfoMarty fromEnv(String env) {
            return EnvInfoMarty.build(env, null, null, null, null);
        }

        public Batchable<String> usersFeeder() {
            return csv(userCsv()).circular();
        }

        public String userCsv() {
            return "data/marty/%s/users.csv".formatted(env());
        }
    }

    // Resolve environment-specific configuration based on the target environment
    public static EnvInfoMarty resolveMartyEnvInfo(String targetEnv) {
        String targetEnvLowerCase = targetEnv.toLowerCase(Locale.ROOT);
        return switch (targetEnvLowerCase) {
            case "dev" ->
                EnvInfoMarty.build(
                        targetEnvLowerCase,
                        "https://api.dev.humand.co/",
                        "",
                        34,
                        23320);
            case "prod" ->
                EnvInfoMarty.build(
                        targetEnvLowerCase,
                        "https://api-prod.humand.co/",
                        "==",
                        101651,
                        4172874);

            default -> EnvInfoMarty.build(targetEnvLowerCase, null, null, null, null);
        };
    }
}
