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

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.Executors

import com.eyeem.flume.client.models.{ FlumeEntity, FlumeEvent }
import org.apache.flume.thrift.{ ThriftSourceProtocol, ThriftFlumeEvent }
import ThriftSourceProtocol.Client
import org.apache.thrift.TException
import org.apache.thrift.protocol.TCompactProtocol
import org.apache.thrift.transport.{ TFramedTransport, TSocket }
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import play.api.libs.json.{ JsValue, Json }

import scala.collection.JavaConverters._
import scala.concurrent.{ ExecutionContext, Future }

class FlumeReporter(flumeConfig: FlumeConfig = Config.flumeConfig) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val random = new scala.util.Random(new java.security.SecureRandom())

  private val threadPoolExec = Executors.newFixedThreadPool(flumeConfig.threadPoolSize)

  implicit val defaultFlumeExecutionContext: ExecutionContext = ExecutionContext.fromExecutor(threadPoolExec)

  if (!flumeConfig.enabled) {
    logger.warn("Flume reporting is disabled via flumeConfig. Not sending anything to Flume.")
  }

  def postEntity(entity: FlumeEntity) = {

    val entityHeaders = Map(
      "application" -> flumeConfig.applicationName,
      "entity" -> entity.name,
      "version" -> entity.version
    )

    submitDataToFlume(entity.json, entityHeaders, flumeConfig.portEntity)

  }

  def shutdown(): Unit = {
    logger.info("Shutting down the thread pool executor")
    threadPoolExec.shutdown()
  }

  def postEvent(eventName: String, data: JsValue): Future[Unit] = {

    val ingestionEvent = FlumeEvent(
      event_name = eventName,
      timestamp = new DateTime().toString,
      user_id = flumeConfig.applicationName,
      salt = random.nextInt(),
      data = data
    )

    val eventHeaders = Map(
      "application" -> flumeConfig.applicationName
    )
    submitDataToFlume(Json.toJson(ingestionEvent), eventHeaders, flumeConfig.portEvent)
  }

  private def submitDataToFlume(
    data: JsValue,
    headers: Map[String, String],
    port: Int,
    host: String = flumeConfig.host,
    encoding: String = "UTF-8"
  ): Future[Unit] = Future {
    if (flumeConfig.enabled) {
      val transport = new TSocket(host, port)
      val fTransport = new TFramedTransport(transport)
      val byteStream = new ByteArrayOutputStream()

      try {
        transport.open()
        val protocol = new TCompactProtocol(fTransport)
        val client = new Client(protocol)
        byteStream.write(data.toString().getBytes(encoding))
        val buffer = ByteBuffer.allocate(byteStream.size())
        buffer.put(byteStream.toByteArray)
        buffer.flip()

        logger.debug(s"Sending event to $host:$port: $data")
        client.append(new ThriftFlumeEvent(headers.asJava, buffer))
      } catch {
        case tException: TException =>
          logger.error(s"TException on sending event $data to flume server", tException)
          throw tException
        case ex: Throwable =>
          logger.error(s"Error on sending event $data to flume server", ex)
          throw ex
      } finally {
        byteStream.close()
        transport.close()
      }
    }
  }
}

