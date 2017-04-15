/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tibco.businessworks6.sonar.plugin.data.model;

import java.util.Collection;
import org.w3c.dom.Node;

public interface BwFlow {

    public Collection<BwTransition> getTransitionList();

    public Collection<BwActivity> getActivityList();

    public Collection<BwVariable> getVariables();

    public Node getNode();

    
}
