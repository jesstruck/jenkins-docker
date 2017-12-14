FROM jenkins/jenkins:2.93-alpine
MAINTAINER Jes Struck

#Install plugins
RUN /usr/local/bin/install-plugins.sh \
  blueocean:1.3.4 \
  disk-usage:0.28 \
  greenballs:1.15 \
  jenkinslint:0.14.0 \
  jobConfigHistory:2.18 \
  ldap:1.18 \
  locale:1.2 \
  monitoring:1.70.0 \
  nested-view:1.14 \
  saferestart:0.3 \
  slack:2.3 \
  tasks:4.52

# To avoid jenkins to run setup wizard
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

#Copy in Jenkins masters private public key
COPY key-pair/* /root/.ssh/

# Environment setup
COPY conf.groovy /usr/share/jenkins/ref/init.groovy.d/conf.groovy

#Job Seeding
COPY jobs.groovy /usr/share/jenkins/ref/init.groovy.d/jobs.groovy

