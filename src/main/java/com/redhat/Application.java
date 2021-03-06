package com.redhat;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan({"spring"})
public class Application extends RouteBuilder {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public void configure() throws Exception {

    from("timer://foo?period=5000")
        .routeId("kafka-producer")
        .setBody().simple("Hello World at ${date:now:yyyy-MM-dd HH:mm:ss,SSS}")
        .log(">>> ${body}")
        .to("kafka:test?brokers={{kafka.bootstrap-servers}}");

    from("kafka:test?brokers={{kafka.bootstrap-servers}}&groupId={{kafka.consumer.group-id}}")
        .routeId("kafka-consumer")
        .log("Message received from Kafka : ${body}")
        .log("    on the topic ${headers[kafka.TOPIC]}")
        .log("    on the partition ${headers[kafka.PARTITION]}")
        .log("    with the offset ${headers[kafka.OFFSET]}")
        .log("    with the key ${headers[kafka.KEY]}")
       // .to("amq:test?jmsMessageType=Text")
    ;
  }
}
