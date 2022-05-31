package model

case class NpcStats(
                     var id: Option[Int],
                     var name: Option[String],
                     hitpoints: Option[Int],
                     att: Option[Int],
                     str: Option[Int],
                     `def`: Option[Int],
                     mage: Option[Int],
                     range: Option[Int],
                     attbns: Option[Int],
                     strbns: Option[Int],
                     amagic: Option[Int],
                     mbns: Option[Int],
                     arange: Option[Int],
                     rngbns: Option[Int],
                     dstab: Option[Int],
                     dslash: Option[Int],
                     dcrush: Option[Int],
                     dmagic: Option[Int],
                     drange: Option[Int],
                     combat: Option[Int],
                     size: Option[Int],
                     isDemon: Option[Boolean],
                     isDragon: Option[Boolean],
                     isKalphite: Option[Boolean],
                     isLeafy: Option[Boolean],
                     isUndead: Option[Boolean],
                     isVampyre: Option[Boolean],
                     isXerician: Option[Boolean],
                   ) {

  def asNpcData: NpcData =
    NpcData(
      skills = Skills(
        levels = Map(
          "HITPOINTS" -> hitpoints.getOrElse(0),
          "ATTACK" -> att.getOrElse(0),
          "STRENGTH" -> str.getOrElse(0),
          "DEFENCE" -> `def`.getOrElse(0),
          "MAGIC" -> mage.getOrElse(0),
          "RANGED" -> range.getOrElse(0)
        )
      ),
      defensiveBonuses = DefensiveBonuses(
        defenseStab = dstab,
        defenseSlash = dslash,
        defenseCrush = dcrush,
        defenseMagic = dmagic,
        defenseRanged = drange,
      ),
      attributes = DefenderAttributes(
        npcId = id.get,
        name = name.get,
        isDemon = isDemon,
        isDragon = isDragon,
        isKalphite = isKalphite,
        isLeafy = isLeafy,
        isUndead = isUndead,
        isVampyre = isVampyre,
        size = size.getOrElse(1),
        accuracyMagic = amagic,
      ),
    )

}
