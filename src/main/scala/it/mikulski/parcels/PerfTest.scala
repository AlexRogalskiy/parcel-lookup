package it.mikulski.parcels

import java.util.UUID

import it.mikulski.parcels.model.Parcel

import scala.concurrent.Await

import scala.concurrent.duration._

object PerfTest extends App with DbHandler {

  Await.ready(createSchemaIfMissing, 1.minute)

  for(i <- 1 to 5) {
    val s = samples(100)
    val t1 = System.currentTimeMillis()
    Await.ready(insert(s), 1.minute)
    val t2 = System.currentTimeMillis()
    println("time taken: " + (t2 - t1))
  }

  def samples(size: Int): Seq[Parcel] = {
    Seq.fill(size)(Parcel(UUID.randomUUID().toString, 75, "mapUrl", "imageUrl"))
  }
}
