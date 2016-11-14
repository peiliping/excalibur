package nebulachain.core;

import java.util.List;

public class Startup {

    public static void main(String[] args) {
        try {
            CmdParams cmdParams = new CmdParams(args);
            attach(cmdParams);
            System.out.println("==========END==========");
        } catch (Throwable t) {
            System.err.println("Failed : " + t);
            System.exit(-1);
        }
    }

    private static void attach(CmdParams cmdParams) throws Exception {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Class<?> vmdClass = loader.loadClass("com.sun.tools.attach.VirtualMachineDescriptor");
        final Class<?> vmClass = loader.loadClass("com.sun.tools.attach.VirtualMachine");

        Object attachVmdObj = null;
        for (Object obj : (List<?>) vmClass.getMethod("list", (Class<?>[]) null).invoke(null, (Object[]) null)) {
            if ((vmdClass.getMethod("id", (Class<?>[]) null).invoke(obj, (Object[]) null)).equals(Integer.toString(cmdParams.getPid()))) {
                attachVmdObj = obj;
            }
        }

        Object vmObj = null;
        try {
            if (null == attachVmdObj) {
                vmObj = vmClass.getMethod("attach", String.class).invoke(null, "" + cmdParams.getPid());
            } else {
                vmObj = vmClass.getMethod("attach", vmdClass).invoke(null, attachVmdObj);
            }
            vmClass.getMethod("loadAgent", String.class, String.class).invoke(vmObj, cmdParams.getAgentPath(), cmdParams.getCorePath());
        } finally {
            if (null != vmObj) {
                vmClass.getMethod("detach", (Class<?>[]) null).invoke(vmObj, (Object[]) null);
            }
        }
    }

}
