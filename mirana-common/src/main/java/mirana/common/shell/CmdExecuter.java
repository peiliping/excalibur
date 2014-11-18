package mirana.common.shell;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class CmdExecuter {

    public static List<String> exec(String cmd) throws IOException {
        Process process = Runtime.getRuntime().exec(cmd);
        InputStreamReader ir = new InputStreamReader(process.getInputStream());
        LineNumberReader input = new LineNumberReader(ir);
        List<String> contents = new ArrayList<String>();
        String line;
        while ((line = input.readLine()) != null)
            contents.add(line);
        input.close();
        ir.close();
        return contents;
    }

    public static CmdResult exec0(String cmd) {
        CmdResult cr = new CmdResult();
        try {
            cr.contents = exec(cmd);
        } catch (IOException e) {
            cr.success = false;
            cr.errorReason = e.getMessage();
        }
        return cr;
    }
}
