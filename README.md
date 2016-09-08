# Scala flume client

[![Download](https://api.bintray.com/packages/eyeem/maven/flume-client/images/download.svg) ](https://bintray.com/eyeem/maven/flume-client/_latestVersion)[![Build Status](https://travis-ci.org/eyeem/scala-flume-client.svg?branch=master)](https://travis-ci.org/eyeem/scala-flume-client)

A tiny Scala library to send events and entities to [Apache Flume](https://flume.apache.org/).  

## Intro

This library differentiates between events and entities:

* Events are information about something that happened at a certain time with attached metadata. (most common use case)
    * At [EyeEm](https://www.eyeem.com/) we send events to Flume e.g. when a user uploads a photo.  
* Entities are considered static, like the deterministic output of a function with version X on some data Y. (less common use case)
    * At [EyeEm](https://www.eyeem.com/) we use this to store the result of expensive deep learning model computations on photos. E.g. photoId=123 contains ["tree", "mountain", "lake"] by keyword function version 3.0. This allows us to do further analysis on the keywords stored in Hadoop later.

## Installation 

Add following lines to your ```build.sbt```: 

```scala
resolvers += Resolver.bintrayRepo("eyeem", "maven")

libraryDependencies += "com.eyeem" %% "flume-client" % "0.1.0"
```

Override these defaults in your application.conf if needed:

```
flume {
  applicationName = "override_your_application_name"
  host = "override_your_hostname"
  portEvent = 9091 # events are sent to this port
  portEntity = 9092 # entities are sent to this port
  enabled = true
  threadPoolSize = 10
}
```

#### Dependency Matrix:

* This project only supports Scala 2.11.x. If you need Scala 2.10 support please open an issue.
* This project was built with Java 8. If you wish to use this library in a Java 7 project let us know by opening an issue.

| module                       | dependsOn                | version  |
| ---------------------------- | ------------------------ | -------- |
| flume-client                 | play-json                | 2.5.x    |
|                              | type-safe config         | >= 1.3.0 |
|                              | pureconfig               | 0.1.9    |
|                              | libthrift                | 0.9.3    |

There is a hard dependency on play-json. If you would like to have support for other json libraries, please open an issue or pull request.

## Usage

You may initialize a FlumeReporter in two ways:

```scala
import com.eyeem.flume.client.FlumeReporter

val flumeReporter = new FlumeReporter() // make sure to override at a minimum flume.applicationName and flume.host in your configuration file
```

or

```scala
import com.eyeem.flume.client.FlumeReporter

val flumeConfig = FlumeConfig(applicationName = "myAppName", host = "myserver.mydomain.overrideMe")
val flumeReporter = new FlumeReporter(flumeConfig) 
```


### Sending Events

```scala
import com.eyeem.flume.client.FlumeReporter
import play.api.libs.json.Json

import scala.concurrent.Future

case class MyEvent(someAttribute: String, otherAttribute: Boolean)

object MyEvent {
  implicit val myEventFormat = Json.format[MyEvent]
}

class SomeClass {

  // initialize your flumeReporter

  def sendMyEvent(): Future[Unit] = {

    // create an event
    val myEvent = MyEvent(someAttribute = "something happened", otherAttribute = true)
    val jsonData = Json.toJson(myEvent)

    // send the event to Flume
    flumeReporter.postEvent("eventName", jsonData)
  }

}
```

The above will send the following json payload to localhost:9091 with headers `application = "myAppName"`:

```
{
  "event_name": "eventName",
  "timestamp": "2016-07-25T18:18:08.180+02:00",
  "user_id": "myAppName",
  "salt": 873375028,
  "data": {
    "someAttribute": "something happened",
    "otherAttribute": true
  }
}
```


### Sending Entities

```scala
import com.eyeem.flume.client.models.FlumeEntity
import com.eyeem.flume.client.FlumeReporter
import play.api.libs.json.Json

import scala.concurrent.Future

case class MyEntity(someAttribute: String, otherAttribute: Boolean)

object MyEntity {
  implicit val myEventFormat = Json.format[MyEntity]
}

class SomeClass {

  // initialize your flumeReporter

  def sendMyEntity(): Future[Unit] = {

    // create a static entity
    val entity = MyEntity(someAttribute = "something happened", otherAttribute = true)
    val entityJson = Json.toJson(entity)
    
    // send an entity
    flumeReporter.postEntity(FlumeEntity("entityName", "v2", entityJson))

  }

}
```

The above will send the following to localhost:9092:

Headers:
* `application = "myAppName"`
* `entity = "entityName"`
* `version = "v2"`

Body:
```
{
  "someAttribute": "something happened",
  "otherAttribute": true
}
```

### Note on Futures and execution context

Both `postEvent` and `postEntity` functions execute asynchronously and return a Scala `Future[Unit]`. This Future will fail if an exception occurs (e.g. incorrect hostname set) - keep this in mind when composing futures. 
Since the underlying calls to Thrift are blocking, it uses a separate execution context (fixed thread pool) configurable through your `application.conf` to avoid introducing blocking code on your default execution context.

## License

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).

## Authors

* [dev-tim](https://github.com/dev-tim)
* [jschaul](https://github.com/jschaul)

## Contributing

Pull requests and issues welcome.

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

### Tests

* `sbt test` for regular tests
* `sbt it:test` runs integration tests (They require a running Flume ingestionAgent. Configuration for that is not part of this repo). Tested with `apache-flume-1.6.0`.

### Change the Thrift code

Java code under `src/main/java` was generated using the [Thrift compiler](https://thrift.apache.org/) from the [Thrift file](src/main/resources/flume.thrift) with

```
thrift --gen java --out src/main/java src/main/resources/flume.thrift
```

### Maintainers

To publish a new version see [Maintainers](MAINTAINERS.md)

