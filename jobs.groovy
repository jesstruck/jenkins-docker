import Jenkins.model.*

pipelineJob("Hello World"){
  logRotator{
  numToKeep 14
  }
  definition{
    cps{
      sandbox()
      
    }
  }
}
