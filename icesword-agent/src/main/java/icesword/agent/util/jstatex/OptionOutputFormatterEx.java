package icesword.agent.util.jstatex;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.tools.jstat.OptionFormat;
import sun.tools.jstat.OptionOutputFormatter;
import sun.tools.jstat.RowClosureEx;

public class OptionOutputFormatterEx extends OptionOutputFormatter {

    private OptionFormat format;
    private MonitoredVm  vm;

    public OptionOutputFormatterEx(MonitoredVm vm, OptionFormat format) throws MonitorException {
        super(vm, format);
        this.format = format;
        this.vm = vm;
    }

    @Override
    public String getRow() throws MonitorException {
        RowClosureEx rc = new RowClosureEx(vm);
        format.apply(rc);
        return rc.getRow();
    }

}
