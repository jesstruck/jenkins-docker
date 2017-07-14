import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import hudson.plugins.git.*;

println('''##############################
#                            #
# Creating Job "MY-PIPELINE" #
#                            #
##############################
''')

def scm = new GitSCM("https://github.com/jesstruck/SpringBoot-CRUD.git")
scm.branches = [new BranchSpec("*/master")];
//MISSING adding credentials to the scm config
//MISSING adding trigger for MR requests/ simple code changes

def flowDefinition = new org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition(scm, "Jenkinsfile")

//Creating the job
def parent = Jenkins.instance
def job = new org.jenkinsci.plugins.workflow.job.WorkflowJob(parent, "my-pipeline")
job.definition = flowDefinition

//reload configurations
parent.reload()
