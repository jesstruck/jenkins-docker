import hudson.tasks.Maven.MavenInstallation;
import hudson.tools.InstallSourceProperty;
import hudson.tools.ToolProperty;
import hudson.tools.ToolPropertyDescriptor;
import hudson.util.DescribableList;
import hudson.model.JDK
import hudson.tools.JDKInstaller
import org.jenkinsci.plugins.openjdk_native.OpenJDKInstaller
import org.jenkinsci.plugins.openjdk_native.OpenJDKInstaller.OpenJDKPackage
import hudson.tools.InstallSourceProperty
import jenkins.model.Jenkins

println('''##############################
#                            #
# Configuring the Maven      #
#                            #
##############################
''')
def mavenDesc = jenkins.model.Jenkins.instance.getExtensionList(hudson.tasks.Maven.DescriptorImpl.class)[0]
def isp = new InstallSourceProperty()
def autoInstaller = new hudson.tasks.Maven.MavenInstaller("3.3.3")
isp.installers.add(autoInstaller)

def proplist = new DescribableList<ToolProperty<?>, ToolPropertyDescriptor>()
proplist.add(isp)

def installation = new MavenInstallation("M3", "", proplist)
mavenDesc.setInstallations(installation)
mavenDesc.save()


println('''#################################################################################################################
#                                                                                                               #
#  Configuring the Java                                                                                         #
#   * Please go to http://${your-host-name}/configureTools/ and put in you oracle credentials for JDK install   #
#                                                                                                               #
#################################################################################################################
''')
def descriptor = new JDK.DescriptorImpl()
if (descriptor.getInstallations()) {
    println(descriptor.getInstallations().dump())
    println 'skip jdk installations'
} else {
    println 'add jdk8'

    Jenkins.instance.updateCenter.getById('default').updateDirectlyNow(true)
    def jdkInstaller = new JDKInstaller('jdk-8u45-oth-JPR', true)
    def jdk8 = new JDK("jdk8", null, [new InstallSourceProperty([jdkInstaller])])

    println 'add openjdk8'
    Jenkins.instance.updateCenter.getById('default').updateDirectlyNow(true)
    def openJDKInstaller8 = new OpenJDKInstaller(OpenJDKPackage.openJDK8)
    def openjdk8 = new JDK("openjdk8", null, [new InstallSourceProperty([openJDKInstaller8])])

    println 'add openjdk7'
    Jenkins.instance.updateCenter.getById('default').updateDirectlyNow(true)
    def openJDKInstaller7 = new OpenJDKInstaller(OpenJDKPackage.openJDK7)
    def openjdk7 = new JDK("openjdk7", null, [new InstallSourceProperty([openJDKInstaller7])])

    descriptor.setInstallations(jdk8,openjdk8,openjdk7)
}

