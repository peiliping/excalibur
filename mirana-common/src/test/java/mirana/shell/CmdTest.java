package mirana.shell;

import mirana.common.shell.CmdExecuter;
import mirana.common.shell.CmdResult;

public class CmdTest {

    public static void main(String[] args) {

        CmdResult c = CmdExecuter.exec0("update");
        System.out.println(c.errorReason);

        CmdResult c1 = CmdExecuter.exec0("uptime");
        System.out.println(c1.contents);
    }

}
