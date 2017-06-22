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
  jobConfigHistory:2.16



# To avoid jenkins to run setup wizard
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
