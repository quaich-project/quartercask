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
package codes.bytes.quartercask.sbt

import java.io.File

import com.amazonaws.event.{ProgressEvent, ProgressEventType, ProgressListener, SyncProgressListener}
import com.amazonaws.{AmazonClientException, AmazonServiceException, AmazonWebServiceRequest}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{Bucket, CannedAccessControlList, PutObjectRequest}
import com.amazonaws.services.s3.transfer.TransferManager
import sbt._

import scala.util.{Failure, Success, Try}

private[quartercask] object AwsS3 {
  private lazy val client = new AmazonS3Client(AwsCredentials.provider)

  def pushJarToS3(jar: File, bucketId: S3BucketId, s3KeyPrefix: String): Try[S3Key] = {
    try {
      val key = s3KeyPrefix + jar.getName
      val objectRequest = new PutObjectRequest(bucketId.value, key, jar)
      objectRequest.setCannedAcl(CannedAccessControlList.AuthenticatedRead)


      val objectMetadata= client.getObjectMetadata(bucketId.value, key)

      addProgressListener(objectRequest, objectMetadata.getContentLength, key)

      client.putObject(objectRequest)

      Success(S3Key(key))
    } catch {
      case ex @ (_ : AmazonClientException |
                 _ : AmazonServiceException) =>
        Failure(ex)
    }
  }

  def getBucket(bucketId: S3BucketId): Option[Bucket] = {
    import scala.collection.JavaConverters._
    client.listBuckets().asScala.find(_.getName == bucketId.value)
  }

  def createBucket(bucketId: S3BucketId): Try[S3BucketId] = {
    try{
      client.createBucket(bucketId.value)
      Success(bucketId)
    } catch {
      case ex @ (_ : AmazonClientException |
                 _ : AmazonServiceException) =>
        Failure(ex)
    }
  }

  /**
    * Progress bar code borrowed from
https://github.com/sbt/sbt-s3/blob/master/src/main/scala/S3Plugin.scala
    */
  private def progressBar(percent:Int) = {
    val b="=================================================="
    val s="                                                 "
    val p=percent/2
    val z:StringBuilder=new StringBuilder(80)
    z.append("\r[")
    z.append(b.substring(0,p))
    if (p<50) {z.append(">"); z.append(s.substring(p))}
    z.append("]   ")
    if (p<5) z.append(" ")
    if (p<50) z.append(" ")
    z.append(percent)
    z.append("%   ")
    z.mkString
  }

  private def addProgressListener(request: AmazonWebServiceRequest, fileSize: Long, key: String) = {
    request.setGeneralProgressListener(new SyncProgressListener {
      var uploadedBytes = 0L
      val fileName = {
        val area = 30
        val n = new File(key).getName
        val l = n.length()
        if (l > area - 3)
          "..." + n.substring(l - area + 3)
        else
          n
      }
      override def progressChanged(progressEvent: ProgressEvent): Unit = {
        if (progressEvent.getEventType == ProgressEventType.REQUEST_BYTE_TRANSFER_EVENT ||
          progressEvent.getEventType == ProgressEventType.RESPONSE_BYTE_TRANSFER_EVENT) {
          uploadedBytes = uploadedBytes + progressEvent.getBytesTransferred
        }
        print(progressBar(if (fileSize > 0) ((uploadedBytes * 100) / fileSize).toInt else 100))
        print(s"Lambda JAR -> S3")
        if (progressEvent.getEventType == ProgressEventType.TRANSFER_COMPLETED_EVENT)
          println()
      }
    })
  }

  def prettyLastMsg(verb:String, objects:Seq[String], preposition:String, bucket:String) =
    if (objects.length == 1) s"$verb '${objects.head}' $preposition the S3 bucket '$bucket'."
    else                     s"$verb ${objects.length} objects $preposition the S3 bucket '$bucket'."
}


