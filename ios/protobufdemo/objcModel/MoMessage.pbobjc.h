// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: moMessage.proto

// This CPP symbol can be defined to use imports that match up to the framework
// imports needed when using CocoaPods.
#if !defined(GPB_USE_PROTOBUF_FRAMEWORK_IMPORTS)
 #define GPB_USE_PROTOBUF_FRAMEWORK_IMPORTS 0
#endif

#if GPB_USE_PROTOBUF_FRAMEWORK_IMPORTS
 #import <Protobuf/GPBProtocolBuffers.h>
#else
 #import "GPBProtocolBuffers.h"
#endif

#if GOOGLE_PROTOBUF_OBJC_VERSION < 30002
#error This file was generated by a newer version of protoc which is incompatible with your Protocol Buffer library sources.
#endif
#if 30002 < GOOGLE_PROTOBUF_OBJC_MIN_SUPPORTED_VERSION
#error This file was generated by an older version of protoc which is incompatible with your Protocol Buffer library sources.
#endif

// @@protoc_insertion_point(imports)

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"

CF_EXTERN_C_BEGIN

@class GPBTimestamp;

NS_ASSUME_NONNULL_BEGIN

#pragma mark - MoMessageRoot

/**
 * Exposes the extension registry for this file.
 *
 * The base class provides:
 * @code
 *   + (GPBExtensionRegistry *)extensionRegistry;
 * @endcode
 * which is a @c GPBExtensionRegistry that includes all the extensions defined by
 * this file and all files that it depends on.
 **/
@interface MoMessageRoot : GPBRootObject
@end

#pragma mark - MoMessage

typedef GPB_ENUM(MoMessage_FieldNumber) {
  MoMessage_FieldNumber_MsgId = 1,
  MoMessage_FieldNumber_DateTime = 2,
  MoMessage_FieldNumber_InstanceIdMsb = 3,
  MoMessage_FieldNumber_InstanceIdLsb = 4,
  MoMessage_FieldNumber_Recipient = 5,
  MoMessage_FieldNumber_Text = 6,
};

@interface MoMessage : GPBMessage

@property(nonatomic, readwrite) int64_t msgId;

@property(nonatomic, readwrite, strong, null_resettable) GPBTimestamp *dateTime;
/** Test to see if @c dateTime has been set. */
@property(nonatomic, readwrite) BOOL hasDateTime;

/** most / least significant bits */
@property(nonatomic, readwrite) uint64_t instanceIdMsb;

@property(nonatomic, readwrite) uint64_t instanceIdLsb;

/** add "repeated" keyword for multiple recipients */
@property(nonatomic, readwrite, copy, null_resettable) NSString *recipient;

/** content */
@property(nonatomic, readwrite, copy, null_resettable) NSString *text;

@end

NS_ASSUME_NONNULL_END

CF_EXTERN_C_END

#pragma clang diagnostic pop

// @@protoc_insertion_point(global_scope)