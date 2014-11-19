package mirana.shell;

import mirana.common.shell.CmdExecuter;
import mirana.common.shell.CmdResult;

public class CmdTest {

    public static void main(String[] args) {
        CmdResult c1 = CmdExecuter.exec0("atosl  -o /home/peiliping/dev/logs/SystemMonitor --arch=armv7 --load-address 0xd2000 0x000f3400");
        System.out.println(c1.contents);
        System.out.println(c1.errorReason);
        System.out.println(c1.success);
    }

}
