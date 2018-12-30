package net.hang321.sample.protobuf.service;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.io.BaseEncoding;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.TextFormat;
import com.google.protobuf.Timestamp;
import net.hang321.sample.protobuf.data.domain.MtDataOut;
import net.hang321.sample.protobuf.data.proto.MoAckProtos.MoAck;
import net.hang321.sample.protobuf.data.proto.MoMsgProtos.MoMessage;
import net.hang321.sample.protobuf.data.proto.MtMsgProtos.MtMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jms.Queue;
import java.io.IOException;
import java.util.UUID;

/**
 * @Author: Steve Chan
 **/
@Service
@MessageEndpoint
public class MessagingService implements IMessagingService {

  private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);

  private final JmsTemplate jmsTemplate;

  private final Queue mtQueue;

  private ObjectMapper objectMapper;

  @Autowired
  public MessagingService(JmsTemplate jmsTemplate, Queue mtQueue) {
    this.jmsTemplate = jmsTemplate;
    this.mtQueue = mtQueue;
  }

  @PostConstruct
  protected void init() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    this.objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

    this.objectMapper.registerModule(new JodaModule());
    this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // ISO-8601 instead of unix timestamp format
  }

  public void receiveMtMessage(String deviceId, String sender, String message) {

    // Device device = deviceRegistryService.lookup(deviceId);
    // String uuid = device.getUuid();
    // use hardcoded uuid instead for demo purpose
    String uuid = "a3e3b0af-af85-4633-a0f5-0b10212bdabd";

    // skip logging, message reformatting etc

    MtDataOut mtDataOut = new MtDataOut();
    mtDataOut.setInstanceId(uuid);
    mtDataOut.setContent(message);
    mtDataOut.setSender(sender);

    // send to JMS broker for another Spring integration process.
    jmsTemplate.convertAndSend(this.mtQueue, mtDataOut);
  }

  /**
   * send Mobile Terminated message
   * @param message
   * @return
   */
  @ServiceActivator(inputChannel = "sendMtMsgChannel", outputChannel="sendMtMsgChannelOut")
  public Message<MtMessage> sendMtMessage(Message<String> message) {

    String mtDataOutJson = message.getPayload();
    MtDataOut mtDataOut = null;
    try {
      mtDataOut = objectMapper.readValue(mtDataOutJson, MtDataOut.class);
    } catch (IOException ioe) {
      logger.warn("cannot convert json string back to POJO: MtDataOut string={}", mtDataOutJson);
      throw new IllegalArgumentException("cannot convert json string back to POJO");
    }

    // skip message enrichment or transformation

    final String sender = mtDataOut.getSender();
    final String content = mtDataOut.getContent();
    final String uuid = mtDataOut.getInstanceId();
    logger.info("Sending to MT message: uuid={}, sender={}, content={}", uuid, sender, content);

    MtMessage mtMessage = MtMessage.newBuilder()
        .setSender(sender)
        .setText(content)
        .setDateTime(Timestamp.newBuilder().build())
        .build();

    Message<MtMessage> smMessage = MessageBuilder
        .withPayload(mtMessage)
        .setHeader("instanceId", uuid)
        .build();
    return smMessage;
  }


  // ----------------------

  /**
   * validate Mobile Originated message
   * @param message
   * @return
   */
  @ServiceActivator(inputChannel = "inputMoChannel", outputChannel = "validInputMoChannel")
  public Message<MoMessage> validateMoChannel(Message<byte[]> message) {

    byte[] bytes = message.getPayload();
    logger.info("processing message payload size: {}, content in hex: {}", bytes.length,
        BaseEncoding.base16().encode(bytes));

    MoMessage moMsg = null;
    try {
      moMsg = MoMessage.parseFrom(bytes);
      logger.info("decoded message payload is: {}",  TextFormat.shortDebugString(moMsg));

    } catch (InvalidProtocolBufferException e) {
      logger.error(e.getMessage(), e);
    }

    String error = null;
    if (moMsg != null) {
      String instanceId = new UUID(moMsg.getInstanceIdMsb(), moMsg.getInstanceIdLsb()).toString();
      // skip instanceId from Device Registry lookup. use hardcoded one.
      // other validation checks...
    }

    if (error != null) {
      throw new MessagingException(message, error);
    }

    Message<MoMessage> nextMessage = MessageBuilder.withPayload(moMsg).copyHeaders(message.getHeaders())
        .setHeader("rawPayload", bytes)
        .build();
    return nextMessage;
  }

  /**
   * process MO message after validation
   * @param message
   * @return
   */
  @ServiceActivator(inputChannel="validInputMoChannel", outputChannel="replySuccessChannel")
  // @Transactional
  public Message<MoMessage> saveIncomingMoData(final Message<MoMessage> message) {

    final MoMessage moMsg = message.getPayload();
    byte[] rawPayload = (byte[]) message.getHeaders().get("rawPayload");
    final String rawPayloadString = BaseEncoding.base16().encode(rawPayload);

    String instanceId = new UUID(moMsg.getInstanceIdMsb(), moMsg.getInstanceIdLsb()).toString();
    logger.info("processing incoming MoData for device: instanceId={}", instanceId);

    // skip, saving incoming message to database, further processing ...etc

    return message;
  }

  /**
   * handle if everything success
   * @param message
   * @return
   */
  @ServiceActivator(inputChannel="replySuccessChannel", outputChannel="moAckOutput")
  public Message<MoAck> handleMoSuccess(Message<MoMessage> message) {

    MoMessage moMessage = message.getPayload();
    long originalMsgId = moMessage.getMsgId();

    MoAck moResponse = MoAck.newBuilder()
        .setDateTime(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build())
        .setStatus(true)
        .setMsgId(originalMsgId)
        .build();

    logger.info("sending back Ack message to MO message of client: {}", originalMsgId);

    return MessageBuilder.withPayload(moResponse)
        .copyHeaders(message.getHeaders())
        .build();
  }

  /**
   * handle any error for MO processing
   * @param errorMessage
   * @return
   */
  @ServiceActivator(inputChannel="inputMoErrorChannel", outputChannel="moAckOutput")
  public Message<?> handleMoError(Message<?> errorMessage) {
    MessagingException me = ((MessagingException) errorMessage.getPayload());
    Message<?> failedMessage = me.getFailedMessage();

    String errorDescription = me.getCause().getMessage();
    logger.info("message={}", (errorDescription != null ? errorDescription : me));


    MoAck.Builder builder = MoAck.newBuilder()
        .setDateTime(Timestamp.newBuilder().setSeconds(System.currentTimeMillis()/1000).build())
        .setStatus(false)
        .setError(errorDescription);

    Long originalMsgId = null;
    try {
      byte[] bytes = (byte[]) failedMessage.getPayload();
      MoMessage moMsg = MoMessage.parseFrom(bytes);
      originalMsgId = moMsg.getMsgId();
    } catch (InvalidProtocolBufferException ignore) {
    }
    if (originalMsgId != null) {
      builder.setMsgId(originalMsgId);
    }
    MoAck moAck = builder.build();

    // copy incoming request header, to determine bearer later
    return MessageBuilder.withPayload(moAck)
        .copyHeaders(failedMessage != null ? failedMessage.getHeaders() : null).build();
  }

}
