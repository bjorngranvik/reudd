package org.reudd

import static org.reudd.Reudd.*;
import org.junit.Test;

public class ReuddTest {

    @Test
    public void testVersion() {

        String[] args = ["version"]
        String result = doVersion()

        assert result != null
        assert result.startsWith("ReUDD v")
    }


    @Test
    public void testHelp() {

        String[] args = ["help"]
        String result = doHelp()

        assert result != null
        assert result.startsWith("Usage: reudd <command>")
    }


    @Test
    public void testImport() {
        String[] args = ["import", "example-import/example-import-data_semicolon.csv"]
        String result = doImport(args)

        assert result != null
        assert result == "example-import/example-import-data_semicolon.csv nodes:27, relationships:27"
    }
}
