package lotr.common.fellowshipQuests.regions;

import lotr.common.world.map.LOTRWaypoint;

public class RegionBase {


    private LOTRWaypoint waypoint;
    private int[][] coords;

    public RegionBase(LOTRWaypoint waypoint, int[][] coords) {
        this.coords = coords;
        this.waypoint = waypoint;
    }

    public int[][] getCoords() {
        return coords;
    }

    public LOTRWaypoint getWaypoint() {
        return waypoint;
    }

    public void setCoords(int[][] coords) {
        this.coords = coords;
    }

    public void setWaypoint(LOTRWaypoint waypoint) {
        this.waypoint = waypoint;
    }
}
