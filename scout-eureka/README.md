Scout Eureka
=====
[![Build Status](https://travis-ci.org/Kixeye/scout.svg?branch=master)](https://travis-ci.org/Kixeye/scout)

A discovery client that can access service information from Eureka services.

Getting Started
==========

[EurekaServiceDiscoveryClient](https://github.com/Kixeye/scout/blob/master/scout-eureka/src/main/java/com/kixeye/scout/eureka/EurekaServiceDiscoveryClient.java) is the primary class for this library.

Example
==========

```java
try (EurekaServiceDiscoveryClient client = new EurekaServiceDiscoveryClient(eurekaUrl, 5, TimeUnit.SECONDS)) {
	List<EurekaServiceInstanceDescriptor> descriptors = client.describeAll();
	
	// do something with the descriptors
}
```

## Binaries

Example for Maven:

```xml
<dependency>
    <groupId>com.kixeye.scout</groupId>
    <artifactId>scout-eureka</artifactId>
    <version>x.y.z</version>
</dependency>
```

and for Ivy:

```xml
<dependency org="com.kixeye.scout" name="scout-eureka" rev="x.y.z" />
```

and for Gradle:

```groovy
compile 'com.kixeye.scout:scout-eureka:x.y.z'
```

## Build

To build:

```
$ git clone git@github.com:KIXEYE/scout.git
$ cd scout/
$ mvn clean package
```

## Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/KIXEYE/scout/issues).

 
## LICENSE

Copyright 2014 KIXEYE, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.