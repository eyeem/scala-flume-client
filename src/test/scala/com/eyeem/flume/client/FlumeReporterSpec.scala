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
import org.apache.thrift.transport.TTransportException
import play.api.libs.json.{ JsNull, Json }

import scala.concurrent.Future

class FlumeReporterSpec extends BaseSpec {

  val incorrectFlumeConfig = FlumeConfig(applicationName = "myAppName", host = "myserver.mydomain.incorrect")

  val incorrectlyConfiguredFlumeReporter = new FlumeReporter(incorrectFlumeConfig)

  "Flume Reporter" must {

    s"return a failed future on sending events if hostname is incorrect" in {

      val result: Future[Unit] = incorrectlyConfiguredFlumeReporter.postEvent("name", JsNull)
      whenReady(result.failed) { e =>
        e mustBe a[TTransportException]
      }

    }

    s"return a failed future on sending entities if hostname is incorrect" in {

      val result: Future[Unit] = incorrectlyConfiguredFlumeReporter.postEntity(FlumeEntity("name", "v1", JsNull))
      whenReady(result.failed) { e =>
        e mustBe a[TTransportException]
      }

    }

  }

}
