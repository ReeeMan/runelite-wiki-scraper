package model

case class ItemStatsV1(
                      var id: Option[Int] = None,
                      var name: Option[String],
                      weaponCategory: Option[String],
                      slot: Option[Int],
                      astab: Option[Int],
                      aslash: Option[Int],
                      acrush: Option[Int],
                      amagic: Option[Int],
                      arange: Option[Int],
                      mdmg: Option[Int],
                      str: Option[Int],
                      rstr: Option[Int],
                      speed: Option[Int],
                      aspeed: Option[Int],
                      prayer: Option[Int],
                      is2h: Option[Boolean],
                      ) {
  def asV2: ItemStatsV2 = ItemStatsV2(
    itemId = id.get,
    name = name.get,
    accuracyStab = astab,
    accuracySlash = aslash,
    accuracyCrush = acrush,
    accuracyMagic = amagic,
    accuracyRanged = arange,
    strengthMelee = str,
    strengthRanged = rstr,
    strengthMagic = mdmg,
    prayer = prayer,
    speed = if (speed.isDefined) speed else aspeed,
    slot = slot,
    is2h = is2h,
    weaponCategory = weaponCategory
  )
}

case class ItemStatsV2(
                      itemId: Int,
                      name: String,
                      accuracyStab: Option[Int],
                      accuracySlash: Option[Int],
                      accuracyCrush: Option[Int],
                      accuracyMagic: Option[Int],
                      accuracyRanged: Option[Int],
                      strengthMelee: Option[Int],
                      strengthRanged: Option[Int],
                      strengthMagic: Option[Int],
                      prayer: Option[Int],
                      speed: Option[Int],
                      slot: Option[Int],
                      is2h: Option[Boolean],
                      weaponCategory: Option[String],
                      )