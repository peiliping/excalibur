package nebulachain.core;

import java.lang.instrument.Instrumentation;

import com.github.ompc.greys.core.command.Command;

public class NBCCommandHandler {

    private final Instrumentation inst;

    public NBCCommandHandler(Instrumentation inst) {
        this.inst = inst;
    }

    public void executeCommand(Command cmd) {

    }

    public void close() {

    }

}
