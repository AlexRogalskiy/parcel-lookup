package it.mikulski.parcels

import it.mikulski.parcels.model.{Parcel, Region}
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

import scala.concurrent.Await
import scala.jdk.CollectionConverters._
import scala.concurrent.duration._
import scala.io.Source

object RegionScanner extends App with DbHandler {

  val municipalities = Seq(
    //"https://geoportal360.pl/12/wielicki/niepolomice-121904",
    //"https://geoportal360.pl/12/wielicki/gdow-121902",
    //"https://geoportal360.pl/12/wielicki/biskupice-121901",
    //"https://geoportal360.pl/12/wielicki/klaj-121903",
    "https://geoportal360.pl/12/wielicki/wieliczka-121905"
  )

  val regions = municipalities.flatMap { m =>
    //Thread.sleep(2000)
    getRegionLinks(m)
  }.sorted

  println(s"found ${regions.size} regions")

  Await.ready(createSchemaIfMissing, 1.minute)

  regions.foreach { regionLink =>
    println(regionLink)
    val doc = Jsoup.connect(regionLink).maxBodySize(0).get()

    val region: Region = Region.from(doc, regionLink)

    val parcels: Seq[Parcel] = doc.getElementsByClass("map-parcel").asScala.map(Parcel.from).toSeq
    println(s"  parsed region ${region.name} and found ${parcels.size} parcels, inserting to db")
    val t0 = System.currentTimeMillis()
    val f = for {
      _ <- insert(region)
      _ <- insert(parcels)
    } yield ()
    Await.ready(f, 10.minute)
    val t1 = System.currentTimeMillis()
    println(s"  time taken: ${(t1 - t0)/1000}s")
  }


  def getRegionLinks(municipalityPage: String): Seq[String] = {
    val doc = Jsoup.connect(municipalityPage).get()
    val links = doc.getElementsByAttribute("href").asScala.map("https://geoportal360.pl" + _.attr("href")).toList
    links.filter(_.startsWith(municipalityPage)).distinct.sorted
  }

}
