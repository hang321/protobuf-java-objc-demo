package net.hang321.sample.protobuf.mqtt;

import com.google.common.io.BaseEncoding;
import net.hang321.sample.protobuf.data.proto.MoAckProtos.MoAck;
import net.hang321.sample.protobuf.data.proto.MtMsgProtos.MtMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @Author: Steve Chan
 **/
@Component
public class MqttOutbound {

  private static final Logger logger = LoggerFactory.getLogger(MqttOutbound.class);

  private MqttConfig mqttConfig;

  public MqttOutbound(MqttConfig mqttConfig) {
    this.mqttConfig = mqttConfig;
  }

  @Bean
  public MqttPahoClientFactory mqttClientFactory() {
    //logger.info("broker: host={}, port={}", mqttConfig.getBrokerHost(), mqttConfig.getBrokerPort());

    DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
    factory.setServerURIs("tcp://"+mqttConfig.getBrokerHost()+":"+mqttConfig.getBrokerPort());
    factory.setCleanSession(false);
    return factory;
  }

  @Bean
  @ServiceActivator(inputChannel = "mqttOutput")
  public MessageHandler sendOutgoingMessage() {
    MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(mqttConfig.getClientId()+"_out", mqttClientFactory());
    messageHandler.setAsync(true);
    messageHandler.setAsyncEvents(true);
    messageHandler.setDefaultQos(2);
    return messageHandler;
  }

  @ServiceActivator(inputChannel = "sendMtMsgChannelOut", outputChannel = "mqttOutput")
  public Message<byte[]> onMtMessage(Message<MtMessage> message) {

    MtMessage mtDataMessage = message.getPayload();

    String instanceId = (String) message.getHeaders().get("instanceId");
    String responseTopic = mqttConfig.getMtRequestTopic() + "/" + instanceId;

    byte[] payload = mtDataMessage.toByteArray();

    return MessageBuilder.withPayload(payload)
        .copyHeaders(message.getHeaders())
        .setHeader(MqttHeaders.TOPIC, responseTopic)
        .build();
  }


  @ServiceActivator(inputChannel="moAckOutput", outputChannel="mqttOutput")
  public Message<byte[]> onReplyGeneralMessage(Message<MoAck> message) {

    MoAck moResponseMessage = message.getPayload();

    String requestTopic = (String) message.getHeaders().get(MqttHeaders.TOPIC);
    String deviceId = requestTopic.substring(requestTopic.lastIndexOf("/") + 1);
    String responseTopic = mqttConfig.getMoResponseTopic() + "/" + deviceId;

    byte[] payload = moResponseMessage.toByteArray();
    logger.debug("outbound topic:{}, payload: {}", responseTopic, BaseEncoding.base16().encode(payload));

    return MessageBuilder.withPayload(payload)
        .copyHeaders(message.getHeaders())
        .setHeader(MqttHeaders.TOPIC, responseTopic)
        .build();
  }

}
