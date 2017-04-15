package com.tibco.businessworks6.sonar.plugin.metric;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Nullable;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;
import org.sonar.api.measures.SumChildValuesFormula;
import org.sonar.api.utils.SonarException;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.tibco.businessworks6.sonar.plugin.data.model.BwSharedResource;

public class BusinessWorksMetrics implements Metrics{

	public static final String BWLANGUAGEFLAG_KEY = "isbwproject";
	public static final Metric BWLANGUAGEFLAG = new Metric.Builder(BWLANGUAGEFLAG_KEY,
			"TIBCO BusinessWorks Nature", Metric.ValueType.BOOL)
			.setDescription("Equals true if the resource is a TIBCO BusinessWorks project or module")
			.setQualitative(false)
			.setDomain(CoreMetrics.DOMAIN_GENERAL).create();
	/*
	 * 
	 * Module Properties metrics
	 * 
	 */
	public static final String GLOBALVARIABLES_KEY = "globalvariables";
	public static final Metric GLOBALVARIABLES = new Metric.Builder(GLOBALVARIABLES_KEY,
			"Module Properties", Metric.ValueType.INT)
			.setDescription("Total number of module properties")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	public static final String JOBSHAREDVARIABLES_KEY = "jobsharedvariables";
	public static final Metric JOBSHAREDVARIABLES = new Metric.Builder(JOBSHAREDVARIABLES_KEY,
			"Job Shared Variables", Metric.ValueType.INT)
			.setDescription("Total number of job shared variables")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	public static final String MODULESHAREDVARIABLES_KEY = "modulesharedvariables";
	public static final Metric MODULESHAREDVARIABLES = new Metric.Builder(MODULESHAREDVARIABLES_KEY,
			"Module Shared Variables", Metric.ValueType.INT)
			.setDescription("Total number of module shared variables")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	public static final String CATCHBLOCK_KEY = "catchblock";
	public static final Metric CATCHBLOCK = new Metric.Builder(CATCHBLOCK_KEY,
			"Catch Blocks", Metric.ValueType.INT)
			.setDescription("Total number of catch blocks")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	public static final String EVENTHANDLER_KEY = "eventhandler";
	public static final Metric EVENTHANDLER = new Metric.Builder(EVENTHANDLER_KEY,
			"Event Handlers", Metric.ValueType.INT)
			.setDescription("Total number of event handlers")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric BWRESOURCES_HTTP_CONNECTION = new Metric.Builder(BwSharedResource.BWResources.http_HttpClientConfiguration.name(),
			"HTTP Client", Metric.ValueType.INT)
			.setDescription("Total of shared HTTP connection resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric BWRESOURCES_HTTP_CONNECTOR = new Metric.Builder(BwSharedResource.BWResources.httpconnector_HttpConnectorConfiguration.name(),
			"HTTP Connector", Metric.ValueType.INT)
			.setDescription("Total of shared HTTP connection resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	public static final Metric BWRESOURCES_JDBC_CONNECTION = new Metric.Builder(BwSharedResource.BWResources.jdbc_JdbcDataSource.name(),
			"JDBC Connections", Metric.ValueType.INT)
			.setDescription("Total of shared JDBC connection resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric BWRESOURCES_JMS_CONNECTION = new Metric.Builder(BwSharedResource.BWResources.jms_JMSConnectionFactory.name(),
			"JMS Connections", Metric.ValueType.INT)
			.setDescription("Total of shared JMS connection resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric XML_AUTHENTICATION = new Metric.Builder(BwSharedResource.BWResources.authxml_XMLConfiguration.name(),
			"XML Authentication", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric SSL_Server_Configuration = new Metric.Builder(BwSharedResource.BWResources.sslserver_SSLServerConfiguration.name(),
			"SSL Server Configuration", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric SSL_Client_Configuration = new Metric.Builder(BwSharedResource.BWResources.sslclient_SSLClientConfiguration.name(),
			"SSL Client Configuration", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric SMTP_Resource = new Metric.Builder(BwSharedResource.BWResources.smtp_SmtpConfiguration.name(),
			"SMTP Resource", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric Data_Format = new Metric.Builder(BwSharedResource.BWResources.dataformat_DataFormat.name(),
			"Data Format", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric SQL_File = new Metric.Builder(BwSharedResource.BWResources.SQLFile.name(),
			"SQL File", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric FTL_Realm_Server_Connection = new Metric.Builder(BwSharedResource.BWResources.ftlsr_FTLRealmServerConnection.name(),
			"FTL Realm Server Connection", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric FTP_Connection = new Metric.Builder(BwSharedResource.BWResources.ftpconnection_ftpconnection.name(),
			"FTP Connection", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric Identity_Provider = new Metric.Builder(BwSharedResource.BWResources.mip_MIPConfiguration.name(),
			"Identity Provider", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric Java_Global_Instance = new Metric.Builder(BwSharedResource.BWResources.javasharedresource_JavaGlobalInstanceResource.name(),
			"Java Global Instance", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric JNDI_Configuration = new Metric.Builder(BwSharedResource.BWResources.jms_JNDIConnection.name(),
			"JNDI Configuration", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric Keystore_Provider = new Metric.Builder(BwSharedResource.BWResources.keystore_KeystoreConfiguration.name(),
			"Keystore Provider", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric LDAP_Authentication = new Metric.Builder(BwSharedResource.BWResources.ldapauth_LDAPConfiguration.name(),
			"LDAP Authentication", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric Proxy_Configuration = new Metric.Builder(BwSharedResource.BWResources.httpproxy_ProxyConfiguration.name(),
			"Proxy Configuration", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric Trust_Provider = new Metric.Builder(BwSharedResource.BWResources.TrustProvider.name(),
			"Trust Provider", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric Threal_Pool = new Metric.Builder(BwSharedResource.BWResources.tp_ThreadPoolConfiguration.name(),
			"Threal Pool", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric Subject_Provider = new Metric.Builder(BwSharedResource.BWResources.subject_SubjectConfiguration.name(),
			"Subject Provider", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	public static final Metric TCP_Connection = new Metric.Builder(BwSharedResource.BWResources.tcpconnection_tcpconnection.name(),
			"TCP Connection", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final Metric WSS_Authentication = new Metric.Builder(BwSharedResource.BWResources.wss_WSSConfiguration.name(),
			"WSS Authentication", Metric.ValueType.INT)
			.setDescription("Total resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	public static final Metric BWRESOURCES_RV_TRANSPORT = new Metric.Builder(BwSharedResource.BWResources.rvsharedresource_RVResource.name(),
			"RV Transport", Metric.ValueType.INT)
			.setDescription("Total of RV Transport resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	
	public static final String BWRESOURCES_KEY = "bwresources";
	public static final Metric[] BWRESOURCES_METRICS_LIST = {
		BWRESOURCES_JDBC_CONNECTION,
		Java_Global_Instance,
		BWRESOURCES_JMS_CONNECTION,
		BWRESOURCES_HTTP_CONNECTION,
		BWRESOURCES_RV_TRANSPORT,
		XML_AUTHENTICATION,
		WSS_Authentication,
		TCP_Connection,
		Subject_Provider,
		Threal_Pool,
		Trust_Provider,
		Proxy_Configuration,
		LDAP_Authentication,
		Keystore_Provider,
		JNDI_Configuration,
		Identity_Provider,
		FTP_Connection,
		FTL_Realm_Server_Connection,
		SQL_File,
		SSL_Server_Configuration,
		SSL_Client_Configuration,
		Data_Format,
		SMTP_Resource,
	};

	//public static final Metric[] BWRESOURCES_METRICS_LIST = resourceMetrics();
			
			
	public static final Metric BWRESOURCES = new Metric.Builder(BWRESOURCES_KEY,
			"Resources", Metric.ValueType.INT)
			.setDescription("Number of BusinessWorks resources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumDependentMetricsFormula(BWRESOURCES_METRICS_LIST))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	/*
	 * 
	 * 
	 * Processes metrics
	 * 
	 */
	
	public static final String PROCESSES_KEY = "processes";
	public static final Metric PROCESSES = new Metric.Builder(PROCESSES_KEY,
			"Processes", Metric.ValueType.INT)
			.setDescription("Number of processes")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	public static final String SUBPROCESSES_KEY = "subprocesses";
	public static final Metric SUBPROCESSES = new Metric.Builder(SUBPROCESSES_KEY,
			"SubProcesses", Metric.ValueType.INT)
			.setDescription("Number of sub processes")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final String GROUPS_KEY = "groups";
	public static final Metric GROUPS = new Metric.Builder(GROUPS_KEY,
			"Groups", Metric.ValueType.INT).setDescription("Number of groups")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final String ACTIVITIES_KEY = "activities";
	public static final Metric ACTIVITIES = new Metric.Builder(ACTIVITIES_KEY,
			"Activities", Metric.ValueType.INT)
			.setDescription("Number of activities")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final String PROCESSSTARTER_KEY = "eventSources";
	public static final Metric PROCESSSTARTER = new Metric.Builder(PROCESSSTARTER_KEY,
			"Event Sources", Metric.ValueType.INT)
			.setDescription("Number of process starters/ event sources")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	public static final String TRANSITIONS_KEY = "transitions";
	public static final Metric TRANSITIONS = new Metric.Builder(
			TRANSITIONS_KEY, "Transitions", Metric.ValueType.INT)
			.setDescription("Number of transitions")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();

	public static final String SERVICES_KEY = "services";
	public static final Metric SERVICES = new Metric.Builder(SERVICES_KEY,
			"Total Services Exposed", Metric.ValueType.INT)
			.setDescription("Exposed Services")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	public static final String SUBSERVICES_KEY = "subservices";
	public static final Metric SUBSERVICES = new Metric.Builder(SUBSERVICES_KEY,
			"Services", Metric.ValueType.INT)
			.setDescription("Services")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	public static final String SUBREFERENCE_KEY = "subreference";
	public static final Metric SUBREFERENCE = new Metric.Builder(SUBREFERENCE_KEY,
			"References", Metric.ValueType.INT)
			.setDescription("References")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	public static final String PROJECTCOMPLEXITY_KEY = "projectcomplexity";
	public static final Metric PROJECTCOMPLEXITY = new Metric.Builder(PROJECTCOMPLEXITY_KEY,
			"Project Complexity", Metric.ValueType.STRING)
			.setDescription("Project Complexity")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	public static final String CODEQUALITY_KEY = "codequality";
	public static final Metric CODEQUALITY = new Metric.Builder(CODEQUALITY_KEY,
			"Code Quality", Metric.ValueType.STRING)
			.setDescription("Code Quality")
			.setDirection(Metric.DIRECTION_WORST).setQualitative(false)
			.setFormula(new SumChildValuesFormula(false))
			.setDomain(CoreMetrics.DOMAIN_SIZE).create();
	
	private static final List<Metric> METRICS;

	static {
		METRICS = Lists.newLinkedList();
		for (Field field : BusinessWorksMetrics.class.getFields()) {
			if (Metric.class.isAssignableFrom(field.getType())) {
				try {
					Metric metric = (Metric) field.get(null);
					METRICS.add(metric);
				} catch (IllegalAccessException e) {
					throw new SonarException("can not introspect "
							+ CoreMetrics.class + " to get metrics", e);
				}
			}
		}
	}

	public List<Metric> getMetrics() {
		return METRICS;
	}

	public static Metric getMetric(final String key) {
		return Iterables.find(METRICS, new Predicate<Metric>() {
			public boolean apply(@Nullable Metric input) {
				return input != null && input.getKey().equals(key);
			}
		});
	}

}
