import jenkins.model.*
import hudson.security.*
import org.jenkinsci.plugins.*
import net.sf.json.JSONObject


//Setting up ldap
String server = 'ldap://1.2.3.4'
String rootDN = 'dc=foo,dc=com'
String userSearchBase = 'cn=users,cn=accounts'
String userSearch = ''
String groupSearchBase = ''
String managerDN = 'uid=serviceaccount,cn=users,cn=accounts,dc=foo,dc=com'
String managerPassword = 'password'
boolean inhibitInferRootDN = false

SecurityRealm ldap_realm = new LDAPSecurityRealm(server, rootDN, userSearchBase, userSearch, groupSearchBase, managerDN, managerPassword, inhibitInferRootDN)
Jenkins.instance.setSecurityRealm(ldap_realm)
Jenkins.instance.save()


// Setting up Slack
JSONObject formData = ['slack': ['tokenCredentialId': '']] as JSONObject

def slack = Jenkins.instance.getExtensionList(jenkins.plugins.slack.SlackNotifier.DescriptorImpl.class)[0]
//valid tokens for testing in the jenkins-slack-plugin-test instance of slack.com
def params = [
              slackTeamDomain: 'my-slack-team-name',
              slackToken: '123456789qwerty',
              slackRoom: '#my-slack-team-name-room #random',
              slackBuildServerUrl: 'http://my-jenkins-server-dns/',
              slackSendAs: 'Jenkins'
              ]
def req = [ getParameter: { name -> params[name] }] as org.kohsuke.stapler.StaplerRequest
slack.configure(req, formData)

slack.save()
println 'Slack configured!'
