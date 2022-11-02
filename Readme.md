# About the project
Multimodule spring boot application project to demonstrate a containerized and scalable kafka producer and consumer.End
to end tests with the help of testcontainers.

## Producer module (REST_PRODUCER)

1. Expose endpoint Post /clients to add new client to registry (non-blocking).
2. Input validation and few unit tests as example for validation.
3. Post to kafka broker, client topic. Do not wait for confirmation.
4. Containerized
5. Easily scalable with the help of a load balancer. We can deploy as many producers as we want, however load balancer
   is needed to distribute the load and to unify the endpoint.Ideas how to achieve this in the improvement section.

## Consumer module (DB_CONSUMER)
1. Spring boot application ( No web server).
2. Connects to kafka through a consumer group and listens for new messages to come.
3. Inserts to db after performing simple business logic operation (gross salary calculation). (liquibase for db
   creation)
4. Containerized
5. Can be easily scaled just by deploying more instances, since all consumers are part of the same consumer group.
   However, Multiple partitions should be defined when topic is created in order to see the benefits of scalability of
   consumer.Rule of thumb is number of consumers <= number of partitions.
6. Very resilient, in case of some recoverable failure ( i.e Database may be down), it attempts to retry up to 3 times,
   with the help of retry topics. As last resort it publishes to dead letter topic to ensure no data loss. If other
   exceptions, it goes straight to the dead letter topic.

## Scaling kafka

1. It is a good idea to scale kafka in order to make communication between producer and consumer more reliable.
   Zookeeper can be scaled into having multiple nodes, same as kafka could have multiple nodes/brokers.
2. Add replication factor.
3. The kafka topic could be created via script (not via application as it is now) and number of partitions should be
   provided there. Partitions will provide the scalability in the producer but especially in the consumer level; .
   Because we have specified the consumer group, all instances of consumer
   will belong to the same consumer group. As a result the broker will route different partitions to each of the
   consumers. Same way kafka broker will hande on itself multiple producers and will push messages in round-robin
   fashion to each partition.

## End To End tests module

1. Separate module which uses testcontainers to mimic the entire infrastructure and run e2e tests on top of it. (Except
   zookeper, which is embedded in kafka and doesnt run as separate container)
2. Tests main flow of making a successful post request and verifies that a new entry is inserted in db.
3. Tests a couple scenarios with invalid request body where no client data entries are inserted in database.


   
## Running procedures
### Starting the whole infrastructure

Since images are not pushed to dockerhub they should be built locally. From project root:

1. Optional: Build docker image of producer: **docker image build -t kafka-producer -f Dockerfile-rest-producer .**
2. Optional Build docker image of consumer: **docker image build -t kafka-consumer -f Dockerfile-db-consumer .**
3. Run command to start the whole infrastructure: **docker-compose -f ./dev-scripts/docker-compose.yml up**
4. Make post request on http://localhost:8080/api/clients. Example body  You can test and observe the logs. To check db, use credentials in the docker compose file to connect to clients_db. Query client table. Example valid body:
{
   "name": "Alice",
   "surname": "Thompson",
   "wage": 5500.75,
   "eventTime": "2012-04-23T18:25:43.511Z"
   }


### Running E2E tests

1. Steps 1 and 2 or step 3 above should have been completed because test containers need built producer and consumer images with
   correct tag.
2. Run tests: **./gradlew :end-to-end-tests:test** (Java 11 needed)

### Running unit tests like normally

1. Units tests are ran while building the image. For running separately read below.
2. Unit tests for producer **gradlew :kafka-producer:test** (Java 11 needed)
3. Unit tests for consumer **gradlew :kafka-consumer:test** (Java 11 needed)


## Improvements

Most of the improvements I would  on the producer side.
* Implement load balancing through zuul proxy and eureka service discovery. 
* Producer, it is very simple in this first iteration. It always returns Accepted status to the rest api client. Given how the kafka client behaves, the kafka sending is not fully asynchronous. 
* Evacuation procedure (in case kafka broker is down) is not in place currently. I would go for a solution where failed messages would be stored in db
and have another thread attempt sending those failed messages. This way we do not put too much stress on the memory and risk having out of memory exception, if broker is down for too long. We also do not lose messages.
* Given that everything we are doing in producer side is non-blocking and asynchronous, it would make sense to increase throughput with reactive spring.
* Some more unit tests. 
* Introduce get request to make api testing easier. Even in e2e tests, currently insertion of records is tested through db queries. This can be avoided with addition of a GET /users request. I chose not to do it
since with current architecture producer which exposes the rest interface doesn't need to have access to the db.
* Topic creation currently is done in the app, it is better if done as part of the deployment process.
* Create a new class ClientMessage for internal transportation, to keep it independent of ClientDTO which should serve
  as rest interface only.

