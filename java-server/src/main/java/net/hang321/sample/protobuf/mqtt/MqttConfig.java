package net.hang321.sample.protobuf.mqtt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Steve Chan
 **/
@Configuration
public class MqttConfig {

  @Value("${demo.mqtt.broker.host}")
  private String brokerHost;

  @Value("${demo.mqtt.broker.port}")
  private String brokerPort;

  @Value("${demo.mqtt.client.clientId}")
  private String clientId;

  // MO
  @Value("${demo.mqtt.topic.messageOriginated.request}")
  private String moRequestTopic;

  @Value("${demo.mqtt.topic.messageOriginated.response}")
  private String moResponseTopic;

  // MT
  @Value("${demo.mqtt.topic.messageTerminated.request}")
  private String mtRequestTopic;

  @Value("${demo.mqtt.topic.messageTerminated.response}")
  private String mtResponseTopic;


  public String getBrokerHost() {
    return brokerHost;
  }

  public void setBrokerHost(String brokerHost) {
    this.brokerHost = brokerHost;
  }

  public String getBrokerPort() {
    return brokerPort;
  }

  public void setBrokerPort(String brokerPort) {
    this.brokerPort = brokerPort;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getMoRequestTopic() {
    return moRequestTopic;
  }

  public void setMoRequestTopic(String moRequestTopic) {
    this.moRequestTopic = moRequestTopic;
  }

  public String getMoResponseTopic() {
    return moResponseTopic;
  }

  public void setMoResponseTopic(String moResponseTopic) {
    this.moResponseTopic = moResponseTopic;
  }

  public String getMtRequestTopic() {
    return mtRequestTopic;
  }

  public void setMtRequestTopic(String mtRequestTopic) {
    this.mtRequestTopic = mtRequestTopic;
  }

  public String getMtResponseTopic() {
    return mtResponseTopic;
  }

  public void setMtResponseTopic(String mtResponseTopic) {
    this.mtResponseTopic = mtResponseTopic;
  }
}
