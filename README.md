#Simple nio chat

##Build
Use:
- jdk 1.8
- maven 3.5
```
mvn package
```

##Launch

###Chat server
```
java -jar server//target//rtb_chat_server-1.0.jar 5555
```
- `5555` - port for listen.

###Chat client
```
java -jar client//target//rtb_chat_client-1.0.jar localhost 5555
```
- `localhost` - destination host.
- `5555` - destination port.

###Chat stress test
```
java -jar stresstest//target//rtb_chat_stress_test-1.0.jar localhost 5555 1000
```
- `localhost` - destination host.  
- `5555` - destination port.  
- `1000` - number of bots.  