package phoenix.sqlparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

public class Main {

    public static void main(String[] args) {

        String sql = "select f , count(1)  from abc where a=1 and b in(2,3,6) and c between 10 and 20 and d > 3 and d < 20 and e = \"abc\"  group by f order by g desc limit 100";

        MySqlStatementParser p = new MySqlStatementParser(sql);

        List<SQLStatement> st = p.parseStatementList();
        Validate.isTrue(st.size() == 1);

        SQLSelectStatement s0 = (SQLSelectStatement) st.get(0);
        SQLBinaryOpExpr mb = (SQLBinaryOpExpr) ((MySqlSelectQueryBlock) s0.getSelect().getQuery()).getWhere();
        Validate.isTrue(mb.getOperator() == SQLBinaryOperator.BooleanAnd);

        Map<String, List<SQLExpr>> results = new HashMap<String, List<SQLExpr>>();
        handle(results, mb);
        List<String> pks = new ArrayList<String>();
        pks.add("a");
        pks.add("b");
        pks.add("c");
        pks.add("f");
        check(pks, results);
    }

    private static void check(List<String> mustPk, Map<String, List<SQLExpr>> results) {
        for (String pk : mustPk) {
            Validate.isTrue(results.containsKey(pk), "No Key :" + pk);
        }
    }

    private static void handle(Map<String, List<SQLExpr>> r, SQLExpr se) {
        if (se instanceof SQLBinaryOpExpr && ((SQLBinaryOpExpr) se).getLeft() instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr sbo = (SQLBinaryOpExpr) se;
            if (sbo.getOperator() == SQLBinaryOperator.BooleanOr)
                return;
            handle(r, sbo.getLeft());
            handle(r, sbo.getRight());
        } else if (se instanceof SQLInListExpr) {
            SQLInListExpr sbo = (SQLInListExpr) se;
            Validate.isTrue(!r.containsKey(sbo.getExpr().toString()));
            r.put(sbo.getExpr().toString(), sbo.getTargetList());
        } else if (se instanceof SQLBetweenExpr) {
            SQLBetweenExpr sbo = (SQLBetweenExpr) se;
            List<SQLExpr> l = new ArrayList<SQLExpr>();
            l.add(sbo.getBeginExpr());
            l.add(sbo.getEndExpr());
            Validate.isTrue(!r.containsKey(sbo.testExpr.toString()));
            r.put(sbo.testExpr.toString(), l);
        } else {
            Validate.isTrue(se instanceof SQLBinaryOpExpr);
            SQLBinaryOpExpr sbo = (SQLBinaryOpExpr) se;
            List<SQLExpr> l = new ArrayList<SQLExpr>();
            if (sbo.getOperator() == SQLBinaryOperator.Equality) {
                l.add(sbo.getRight());
                Validate.isTrue(!r.containsKey(sbo.getLeft().toString()));
                r.put(sbo.getLeft().toString(), l);
            } else if (sbo.getOperator() == SQLBinaryOperator.LessThan || sbo.getOperator() == SQLBinaryOperator.LessThanOrEqual) {
                l.add(new SQLIntegerExpr(Long.MIN_VALUE));
                l.add(sbo.getRight());
                Validate.isTrue(!r.containsKey(sbo.getLeft().toString()) || r.get(sbo.getLeft().toString()).size() == 2);
                if (r.containsKey(sbo.getLeft().toString())) {
                    SQLExpr t = r.get(sbo.getLeft().toString()).get(1);
                    if (Double.valueOf(t.toString()) > Double.valueOf(sbo.getRight().toString())) {
                        r.get(sbo.getLeft().toString()).set(1, sbo.getRight());
                    }
                } else {
                    r.put(sbo.getLeft().toString(), l);
                }
            } else if (sbo.getOperator() == SQLBinaryOperator.GreaterThan || sbo.getOperator() == SQLBinaryOperator.GreaterThanOrEqual) {
                l.add(sbo.getRight());
                l.add(new SQLIntegerExpr(Long.MAX_VALUE));
                Validate.isTrue(!r.containsKey(sbo.getLeft().toString()) || r.get(sbo.getLeft().toString()).size() == 2);
                if (r.containsKey(sbo.getLeft().toString())) {
                    SQLExpr t = r.get(sbo.getLeft().toString()).get(0);
                    if (Double.valueOf(t.toString()) < Double.valueOf(sbo.getRight().toString())) {
                        r.get(sbo.getLeft().toString()).set(0, sbo.getRight());
                    }
                } else {
                    r.put(sbo.getLeft().toString(), l);
                }
            } else {
                System.out.println(((SQLBinaryOpExpr) se).getOperator());
            }
        }
    }
}
