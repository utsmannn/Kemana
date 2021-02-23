## KEMANA - Online Ojek Open Source (like uber or gojek)
# DEPRECATED !!

<p align="center">
  <img src="https://i.ibb.co/g4vzFyt/ezgif-com-optimize.gif"/>
</p>

## Prerequisite
- JDK 8
- MongoDB
- RabbitMQ (optional, your can use cloud)

## How To Build
- Build and run backend-side
- Run rabbitmq server
  - On local server, if connection refuse, delete `NODE_IP_ADDRESS=127.0.0.1` on `/usr/local/etc/rabbitmq/rabbitmq-env.conf` (check on https://stackoverflow.com/a/25734221/8581826 and https://stackoverflow.com/a/50817538/8581826)
  - On local you need create new user with access virtualhost, visit http://localhost:15672/ (user: guest, pass: guest)
  - Or use cloud for alternative, https://www.cloudamqp.com/
- Change url app in `android/base/src/main/java/com/utsman/kemana/base/KEY.kt`
  - `REMOTE_URL` for url your `backend-side`
  - `RABBIT_URL` for rabbitmq url, check format uri https://www.rabbitmq.com/uri-spec.html
  - For cloudamqp, create new instance and copy amqp url, paste in `RABBIT_URL`
- Open `android` project in AndroidStudio, and run app driver and passenger
- Mock gps with Lockito (https://play.google.com/store/apps/details?id=fr.dvilleneuve.lockito&hl=en), use in driver app with gps setting low priority (device only)

## Android
- Architecture
    - MVP
    - Modularization

- Core
    - Notify - https://github.com/isfaaghyth/notify
    - Dexter - https://github.com/Karumi/Dexter
    - RxJava - https://github.com/ReactiveX/RxJava
    - RxAndroid - https://github.com/ReactiveX/RxAndroid

- Maps and Location
    - Mapbox - https://www.mapbox.com/
    - Smart Marker - https://github.com/utsmannn/SmartMarker
    - ReactiveLocation - https://github.com/mcharmas/Android-ReactiveLocation

- Messaging
    - Retrofit - https://square.github.io/retrofit/
    - RabbitMQ - https://www.rabbitmq.com/

## Backend
- Architecture
    - MVC

- Framework
    - Spring - https://spring.io/

- User Authentication
    - Firebase Auth - https://firebase.google.com/docs/auth

- Database
    - Mongodb - https://www.mongodb.com/

- Messaging
    - RabbitMQ - https://www.rabbitmq.com/


---
```
Copyright 2019 Muhammad Utsman

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
