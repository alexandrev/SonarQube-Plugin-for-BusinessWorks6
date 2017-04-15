
import com.tibco.businessworks6.sonar.plugin.data.model.BwProcess;
import java.io.File;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alexandrev
 */
public class Test {
    
    public static void main(String args[]){
        BwProcess p = new BwProcess(new File("/Users/alexandrev/test_2/tibco.bw.sample.core.dynamic.processInvocation.ServiceConsumer/Processes/tibco/bw/sample/core/dynamic/processinvocation/serviceconsumer/Consumer.bwp"));
        int l1 = p.getActivityList().iterator().next().getLine();
         int l2 = p.getActivityList().iterator().next().getLineNumber();
        int a = 0;
    }
}
