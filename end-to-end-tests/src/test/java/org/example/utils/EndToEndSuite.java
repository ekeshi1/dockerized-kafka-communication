package org.example.utils;


import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.example.utils.EndToEndSuite.Initializer;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
@ContextConfiguration(initializers = Initializer.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import({

        TestRestFacade.class,
        DbFacade.class
})
public class EndToEndSuite {
    private static final Network SHARED_NETWORK = Network.newNetwork();
    private static KafkaContainer KAFKA;
    private static MySQLContainer DB;

    private static GenericContainer<?> REST_PRODUCER;
    private static GenericContainer<?> DB_CONSUMER;
    @Autowired
    protected TestRestFacade rest;

    @Autowired
    protected DbFacade dbFacade;
    static class Initializer implements
            ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            final var environment = context.getEnvironment();
            KAFKA = createKafkaContainer();
            DB = createMySqlContainer(environment);

            Startables.deepStart(KAFKA, DB).join();
            final var apiExposedPort = requireNonNull(
                    environment.getProperty("api.exposed-port", Integer.class),
                    "API Exposed Port is null"
            );
            REST_PRODUCER = createRestProducer(environment, apiExposedPort);
            DB_CONSUMER = createDbConsumer(environment);


            Startables.deepStart(REST_PRODUCER, DB_CONSUMER).join();
            setPropertiesForConnections(environment);
        }

        private MySQLContainer<?>  createMySqlContainer(Environment environment) {
            return  new MySQLContainer<>(DockerImageName.parse("mysql:5.7"))
                    .withNetwork(SHARED_NETWORK)
                    .withNetworkAliases("db")
                    .withDatabaseName("clients")
                    .withUsername("ek")
                    .withPassword("ek2")
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("Db")))
                    .withExposedPorts(3306);

        }

        private KafkaContainer createKafkaContainer() {
            return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
                    .withNetwork(SHARED_NETWORK)
                    .withEmbeddedZookeeper()
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("Kafka")))
                    .withNetworkAliases("kafka");

        }

        private GenericContainer<?> createRestProducer(
                Environment environment,
                int apiExposedPort
        ) {
            final var apiServiceImage = requireNonNull(
                    environment.getProperty(
                            "image.rest-producer",
                            String.class
                    ),
                    "Rest-Producer image is null"
            );


            return new GenericContainer<>(apiServiceImage)
                    .withEnv("SPRING_KAFKA_PRODUCER_BOOTSTRAP-SERVERS", "kafka:9092")
                    .withExposedPorts(8080)
                    .withNetwork(SHARED_NETWORK)
                    .withNetworkAliases("rest-producer")
                    .withCreateContainerCmdModifier(
                            cmd -> cmd.withHostConfig(
                                    new HostConfig()
                                            .withNetworkMode(SHARED_NETWORK.getId())
                                            .withPortBindings(new PortBinding(
                                                    Ports.Binding.bindPort(apiExposedPort),
                                                    new ExposedPort(8080)
                                            ))
                            )
                    )
                    .waitingFor(
                            Wait.forHttp("/api/actuator/health")
                                    .forStatusCode(200)
                    )
                    .withImagePullPolicy(PullPolicy.defaultPolicy())
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("rest-producer")));
        }

        private GenericContainer<?> createDbConsumer(Environment environment) {
            final var dbConsumerImage = requireNonNull(
                    environment.getProperty(
                            "image.db-consumer",
                            String.class
                    ),
                    "Rest consumer image is null"
            );

            return new GenericContainer<>(dbConsumerImage)
                    .withNetwork(SHARED_NETWORK)
                    .withEnv("SPRING_DATASOURCE_URL", format("jdbc:mysql://db:%s/%s","3306", DB.getDatabaseName()))
                    .withEnv("SPRING_KAFKA_CONSUMER_BOOTSTRAP-SERVERS", "kafka:9092")
                    .withEnv("SPRING_DATASOURCE_USERNAME", DB.getUsername())
                    .withEnv("SPRING_DATASOURCE_PASSWORD", DB.getPassword())
                    .withImagePullPolicy(PullPolicy.defaultPolicy())
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("db-consumer")));
        }

        private void setPropertiesForConnections(ConfigurableEnvironment environment) {
            environment.getPropertySources().addFirst(
                    new MapPropertySource(
                            "testcontainers",
                            Map.of(
                                    "spring.kafka.producer.bootstrap-servers", KAFKA.getBootstrapServers(),

                                    "spring.datasource.url", format("jdbc:mysql://localhost:%s/clients",DB.getMappedPort(3306)),
                                    "spring.datasource.username", DB.getUsername(),
                                    "spring.datasource.password", DB.getPassword(),

                                    "api.host", REST_PRODUCER.getHost()
                            )
                    )
            );
        }
    }
}
