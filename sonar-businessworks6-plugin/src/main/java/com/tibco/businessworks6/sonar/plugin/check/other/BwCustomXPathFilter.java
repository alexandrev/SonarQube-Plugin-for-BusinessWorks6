package com.tibco.businessworks6.sonar.plugin.check.other;

import com.tibco.businessworks6.sonar.plugin.check.AbstractXmlCheck;
import com.tibco.businessworks6.sonar.plugin.data.model.helper.XmlHelper;
import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import com.tibco.businessworks6.sonar.plugin.source.XmlSource;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.w3c.dom.Element;

@Rule(
        name = "BwCustomXPathFilter",
        description = "BwCustomXPathFilter",
        key = BwCustomXPathFilter.RULE_KEY,
        priority = Priority.CRITICAL)
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.MAJOR)
public class BwCustomXPathFilter extends AbstractXmlCheck {

    public static final String RULE_KEY = "BwCustomXPathFilter";

   
    @RuleProperty(
            key = "regularExpressionFileAffected",
            type = "TEXT",
            defaultValue = ".*\\.bwp")
    private String regularExpressionFileAffected;

    @RuleProperty(
            key = "xpathExpression",
            type = "TEXT",
            defaultValue = "//")
    private String xpathExpression;

    @RuleProperty(
            key = "expectedValue",
            type = "TEXT",
            defaultValue = "//")
    private String expectedValue;

    @RuleProperty(
            key = "alertMessage",
            type = "TEXT",
            defaultValue = "//")
    private String message;

    @Override
    public void validate(XmlSource processXml) {
        LOG.debug("Started rule: " + this.getClass());

        LOG.debug("Trying to check: " + processXml.getXmlFile().getIOFile().getAbsolutePath());
        if (isFileAffected(processXml.getXmlFile().getIOFile())) {
            LOG.debug("The file has been elected to check the custom check:" + processXml.getXmlFile().getIOFile().getAbsolutePath());
            LOG.debug("Going to check against this xpath expression: " + xpathExpression);
            Element node = XmlHelper.evalueXPathSingleElement(processXml.getDocument(true).getDocumentElement(), xpathExpression);
            if (node != null) {
                String value = node.getNodeValue();
                LOG.debug("Value recovered: " + value);
                LOG.debug("Checking the recovered value against the expected value: " + expectedValue);
                if (value != null && value.equals(expectedValue)) {
                    LOG.warn("Going to raise an issue with this message: " + message);
                    addError(XmlHelper.getLineNumber(node), message, processXml);
                }
            }
        }
        LOG.debug("Finished rule: " + this.getClass());
    }

    private boolean isFileAffected(File processXml) {
        if (processXml != null) {
            String name = processXml.getName();
            if (name != null && !name.isEmpty()) {
                Pattern pattern = Pattern.compile(regularExpressionFileAffected);
                if (pattern != null) {
                    Matcher matcher = pattern.matcher(name);
                    return matcher != null && matcher.matches();
                }
            }
        }
        return false;
    }

    /**
     * @param regularExpressionFileAffected the regularExpressionFileAffected to
     * set
     */
    public void setRegularExpressionFileAffected(String regularExpressionFileAffected) {
        this.regularExpressionFileAffected = regularExpressionFileAffected;
    }

    /**
     * @param xpathExpression the xpathExpression to set
     */
    public void setXpathExpression(String xpathExpression) {
        this.xpathExpression = xpathExpression;
    }

    /**
     * @param expectedValue the expectedValue to set
     */
    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
