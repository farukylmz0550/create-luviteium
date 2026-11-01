#!/usr/bin/env python3
"""Headless QA harness: drives the dedicated server over RCON and asserts
behavior of the Casting Table, Biomass Generator, and chunk persistence.

Prerequisites:
  - enable-rcon=true, rcon.password=opencode, rcon.port=25575 in
    runs/server/server.properties (done by the repo once).
  - server started with `./gradlew runServer -q`.

Run:  python3 tools/qa/run_qa.py [--host H] [--port P] [--password PW]
Exit code 0 = all assertions passed, 1 = failures (printed).
"""
import argparse
import re
import sys
import time

sys.path.insert(0, "tools/qa")
from rcon import Rcon  # noqa: E402

# Test area coordinates (chunk -8..7 / -8..7 loaded via forceload batch A).
CT_POS = (48, 200, 48)
GEN_POS = (64, 200, 64)
RL_POS = (200, 200, 200)          # far area, batch D
MOLTEN = "luviteium:molten_luviteium"
TABLE = "luviteium:casting_table"
GENERATOR = "luviteium:biomass_generator"
HOT_INGOT = "luviteium:hot_luviteium_ingot"

results = []


def record(name, passed, detail=""):
    results.append((name, passed, detail))
    print(("PASS  " if passed else "FAIL  ") + name + (": " + detail if detail else ""))


def test_harvest_gate_tags():
    """Static audit: 1.21.1 harvest gating needs the ore in the vanilla tags
    #minecraft:mineable/pickaxe plus #minecraft:incorrect_for_{wooden,stone}_tool.
    (`needs_iron_tool` is dead in 1.21.1 - Tier.createToolProperties only uses
    deniesDrops(incorrect_for_<tier>_tool) + minesAndDrops(mineable/<tool>).)"""
    import glob
    import json
    expected = {
        "mineable/pickaxe": True,
        "incorrect_for_wooden_tool": True,
        "incorrect_for_stone_tool": True,
        "needs_iron_tool": False,  # must NOT exist in any namespace
    }
    ok = True
    details = []
    for tag, should_exist in expected.items():
        paths = glob.glob(f"src/main/resources/data/*/tags/block/{tag}.json")
        if should_exist:
            good = False
            for p in paths:
                if "/data/minecraft/" in p:
                    vals = json.load(open(p)).get("values", [])
                    good = "luviteium:luviteium_ore" in vals
            ok = ok and good
            details.append(f"{tag}={'ok' if good else 'MISSING/wrong-namespace'}")
        else:
            gone = len(paths) == 0
            ok = ok and gone
            details.append(f"{tag}={'absent' if gone else 'STILL PRESENT'}")
    record("HG1 ore harvest gate uses vanilla 1.21.1 tags "
           "(mineable/pickaxe + incorrect_for wood/stone)", ok, ", ".join(details))


def r(cmd):
    out = rc.command(cmd)
    return out


def q(x, y, z):
    return f"data get block {x} {y} {z}"


def path(x, y, z, pth):
    return f"data get block {x} {y} {z} {pth}"


def wait_ticks(tick_count):
    time.sleep(tick_count / 20.0 + 0.20)


def nbt_of(parsed):
    """Extract the NBT payload after the first ': ' separator."""
    if ":" in parsed:
        return parsed.split(":", 1)[1].strip()
    return parsed


def norm(s):
    return s.replace(" ", "")


def has(nbt, needle):
    return needle in norm(nbt)


# ---------------------------------------------------------------- cast tests
def test_cast_produces_hot_ingot():
    x, y, z = CT_POS
    r(f"setblock {x} {y} {z} {TABLE}")
    r(f"data merge block {x} {y} {z} {{Inventory:{{Size:2,Items:[{{Slot:0,id:\"luviteium:ingot_mold\",count:1}}]}}}}")
    r(f"data merge block {x} {y} {z} {{Fluid:{{Fluid:{{Amount:144,id:\"{MOLTEN}\"}}}}}}")
    wait_ticks(25)  # 20 ticks required + margin
    inv = nbt_of(r(path(x, y, z, "Inventory")))
    fluid = nbt_of(r(path(x, y, z, "Fluid")))
    progress = r(path(x, y, z, "Progress"))
    out_ok = has(inv, f'id:"{HOT_INGOT}"') and has(inv, "count:1")
    drained = not has(fluid, "Amount:144")  # empty tank serializes as {}
    reset = nbt_of(progress).strip() == "0"
    record("CT1 ingot cast produces hot ingot, drains fluid, resets progress",
           out_ok and drained and reset,
           f"inv={inv} fluid={fluid} prog={progress}")


def test_no_mold_no_cast():
    x, y, z = CT_POS
    r(f"setblock {x} {y} {z} {TABLE}")
    r(f"data merge block {x} {y} {z} {{Inventory:{{Size:2,Items:[]}}}}")
    r(f"data merge block {x} {y} {z} {{Fluid:{{Fluid:{{Amount:144,id:\"{MOLTEN}\"}}}}}}")
    wait_ticks(25)
    out_items = r(path(x, y, z, "Inventory.Items"))
    fluid = nbt_of(r(path(x, y, z, "Fluid")))
    ok = "[]" in out_items and has(fluid, "Amount:144")
    record("CT2 no mold -> no cast, fluid retained", ok, f"items={out_items!r} fluid={fluid}")
    r(f"setblock {x} {y} {z} air")


def test_output_stack_cap():
    x, y, z = CT_POS
    r(f"setblock {x} {y} {z} {TABLE}")
    r(f"data merge block {x} {y} {z} "
      f"{{Inventory:{{Size:2,Items:["
      f"{{Slot:0,id:\"luviteium:ingot_mold\",count:1}},"
      f"{{Slot:1,id:\"{HOT_INGOT}\",count:64}}]}}}}")
    r(f"data merge block {x} {y} {z} {{Fluid:{{Fluid:{{Amount:144,id:\"{MOLTEN}\"}}}}}}")
    wait_ticks(25)
    inv = nbt_of(r(path(x, y, z, "Inventory")))
    fluid = nbt_of(r(path(x, y, z, "Fluid")))
    stack_capped = has(inv, "count:64") and not has(inv, "count:65")
    fluid_kept = has(fluid, "Amount:144")
    record("CT3 output max stack blocks casting, fluid kept", stack_capped and fluid_kept,
           f"inv={inv} fluid={fluid}")
    r(f"setblock {x} {y} {z} air")


def test_on_remove_drops():
    x, y, z = CT_POS
    r(f"setblock {x} {y} {z} {TABLE}")
    r(f"data merge block {x} {y} {z} {{Inventory:{{Size:2,Items:["
      f"{{Slot:0,id:\"luviteium:ingot_mold\",count:1}},"
      f"{{Slot:1,id:\"{HOT_INGOT}\",count:1}}]}}}}")
    r(f"setblock {x} {y} {z} air")
    time.sleep(0.5)
    mold = r(f"data get entity @e[type=minecraft:item,x={x},y={y},z={z},distance=..256,"
             f"nbt={{Item:{{id:\"luviteium:ingot_mold\"}}}},limit=1] Item")
    out = r(f"data get entity @e[type=minecraft:item,x={x},y={y},z={z},distance=..256,"
            f"nbt={{Item:{{id:\"{HOT_INGOT}\"}}}},limit=1] Item")
    ok = ("id" in mold and "ingot_mold" in mold) and ("id" in out and HOT_INGOT in out)
    record("CT4 onRemove drops mold and output", ok, f"mold={mold!r} out={out!r}")


# ------------------------------------------------------------ generator test
def test_generator_full_stack():
    x, y, z = GEN_POS
    r(f"setblock {x} {y} {z} {GENERATOR}")
    time.sleep(0.5)
    start = time.time()
    r(f"data merge block {x} {y} {z} {{Inventory:{{Size:1,Items:[{{Slot:0,id:\"minecraft:oak_leaves\",count:64}}]}}}}")
    time.sleep(1.0)
    inv1 = nbt_of(r(path(x, y, z, "Inventory")))
    speed = nbt_of(r(path(x, y, z, "Speed")))
    m = re.search(r"count:\s*(\d+)", inv1)
    started = bool(m) and int(m.group(1)) < 64  # first leaf consumed
    ok_speed = has(speed, "64.0f")
    record("GEN1 generator starts with fuel and outputs 64 RPM",
           started and ok_speed, f"inv={inv1!r} speed={speed!r}")

    deadline = start + 60
    fueled = True
    while time.time() < deadline:
        bt = r(path(x, y, z, "BurnTime"))
        items = nbt_of(r(path(x, y, z, "Inventory")))
        if "Items: []" in items:
            fueled = False
            break
        time.sleep(2.0)
    elapsed = time.time() - start
    elapsed_ticks = elapsed * 20
    expected = 64 * 15  # oak_leaves -> #minecraft:leaves -> 15 ticks
    ok_total = abs(elapsed_ticks - expected) <= 80
    record("GEN2 full 64-leaf stack burns cumulatively ~960 ticks",
           ok_total and not fueled,
           f"elapsed={elapsed:.2f}s (~{elapsed_ticks:.0f} ticks), expected {expected} "
           f"(fuel empty={not fueled})")
    time.sleep(1.0)
    speed_end = nbt_of(r(path(x, y, z, "Speed")))
    record("GEN3 generator stops (Speed 0) when fuel exhausted",
           "Speed: 0" in speed_end, f"speed={speed_end!r}")
    r(f"setblock {x} {y} {z} air")


# ------------------------------------------------------- reload + interruption
def test_reload_preserves_state():
    """Unload/reload a mid-operation Casting Table.

    The cast is deliberately blocked by a full output stack so the state
    (mold + full tank + progress) is deterministic across the unload: after
    the reload, freeing one output slot must let the interrupted cast resume
    and complete.
    """
    x, y, z = RL_POS
    r(f"setblock {x} {y} {z} {TABLE}")
    r(f"data merge block {x} {y} {z} "
      f"{{Inventory:{{Size:2,Items:["
      f"{{Slot:0,id:\"luviteium:ingot_mold\",count:1}},"
      f"{{Slot:1,id:\"{HOT_INGOT}\",count:64}}]}},Progress:5}}")
    r(f"data merge block {x} {y} {z} {{Fluid:{{Fluid:{{Amount:144,id:\"{MOLTEN}\"}}}}}}")
    time.sleep(1.0)
    # Casting is blocked while loaded: the full stack must keep progress at 5.
    prog_before = nbt_of(r(path(x, y, z, "Progress")))
    r("forceload remove all")
    time.sleep(3.0)  # chunk unloads and saves
    r(f"forceload add {x - 32} {z - 32} {x + 31} {z + 31}")
    time.sleep(2.0)  # chunk reloads and the BE reads its NBT back
    fluid = nbt_of(r(path(x, y, z, "Fluid")))
    prog = r(path(x, y, z, "Progress"))
    inv_before = nbt_of(r(path(x, y, z, "Inventory")))
    retained = (has(fluid, "Amount:144") and nbt_of(prog).strip() == "5"
                and has(inv_before, "count:64"))
    # Free one output slot: the interrupted cast must now resume and finish.
    r(f"data merge block {x} {y} {z} "
      f"{{Inventory:{{Size:2,Items:["
      f"{{Slot:0,id:\"luviteium:ingot_mold\",count:1}},"
      f"{{Slot:1,id:\"{HOT_INGOT}\",count:63}}]}}}}")
    wait_ticks(25)
    inv = nbt_of(r(path(x, y, z, "Inventory")))
    finished = has(inv, "count:64") and has(inv, f'id:"{HOT_INGOT}"')
    record("RL1 unload/reload preserves fluid+progress and resumes interrupted cast",
           retained and nbt_of(prog_before).strip() == "5" and finished,
           f"fluid={fluid} prog={prog!r} inv={inv}")
    r(f"setblock {x} {y} {z} air")


def load_sample_batches():
    """Load 4 batches of 16x16 chunks (1024 chunks) for ore sampling.

    Explicit block-coordinate ranges; forceload converts to chunk ranges
    (block -128..127 -> chunks -8..7).
    """
    batches = [(-128, -128, 127, 127), (-128, 128, 127, 255),
               (128, -128, 255, 127), (128, 128, 255, 255)]
    for x1, z1, x2, z2 in batches:
        out = r(f"forceload add {x1} {z1} {x2} {z2}")
        print("forceload:", out[:120])
        time.sleep(2.0)


def wait_for_chunks():
    """Block until the far corner of every forceload batch exists."""
    # Far block corners of the four batches (chunk 7 or 15 -> blocks 112..127 / 240..255).
    probes = [(120, 120), (120, 248), (248, 120), (248, 248),
              (-8, -8), (-8, 248), (248, -8), (-8, 120)]
    deadline = time.time() + 300
    supported = True
    while time.time() < deadline:
        if not supported:
            time.sleep(5.0)
            return
        pending = 0
        for px, pz in probes:
            out = r(f"execute if chunk loaded {px} 64 {pz} run say qa-probe")
            if "chunk is loaded" in out or "qa-probe" in out or "True" in out:
                continue
            if "Unknown" in out or "Invalid" in out or "Expected" in out:
                supported = False
                print("chunk-loaded probe unsupported; falling back to sleep")
                break
            pending += 1
        if pending == 0:
            print("all sample chunks generated")
            return
        time.sleep(3.0)
    print("WARNING: chunk generation did not finish within 300s")


def main():
    global rc
    ap = argparse.ArgumentParser()
    ap.add_argument("--host", default="127.0.0.1")
    ap.add_argument("--port", type=int, default=25575)
    ap.add_argument("--password", default="opencode")
    args = ap.parse_args()

    rc = None
    for attempt in range(30):
        try:
            rc = Rcon(args.host, args.port, args.password, timeout=120)
            break
        except OSError:
            time.sleep(2.0)
    if rc is None:
        print("FAIL  could not connect to RCON")
        return 1

    test_harvest_gate_tags()
    try:
        load_sample_batches()
        test_cast_produces_hot_ingot()
        test_no_mold_no_cast()
        test_output_stack_cap()
        test_on_remove_drops()
        test_generator_full_stack()
        test_reload_preserves_state()
        wait_for_chunks()
        r("forceload remove all")
        time.sleep(2.0)
        r("stop")
    finally:
        rc.close()

    failed = [n for n, ok, _ in results if not ok]
    print(f"\n{len(results) - len(failed)}/{len(results)} passed")
    if failed:
        print("failed:", ", ".join(failed))
    return 0 if not failed else 1


if __name__ == "__main__":
    sys.exit(main())
