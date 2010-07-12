<#foreach queryKey in cfg.namedSQLQueries.keySet()>
<#assign queryDef = cfg.namedSQLQueries.get(queryKey)>
    <sql-query 
        name="${queryKey}"
<#if queryDef.flushMode?exists>
        flush-mode="${queryDef.flushMode.toString().toLowerCase()}"
</#if>
<#if queryDef.isCacheable()>
	    cacheable="${queryDef.isCacheable()?string}"
</#if>
<#if queryDef.cacheRegion?exists>
	    cache-region="${queryDef.cacheRegion}"
</#if>
<#if queryDef.fetchSize?exists>
        fetch-size="${queryDef.fetchSize}"
</#if>
<#if queryDef.timeout?exists>
        timeout="${queryDef.timeout?c}"
</#if>    
>
<#foreach tableName in queryDef.querySpaces>
	    <synchronize table="${tableName}" />
</#foreach>
<#foreach returnDef in queryDef.queryReturns>
<#assign returnTag = c2h.getNamedSQLReturnTag(returnDef)>
	    <${returnTag}
             alias="${returnDef.alias}"
<#if c2h.isNamedSQLReturnRoot(returnDef)>
             class="${returnDef.returnEntityName}"
<#elseif c2h.isNamedSQLReturnRole(returnDef)>
             property="${returnDef.ownerAlias}.${returnDef.ownerProperty}"
<#elseif c2h.isNamedSQLReturnCollection(returnDef)>
             role="${returnDef.ownerEntityName}.${returnDef.ownerProperty}"
</#if>
<#if returnDef.lockMode?exists>
             lock-mode="${returnDef.lockMode.toString().toLowerCase()}"
</#if>	    />
</#foreach>        
      <![CDATA[${queryDef.queryString.trim()}]]>
    </sql-query>
    
</#foreach>