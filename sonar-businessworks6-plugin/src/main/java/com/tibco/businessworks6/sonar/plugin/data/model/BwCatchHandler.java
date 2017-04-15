/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tibco.businessworks6.sonar.plugin.data.model;

/**
 *
 * @author alexandrev
 */
public class BwCatchHandler extends BwGroup{

    public BwCatchHandler(BwProcess p) {
        super(p);
    }
    
    
    private boolean catchAll;

    /**
     * @return the catchAll
     */
    public boolean isCatchAll() {
        return catchAll;
    }

    /**
     * @param catchAll the catchAll to set
     */
    public void setCatchAll(boolean catchAll) {
        this.catchAll = catchAll;
    }
    
    
}
