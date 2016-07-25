server.xml
<Valve className="tsar.tomcat.status.TsarValve" directory="logs" prefix="localhost_access_log" suffix=".txt" pattern="%h %l %u %t &quot;%r&quot; %s %b" />

web.xml
<filter>
	<filter-name>tomcat-status-filter</filter-name>
        <filter-class>tsar.tomcat.status.TsarFilter</filter-class>
	<async-supported>true</async-supported>
</filter>
<filter-mapping>
        <filter-name>tomcat-status-filter</filter-name>
        <url-pattern>/*</url-pattern>
	<dispatcher>REQUEST</dispatcher>
</filter-mapping>
