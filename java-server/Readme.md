#Configuration
MQTT broker, set its IP address or domain name in application.yaml
 
 
# send MT message (from spring boot to mobile device)
edit the -d content 
```
# html form
curl -w '\n' -X POST -H "Content-Type:application/x-www-form-urlencoded" http://localhost:8080/message -d "deviceId=1234&sender=Steve&msg=ThisIsTestMessage" 
# OR json
curl -w '\n' -X POST -H "Content-Type:application/json; charset=utf-8" http://localhost:8080/message -d '{"deviceId":"1234","sender":"Steve","message":"This is a test message"}'
```

verification :
To verify MT message send to MQTT, without running mobile app:
mosquitto_sub -t "demo/mt/request/+"



# receive MO message (from mobile device to spring boot)
see log file in MessagingService class. Hexcode and decoded data is logged.