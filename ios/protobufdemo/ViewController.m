//
//  ViewController.m
//  protobufdemo
//
//  Created by Steve Chan on 31/12/18.
//  Copyright © 2018 HanG321.net. All rights reserved.
//

#import "ViewController.h"
#import "ChatCell.h"

#import "objcModel/MtMessage.pbobjc.h"
#import "objcModel/MoMessage.pbobjc.h"

@interface ViewController ()
@property (weak, nonatomic) IBOutlet UILabel *status;
@property (weak, nonatomic) IBOutlet UITextField *message;
@property (weak, nonatomic) IBOutlet UIButton *sendButton;
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (strong, nonatomic) NSDictionary *mqttSettings;
@property (strong, nonatomic) NSMutableArray *chats;

@property (strong, nonatomic) MQTTSessionManager *manager;
@property (strong, nonatomic) NSString *clientId;
@end

@implementation ViewController

- (void)viewDidLoad {
  [super viewDidLoad];
  
  // hardcode here
  // NSString *clientId    = [[[[UIDevice currentDevice] identifierForVendor] UUIDString] lowercaseString];
  self.clientId = @"a3e3b0af-af85-4633-a0f5-0b10212bdabd";
  [self storeMsbLsbOnRegister];
  
  NSURL *bundleURL = [[NSBundle mainBundle] bundleURL];
  NSURL *mqttPlistUrl = [bundleURL URLByAppendingPathComponent:@"mqtt.plist"];
  self.mqttSettings = [NSDictionary dictionaryWithContentsOfURL:mqttPlistUrl];
  DDLogInfo(@"mqttSettings: %@", self.mqttSettings.description);
  
  self.chats = [[NSMutableArray alloc] init];
  
  self.tableView.delegate = self;
  self.tableView.dataSource = self;
  self.tableView.estimatedRowHeight = 150;
  self.tableView.rowHeight = UITableViewAutomaticDimension;
  
  self.message.delegate = self;
  
  
  if (!self.manager) {
    self.manager = [[MQTTSessionManager alloc] init];
    self.manager.delegate = self;
    self.manager.subscriptions = [NSDictionary dictionaryWithObject:[NSNumber numberWithInt:MQTTQosLevelExactlyOnce]
                                                             forKey:[NSString stringWithFormat:@"demo/mt/request/%@", self.clientId]];
    
    MQTTSSLSecurityPolicy *policy = [MQTTSSLSecurityPolicy policyWithPinningMode:MQTTSSLPinningModeNone];
    policy.allowInvalidCertificates = YES;
    policy.validatesCertificateChain = NO;
    policy.validatesDomainName = NO;
    
    [self.manager connectTo:self.mqttSettings[@"host"]
                       port:[self.mqttSettings[@"port"] intValue]
                        tls:[self.mqttSettings[@"tls"] boolValue]
                  keepalive:540
                      clean:false
                       auth:false
                       user:nil
                       pass:nil
//                       will:true
//                  willTopic:[NSString stringWithFormat:@"demo/will/%@", self.clientId]
//                    willMsg:[@"offline" dataUsingEncoding:NSUTF8StringEncoding]
                       will:false
                  willTopic:nil
                    willMsg:nil
                    willQos:MQTTQosLevelExactlyOnce
             willRetainFlag:false
               withClientId:self.clientId
             securityPolicy:policy
               certificates:nil
              protocolLevel:MQTTProtocolVersion311
             connectHandler:^(NSError *error) { DDLogError(@"error %@", error); } ];
    
  } else {
    [self.manager connectToLast:^(NSError *error) { DDLogError(@"error %@", error); } ];
  }
  
  /*
   * MQTTCLient: observe the MQTTSessionManager's state to display the connection status
   */
  [self.manager addObserver:self
                 forKeyPath:@"state"
                    options:NSKeyValueObservingOptionInitial | NSKeyValueObservingOptionNew
                    context:nil];

}


- (void) observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context {
  switch (self.manager.state) {
    case MQTTSessionManagerStateClosed:
      self.status.text = @"closed";
      break;
    case MQTTSessionManagerStateClosing:
      self.status.text = @"closing";
      break;
    case MQTTSessionManagerStateConnected:
      self.status.text = [NSString stringWithFormat:@"connected as %@-%@",
                          [UIDevice currentDevice].name,
                          self.tabBarItem.title];
      [self.manager sendData:[@"joins chat" dataUsingEncoding:NSUTF8StringEncoding]
                       topic:[NSString stringWithFormat:@"demo/mo/request/%@", self.clientId]
                         qos:MQTTQosLevelExactlyOnce
                      retain:FALSE];
      
      break;
    case MQTTSessionManagerStateConnecting:
      self.status.text = @"connecting";
      break;
    case MQTTSessionManagerStateError:
      self.status.text = @"error";
      break;
    case MQTTSessionManagerStateStarting:
    default:
      self.status.text = @"not connected";
      break;
  }

}

/**
 *  MQTTClient: send data to broker
 */
- (IBAction)send:(id)sender {

  GPBTimestamp *timestamp = [[GPBTimestamp alloc] init];
  timestamp.seconds = [[NSDate date] timeIntervalSince1970];
  
  NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
  long long msb = [userDefaults integerForKey:@"msb"];
  long long lsb = [userDefaults integerForKey:@"lsb"];
  
  MoMessage *moMsg = [[MoMessage alloc] init];
  moMsg.dateTime = timestamp;
  moMsg.instanceIdMsb = msb;
  moMsg.instanceIdLsb = lsb;

  moMsg.recipient = @"hard-coded-recipient";
  moMsg.text = self.message.text;

  NSData *data = [moMsg data];
  // convert NSData to hexadecimal string, then back to NSData so that Java server can understand the contentt
  NSString *hexString = [self convertNSDataToHexString:data];
  data = [self convertHexStringToNSData:hexString];
  
  [self.manager sendData:data
                   topic:[NSString stringWithFormat:@"demo/mo/request/%@", self.clientId]
                     qos:MQTTQosLevelExactlyOnce
                  retain:FALSE];
  
}

/*
 * MQTTSessionManagerDelegate
 */
- (void)handleMessage:(NSData *)data onTopic:(NSString *)topic retained:(BOOL)retained {
  // MQTTClient: process received message
  
  NSError *error          = nil;
  MtMessage *mtMsg        = [[MtMessage alloc] initWithData:data error:&error];
  NSString *des           = [mtMsg debugDescription];
  DDLogDebug(@"MT Message received = %@", des);
  
  NSString *sender = [mtMsg.sender description];
  NSString *content = [mtMsg.text description];
  
  [self.chats insertObject:[NSString stringWithFormat:@"%@:  %@", sender, content] atIndex:0];
  [self.tableView reloadData];
}

/*
 * UITableViewDelegate
 */
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
  return NO;
}

- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
  return NO;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
  ChatCell *cell = [self.tableView dequeueReusableCellWithIdentifier:@"line"];
  cell.textView.text = self.chats[indexPath.row];
  // DDLogDebug(@"chat text: %@", self.chats[indexPath.row]);
  return cell;
}

/*
 * UITableViewDataSource
 */
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
  return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
  return self.chats.count;
}





 /**
  * calcuate and store the most & least significant bit on startup. only need to run once.
  */
 - (void)storeMsbLsbOnRegister {
 
 // generate random UUID
 // NSUUID *uuid = [NSUUID UUID];
 
 // convert back from generated clientId string to UUID class
 NSUUID *uuid = [[NSUUID alloc] initWithUUIDString:self.clientId];
 uuid_t uuidBytes;
 [uuid getUUIDBytes:uuidBytes];
 NSData *uuidData = [NSData dataWithBytes:uuidBytes length:16];
 // separated into 2 byte arrays, later convert to longlong type.
 NSData *msbBytes = [uuidData subdataWithRange:NSMakeRange(0, 8)];
 NSData *lsbBytes = [uuidData subdataWithRange:NSMakeRange(8, 8)];
 DDLogDebug(@"generated UUID -> %@", uuid.description);
 DDLogDebug(@"uuidData msbBytes - > %@, lsbBytes -> %@", msbBytes.description, lsbBytes.description);
 
 long long msb = [self convertNSDataToLong:msbBytes];
 long long lsb = [self convertNSDataToLong:lsbBytes];
 // msb & lsb will be saved in registerResponse below.
 DDLogDebug(@"uuidData msb - > %lld, lsb -> %lld", msb, lsb);
 
 NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
 [userDefaults setObject:[NSNumber numberWithLongLong:lsb] forKey:@"lsb"];
 [userDefaults setObject:[NSNumber numberWithLongLong:msb] forKey:@"msb"];
 [userDefaults synchronize];
 
 }


// ====== Util function, here for demo purpose instead of standalone class =======

- (long) convertNSDataToLong:(NSData *)data {
  unsigned char bytes[8];
  [data getBytes:bytes length:8];
  long n = (long)bytes[0] << 56;
  n |= (long)bytes[1] << 48;
  n |= (long)bytes[2] << 40;
  n |= (long)bytes[3] << 32;
  n |= (long)bytes[4] << 24;
  n |= (long)bytes[5] << 16;
  n |= (long)bytes[6] << 8;
  n |= (long)bytes[7];
  return n;
}

- (NSData *) convertLongToNSData:(long)data {
  Byte *buf = (Byte*)malloc(8);
  for (int i=7; i>=0; i--) {
    buf[i] = data & 0x00000000000000ff;
    data = data >> 8;
  }
  NSData *result =[NSData dataWithBytes:buf length:8];
  return result;
}


- (NSString*) convertNSDataToHexString:(NSData *) data {
  NSUInteger dataLength = [data length];
  NSMutableString *string = [NSMutableString stringWithCapacity:dataLength*2];
  const unsigned char *dataBytes = [data bytes];
  for (NSInteger idx = 0; idx < dataLength; ++idx) {
    [string appendFormat:@"%02x", dataBytes[idx]];
  }
  return string;
}

- (NSData *)convertHexStringToNSData:(NSString *)string {
  string = [string lowercaseString];
  NSMutableData *data= [NSMutableData new];
  unsigned char whole_byte;
  char byte_chars[3] = {'\0','\0','\0'};
  int i = 0;
  NSUInteger length = string.length;
  while (i < length-1) {
    char c = [string characterAtIndex:i++];
    if (c < '0' || (c > '9' && c < 'a') || c > 'f')
      continue;
    byte_chars[0] = c;
    byte_chars[1] = [string characterAtIndex:i++];
    whole_byte = strtol(byte_chars, NULL, 16);
    [data appendBytes:&whole_byte length:1];
  }
  return data;
}

@end
