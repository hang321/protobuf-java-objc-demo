// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: mtAck.proto

// This CPP symbol can be defined to use imports that match up to the framework
// imports needed when using CocoaPods.
#if !defined(GPB_USE_PROTOBUF_FRAMEWORK_IMPORTS)
 #define GPB_USE_PROTOBUF_FRAMEWORK_IMPORTS 0
#endif

#if GPB_USE_PROTOBUF_FRAMEWORK_IMPORTS
 #import <Protobuf/GPBProtocolBuffers_RuntimeSupport.h>
#else
 #import "GPBProtocolBuffers_RuntimeSupport.h"
#endif

#if GPB_USE_PROTOBUF_FRAMEWORK_IMPORTS
 #import <Protobuf/Timestamp.pbobjc.h>
#else
 #import "google/protobuf/Timestamp.pbobjc.h"
#endif

 #import "MtAck.pbobjc.h"
// @@protoc_insertion_point(imports)

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"

#pragma mark - MtAckRoot

@implementation MtAckRoot

// No extensions in the file and none of the imports (direct or indirect)
// defined extensions, so no need to generate +extensionRegistry.

@end

#pragma mark - MtAckRoot_FileDescriptor

static GPBFileDescriptor *MtAckRoot_FileDescriptor(void) {
  // This is called by +initialize so there is no need to worry
  // about thread safety of the singleton.
  static GPBFileDescriptor *descriptor = NULL;
  if (!descriptor) {
    GPB_DEBUG_CHECK_RUNTIME_VERSIONS();
    descriptor = [[GPBFileDescriptor alloc] initWithPackage:@"demo"
                                                     syntax:GPBFileSyntaxProto3];
  }
  return descriptor;
}

#pragma mark - MtAck

@implementation MtAck

@dynamic hasDateTime, dateTime;
@dynamic instanceIdMsb;
@dynamic instanceIdLsb;
@dynamic msgId;

typedef struct MtAck__storage_ {
  uint32_t _has_storage_[1];
  GPBTimestamp *dateTime;
  uint64_t instanceIdMsb;
  uint64_t instanceIdLsb;
  int64_t msgId;
} MtAck__storage_;

// This method is threadsafe because it is initially called
// in +initialize for each subclass.
+ (GPBDescriptor *)descriptor {
  static GPBDescriptor *descriptor = nil;
  if (!descriptor) {
    static GPBMessageFieldDescription fields[] = {
      {
        .name = "dateTime",
        .dataTypeSpecific.className = GPBStringifySymbol(GPBTimestamp),
        .number = MtAck_FieldNumber_DateTime,
        .hasIndex = 0,
        .offset = (uint32_t)offsetof(MtAck__storage_, dateTime),
        .flags = (GPBFieldFlags)(GPBFieldOptional | GPBFieldTextFormatNameCustom),
        .dataType = GPBDataTypeMessage,
      },
      {
        .name = "instanceIdMsb",
        .dataTypeSpecific.className = NULL,
        .number = MtAck_FieldNumber_InstanceIdMsb,
        .hasIndex = 1,
        .offset = (uint32_t)offsetof(MtAck__storage_, instanceIdMsb),
        .flags = (GPBFieldFlags)(GPBFieldOptional | GPBFieldTextFormatNameCustom),
        .dataType = GPBDataTypeFixed64,
      },
      {
        .name = "instanceIdLsb",
        .dataTypeSpecific.className = NULL,
        .number = MtAck_FieldNumber_InstanceIdLsb,
        .hasIndex = 2,
        .offset = (uint32_t)offsetof(MtAck__storage_, instanceIdLsb),
        .flags = (GPBFieldFlags)(GPBFieldOptional | GPBFieldTextFormatNameCustom),
        .dataType = GPBDataTypeFixed64,
      },
      {
        .name = "msgId",
        .dataTypeSpecific.className = NULL,
        .number = MtAck_FieldNumber_MsgId,
        .hasIndex = 3,
        .offset = (uint32_t)offsetof(MtAck__storage_, msgId),
        .flags = (GPBFieldFlags)(GPBFieldOptional | GPBFieldTextFormatNameCustom),
        .dataType = GPBDataTypeInt64,
      },
    };
    GPBDescriptor *localDescriptor =
        [GPBDescriptor allocDescriptorForClass:[MtAck class]
                                     rootClass:[MtAckRoot class]
                                          file:MtAckRoot_FileDescriptor()
                                        fields:fields
                                    fieldCount:(uint32_t)(sizeof(fields) / sizeof(GPBMessageFieldDescription))
                                   storageSize:sizeof(MtAck__storage_)
                                         flags:GPBDescriptorInitializationFlag_None];
#if !GPBOBJC_SKIP_MESSAGE_TEXTFORMAT_EXTRAS
    static const char *extraTextFormatInfo =
        "\004\001\010\000\002\r\000\003\r\000\004\005\000";
    [localDescriptor setupExtraTextInfo:extraTextFormatInfo];
#endif  // !GPBOBJC_SKIP_MESSAGE_TEXTFORMAT_EXTRAS
    NSAssert(descriptor == nil, @"Startup recursed!");
    descriptor = localDescriptor;
  }
  return descriptor;
}

@end


#pragma clang diagnostic pop

// @@protoc_insertion_point(global_scope)
