
## source code
Java and Objective-C source code are generated

## compile for Java
```
$ protoc -I=./src/main/resources --java_out=./src/main/java ./src/main/resources/*.proto
```

## compile for ObjectiveC
```
$ protoc -I=./src/main/resources --objc_out=../objc/proto-demo/objcModel ./src/main/resources/*.proto
```