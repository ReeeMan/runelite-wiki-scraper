import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.{Json, OFormat}

import java.io.File
import java.nio.file.{Files, StandardOpenOption}

object Main extends App {

  import model._
  implicit val NpcStatsFormat: OFormat[NpcStats] = Jsonx.formatCaseClassUseDefaults[NpcStats]
  implicit val DefenderAttributesFormat: OFormat[DefenderAttributes] = Jsonx.formatCaseClassUseDefaults[DefenderAttributes]
  implicit val DefensiveBonusesFormat: OFormat[DefensiveBonuses] = Jsonx.formatCaseClassUseDefaults[DefensiveBonuses]
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
  var baseIds: Map[String, Int] = Map()
  var dupes: Seq[Int] = Seq()
  news.values.foreach { stats =>
    if (!dupes.exists(stats.id.contains)) {
      val localDupes = news.values
        .filter(s => s.name == stats.name && s.id != stats.id)
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
          statSeq.head.name = Some(s"${statSeq.head.name} (Lvl ${combatLevel.get})")
        else
          statSeq.zipWithIndex.foreach { tuple =>
            val (stats, ix) = tuple
            if (groups.size > 1)
              stats.name = Some(s"${stats.name} (Lvl ${combatLevel.get}) ($ix)")
            else
              stats.name = Some(s"${stats.name} ($ix)")
          }
      }
  }
  
  val converted = news.map(t => t._1 -> t._2.asNpcData)
  Files.write(new File("npcs.json").toPath, Json.prettyPrint(Json.toJson(converted)).getBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  Files.write(new File("npcs.min.json").toPath, Json.toBytes(Json.toJson(converted)), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  Files.write(new File("npc-base-ids.json").toPath, Json.prettyPrint(Json.toJson(baseIds)).getBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  Files.write(new File("npc-base-ids.min.json").toPath, Json.toBytes(Json.toJson(baseIds)), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  
  println()
  println("==== Items ====")
  val itemsRoot = Json.parse(Files.readString(new File("items-dps-calc.min.json").toPath)).as[Map[String, ItemStatsV1]]
  println("Original: " + itemsRoot.size)

  var itemsFiltered =
    itemsRoot.filter(_._2.name.isDefined)
      .filter(t => t._2.slot.isDefined)
      .filter(t => t._2.slot.get != 3 || t._2.weaponCategory.isDefined)
      .map { tuple =>
        tuple._2.id = tuple._1.toIntOption
        tuple._1 -> tuple._2
      }
      .filter(_._2.id.isDefined)
  println("Filtered: " + itemsFiltered.size)
  
  var dupeIds = Set[String]()
  itemsFiltered.foreach { tuple =>
    val (id, stats) = tuple
    if (!dupeIds.contains(id)) {
      dupeIds ++= 
        itemsFiltered.filter(p => p._1 != id && p._2.name == stats.name)
          .filter(p => p._2.copy(id = id.toIntOption) == stats)
          .keys
    }
  }
  val itemsDeduped = itemsFiltered.filterNot(t => dupeIds.contains(t._1))
  println("Without duplicates: " + itemsDeduped.size)
  
  val convertedItems = itemsDeduped.map(t => t._1 -> t._2.asV2)
  Files.write(new File("items.json").toPath, Json.prettyPrint(Json.toJson(convertedItems)).getBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  Files.write(new File("items.min.json").toPath, Json.toBytes(Json.toJson(convertedItems)), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)

}