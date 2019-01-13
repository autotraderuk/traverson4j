# traverson4j

## About

A Java port of the javascript library found https://github.com/basti1302/traverson. Simpler way of navigating and consuming json+hal webservices.

## Getting started

We are now hosted on jcenter! 

Gradle:
```gradle
repositories {
    jcenter()
}
```

traverson4j comes with a http adapter using Apache Http Components 4. So lets use that...

Gradle:
```gradle
'uk.co.autotrader:traverson4j-hc4:1.0.0-rc.2'
```

Maven:
```xml
<dependency>
  <groupId>uk.co.autotrader</groupId>
  <artifactId>traverson4j-hc4</artifactId>
  <version>1.0.0-rc.2</version>
</dependency>
```

### Jackson support
To use the new support for Jackson databind 2, you can include the traverson4j-jackson2 module

Gradle:
```gradle
'uk.co.autotrader:traverson4j-jackson2:1.0.0-rc.2'
```

Maven:
```xml
<dependency>
  <groupId>uk.co.autotrader</groupId>
  <artifactId>traverson4j-jackson2</artifactId>
  <version>1.0.0-rc.2</version>
</dependency>
```

### License

   Copyright 2018 Auto Trader Limited

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

