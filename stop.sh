#! /bin/bash

# 停止接入程序
cd kemao_2
mvn  spring-boot:stop

# 停止关注程序
cd ../subscribe
mvn  spring-boot:stop

# 停止取消关注程序
cd ../unsubscribe
mvn  spring-boot:stop
