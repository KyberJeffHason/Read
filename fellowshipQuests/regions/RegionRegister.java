package lotr.common.fellowshipQuests.regions;

import lotr.common.fellowshipQuests.regions.list.RegionTopazBay;
import lotr.common.fellowshipQuests.regions.list.RegionVoldholl;
import lotr.common.world.map.LOTRWaypoint;

import java.util.ArrayList;

public class RegionRegister {

    public static ArrayList<RegionBase> regions = new ArrayList<>();



    public static void registerRegions() {
        regions.add(new RegionVoldholl());
        regions.add(new RegionTopazBay());
    }

    public static RegionBase getRegionByPoint(LOTRWaypoint W) {
        if(!regions.isEmpty()) {
            for (RegionBase r : regions) {
                if (r.getWaypoint() == W) {
                    return r;
                }
            }
        }
        return null;
    }




}
