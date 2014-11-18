package mirana.common.shell;

import java.util.ArrayList;
import java.util.List;

public class CmdResult {

    public boolean      success     = true;
    public List<String> contents    = new ArrayList<String>();
    public String       errorReason = "";

}
