import org.dav95s.openNTRIP.CRSUtils.GridShift.Node;
import org.dav95s.openNTRIP.CRSUtils.GridShift.RTree;
import org.dav95s.openNTRIP.CRSUtils.GridShift.Triangle;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class Triangles {

    @Test
    public void one() {
        Node[] nodes = new Node[]{
                new Node(335, 510, 0, 0),
                new Node(830, 779, 0, 0),
                new Node(292, 1323, 0, 0)
        };

        Triangle triangle = new Triangle(nodes);
        RTree<Triangle> rTree = new RTree<>();
        rTree.insert(triangle.coords, triangle.dimensions, triangle);

        List<Triangle> search = rTree.search(new float[]{306, 697}, new float[]{679, 328});

        System.out.println(search.size());
    }
}
