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
    drained = has(fluid, "Amount:0")
    reset = has(progress, "Progress: 0")
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
    bt = nbt_of(r(path(x, y, z, "Inventory")))
    speed = nbt_of(r(path(x, y, z, "Speed")))
    ok_start = has(bt, "count:63")  # first leaf consumed
    ok_speed = has(speed, "64.0f")
    record("GEN1 generator starts with fuel and outputs 64 RPM",
           ok_start and ok_speed, f"inv={bt!r} speed={speed!r}")

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
    x, y, z = RL_POS
    r(f"setblock {x} {y} {z} {TABLE}")
    r(f"data merge block {x} {y} {z} "
      f"{{Inventory:{{Size:2,Items:[{{Slot:0,id:\"luviteium:ingot_mold\",count:1}}]}},"
      f"Progress:5}}")
    r(f"data merge block {x} {y} {z} {{Fluid:{{Fluid:{{Amount:144,id:\"{MOLTEN}\"}}}}}}")
    r("forceload remove all")
    time.sleep(3.0)
    r(f"forceload add {x - 32} {z - 32} {x + 31} {z + 31}")
    time.sleep(2.0)
    fluid = nbt_of(r(path(x, y, z, "Fluid")))
    prog = r(path(x, y, z, "Progress"))
    retained = fluid.count("Amount:144") == 1 and "Progress: 5" in prog
    wait_ticks(25)  # interrupted cast resumes and completes
    inv = nbt_of(r(path(x, y, z, "Inventory")))
    finished = inv.count(f"id:\"{HOT_INGOT}\"") == 1
    record("RL1 unload/reload preserves fluid+progress and resumes interrupted cast",
           retained and finished, f"fluid={fluid!r} prog={prog!r}")
    r(f"setblock {x} {y} {z} air")


def load_sample_batches():
    """Load 4 batches of 16x16 chunks (1024 chunks) for ore sampling."""
    batches = [(-128, -128), (-128, 128), (128, -128), (128, 128)]
    for bx, bz in batches:
        out = r(f"forceload add {bx} {bz} {bx + 127} {bz + 127}")
        print("forceload:", out[:120])
        time.sleep(2.0)


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

    try:
        load_sample_batches()
        test_cast_produces_hot_ingot()
        test_no_mold_no_cast()
        test_output_stack_cap()
        test_on_remove_drops()
        test_generator_full_stack()
        test_reload_preserves_state()
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
