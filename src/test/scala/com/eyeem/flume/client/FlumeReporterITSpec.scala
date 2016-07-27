/*
 * Copyright 2016 EyeEm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eyeem.flume.client

import com.eyeem.flume.client.models.FlumeEntity
import play.api.libs.json.{ JsNull, Json }

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.runtime.BoxedUnit

/**
 * Integration tests can be run with
 * sbt it:test
 *
 * They require a flume ingestionAgent running on localhost / vagrant box with forwarded ports
 */
class FlumeReporterITSpec extends BaseSpec {

  val flumeConfig = FlumeConfig(applicationName = "myAppName", host = "localhost")

  val flumeReporter = new FlumeReporter(flumeConfig)

  "Flume Reporter [requires a running flume ingestionAgent on localhost!]" must {

    s"correctly send events if it can connect to host" in {

      val result: Future[Unit] = flumeReporter.postEvent("name", Json.obj("mykey" -> "myValue"))
      whenReady(result) { u =>
        u mustBe a[BoxedUnit]
      }

    }

    s"correctly send entities if it can connect to host" in {

      val result: Future[Unit] = flumeReporter.postEntity(FlumeEntity("name", "v1", Json.obj("mykey" -> "myValue")))
      whenReady(result) { u =>
        u mustBe a[BoxedUnit]
      }

    }
  }

}
