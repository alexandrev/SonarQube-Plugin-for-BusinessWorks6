package com.tibco.businessworks6.sonar.plugin.rulerepository;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;

import com.tibco.businessworks6.sonar.plugin.BwConstants;
import com.tibco.businessworks6.sonar.plugin.services.l10n.LocalizationService;
import com.tibco.businessworks6.sonar.plugin.services.l10n.impl.LocalizationServiceImpl;
import java.util.Locale;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.squidbridge.rules.ExternalDescriptionLoader;
import org.sonar.squidbridge.rules.PropertyFileLoader;

public final class ProcessRuleDefinition implements RulesDefinition {

    protected LocalizationService l10n = LocalizationServiceImpl.getInstance();

    public static List<Class> checkRules;
    public static Class check[] = {
        com.tibco.businessworks6.sonar.plugin.check.process.NoDescriptionCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.NumberofActivitiesCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.TransitionLabelCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.ChoiceOtherwiseCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.JDBCWildCardCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.JDBCHardCodeCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.MultipleTransitionCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.project.DeadLockCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.LogOnlyInSubprocessCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.JMSHardCodeCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.ForEachMappingCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.ForEachGroupCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.NumberofServicesCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.CheckpointAfterHttpCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.CheckpointAfterRESTCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.CheckpointAfterJDBCÇheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.CheckpointInTransation.class,
        com.tibco.businessworks6.sonar.plugin.check.process.JMSAcknowledgementModeCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.CriticalSectionCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.SubProcessInlineCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.ProcessVersionCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.process.BwTransitionXPathNoOtherwiseCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.project.BwModulePropertyNotUsed.class,
        com.tibco.businessworks6.sonar.plugin.check.project.BwSharedVariableNotUsed.class,
        com.tibco.businessworks6.sonar.plugin.check.resources.BwSharedResourceNotUsed.class,
        com.tibco.businessworks6.sonar.plugin.check.resources.BwSharedResourceParameterRequired.class,
        com.tibco.businessworks6.sonar.plugin.check.resources.BwSharedResourceUsingModuleProperty.class,
        com.tibco.businessworks6.sonar.plugin.check.other.BwCustomXPathFilter.class,
        com.tibco.businessworks6.sonar.plugin.check.process.ProcessNameCheck.class,
        com.tibco.businessworks6.sonar.plugin.check.project.BwProjectProfileConvention.class,
        com.tibco.businessworks6.sonar.plugin.check.process.ProcessNamespaceCheck.class
    };

    public ProcessRuleDefinition() {

    }

    public void define(Context context) {
        NewRepository repository = context.createRepository(BwConstants.REPOSITORY_KEY, BwConstants.LANGUAGE_KEY).setName(BwConstants.LANGUAGE_NAME);
        RulesDefinitionAnnotationLoader annotationLoader = new RulesDefinitionAnnotationLoader();
        annotationLoader.load(repository, getChecks());
        String externalDescriptionBasePath = l10n.getLocalizedFolderName(String.format("/org/sonar/l10n/%s", BwConstants.LANGUAGE_KEY));
        externalDescriptionBasePath = String.format("%s/rules/%s", externalDescriptionBasePath, repository.key());
        ExternalDescriptionLoader.loadHtmlDescriptions(repository,externalDescriptionBasePath);
        PropertyFileLoader.loadNames(repository, ProcessRuleDefinition.class.getResourceAsStream(l10n.getLocalizedFilePath("/org/sonar/l10n/bw")));
        l10n.setupExternalNames(repository.rules(), repository);
        repository.done();
    }

    @SuppressWarnings("rawtypes")
    public static Class[] getChecks() {
        checkRules = Arrays.asList(check);
        return check;

    }

    protected void extractRulesData(NewRepository repository, String xmlRulesFilePath, String htmlDescriptionFolder) {
        RulesDefinitionXmlLoader ruleLoader = new RulesDefinitionXmlLoader();
        ruleLoader.load(repository, ProcessRuleDefinition.class.getResourceAsStream(xmlRulesFilePath), "UTF-8");

    }

}
