package eu.wauz.wazera.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class QueryBuilder {

    private String select;

    private List<String> joins = new ArrayList<String>();

    private List<String> wheres = new ArrayList<String>();

    private List<String> groupBys = new ArrayList<String>();

    private List<String> orderBys = new ArrayList<String>();

    public QueryBuilder() {
    	
    }

    public QueryBuilder(String select) {
        this.select = select;
    }

    public String buildQuery() {
        String queryString = StringUtils.trim(select);

        if(!joins.isEmpty()) {
        	for (String join : joins) {
        		queryString += " " + StringUtils.trim(join);
        	}
        }

        if(!wheres.isEmpty()) {
            String op = "where";
            for (String where : wheres) {
                queryString += " " + op + " " + StringUtils.trim(where);
                op = "and";
            }
        }

        if(!groupBys.isEmpty()) {
        	String op = "group by";
        	for (String groupBy : groupBys) {
        		queryString += " " + op + " " + StringUtils.trim(groupBy);
        		op = ", ";
        	}
        }

        if(!orderBys.isEmpty()) {
            String op = "order by";
            for (String orderBy : orderBys) {
                queryString += " " + op + " " + StringUtils.trim(orderBy);
                op = ", ";
            }
        }

        return queryString;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public List<String> getJoins() {
        return joins;
    }

    public void setJoins(List<String> joins) {
        this.joins = joins;
    }

    public void addJoin(String join) {
        if(join != null) {
            if(!joins.contains(join)) {
            	joins.add(join);
            }
        }
    }

    public void addInnerJoin(String join) {
        if(join != null) {
            join = "inner join " + join;
            if(!joins.contains(join)) {
            	joins.add(join);
            }
        }
    }

    public void addLeftJoin(String join) {
        if(join != null) {
            join = "left join " + join;
            if(!joins.contains(join)) {
            	joins.add(join);
            }
        }
    }

    public List<String> getWheres() {
        return wheres;
    }

    public void setWheres(List<String> wheres) {
        this.wheres = wheres;
    }

    public void addWhere(String where) {
        if(!wheres.contains(where)) {
        	wheres.add(where);
        }
    }

    public void addGroupBy(String groupBy) {
    	if(!groupBys.contains(groupBy)) {
    		groupBys.add(groupBy);
    	}
    }

    public List<String> getOrderBys() {
        return orderBys;
    }

    public void setOrderBys(List<String> orderBys) {
        this.orderBys = orderBys;
    }

    public void addOrderBy(String orderBy) {
        if(!orderBys.contains(orderBy)) {
        	orderBys.add(orderBy);
        }
    }

    public String toString() {
        return buildQuery();
    }

}
