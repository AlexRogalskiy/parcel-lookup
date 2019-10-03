package it.mikulski.parcels

import it.mikulski.parcels.model.{Parcel, Region}

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.Tag

trait DbHandler {

  implicit val ec: ExecutionContext = ExecutionContext.global

  private val db = Database.forConfig("parcellookup")

  private val regions = TableQuery[RegionsTable]
  private val parcels = TableQuery[ParcelsTable]

  def createSchemaIfMissing: Future[Unit] = {
    val action = DBIO.seq(
      regions.schema.createIfNotExists,
      parcels.schema.createIfNotExists
    )
    db.run(action)
  }

  def insert(region: Region): Future[Unit] = {
    val action = DBIO.seq(regions += Region.toRow(region))
    db.run(action)
  }

  def insert(pseq: Seq[Parcel]): Future[Unit] = {
    val action = DBIO.seq(parcels ++= pseq.map(Parcel.toRow))
    db.run(action)
  }

}

class RegionsTable(tag: Tag) extends Table[Region.RegionRow](tag, "regions") {
  def id: Rep[String] = column[String]("id", O.PrimaryKey)

  def name: Rep[String] = column[String]("name")

  def county: Rep[String] = column[String]("county")

  def municipality: Rep[String] = column[String]("municipality")

  def pageUrl: Rep[String] = column[String]("pageUrl")

  def imageUrl: Rep[String] = column[String]("imageUrl")

  def mapUrl: Rep[String] = column[String]("mapUrl")

  def neighbourString: Rep[String] = column[String]("neighbourString")

  def * = (id, name, county, municipality, pageUrl, imageUrl, mapUrl, neighbourString)
}

class ParcelsTable(tag: Tag) extends Table[Parcel.ParcelRow](tag, "parcels") {
  def id: Rep[String] = column[String]("id", O.PrimaryKey)

  def area: Rep[Int] = column[Int]("area")

  def mapUrl: Rep[String] = column[String]("mapUrl")

  def imageUrl: Rep[String] = column[String]("imageUrl")

  def * = (id, area, mapUrl, imageUrl)
}