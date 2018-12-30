package net.hang321.sample.protobuf.data.domain;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * @Author: Steve Chan
 **/
// @Entity
// @Table(name = "mt_data_out")
public class MtDataOut {

  private static final long serialVersionUID = 327359167175967034L;

  @Id
  @GeneratedValue
  private Long id;

//  @Column(name="device_registry_id", nullable=false)
//  private Long deviceRegistryId;

  @Column(name="instance_id", nullable=false)
  private String instanceId;

  @Column
  private String sender;

  @Column()
  private String content;

//  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
//  @Column(name="submit_timestamp", nullable=false)
//  private DateTime submitTimestamp;
//
//  @Column(nullable=false)
//  private String status;
//
//  @Column(name="response_text", nullable=false)
//  private String responseText;
//
//  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
//  @Column(name="done_timestamp", nullable=false)
//  private DateTime doneTimestamp;

  @Version
  @Column
  private int version;

  public MtDataOut() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

//  public Long getDeviceRegistryId() {
//    return deviceRegistryId;
//  }
//
//  public void setDeviceRegistryId(Long deviceRegistryId) {
//    this.deviceRegistryId = deviceRegistryId;
//  }

  public String getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }
}
