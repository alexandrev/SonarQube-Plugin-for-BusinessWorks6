package com.tibco.businessworks6.sonar.plugin.check.project;

import com.tibco.businessworks6.sonar.plugin.check.AbstractProjectCheck;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.tibco.businessworks6.sonar.plugin.profile.ProcessSonarWayProfile;
import com.tibco.businessworks6.sonar.plugin.source.ProcessSource;
import com.tibco.businessworks6.sonar.plugin.source.ProjectSource;
import com.tibco.businessworks6.sonar.plugin.data.model.BwProject;
import com.tibco.businessworks6.sonar.plugin.data.model.BwService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.sonar.api.batch.fs.InputFile;

@Rule(key = DeadLockCheck.RULE_KEY, name = "Deadlock Detection Check", priority = Priority.BLOCKER, description = "There are many situations in which deadlocks can be created between communicating web services. This rule checks for deadlocks and infinite loops in BW6 process design.")
@BelongsToProfile(title = ProcessSonarWayProfile.defaultProfileName, priority = Priority.BLOCKER)
public class DeadLockCheck extends AbstractProjectCheck {

    private Map<String, com.tibco.businessworks6.sonar.plugin.data.model.BwProcess> servicetoprocess = new HashMap<>();

    public static final String RULE_KEY = "DeadlockDetection";

    @Override
    protected void validate(ProjectSource processSource) {
       BwProject project = processSource.getResourceModel();
       if(project != null){
           analyseDeadLock(project);
       }
    }

    protected void analyseDeadLock(BwProject project) {
        for (com.tibco.businessworks6.sonar.plugin.data.model.BwProcess process : project.getProcess()) {
            Map<String, BwService> services = process.getServices();
            for (String servicename : services.keySet()) {
                String key = servicename + "-" + services.get(servicename).getNamespace() + "-" + process.getName();
                servicetoprocess.put(key, process);
            }
        }
        //------All set ready to go
        String processName = null;
        for (com.tibco.businessworks6.sonar.plugin.data.model.BwProcess process : project.getProcess()) {
            String proc1 = process.getName();
            //TODO This should be a process metod (getBaseName();
            proc1 = proc1.substring(proc1.lastIndexOf(".") + 1).concat(".bwp");
            findDeadLock(process.getServices(), process.getProcessReferenceServices(), process, process.getSource(), process.getResource(), processName);

        }
    }

    public void findDeadLock(Map<String, BwService> services, Map<String, BwService> referencedServices, com.tibco.businessworks6.sonar.plugin.data.model.BwProcess process, ProcessSource sourceCode, InputFile resource, String processName) {
        if (services.size() > 0 && referencedServices.size() > 0) {
            Set<String> serviceName = services.keySet();
            Set<String> referenceServiceName = referencedServices.keySet();
            Set<String> dupReferencedServiceName = new HashSet<>(referenceServiceName);

            dupReferencedServiceName.retainAll(serviceName);
            if (dupReferencedServiceName.size() > 0) {
                String[] deadlockedService = dupReferencedServiceName.toArray(new String[dupReferencedServiceName.size()]);
                String referencedServiceNameSpace = referencedServices.get(deadlockedService[0]).getNamespace();
                String serviceNamespace = services.get(deadlockedService[0]).getNamespace();
                String referenceProcessName = referencedServices.get(deadlockedService[0]).getImplementationProcess();
                String proc2 = process.getName();

                if (referencedServiceNameSpace.equals(serviceNamespace) && referenceProcessName != null && proc2.equals(referenceProcessName)) {
                    if (processName == null) {
                        addError(1, "There is a very high possibility of deadlock in the implementation of service " + deadlockedService[0] + " exposed by process " + proc2, sourceCode);

                    } else {
                        addError(1, "Deadlock is detected between processes " + proc2 + " and " + processName + ". There is a very high possibility of deadlock in the implementation of service " + deadlockedService[0] + " exposed by process " + proc2 + " and consumed by process " + processName, sourceCode);
                    }
                    //TODO saveIssues(sourceCode, resource);
                } else {
                    for (String name : referenceServiceName) {
                        com.tibco.businessworks6.sonar.plugin.data.model.BwProcess proc = servicetoprocess.get(name + "-" + referencedServices.get(name).getNamespace() + "-" + referencedServices.get(name).getImplementationProcess());
                        if (proc.getProcessReferenceServices() != null) {
                            processName = null;
                            processName = proc.getName();
                            processName = processName.substring(processName.lastIndexOf(".") + 1).concat(".bwp");
                            findDeadLock(services, proc.getProcessReferenceServices(), process, sourceCode, resource, processName);
                        }
                    }
                }
            } else {
                for (String name : referenceServiceName) {
                    com.tibco.businessworks6.sonar.plugin.data.model.BwProcess proc = servicetoprocess.get(name + "-" + referencedServices.get(name).getNamespace() + "-" + referencedServices.get(name).getImplementationProcess());
                    if (proc != null && proc.getProcessReferenceServices() != null) {
                        processName = null;
                        processName = proc.getName();
                        processName = processName.substring(processName.lastIndexOf(".") + 1).concat(".bwp");
                        findDeadLock(services, proc.getProcessReferenceServices(), process, sourceCode, resource,processName);
                    }
                }

            }
        }
    }

}
