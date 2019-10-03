package it.mikulski.parcels.model

import org.jsoup.nodes.Element

case class Parcel(id: String, area: Int, mapUrl: String, imageUrl: String)

object Parcel {

  type ParcelRow = (String, Int, String, String)

  def toRow(parcel: Parcel): ParcelRow = Parcel.unapply(parcel).get

  def from(row: ParcelRow): Parcel = (Parcel.apply _).tupled(row)

  def from(div: Element): Parcel = {
    val id = div.child(0).id()
    val area = getAreaFromDescription(div.child(1).text)
    val mapUrl = "https://geoportal360.pl" + div.child(2).attr("href")
    val imageUrl = div.child(2).child(0).attr("src")
    Parcel(id = id, area = area, mapUrl = mapUrl, imageUrl = imageUrl)
  }

  private def getAreaFromDescription(desc: String): Int = {
    desc.replaceAll("Mapa numer \\d+(/\\d+)?", "").replaceAll("m2", "").replaceAll(" ", "").toInt
  }

}
