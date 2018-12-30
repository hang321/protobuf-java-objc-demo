package net.hang321.sample.protobuf.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

/**
 * @Author: Steve Chan
 **/
@Component
public class MqttInbound {

  private static final Logger logger = LoggerFactory.getLogger(MqttInbound.class);

  private MqttConfig mqttConfig;

  public MqttInbound(MqttConfig mqttConfig) {
    this.mqttConfig = mqttConfig;
  }

  @Bean
  @Description("Entry to the messaging system through the MQTT inbound adapter for MO message")
  public MessageChannel inputMoChannel() {
    return new DirectChannel();
  }

  @Bean
  @Description("error channel during MO traffic ")
  public MessageChannel inputMoErrorChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel inputMtAckChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel mtAckErrorChannel() {
    return new DirectChannel();
  }

  // ===== helper =====

  @Bean
  protected MqttPahoClientFactory mqttClientFactory() {

    DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
    factory.setServerURIs("tcp://"+mqttConfig.getBrokerHost()+":"+mqttConfig.getBrokerPort());
    factory.setCleanSession(false);
    return factory;
  }

  /**
   * common route to create inbound adapter, with settings:
   *  payloadAsByte = true
   *  QoS = 2
   *  CompletionTimeout = 30 sec
   * @param uniqueClientId
   * @param topic
   * @param outputChannelName
   * @param errorChannelName
   * @return
   */
  private MqttPahoMessageDrivenChannelAdapter createAdapter(final String uniqueClientId, final String topic,
                                                            final String outputChannelName, final String errorChannelName) {

    logger.info("creating inbound MQTT client: uniqueClientId={}, topic={}, outputChannel={}, errorChannel={}",
        uniqueClientId, topic, outputChannelName, errorChannelName);

    DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
    converter.setPayloadAsBytes(true);

    MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
        uniqueClientId, mqttClientFactory(), topic);
    adapter.setCompletionTimeout(30000);
    adapter.setConverter(converter);
    adapter.setQos(2);
    adapter.setOutputChannelName(outputChannelName);
    adapter.setErrorChannelName(errorChannelName);
    return adapter;
  }



  // ===== end point ====
  /**
   * inbound MQTT message-driven channel adapter for others async message
   * @return
   */
  @Bean
  public MessageProducerSupport inboundMo() {

    final String uniqueClientId = mqttConfig.getClientId() + "_moIn";
    final String topic = mqttConfig.getMoRequestTopic();
    return createAdapter(uniqueClientId, topic, "inputMoChannel", "inputMoErrorChannel");
  }

  /**
   * inbound MQTT message-driven channel adapter for MT-ACK
   * @return
   */
  @Bean
  public MessageProducerSupport inboundMtMesageAck() {

    String uniqueClientId = mqttConfig.getClientId() + "_mtAckIn";
    final String topic =  mqttConfig.getMtResponseTopic();
    return createAdapter(uniqueClientId, topic, "inputMtAckChannel", "mtAckErrorChannel");
  }

}
