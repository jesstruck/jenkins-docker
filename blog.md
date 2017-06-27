# Scripting a complete Jenkins environment

Ok, there is many post/ article on the web on how to setup Jenkins as a Docker images, there is also a lot of post that describes how to seed job into Jenkins using DSL. But in my world ALL of them are half done. my requirements to such a setup is much higher. I would expect

* That Jenkins preinstalled with all plugins.
* The system is preconfigured, so that all plugins and system setting is set.
* all PIPELINE jobs is scripted.
* All the configurations should be scripted (NO XML)

so this post is about, how to achieve all of these three things in on go. The first two is of course already describe so many times' so i will jsut go trough them fast

## Preinstalled Jenkins plugins

    FROM jenkins
    MAINTAINER Jes Struck

    RUN /usr/local/bin/install-plugins.sh \
      ldap:1.15 \
      disk-usage:0.28 \
      monitoring:1.67.0 \
      nested-view:1.14 \
      saferestart:0.3 \
      greenballs:1.15 \
      tasks:4.51 \
      jobConfigHistory:2.16 \
      slack:2.2 \
      blueocean:1.1.2


or you can pass the the plugins.txt to the install-plugins.sh see https://github.com/jenkinsci/docker#Preinstalling_plugins

    FROM jenkins
    COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
    RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt


## Preconfigure Jenkins
This section of course is very much dependent on which tools you select in your install part, but this section should be considered as inspiration for how things should be done.

create conf.groovy and include all conf*.groovy files.

### Avoid startup wizard

    ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

### Configuring LDAP
create conf/ldap.groovy with the following content

    import jenkins.model.*
    import hudson.security.*
    import org.jenkinsci.plugins.*
s
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

### Slack
create conf/slack.groovy

    import net.sf.json.JSONObject
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

## Configuring Jenkins Pipeline jobs
