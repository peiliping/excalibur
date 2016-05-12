package icesword.agent.service;

import java.io.IOException;

import com.sun.tools.attach.VirtualMachine;

public class VMAttachService {

    public static String flag(String pid) throws IOException {
        VirtualMachine vm = attach(pid);
        String r = (vm.getSystemProperties().getProperty("java.vm.specification.version"));
        vm.detach();
        return r;
    }

    // Attach to <pid>, exiting if we fail to attach
    private static VirtualMachine attach(String pid) {
        try {
            return VirtualMachine.attach(pid);
        } catch (Exception x) {
            String msg = x.getMessage();
            if (msg != null) {
                System.err.println(pid + ": " + msg);
            } else {
                x.printStackTrace();
            }
            System.exit(1);
            return null; // keep compiler happy
        }
    }
}
