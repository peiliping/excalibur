mvn clean 
mvn package
rm -rf target && mkdir target
cp agent/target/agent-jar-with-dependencies.jar target/agent.jar
cp  core/target/core-jar-with-dependencies.jar  target/core.jar
