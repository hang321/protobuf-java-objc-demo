package net.hang321.sample.protobuf;

import com.google.protobuf.Timestamp;
import net.hang321.sample.protobuf.data.proto.MtMsgProtos.MtMessage;
import net.hang321.sample.protobuf.service.IMessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Steve Chan
 **/
@RestController
public class MobileTerminatedController {

  private final IMessagingService messagingService;

  @Autowired
  public MobileTerminatedController(IMessagingService messagingService) {
    this.messagingService = messagingService;
  }

  @RequestMapping("/message")
  public void receiveMessage(
      @RequestParam(value = "deviceId") String deviceId,
      @RequestParam(value = "sender") String sender,
      @RequestParam(value = "msg") String message) {

    // simple implementation, skip validation here

    messagingService.receiveMtMessage(deviceId, sender, message);

  }

}
