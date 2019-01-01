package net.hang321.sample.protobuf;

import com.google.protobuf.Timestamp;
import net.hang321.sample.protobuf.data.proto.MtMsgProtos.MtMessage;
import net.hang321.sample.protobuf.service.IMessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

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

  /**
   * traditional www-form-urlencoded
   * @param deviceId
   * @param sender
   * @param message
   */
  @RequestMapping("/message")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void receiveMessage(
      @RequestParam(value = "deviceId") String deviceId,
      @RequestParam(value = "sender") String sender,
      @RequestParam(value = "msg") String message) {

    // simple implementation, skip validation here

    messagingService.receiveMtMessage(deviceId, sender, message);

  }


  /**
   * Json request
   * @param dto
   */
  @PostMapping("/message")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void receiveMessage(@RequestBody RcvMsgDto dto) {

    // simple implementation, skip validation here
    messagingService.receiveMtMessage(dto.getDeviceId(), dto.getSender(), dto.getMessage());

  }


}

class RcvMsgDto {

  String deviceId;
  String sender;
  String message;

  public RcvMsgDto() {
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}