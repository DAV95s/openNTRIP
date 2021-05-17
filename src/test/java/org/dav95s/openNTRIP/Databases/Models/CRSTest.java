package org.dav95s.openNTRIP.Databases.Models;

import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class CRSTest {
    @Test
    public void readTest() {
        try {
            CRS crs = new CRS(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}