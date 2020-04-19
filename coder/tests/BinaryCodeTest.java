import code.BinaryCode;
import exception.InvalidInputFormatException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BinaryCodeTest {

    @Test(expected = InvalidInputFormatException.class)
    public void testValidateInput() throws InvalidInputFormatException{
        BinaryCode code = new BinaryCode("abc010");
    }

    @Test
    public void testToString() throws InvalidInputFormatException{
        String expected = "00011000";
        BinaryCode code = new BinaryCode(expected);
        Assert.assertEquals(code.toString(),expected);
    }

    @Test
    public void testLength() throws InvalidInputFormatException{
        String expected = "00011000";
        int length = new BinaryCode(expected).length();
        Assert.assertEquals(length,expected.length());
    }

    @Test
    public void testGetBits() throws InvalidInputFormatException{
        List<Boolean> expected = new ArrayList<Boolean>(
                Arrays.asList(false,false,false,true,true,false,false,false));
        BinaryCode code = new BinaryCode("00011000");
        Assert.assertEquals(code.getBits(),expected);
    }
}
