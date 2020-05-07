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

import pureconfig.generic.ProductHint
import pureconfig.generic.auto._
import pureconfig.generic._
import pureconfig._

/**
 * PureConfig usage:
 *
 * Arguments to PureConfig and its subelements must be matching the ones in reference.conf
 *
 * e.g.
 *
 * for
 *
 * case class PureConfig(sub1: Sub1Config)
 * case class Sub1Config(key1: String, key2: Int, sub2: Sub2Config)
 * case class Sub2Config(key3: String)
 *
 * the following must exist in reference.conf
 *
 * sub1 {
 *   key1 = "hello"
 *   key2 = 123
 *   sub2 {
 *     key3 = "world"
 *   }
 * }
 *
 * For more details see https://github.com/melrief/pureconfig
 *
 */
case class PureConfig(flume: FlumeConfig)

case class FlumeConfig(
  applicationName: String,
  host: String,
  portEvent: Int = 9091,
  portEntity: Int = 9092,
  enabled: Boolean = true,
  threadPoolSize: Int = 20
)

object Config {
  implicit def productHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  private lazy val config: PureConfig = loadConfig[PureConfig].right.get

  lazy val flumeConfig = config.flume
}

