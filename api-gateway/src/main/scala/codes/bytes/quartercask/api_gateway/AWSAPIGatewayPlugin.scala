package codes.bytes.quartercask.api_gateway

/*
 * Copyright (c) 2016 Brendan McAdams & Thomas Lockney
 * Portions Copyright (c) Gilt Groupe, based upon their work
 * at https://github.com/gilt/sbt-aws-lambda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import codes.bytes.quartercask._
import sbt._

object AWSAPIGatewayPlugin extends AutoPlugin {
  object autoImport {
    // TODO: Switch to commands, not tasks...
    val createAPI = taskKey[APIId]("Creates a new AWS API Gateway deployment.")
    val updateAPI = taskKey[APIId]("Updates an existing AWS API Gateway deployment.")

    val apiName = settingKey[String]("The name to give to the API Gateway deployment")
    val apiDescription = settingKey[Option[String]]("An optional description for the deployed API")
    val queryStringKeys = settingKey[Map[String, Boolean]]("[validation] An optional mapping of valid querystring parameters, with a boolean indicating if caching should be used or not for each value.")
    val httpRequestHeaders = settingKey[Map[String, Boolean]]("[validation] An optional mapping of valid HTTP Request Headers, with a boolean indicating if caching should be used or not for each value.")


  }

}
