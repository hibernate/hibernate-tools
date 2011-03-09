package org.hibernate.tool.ide.completion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.hibernate.hql.antlr.HqlSqlTokenTypes;

/**
 * The HQLAnalyzer can answer certain questions about a HQL String.
 * 
 * @author leon, max.andersen@jboss.com
 */
public class HQLAnalyzer {

	/** Defines the HQL keywords. Based on hql.g antlr grammer in 2005 ;) */
    private static String[] hqlKeywords = { "between", "class", "delete",
			"desc", "distinct", "elements", "escape", "exists", "false",
			"fetch", "from", "full", "group", "having", "in", "indices",
			"inner", "insert", "into", "is", "join", "left", "like", "new",
			"not", "null", "or", "order", "outer", "properties", "right",
			"select", "set", "some", "true", "union", "update", "versioned",
			"where", "and", "or", "as","on", "with",

   		// -- SQL tokens --
   		// These aren't part of HQL, but recognized by the lexer. Could be
		// usefull for having SQL in the editor..but for now we keep them out
    	// "case", "end", "else", "then", "when", 
    	 

    	// -- EJBQL tokens --
    	"both", "empty", "leading", "member", "object", "of", "trailing", 
    };
    

    /**
	 * built-in function names. Various normal builtin functions in SQL/HQL.
	 * Maybe sShould try and do this dynamically based on dialect or
	 * sqlfunctionregistry
	 */
    private static String[] builtInFunctions = {
			// standard sql92 functions
			"substring", "locate", "trim", "length", "bit_length", "coalesce",
			"nullif", "abs", "mod", "sqrt",
			"upper",
			"lower",
			"cast",
			"extract",

			// time functions mapped to ansi extract
			"second", "minute", "hour", "day",
			"month",
			"year",

			"str",

			// misc functions - based on oracle dialect
			"sign", "acos", "asin", "atan", "cos", "cosh", "exp", "ln", "sin",
			"sinh", "stddev", "sqrt", "tan", "tanh", "variance",

			"round", "trunc", "ceil", "floor",

			"chr", "initcap", "lower", "ltrim", "rtrim", "soundex", "upper",
			"ascii", "length", "to_char", "to_date",

			"current_date", "current_time", "current_timestamp", "lastday",
			"sysday", "systimestamp", "uid", "user",

			"rowid", "rownum",

			"concat", "instr", "instrb", "lpad", "replace", "rpad", "substr",
			"substrb", "translate",

			"substring", "locate", "bit_length", "coalesce",

			"atan2", "log", "mod", "nvl", "nvl2", "power",

			"add_months", "months_between", "next_day",

			"max", "min", };
    
    static {
    	// to allow binary search
    	Arrays.sort(builtInFunctions);
    	Arrays.sort(hqlKeywords);
    }

    protected SimpleHQLLexer getLexer(char chars[], int end) {
    	return new AntlrSimpleHQLLexer(chars,end);
    }
    
    protected SimpleHQLLexer getLexer(char chars[]) {
    	return new AntlrSimpleHQLLexer(chars,chars.length);
    }
    
    /**
     * Returns true if the position is at a location where an entityname makes sense.
     * e.g. "from Pr| where x" 
     * @param query
     * @param cursorPosition
     * @return
     */
    public boolean shouldShowEntityNames(String query, int cursorPosition) {
    	return shouldShowEntityNames( query.toCharArray(), cursorPosition );
    }
    
    public boolean shouldShowEntityNames(char chars[], int cursorPosition) {
    	SimpleHQLLexer lexer = getLexer( chars, cursorPosition );
        int tokenId = -1;
        boolean show = false;
        while ((tokenId = lexer.nextTokenId()) != HqlSqlTokenTypes.EOF) {
            if ((tokenId == HqlSqlTokenTypes.FROM ||
                    tokenId == HqlSqlTokenTypes.DELETE ||
                    tokenId == HqlSqlTokenTypes.UPDATE) &&
                    (lexer.getTokenOffset() + lexer.getTokenLength()) < cursorPosition) {
                show = true;
            } else if (tokenId != HqlSqlTokenTypes.DOT && tokenId != HqlSqlTokenTypes.AS && tokenId != HqlSqlTokenTypes.COMMA && tokenId != HqlSqlTokenTypes.IDENT && tokenId != HqlSqlTokenTypes.WS) {
                show = false;                
            } 
        }
        return show;    	
    }
    
    public List getVisibleSubQueries(char[] chars, int position) {
    	SubQueryList sqList = getSubQueries(chars, position);
        List visible = new ArrayList();
        for (Iterator iter = sqList.subQueries.iterator(); iter.hasNext();) {
			SubQuery sq = (SubQuery) iter.next();
			 if (sqList.caretDepth >= sq.depth && (sq.startOffset <= position || sq.endOffset >= position)) {
                visible.add(sq);
            }
        }
        return visible;
    }

    public List getVisibleEntityNames(char[] chars, int position) {
        List sqs = getVisibleSubQueries(chars, position);
        List entityReferences = new ArrayList();
        for (Iterator iter = sqs.iterator(); iter.hasNext();) {
			SubQuery sq = (SubQuery) iter.next();
			entityReferences.addAll(sq.getEntityNames());
		}
        return entityReferences;
    }

    protected SubQueryList getSubQueries(char[] query, int position) {
    	SimpleHQLLexer syntax = getLexer( query );
    	int numericId = -1;
        List subQueries = new ArrayList();
        int depth = 0;
        int caretDepth = 0;
        Map level2SubQuery = new HashMap();
        SubQuery current = null;
        while ((numericId = syntax.nextTokenId()) != HqlSqlTokenTypes.EOF) {
            boolean tokenAdded = false;
            if (numericId == HqlSqlTokenTypes.OPEN) {
                depth++;
                if (position > syntax.getTokenOffset()) {
                    caretDepth = depth;
                }
            } else if (numericId == HqlSqlTokenTypes.CLOSE) {
                SubQuery currentDepthQuery = (SubQuery) level2SubQuery.get(new Integer(depth));
                // We check if we have a query on the current depth.
                // If yes, we'll have to close it
                if (currentDepthQuery != null && currentDepthQuery.depth == depth) {
                    currentDepthQuery.endOffset = syntax.getTokenOffset();
                    currentDepthQuery.tokenIds.add(new Integer(numericId));
                    currentDepthQuery.tokenText.add(String.valueOf(query, syntax.getTokenOffset(), syntax.getTokenLength()));
                    subQueries.add(currentDepthQuery);
                    level2SubQuery.remove(new Integer(depth));
                    tokenAdded = true;
                }
                depth--;
                if (position > syntax.getTokenOffset()) {
                    caretDepth = depth;
                }
            }
            switch (numericId) {
                case HqlSqlTokenTypes.FROM:
                case HqlSqlTokenTypes.UPDATE:
                case HqlSqlTokenTypes.DELETE:
                case HqlSqlTokenTypes.SELECT:
                    if (!level2SubQuery.containsKey(new Integer(depth))) {
                        current = new SubQuery();
                        current.depth = depth;
                        current.startOffset = syntax.getTokenOffset();
                        level2SubQuery.put(new Integer(depth), current);
                    }
                    current.tokenIds.add(new Integer(numericId));
                    current.tokenText.add(String.valueOf(query, syntax.getTokenOffset(), syntax.getTokenLength()));
                    break;
                default:
                    if (!tokenAdded) {
                        SubQuery sq = (SubQuery) level2SubQuery.get(new Integer(depth));
                        int i = depth;
                        while (sq == null && i >= 0) {
                            sq = (SubQuery) level2SubQuery.get(new Integer(i--));
                        }
                        if (sq != null) {
                            sq.tokenIds.add(new Integer(numericId));
                            sq.tokenText.add(String.valueOf(query, syntax.getTokenOffset(), syntax.getTokenLength()));
                        }
                    }
            }
        }
        for (Iterator iter = level2SubQuery.values().iterator(); iter.hasNext();) {
			SubQuery sq = (SubQuery) iter.next();
			sq.endOffset = syntax.getTokenOffset() + syntax.getTokenLength();
            subQueries.add(sq);
        }
        Collections.sort(subQueries);
        SubQueryList sql = new SubQueryList();
        sql.caretDepth = caretDepth;
        sql.subQueries = subQueries;
        return sql;
    }

    
    /** Returns reference name found from position and backwards in the array.
     **/
    public static String getEntityNamePrefix(char[] chars, int position) {
        StringBuffer buff = new StringBuffer();
        for (int i = position - 1; i >= 0; i--) {
            char c = chars[i];
            if (c == '.' || Character.isJavaIdentifierPart(c)) {
                buff.insert(0, c);
            } else {
                break;
            }
        }
        return buff.toString();
    }

    public static class SubQuery implements Comparable {

        public int compareTo(Object s) {
            return startOffset - ((SubQuery)s).startOffset;
        }
        
        private List tokenIds = new ArrayList();

        private List tokenText = new ArrayList();

        private int startOffset;

        private int endOffset;
        
        private int depth;

        public int getTokenCount() {
            return tokenIds.size();
        }

        public int getToken(int i) {
            return ((Number)tokenIds.get(i)).intValue();
        }

        public String getTokenText(int i) {
            return (String) tokenText.get(i);
        }

        public List getEntityNames() {
            boolean afterFrom = false;
            boolean afterJoin = false;
            StringBuffer tableNames = new StringBuffer();
            StringBuffer joins = new StringBuffer();
            int i = 0;
            boolean cont = true;
            int lastToken = HqlSqlTokenTypes.EOF;
            for (Iterator iter = tokenIds.iterator(); iter.hasNext();) {
				Integer typeInteger = (Integer) iter.next();
				int type = typeInteger.intValue();
				if (!cont) {
                    break;
                }
                if (!afterFrom &&
                        (type == HqlSqlTokenTypes.FROM ||
                        type == HqlSqlTokenTypes.UPDATE ||
                        type == HqlSqlTokenTypes.DELETE)) {
                    afterFrom = true;
                } else if (afterJoin) {
                    switch (type) {
                        case HqlSqlTokenTypes.ORDER:
                        case HqlSqlTokenTypes.WHERE:
                        case HqlSqlTokenTypes.GROUP:
                        case HqlSqlTokenTypes.HAVING:
                            cont = false;
                            break;
                        case HqlSqlTokenTypes.INNER:
                        case HqlSqlTokenTypes.OUTER:
                        case HqlSqlTokenTypes.LEFT:
                        case HqlSqlTokenTypes.RIGHT:
                        case HqlSqlTokenTypes.JOIN:
                            joins.append(",");
                            break;
                        case HqlSqlTokenTypes.COMMA: 
                        	joins.append(","); //TODO: we should detect this and create the list directly instead of relying on the tokenizer
                        	break;
                        case HqlSqlTokenTypes.DOT:
                        	joins.append("."); 
                        	break;
                        case HqlSqlTokenTypes.IDENT:
                        	if(lastToken!=HqlSqlTokenTypes.DOT) {
                        		joins.append(" ");
                        	} 
                            joins.append(tokenText.get(i));
                            break;
                    }
                } else if (afterFrom) {
                    switch (type) {
                        case HqlSqlTokenTypes.ORDER:
                        case HqlSqlTokenTypes.WHERE:
                        case HqlSqlTokenTypes.GROUP:
                        case HqlSqlTokenTypes.HAVING:
                        case HqlSqlTokenTypes.SET:
                            cont = false;
                            break;
                        case HqlSqlTokenTypes.COMMA: 
                        	tableNames.append(","); //TODO: we should detect this and create the list directly instead of relying on the tokenizer
                        	break;
                        case HqlSqlTokenTypes.DOT:
                        	tableNames.append("."); 
                        	break;
                        case HqlSqlTokenTypes.IDENT:
                        	if(lastToken!=HqlSqlTokenTypes.DOT) {
                        		tableNames.append(" ");
                        	} 
                            tableNames.append(tokenText.get(i));
                            break;
                        case HqlSqlTokenTypes.JOIN:
                        	tableNames.append(",");
                            afterJoin = true;
                            break;
                        default:
                        	break;
                    }
                }
                i++;
                lastToken = type;
            }
            List tables = new ArrayList();
            addEntityReferences(tables, tableNames);
            addEntityReferences(tables, joins);
            return tables;
        }

        private void addEntityReferences(final List tables, final StringBuffer tableNames) {
            StringTokenizer tableTokenizer = new StringTokenizer(tableNames.toString(), ",");
            while (tableTokenizer.hasMoreTokens()) {
                String table = tableTokenizer.nextToken().trim();
                if (table.indexOf(' ') == -1 && table.length() > 0) {
                    tables.add(new EntityNameReference(table, table));
                } else {
                    StringTokenizer aliasTokenizer = new StringTokenizer(table, " ");
                    if (aliasTokenizer.countTokens() >= 2) {
                        String type = aliasTokenizer.nextToken().trim();
                        String alias = aliasTokenizer.nextToken().trim();
                        if (type.length() > 0 && alias.length() > 0) {
                            tables.add(new EntityNameReference(type, alias));
                        }
                    }
                }
            }
        }
    }

    static class SubQueryList {

        int caretDepth;

        List subQueries;
    }

    
    static String[] getHQLKeywords() {
        return hqlKeywords;
    }
    
    static String[] getHQLFunctionNames() {
        return builtInFunctions;
    }
    
}
