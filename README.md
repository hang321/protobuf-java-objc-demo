
# Project Structure

This project is demonstrating IoT application data exchange using protobuf to minimize payload and sending via MQTT, 
and the conversion of UUID between Java and Objective-C. 

UUID is used for mqtt topic name and device identifier in mobile app, message-originated (MO) message includes this 
property inside message payload. So Java and objective-C need to understand each other.

UUID can be send as string (32/36 bytes) if size is not a concern. However, in order to minimize it, 
UUID can be divided into 2 8-bytes as most & least significant bit (msb/lsb) which reduce 16 bytes for each message. 

For simplicity, this UUID is hardcoded in this demo.  
UUID: a3e3b0af-af85-4633-a0f5-0b10212bdabd

It consist of 4 parts: protobuf file, java server, android, ios. 

MQTT broker also requires for this project, setup your own or use a public one, e.g. iot.eclipse.org 


# 1. Protobuf file
## source 
see the resources folder. files as *.proto
## binary
From .proto, run the command to generate java file and objective-c (\*.m/*.h) files
 

# Server: Spring boot (java)
This server send MT message to mobile app and receive MO message from mobile app.

# Client 1: android
Receive MT message and send MO message, in java

# Client 2: iOS
Receive MT message and send MO message, in objective-C
