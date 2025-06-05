/* (C)2025 */
package co.humand.communications.marty.performance.marty;

import static co.humand.communications.marty.performance.configuration.Configuration.ENV_MARTY;
import static co.humand.communications.marty.performance.configuration.Configuration.MESSAGES_FEEDER;
import static co.humand.communications.marty.performance.configuration.Configuration.MESSAGES_PER_USER;
import static co.humand.communications.marty.performance.configuration.Configuration.ULID_HEADER_GENERATOR;
import static io.gatling.javaapi.core.CoreDsl.StringBody;
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

public class MartySendOneToOne {
    private static final HttpRequestActionBuilder createChannelWithUser = http("Chats 2.0: Create channel")
            .post("/api/v1/marty/conversations.open")
            .header("Authorization", "Basic " + ENV_MARTY.apiKey())
            .header("Idempotency-Key", ULID_HEADER_GENERATOR)
            .header("x-request-id", ULID_HEADER_GENERATOR)
            .body(StringBody("""
                    {"external_ids": ["#{employeeInternalId}"]}"""))
            .asJson()
            .check(status().in(200, 201))
            .check(jmesPath("ok").ofBoolean().is(true))
            .check(jmesPath("channel.id").saveAs("channelId"));

    private static final ChainBuilder sendTextMessageToChannel = repeat(MESSAGES_PER_USER)
            .on(http("Chats 2.0: Send text message to channel")
                    .post("/api/v1/marty/chat.postMessage")
                    .header("Authorization", "Basic " + ENV_MARTY.apiKey())
                    .header("Idempotency-Key", ULID_HEADER_GENERATOR)
                    .header("x-request-id", ULID_HEADER_GENERATOR)
                    .body(
                            StringBody(
                                    """
                    {
                        "channel": "#{channelId}",
                        "text": "#{message}"
                    }"""))
                    .asJson()
                    .check(status().in(201))
                    .check(jmesPath("ok").ofBoolean().is(true)));

    public static final ScenarioBuilder scenario = scenario("Chats 2.0: Send text message to a user")
            .exitBlockOnFail()
            .on(
                    feed(MESSAGES_FEEDER),
                    feed(ENV_MARTY.usersFeeder()),
                    group("Chats 2.0").on(createChannelWithUser, sendTextMessageToChannel));
}
