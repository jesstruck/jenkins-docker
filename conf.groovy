import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import hudson.util.Secret
import hudson.security.*
import hudson.plugins.sshslaves.*
import hudson.security.csrf.DefaultCrumbIssuer
import java.util.HashSet
import jenkins.*
import jenkins.model.*
import jenkins.model.Jenkins
import jenkins.security.s2m.AdminWhitelistRule
import net.sf.json.JSONObject
import org.jenkinsci.plugins.*
import org.jenkinsci.plugins.plaincredentials.*
import org.jenkinsci.plugins.plaincredentials.impl.*
import org.apache.commons.fileupload.*
import org.apache.commons.fileupload.disk.*

class Ldap{
    def conf(){
        println ''
        //Setting up ldap
        String server = 'ldap://1.2.3.4'
        String rootDN = 'dc=foo,dc=com'
        String userSearchBase = 'cn=users,cn=accounts'
        String userSearch = ''
        String groupSearchBase = ''
        String managerDN = 'uid=serviceaccount,cn=users,cn=accounts,dc=foo,dc=com'
        //Todo must encrypted
        String managerPassword = 'password'
        boolean inhibitInferRootDN = false
        SecurityRealm ldap_realm = new LDAPSecurityRealm(server, rootDN, userSearchBase, userSearch, groupSearchBase, managerDN, managerPassword, inhibitInferRootDN)
        Jenkins.instance.setSecurityRealm(ldap_realm)
        Jenkins.instance.save()
    }
}

class Slack {
    def conf() {
        // Setting up Slack
        JSONObject formData = ['slack': ['tokenCredentialId': 'SlackIntegration']] as JSONObject
        def slack = Jenkins.instance.getExtensionList(jenkins.plugins.slack.SlackNotifier.DescriptorImpl.class)[0]
        //valid tokens for testing in the jenkins-slack-plugin-test instance of slack.com
        def params = [
                slackBaseUrl: new jenkins.model.JenkinsLocationConfiguration().getUrl(),
                slackTeamDomain: 'team-name',
                slackToken: '',
                slackBotUser: 'true',
                slackRoom: 'jenkins',
                slackSendAs: 'Jenkins',
                slackNotifySuccess: 'false'
        ]
        def req = [ getParameter: { name -> params[name] }] as org.kohsuke.stapler.StaplerRequest
        slack.configure(req, formData)

        slack.save()
        println 'Slack configured!'
    }
}

class Credentials{
    def conf(){
      def credentials_store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()
      def global_domain = Domain.global()
      //Ex, on how to add username and password
      // usernameAndPassword = new UsernamePasswordCredentialsImpl(
      //                                         CredentialsScope.GLOBAL,
      //                                         "jenkins-slave-password", "Jenkis Slave with Password Configuration",
      //                                         "root",
      //                                        "jenkins" )
      // credentials_store.addCredentials(global_domain, usernameAndPassword)

      // Create a global credential to work with git, this is needed to access the git related jobs.
      // which means all the jobs, since all our jobs are somewhat working with git :)
      // This assumes there is a ssh private key in /root/.ssh/ which i actually added in dockerfile , no need to worry
      def key_credentials = new BasicSSHUserPrivateKey(
                                              CredentialsScope.GLOBAL, "Jenkins2Gitlab", "jenkins",
                                              new BasicSSHUserPrivateKey.UsersPrivateKeySource(),
                                              "", "Used for Jenkins to access Gitlab server")
      credentials_store.addCredentials(global_domain, key_credentials)
      def secretText = new StringCredentialsImpl(
                                            CredentialsScope.GLOBAL,
                                            "SlackIntegration", //ID
                                            "", //description
                                            Secret.fromString("your-secret-slack-token"))
      credentials_store.addCredentials(global_domain, secretText)
    }
}

class JenkinsConfigure{
    def instance = Jenkins.getInstance()
    def conf(){
        locale()
        exeuction()
        securityVulnerabilities()
        createAdminUser()
        instance.save()
    }

    def exeuction(){
        instance.setNumExecutors(0)
        instance.setSlaveAgentPort([55000])
    }

    def securityVulnerabilities(){
        //disables cli -remoting - google it
        jenkins.CLI.get().setEnabled(false)

        //enforces login, but all logged in users can do anything
        def strategy = new hudson.security.FullControlOnceLoggedInAuthorizationStrategy()
        strategy.setAllowAnonymousRead(false)
        instance.setAuthorizationStrategy(strategy)

        //CSRF
        instance.setCrumbIssuer(new DefaultCrumbIssuer(true))

        //Agent to master security subsystem
        instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)

        //TODO: consider -> Disable jnlp
        //jenkins.setSlaveAgentPort(-1)

        /** Agent protocols ("Java Web Start Agent Protocol/1", Java Web Start Agent Protocol/2  )
        *   Disable old Non-Encrypted protocols
         *  This still gives a warning in log during start up, because the startup test is done before this groovy hook is executed
        */
        instance.setAgentProtocols( (Set<String>) ['JNLP4-connect', 'Ping'])
    }

    def createAdminUser(){
        def hudsonRealm = new HudsonPrivateSecurityRealm(false)
        //find your own secure admin username / password combo (you will not get mine :))
        hudsonRealm.createAccount("admin","admin")
        instance.setSecurityRealm(hudsonRealm)
    }
    def locale(){
        def plugin = instance.getPluginManager().getPlugin('locale').getPlugin()
        plugin.setSystemLocale('en')
        plugin.ignoreAcceptLanguage = true
    }
}


//Main loop
new JenkinsConfigure().conf()
//Credentials
new Credentials().conf()
//LDAP
new Ldap().conf()
//Slack
new Slack().conf()
