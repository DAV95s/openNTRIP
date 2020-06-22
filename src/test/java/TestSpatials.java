import org.adv25.ADVNTRIP.Spatial.Point_lla;
import org.junit.Test;

public class TestSpatials {

    @Test
    public void lea_distentions() {
        Point_lla Frankfurt = new Point_lla(50.110389, 8.682100);
        Point_lla Wuhan = new Point_lla(30.608273, 114.251080);

        System.out.println(Frankfurt.distance(Wuhan));

        Point_lla LisiyNos = new Point_lla(60.017758, 30.001344);
        Point_lla Lahta = new Point_lla(59.987124, 30.177415);

        System.out.println(LisiyNos.distance(Lahta));

        Point_lla Paris = new Point_lla(48.856663, 2.351556);
        Point_lla Madagascar = new Point_lla(-21.114296, 46.317450);

        System.out.println(Paris.distance(Madagascar));

        Point_lla Capetown = new Point_lla(-33.919785, 18.425596);
        Point_lla CapetownAirPort = new Point_lla(-33.969318, 18.596905);

        System.out.println(Capetown.distance(CapetownAirPort));

        Point_lla Santiago = new Point_lla(-33.477669, -70.642364);

        System.out.println(CapetownAirPort.distance(Santiago));

    }
}
