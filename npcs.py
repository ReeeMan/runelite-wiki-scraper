import traceback
import re
import mwparserfromhell as mw
import api
import util
from typing import *
import copy

# Modification here to include many more attributes
npc_trait_keys = ["hitpoints", "att", "str", "def", "mage", "range", "attbns", "strbns", "amagic", "mbns", "arange", "rngbns", "defbns", "amagic", "mbns",
				  "arange", "rngbns", "dstab", "dslash", "dcrush", "dmagic", "drange", "combat", "size"]


def run():
	npcs = {}

	npc_pages = api.query_category("Monsters")
	for name, page in npc_pages.items():
		if name.startswith("Category:"):
			continue

		try:
			code = mw.parse(page, skip_style_tags=True)

			for (vid, version) in util.each_version("Infobox Monster", code):
				if "removal" in version and not str(version["removal"]).strip().lower() in ["", "no"]:
					continue

				doc = util.get_doc_for_id_string(name + str(vid), version, npcs)
				if doc == None:
					continue
				util.copy("name", doc, version)
				if not "name" in doc:
					doc["name"] = name

				if "attributes" in version:
					attrs = [x.strip() for x in version["attributes"].split(",") if x.strip()]
					for attr in attrs:
						doc[f"is{attr[0].upper()}{attr[1:]}"] = True

				for key in npc_trait_keys:
					try:
						util.copy(key, doc, version, lambda x: int(x))
					except ValueError:
						print("NPC {} has an non integer {}".format(name, key))

		except (KeyboardInterrupt, SystemExit):
			raise
		except:
			print("NPC {} failed:".format(name))
			traceback.print_exc()

	util.write_json("npcs-dps-calc.json", "npcs-dps-calc.min.json", npcs)
