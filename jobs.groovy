import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import hudson.plugins.git.*;

def scm = new GitSCM("https://github.com/jesstruck/jenkins.git")
scm.branches = [new BranchSpec("ready/*")];
//scm.credentials

def flowDefinition = new org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition(scm, "Jenkinsfile")

def parent = Jenkins.instance
def job = new org.jenkinsci.plugins.workflow.job.WorkflowJob(parent, "my-pipeline")
job.definition = flowDefinition



parent.reload()
