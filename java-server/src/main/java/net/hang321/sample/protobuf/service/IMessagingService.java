package net.hang321.sample.protobuf.service;

import net.hang321.sample.protobuf.data.proto.MtMsgProtos;
import net.hang321.sample.protobuf.data.proto.MtMsgProtos.MtMessage;
import org.springframework.messaging.Message;

/**
 * @Author: Steve Chan
 **/
public interface IMessagingService {

  void receiveMtMessage(String deviceId, String sender, String message);

  Message<MtMessage> sendMtMessage(Message<String> message);
}
