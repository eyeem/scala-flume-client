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

import org.mockito.Mockito._
import org.scalatest.{ WordSpecLike, OptionValues, MustMatchers }
import org.scalatest.concurrent.{ AsyncAssertions, ScalaFutures }
import org.scalatest.mock.MockitoSugar
import org.scalatest.time.{ Millis, Seconds, Span }

import org.slf4j.LoggerFactory

abstract class BaseSpec extends WordSpecLike with MustMatchers with OptionValues with MockitoSugar with AsyncAssertions with ScalaFutures {

  val log = LoggerFactory.getLogger(this.getClass)

  implicit override val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(50, Millis))

}
