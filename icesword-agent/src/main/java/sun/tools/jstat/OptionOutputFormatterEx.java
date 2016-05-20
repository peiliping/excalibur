package sun.tools.jstat;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.tools.jstat.OptionFormat;
import sun.tools.jstat.OptionOutputFormatter;

/**
 * 
 * Support Custom Delimiter
 * 
 * @author peiliping
 *
 */

public class OptionOutputFormatterEx extends OptionOutputFormatter {

    public static final String DEFAULT_COLUMN_DELIMITER     = " ";   // print to console
    public static final String COLUMN_DELIMITER_4_SERIALIZE = "\001"; // string split

    private OptionFormat       format;
    private MonitoredVm        vm;
    private boolean            needSearialize;

    public OptionOutputFormatterEx(MonitoredVm vm, OptionFormat format, boolean needSearialize) throws MonitorException {
        super(vm, format);
        this.format = format;
        this.vm = vm;
        this.needSearialize = needSearialize;
    }

    @Override
    public String getRow() throws MonitorException {
        String dlt = needSearialize ? COLUMN_DELIMITER_4_SERIALIZE : DEFAULT_COLUMN_DELIMITER;
        RowClosureEx rc = RowClosureEx.create(vm, dlt);
        format.apply(rc);
        return rc.getRow();
    }
}
