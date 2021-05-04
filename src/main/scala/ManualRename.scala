import net.runelite.api.NpcID._

object ManualRename {
  
  def apply(npc: NpcStats) = npc.name = Some(renames.getOrElse(npc.id.get, npc.name.get))
  
  val renames: Map[Int, String] = Map(
    ZULRAH -> "Zulrah (Serpentine/Ranged)",
    ZULRAH_2043 -> "Zulrah (Magma/Melee)",
    ZULRAH_2044 -> "Zulrah (Tanzanite/Mage)",
  )

}
