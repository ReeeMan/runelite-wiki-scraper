import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import net.runelite.api.NpcID.{THE_MAIDEN_OF_SUGADINTI_10824, THE_MAIDEN_OF_SUGADINTI_10825, THE_MAIDEN_OF_SUGADINTI_8362, THE_MAIDEN_OF_SUGADINTI_8363, _}
import play.api.libs.json.{Json, OFormat}

import java.io.File
import java.nio.file.{Files, StandardOpenOption}

object Main extends App {

  import model._
  implicit val NpcStatsFormat: OFormat[NpcStats] = Jsonx.formatCaseClassUseDefaults[NpcStats]
  implicit val DefenderAttributesFormat: OFormat[DefenderAttributes] = Jsonx.formatCaseClassUseDefaults[DefenderAttributes]
  implicit val DefensiveBonusesFormat: OFormat[DefensiveBonuses] = Jsonx.formatCaseClassUseDefaults[DefensiveBonuses]
  implicit val AggressiveBonusesFormat: OFormat[AggressiveBonuses] = Jsonx.formatCaseClassUseDefaults[AggressiveBonuses]
  
  implicit val SkillsFormat: OFormat[Skills] = Jsonx.formatCaseClassUseDefaults[Skills]
  implicit val NpcDataFormat: OFormat[NpcData] = Jsonx.formatCaseClassUseDefaults[NpcData]
  
  implicit val ItemsV1Format: OFormat[ItemStatsV1] = Jsonx.formatCaseClassUseDefaults[ItemStatsV1]
  implicit val ItemsV2Format: OFormat[ItemStatsV2] = Jsonx.formatCaseClassUseDefaults[ItemStatsV2]

  // need from wiki scraper
  println("==== NPCs ====")
  val rootJs = Json.parse(Files.readString(new File("npcs-dps-calc.min.json").toPath)).as[Map[String, NpcStats]]
  println("Original: " + rootJs.size)

  // convert field names
  var news =
    rootJs.filter(_._2.name.isDefined)
      .filter(_._2.combat.isDefined)
      .map { tuple =>
        tuple._2.id = tuple._1.toIntOption
        ManualRename(tuple._2)

        tuple._1 -> tuple._2
      }
  println("Non-null names: " + news.size)

  // clear true dupes
  var baseIds: Map[String, Int] = Map(
    THE_MAIDEN_OF_SUGADINTI_8361.toString -> THE_MAIDEN_OF_SUGADINTI,
    THE_MAIDEN_OF_SUGADINTI_8362.toString -> THE_MAIDEN_OF_SUGADINTI,
    THE_MAIDEN_OF_SUGADINTI_8363.toString -> THE_MAIDEN_OF_SUGADINTI,
    THE_MAIDEN_OF_SUGADINTI_10815.toString -> THE_MAIDEN_OF_SUGADINTI_10814,
    THE_MAIDEN_OF_SUGADINTI_10816.toString -> THE_MAIDEN_OF_SUGADINTI_10814,
    THE_MAIDEN_OF_SUGADINTI_10817.toString -> THE_MAIDEN_OF_SUGADINTI_10814,
    THE_MAIDEN_OF_SUGADINTI_10823.toString -> THE_MAIDEN_OF_SUGADINTI_10822,
    THE_MAIDEN_OF_SUGADINTI_10824.toString -> THE_MAIDEN_OF_SUGADINTI_10822,
    THE_MAIDEN_OF_SUGADINTI_10825.toString -> THE_MAIDEN_OF_SUGADINTI_10822,
  )
  var dupes: Seq[Int] = Seq()
  val ordered = news.values.toSeq.sortBy(_.id)
  ordered.foreach { stats =>
    if (!dupes.exists(stats.id.contains)) {
      val localDupes = ordered.filter(s => s.name == stats.name && s.id != stats.id)
        .filter(s => s.copy(id = stats.id) == stats)
        .map(_.id.get)
      localDupes.foreach(d => baseIds += d.toString -> stats.id.get)
      dupes ++= localDupes
    }
  }
  news = news.filterNot(s => dupes.contains(s._1.toInt))
  println("Without duplicates: " + news.size)

  // rename name-only dupes
  news.values.foreach { stats =>
    val groups = news.values
      .filter(s => s.name == stats.name && s.id != stats.id)
      .groupBy(_.combat)
    
    groups.foreach { tuple =>
        val (combatLevel, statSeq) = tuple
        if (statSeq.size == 1)
          statSeq.head.name = Some(s"${statSeq.head.name.get} (Lvl ${combatLevel.get})")
        else
          statSeq.zipWithIndex.foreach { tuple =>
            val (stats, ix) = tuple
            if (groups.size > 1)
              stats.name = Some(s"${stats.name.get} (Lvl ${combatLevel.get}) ($ix)")
            else
              stats.name = Some(s"${stats.name.get} ($ix)")
          }
      }
  }
  
  val converted = news.map(t => t._1 -> t._2.asNpcData)
  Files.write(new File("npcs.json").toPath, Json.prettyPrint(Json.toJson(converted)).getBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  Files.write(new File("npcs.min.json").toPath, Json.toBytes(Json.toJson(converted)), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  Files.write(new File("npc-base-ids.json").toPath, Json.prettyPrint(Json.toJson(baseIds)).getBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  Files.write(new File("npc-base-ids.min.json").toPath, Json.toBytes(Json.toJson(baseIds)), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  

  
}
