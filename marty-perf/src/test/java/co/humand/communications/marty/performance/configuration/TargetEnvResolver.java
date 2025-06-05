/* (C)2025 */
package co.humand.communications.marty.performance.configuration;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static java.util.Objects.requireNonNull;

import io.gatling.javaapi.core.FeederBuilder.Batchable;
import java.util.Locale;
import javax.annotation.Nullable;

public class TargetEnvResolver {
    // Record to store environment-specific information
    public record EnvInfoMarty(
            String env, String baseUrl, String apiKey, int instanceId, int botUserId, String recipientExternalId) {

        public static EnvInfoMarty build(
                String env,
                @Nullable String baseUrl,
                @Nullable String apiKey,
                @Nullable Integer instanceId,
                @Nullable Integer botUserId,
                @Nullable String recipientExternalId) {

            return new EnvInfoMarty(
                    env,
                    requireNonNull(System.getProperty("baseUrl", baseUrl), "Missing baseUrl"),
                    requireNonNull(System.getProperty("apiKey", apiKey), "Missing apiKey"),
                    requireNonNull(Integer.getInteger("instanceId", instanceId), "Missing instanceId"),
                    requireNonNull(Integer.getInteger("botUserId", botUserId), "Missing botUserId"),
                    requireNonNull(
                            System.getProperty("recipientExternalId", recipientExternalId),
                            "Missing recipientExternalId"));
        }

        public static EnvInfoMarty fromEnv(String env) {
            return EnvInfoMarty.build(env, null, null, null, null, null);
        }

        public Batchable<String> usersFeeder() {
            return csv(userCsv()).circular();
        }

        public String userCsv() {
            return "data/marty/%s/users.csv".formatted(env());
        }
    }

    public record EnvInfoLegacyChat(
            String env,
            String baseUrl,
            String apiKey,
            int instanceId,
            int botUserId,
            String botExternalId,
            String recipientExternalId) {
        public static EnvInfoLegacyChat build(
                String env,
                @Nullable String baseUrl,
                @Nullable String apiKey,
                @Nullable Integer instanceId,
                @Nullable Integer botUserId,
                @Nullable String botExternalId,
                @Nullable String recipientExternalId) {

            return new EnvInfoLegacyChat(
                    env,
                    requireNonNull(System.getProperty("baseUrl", baseUrl), "Missing baseUrl"),
                    requireNonNull(System.getProperty("apiKey", apiKey), "Missing apiKey"),
                    requireNonNull(Integer.getInteger("instanceId", instanceId), "Missing instanceId"),
                    requireNonNull(Integer.getInteger("botUserId", botUserId), "Missing botUserId"),
                    requireNonNull(System.getProperty("botExternalId", botExternalId), "Missing botExternalId"),
                    requireNonNull(
                            System.getProperty("recipientExternalId", recipientExternalId),
                            "Missing recipientExternalId"));
        }

        public static EnvInfoLegacyChat fromEnv(String env) {
            return EnvInfoLegacyChat.build(env, null, null, null, null, null, null);
        }

        public Batchable<String> usersFeeder() {
            return csv(userCsv()).circular();
        }

        public String userCsv() {
            return "data/legacy/%s/users.csv".formatted(env());
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
                        "Jb58iYCzGZoQaeftapzXOeYxQsubMVw0",
                        34,
                        23320,
                        "dami");

            default -> EnvInfoMarty.fromEnv(targetEnvLowerCase);
        };
    }

    public static EnvInfoLegacyChat resolveLegacyChatEnvInfo(String targetEnv) {
        String targetEnvLowerCase = targetEnv.toLowerCase(Locale.ROOT);
        return switch (targetEnvLowerCase) {
            case "dev" ->
                EnvInfoLegacyChat.build(
                        targetEnvLowerCase,
                        "https://api.dev.humand.co/",
                        "vA8NFuvvyiPQl2z5MJ0wsWRrtVrmVANk",
                        1750,
                        65940,
                        "chats1",
                        "pablo.nussembaum@humand.co");
            default -> EnvInfoLegacyChat.fromEnv(targetEnvLowerCase);
        };
    }
}
