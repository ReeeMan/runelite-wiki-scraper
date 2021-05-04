import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.{Json, OFormat}

import java.io.File
import java.nio.file.{Files, StandardOpenOption}

object Main extends App {

  implicit val NpcStatsReader: OFormat[NpcStats] = Jsonx.formatCaseClassUseDefaults[NpcStats]
  implicit val NewNpcStatsReader: OFormat[NewNpcStats] = Jsonx.formatCaseClassUseDefaults[NewNpcStats]

  // need from wiki scraper
  val rootJs = Json.parse(Files.readString(new File("npcs-dps-calc.json").toPath))
  println("Original: " + rootJs.as[Map[String, NpcStats]].size)

  // convert field names
  var news =
    rootJs.as[Map[String, NpcStats]]
      .filter(_._2.name.isDefined)
      .filter(_._2.combat.isDefined)
      .map { tuple =>
        tuple._2.id = tuple._1.toIntOption
        ManualRename(tuple._2)

        tuple._1 -> tuple._2.toNewNpcStats()
      }
  println("Non-null names: " + news.size)

  // clear true dupes
  var baseIds: Map[String, Int] = Map()
  var dupes: Seq[Int] = Seq()
  news.values.foreach { stats =>
    if (!dupes.contains(stats.id)) {
      val localDupes = news.values
        .filter(s => s.name == stats.name && s.id != stats.id)
        .filter(s => s.copy(id = stats.id) == stats)
        .map(_.id)
      localDupes.foreach(d => baseIds += d.toString -> stats.id)
      dupes ++= localDupes
    }
  }
  news = news.filterNot(s => dupes.contains(s._1.toInt))
  println("Without duplicates: " + news.size)

  // rename name-only dupes
  news.values.foreach { stats =>
    val groups = news.values
      .filter(s => s.name == stats.name && s.id != stats.id)
      .groupBy(_.combatLevel)
    
    groups.foreach { tuple =>
        val (combatLevel, statSeq) = tuple
        if (statSeq.size == 1)
          statSeq.head.name = s"${statSeq.head.name} (Lvl ${combatLevel.get})"
        else
          statSeq.zipWithIndex.foreach { tuple =>
            val (stats, ix) = tuple
            if (groups.size > 1)
              stats.name = s"${stats.name} (Lvl ${combatLevel.get}) ($ix)"
            else
              stats.name = s"${stats.name} ($ix)"
          }
      }
  }
  
  Files.write(new File("npcs.min.json").toPath, Json.toJson(news).toString().getBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  Files.write(new File("npc-base-ids.min.json").toPath, Json.toJson(baseIds).toString().getBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)

}
