package org.dav95s.openNTRIP.CRSUtils.GridShift;

public class Triangle {
    public Node[] nodes;
    public float[] coords;
    public float[] dimensions;

    public Triangle(Node[] nodes) {
        this.nodes = nodes;
        initCoordsAndDimensions();

    }

    private void initCoordsAndDimensions() {
        double nmax = nodes[0].north;
        double nmin = nodes[0].north;
        double emax = nodes[0].east;
        double emin = nodes[0].east;

        for (Node node : nodes) {
            if (nmax < node.north) {
                nmax = node.north;
            } else if (nmin > node.north) {
                nmin = node.north;
            }
            if (emax < node.east) {
                emax = node.east;
            } else if (emin > node.east) {
                emin = node.east;
            }
        }
        coords = new float[]{(float) emin, (float) nmax};
        dimensions = new float[]{(float) (emax - emin), (float) (nmax - nmin)};
    }


}
