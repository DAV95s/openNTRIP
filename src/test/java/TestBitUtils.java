import org.dav95s.openNTRIP.Tools.BitUtils;
import org.junit.Test;

public class TestBitUtils {
    @Test
    public void uintToBinary() {
        BitUtils bitUtils = new BitUtils();
        bitUtils.setInt(800, 13);

        String back = bitUtils.toString();
        System.out.println(back);

        bitUtils = new BitUtils(back);
        System.out.println(bitUtils.getSignedInt(13));

    }
}
