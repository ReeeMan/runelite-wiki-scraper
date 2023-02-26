package model

case class NpcData(
                    skills: Skills,
                    defensiveBonuses: DefensiveBonuses,
                    aggressiveBonuses: AggressiveBonuses,
                    attributes: DefenderAttributes
                  )

case class Skills(levels: Map[String, Int])

case class DefensiveBonuses(
                             defenseStab: Option[Int],
                             defenseSlash: Option[Int],
                             defenseCrush: Option[Int],
                             defenseRanged: Option[Int],
                             defenseMagic: Option[Int]
                           )

case class AggressiveBonuses(
                             aggressiveAttack: Option[Int],
                             aggressiveStrength: Option[Int],
                             aggressiveMagic: Option[Int],
                             aggressiveMagicStrength: Option[Int],
                             aggressiveRange: Option[Int],
                             aggressiveRangeStrength: Option[Int]
                           )

case class DefenderAttributes(
                               npcId: Int,
                               var name: String,
                               isDemon: Option[Boolean],
                               isDragon: Option[Boolean],
                               isKalphite: Option[Boolean],
                               isLeafy: Option[Boolean],
                               isUndead: Option[Boolean],
                               isVampyre1: Option[Boolean],
                               isVampyre2: Option[Boolean],
                               isVampyre3: Option[Boolean],
                               size: Int,
                               accuracyMagic: Option[Int]
                             )
