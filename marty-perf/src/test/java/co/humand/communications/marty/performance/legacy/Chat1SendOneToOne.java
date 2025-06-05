/* (C)2025 */
package co.humand.communications.marty.performance.legacy;

import static co.humand.communications.marty.performance.configuration.Configuration.ENV_LEGACY_CHAT;
import static co.humand.communications.marty.performance.configuration.Configuration.MESSAGES_FEEDER;
import static co.humand.communications.marty.performance.configuration.Configuration.MESSAGES_PER_USER;
import static co.humand.communications.marty.performance.configuration.Configuration.ULID_HEADER_GENERATOR;
import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.feed;
import static io.gatling.javaapi.core.CoreDsl.group;
import static io.gatling.javaapi.core.CoreDsl.jmesPath;
import static io.gatling.javaapi.core.CoreDsl.repeat;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;

public class Chat1SendOneToOne {
    private static final HttpRequestActionBuilder createChannelWithUser = http("Chats 1.0: Create One to One")
            .post("/public/api/v1/chats/one-to-one-chats")
            .header("Authorization", "Basic " + ENV_LEGACY_CHAT.apiKey())
            .header("x-request-id", ULID_HEADER_GENERATOR)
            .body(StringBody(
                    """
                    {
                        "user": {
                            "employeeInternalId": "%s"
                        },
                        "otherUser": {
                            "employeeInternalId": "#{employeeInternalId}"
                        }
                    }"""
                            .formatted(ENV_LEGACY_CHAT.botExternalId())))
            .asJson()
            .check(status().in(200, 201))
            .check(jmesPath("chat.id").saveAs("chatId"));

    private static final ChainBuilder sendTextMessageToChannel = repeat(MESSAGES_PER_USER)
            .on(http("Chats 1.0: Send text message to channel")
                    .post("/public/api/v1/chats/#{chatId}/messages")
                    .header("Authorization", "Basic " + ENV_LEGACY_CHAT.apiKey())
                    .header("Idempotency-Key", ULID_HEADER_GENERATOR)
                    .header("x-request-id", ULID_HEADER_GENERATOR)
                    .body(
                            StringBody(
                                    """
                                    {
                                      "message": {
                                        "text": "#{message}",
                                        "type": "TEXT"
                                      },
                                      "senderEmployeeInternalId": "#{sender}"
                                    }"""))
                    .asJson()
                    .check(status().in(201))
                    .check(jmesPath("chatId").isEL("#{chatId}"))
                    .check(jmesPath("text").isEL("#{message}")));

    public static final ScenarioBuilder scenario = scenario("Chats 1.0: Send text message to a user")
            .exitBlockOnFail()
            .on(
                    exec(session -> session.set("sender", ENV_LEGACY_CHAT.botExternalId())),
                    feed(MESSAGES_FEEDER),
                    feed(ENV_LEGACY_CHAT.usersFeeder()),
                    group("Chats 1.0").on(createChannelWithUser, sendTextMessageToChannel));
}
