<#foreach importKey in cfg.imports.keySet()>
<#assign importDef = cfg.imports.get(importKey)>
<#if !importKey.equals(importDef)>
    <import class="${importKey}" rename="${importDef}"/>
</#if></#foreach>