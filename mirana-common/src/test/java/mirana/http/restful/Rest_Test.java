package mirana.http.restful;

import java.util.HashSet;
import java.util.Set;

import mirana.common.http.restful.RestfulUrlTool;

public class Rest_Test {

    public static void main(String[] args) {

        Set<String> ps = new HashSet<String>();
        ps.add("/mobile/{b}/userid/{c}");
        ps.add("/mobile/{b}/userid/{c}.jsp");
        ps.add("/mobile/{b}/user{vv}/{c}.{d}");
        ps.add("/mobile/{b}/dce/{v}.jsp");
        ps.add("/mobile/{b}/userid/sdf/{ke}{c}.jsp");
        ps.add("/mobile/{b}/userid/sdf/aaa{c}.html");

        String a = RestfulUrlTool.handle("/mobile/3/userid/sdf/aaaxb.jsp", ps);
        
        System.out.println(a);

    }

}
