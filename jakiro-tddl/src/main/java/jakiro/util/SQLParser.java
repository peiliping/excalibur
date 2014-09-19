package jakiro.util;

import jakiro.mybatis.TableNameHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLParser {

    /**
     * @link 
     *       https://github.com/alibaba/tb_tddl/blob/master/tddl-common/src/main/java/com/taobao/tddl
     *       /common/SQLPreParser.java
     */

    private static Pattern ptable        = Pattern.compile("\\s+([a-z0-9_@\\.\"$]+)\\s+");
    private static Pattern pinsert_into  = Pattern.compile("\\s+into\\s+([a-z0-9_@\\.\"$]+)[\\s(]+");
    private static Pattern pdelete_from  = Pattern.compile("\\s+from\\s+([a-z0-9_@\\.\"$]+)\\s+");
    private static Pattern pselect_from  = Pattern.compile("\\s+from\\s+([a-z0-9_@\\.\"$]+)[\\s)]+");
    private static Pattern preplace_from = Pattern.compile("\\s+into\\s+([a-z0-9_@\\.\"$]+)[\\s(]+");
    private static Pattern pfrom_where   = Pattern.compile("\\s+from\\s+(.*)\\s+where\\s+");
    private static String  hintregx      = "/\\*.*?\\*/";

    public static Pair<String, String> findTableNameAndType(String sql0) {
        if (sql0 == null)
            return null;
        sql0 = sql0.trim();
        if (sql0.length() < 7) {
            return null;
        }

        if (sql0.indexOf("/*") != -1) {
            sql0 = sql0.replaceAll(hintregx, "").trim();
        }
        sql0 = sql0.toLowerCase();
        sql0 = sql0 + " ";

        if (sql0.startsWith("update")) {
            Matcher m = ptable.matcher(sql0);
            if (m.find(6)) {
                return Pair.of(m.group(1), TableNameHandler.SQL_TYPE_UPDATE);
            }
            return null;
        }

        if (sql0.startsWith("delete")) {
            Matcher m = pdelete_from.matcher(sql0);
            if (m.find(6)) {
                return Pair.of(m.group(1), TableNameHandler.SQL_TYPE_DELETE);
            }

            m = ptable.matcher(sql0);
            if (m.find(6)) {
                return Pair.of(m.group(1), TableNameHandler.SQL_TYPE_DELETE);
            }
            return null;
        }

        if (sql0.startsWith("insert")) {
            Matcher m = pinsert_into.matcher(sql0);
            if (m.find(6)) {
                return Pair.of(m.group(1), TableNameHandler.SQL_TYPE_INSERT);
            }
            return null;
        }

        if (sql0.startsWith("replace")) {
            Matcher m = preplace_from.matcher(sql0);
            if (m.find(6)) {
                return Pair.of(m.group(1), TableNameHandler.SQL_TYPE_OTHER);
            }
            return null;
        }

        if (!sql0.startsWith("select")) {
            return null;
        }

        Matcher m = pselect_from.matcher(sql0);
        if (m.find(6)) {
            return Pair.of(m.group(1), TableNameHandler.SQL_TYPE_SELECT);
        }

        m = pfrom_where.matcher(sql0);
        if (m.find(6)) {
            String from2where = m.group(1);
            String[] tables = from2where.split(",");
            for (int i = 1; i < tables.length; i++) {
                if (tables[i].indexOf('(') == -1) {
                    return Pair.of(tables[i].trim().split("\\s")[0], TableNameHandler.SQL_TYPE_SELECT);
                } else {
                    Pair<String, String> r = findTableNameAndType(tables[i]);
                    if (r != null) {
                        return r;
                    }
                }
            }
        }

        if (sql0.indexOf(")from") != -1) {
            sql0 = sql0.replaceAll("\\)from", ") from");
            return findTableNameAndType(sql0);
        }

        return null;
    }

    public static void main(String[] args) throws IOException {
        List<String> sqls = new ArrayList<String>();
        // sqls.add("	\r	\r\n \n   	update 	t_a$ble0 set a=1");
        // sqls.add("delete from t_a$ble0\r\n t where t.id = 0");
        // sqls.add("delete from t_a$ble0");
        // sqls.add("insert into t_a$ble0 t values(?,?) where t.id = 0");
        // sqls.add("insert into t_a$ble0(col_a, col_b) values(?,?) where id = 0");
        // sqls.add("select count(*) from t_a$ble0");
        // sqls.add("select 1 from t_a$ble0 t where t.id=0");
        // sqls.add("select 1 from (select id from t_a$ble0) t where t.id = 5");
        // sqls.add("select 1 from(select id from t_a$ble0) t where t.id = 5");
        // sqls.add("select 1 from (select id from table2) t, t_a$ble0 a where t.id = a.id");
        // sqls.add("select 1 from t_a$ble0 a, (select id from table2) t where t.id = a.id");
        // sqls.add("select count(*) from CRM_KNOWLEDGE_DETAIL kc,CRM_KNOWLEDGE_BASE a where a.id=kc.KNOWLEDGE_ID");
        // sqls.add("SELECT * FROM (SELECT CAST(STR2NUMLIST(#in#) AS NUMTABLETYPE) FROM dual) WHERE rownum <= 200");
        // sqls.add("insert into ic_cache@lnk_icdb0 values (:b1 , sysdate) ");
        // sqls.add("select a ,r from icuser.tb0 where spu_id=:f1 and auction_type <> 'a' ");
        // sqls.add("select id from tb0 a, table(cast(str2numlist(:1) as numtabletype )) t where a.id=:2");
        // sqls.add("select id from table(cast(str2numlist(:1) as numtabletype )) t, tb0 a where a.id=:2");
        // sqls.add("select id from table(cast(str2numlist(:1) as numtabletype )) t, table(cast(str2numlist(:1) as numtabletype )) b, tb0 a where a.id=:2");
        // sqls.add("select id from table(cast(str2numlist(:1) as numtabletype )) t, (select col1 from tb2) b, tb0 a where a.id=:2");
        // sqls.add("select id from table(cast(str2numlist(:1) as numtabletype )) t, (select col1,col2 from tb2) b, tb0 a where a.id=:2");
        // sqls.add("select id from table(cast(str2numlist(:1) as numtabletype )) t, (select col1,col2 from tb2 where tb2.id=0) b, tb0 a where a.id=:2");
        // sqls.add("select max(mod(nvl(option$,0),2))from objauth$ where obj#=:1 group by grantee# order by grantee# ");
        // sqls.add("select output from table(dbms_workload_repository.awr_report_html(:dbid, :inst, :bid, :eid, :rpt_options))");
        // sqls.add("DELETE crm_adgroup_detail WHERE status = 1 AND adgroupno = :1");
        // sqls.add("SELECT * FROM \"ALIMM\".\"ADZONESCORE\"");
        // sqls.add("select nvl(min(ts#), -1) \"sysauxts#\" from sys.ts$ where name = 'sysaux'");
        // sqls.add("/* oracleoem */ select nvl(min(ts#), -1) \"sysauxts#\" from sys.ts$ where name = 'sysaux'");
        // sqls.add("/* oracleoem */ select /* sss */nvl(min(ts#), -1) \"sysauxts#\" from sys.ts$ where name = 'sysaux'");
        // sqls.add("failed:select u.id from (table(str2numlist(:1))) n join et_airsupply_users u on n.column_value = u.id");
        // sqls.add("replace into t (i,c,d,ui) values (?,?,?,?)");
        // sqls.add(" SELECT /*+ ordered use_nl(acc,rb) */ rb.ID,rb.USER_ID,rb.DATABASE_CODE,EVENT_EXTEND FROM (SELECT /*+index(crb,IDX_RA_SC_BILL_STAT) */ crb.USER_ID, min(crb.id) dt FROM RA_SC_BILL crb  WHERE crb.status = 1 and crb.process_mode = 0 and rownum <= 20000 and DATABASE_CODE in (1, 2, 3) GROUP BY crb.USER_ID) acc, RA_SC_BILL rb WHERE rb.Id = acc.dt  and rownum <= 123  and not exists (select 1 from RA_SC_BILL up where up.status = 2 and up.USER_ID = acc.USER_ID)");
        // sqls.add("select	k,v from kv where k = ?#");
        // sqls.add("select id,name,sex from people force index (name) where id=5");
        sqls.add("insert into medd values (?,?),(?,?)");
        for (String sql : sqls) {
            System.out.println(findTableNameAndType(sql) + " <-- " + sql);
        }
    }
}
