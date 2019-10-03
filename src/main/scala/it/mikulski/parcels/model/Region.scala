package it.mikulski.parcels.model

import org.jsoup.nodes.Document

import scala.jdk.CollectionConverters._


//działka Przewóz 8-14:   126104_9.0019.60/12
//obręb:                  126104_9.0019
//TERYT:                  126104_9
//https://geoportal360.pl/12/krakow/krakow-podgorze-126104/9/0019-19

case class Region(id: String, name: String, county: String, municipality: String, pageUrl: String, imageUrl: String, mapUrl: String, neighbourString: String) {
  def teryt: String = id.takeWhile(_ != '.')
  def number: String = id.substring(id.indexOf('.') + 1)
  def neighbours: Seq[String] = neighbourString.split(',')
}

object Region {

  type RegionRow = (String, String, String, String, String, String, String, String)

  def toRow(region: Region): RegionRow = Region.unapply(region).get

  def from(row: RegionRow): Region = (Region.apply _).tupled(row)

  def from(doc: Document, pageUrl: String): Region = {
    val props = doc.getElementsByClass("properties").first.child(0).children.asScala.map(row => (row.child(0).text, row.child(1).text)).toMap
    val name = props("Nazwa obrębu")
    val county = props("Powiat")
    val municipality = props("Gmina")
    val teryt = props("TERYT")
    val number = props("Numer obrębu")
    val id = s"$teryt.$number"

    val map = doc.getElementsByClass("map-standalone").first
    val mapUrl = "https://geoportal360.pl" + map.attr("href")
    val imageUrl = map.child(0).attr("src")

    val neighbours = doc.getElementsByClass("properties").first.siblingElements.get(1).children.asScala.map(_.child(0).text).mkString(",")

    Region(id = id, name = name, county = county, municipality = municipality, pageUrl = pageUrl, imageUrl = imageUrl, mapUrl = mapUrl, neighbourString = neighbours)
  }

}
