package net.hang321.sample.protobuf.jms;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import net.hang321.sample.protobuf.service.LogErrorHandler;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import java.util.Arrays;

/**
 * @Author: Steve Chan
 **/
@Configuration
@EnableJms
public class JmsConfig {

  @Bean
  public CachingConnectionFactory connectionFactory() {
    ActiveMQConnectionFactory amqcf = new ActiveMQConnectionFactory();
    amqcf.setBrokerURL("vm://localhost?broker.persistent=false");
    amqcf.setNonBlockingRedelivery(true);
    amqcf.setTrustedPackages(Arrays.asList(
        "net.hang321.sample.protobuf.data.domain",
        "com.google.protobuf"));

    // Configure the re-delivery policy and the dead letter queue.
    RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
    redeliveryPolicy.setInitialRedeliveryDelay(0);
    redeliveryPolicy.setRedeliveryDelay(60_000);  // 1 minute
    redeliveryPolicy.setMaximumRedeliveries(5);  // adjust this
    redeliveryPolicy.setUseExponentialBackOff(true);
    redeliveryPolicy.setBackOffMultiplier(2);
    amqcf.setRedeliveryPolicy(redeliveryPolicy);

    CachingConnectionFactory connectionFactory = new CachingConnectionFactory(amqcf);
    connectionFactory.setSessionCacheSize(10);
    connectionFactory.setCacheProducers(false);
    return connectionFactory;
  }

  @Bean
  public JmsListenerContainerFactory<?> jmsListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                    DefaultJmsListenerContainerFactoryConfigurer configurer,
                                                                    LogErrorHandler errorHandler) {

    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    // This provides all boot's default to this factory, including the message converter
    factory.setErrorHandler(errorHandler);
    configurer.configure(factory, connectionFactory);
    return factory;
  }


  /** Serialize message content to json using TextMessage */
  @Bean
  public MessageConverter jacksonJmsMessageConverter() {
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setTargetType(MessageType.TEXT);
    converter.setTypeIdPropertyName("_type");

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // ISO-8601 instead of unix timestamp format
    objectMapper.registerModule(new JodaModule());

    converter.setObjectMapper(objectMapper);
    return converter;
  }



  @Bean
  public JmsTemplate JmsTemplate() {
    JmsTemplate template = new JmsTemplate(connectionFactory());
    template.setMessageConverter(jacksonJmsMessageConverter());
    template.setExplicitQosEnabled(true);
    return template;
  }

  // queue use within application declare here. DLQ declare in activemq.xml explicitly for monitoring purpose.
  @Bean
  public Queue mtQueue() {
    return new ActiveMQQueue("net.hang321.sample.protobuf.mt");
  }

  @Bean
  public Queue moQueue() {
    return new ActiveMQQueue("net.hang321.sample.protobuf.mo");
  }
}
